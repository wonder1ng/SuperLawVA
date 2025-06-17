# contract_service.py
"""
Description: 계약서 특약사항 생성 핵심 비즈니스 로직 및 벡터DB·LLM 연동 구현
Author: ooheunsu
Date: 2025-06-16
Requirements: python-dotenv, langchain-anthropic, langchain-openai, langchain-chroma, pydantic, asyncio, json, re, os
"""
from langchain_anthropic import ChatAnthropic
from langchain.prompts import ChatPromptTemplate
from langchain.schema import HumanMessage, SystemMessage
from langchain.output_parsers import PydanticOutputParser
from langchain.schema.runnable import RunnablePassthrough
from langchain.schema.output_parser import BaseOutputParser
from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings
from typing import List, Dict, Any, Set
import asyncio
import json
import re
import os
from dotenv import load_dotenv

from contract_schema import ContractInput, ContractOutput, RecommendedAgreement, LegalBasis, CaseBasis

# 환경변수 로드
load_dotenv()

class CustomJSONOutputParser(BaseOutputParser[ContractOutput]):
    """JSON 마크다운 블록을 처리할 수 있는 커스텀 파서"""
    
    def parse(self, text: str) -> ContractOutput:
        """JSON 텍스트를 파싱하여 ContractOutput 객체로 변환"""
        try:
            print(f"🔍 파싱할 원본 텍스트 (처음 200자): {text[:200]}...")
            
            # 1. 마크다운 JSON 블록 제거
            json_pattern = r'```json\s*(.*?)\s*```'
            match = re.search(json_pattern, text, re.DOTALL)
            
            if match:
                json_text = match.group(1)
                print("✅ 마크다운 JSON 블록 발견 및 추출")
            else:
                # 마크다운 블록이 없으면 전체 텍스트에서 JSON 추출 시도
                json_text = text.strip()
                print("⚠️ 마크다운 블록 없음, 전체 텍스트로 파싱 시도")
            
            print(f"🔍 추출된 JSON 텍스트 (처음 200자): {json_text[:200]}...")
            
            # 2. JSON 파싱
            parsed_data = json.loads(json_text)
            print("✅ JSON 파싱 성공")
            
            # 3. law_id 타입 변환 (숫자 → 문자열)
            if 'legal_basis' in parsed_data:
                for legal in parsed_data['legal_basis']:
                    if 'law_id' in legal and legal['law_id'] is not None:
                        original_id = legal['law_id']
                        legal['law_id'] = str(legal['law_id'])
                        print(f"🔄 law_id 변환: {original_id} → {legal['law_id']}")
            
            # 4. case_id 타입 변환 (숫자 → 문자열)
            if 'case_basis' in parsed_data:
                for case in parsed_data['case_basis']:
                    if 'case_id' in case and case['case_id'] is not None:
                        original_id = case['case_id']
                        case['case_id'] = str(case['case_id'])
                        print(f"🔄 case_id 변환: {original_id} → {case['case_id']}")
            
            # 5. 불필요한 필드 제거 (created_date 등)
            filtered_data = {
                'recommended_agreements': parsed_data.get('recommended_agreements', []),
                'legal_basis': parsed_data.get('legal_basis', []),
                'case_basis': parsed_data.get('case_basis', [])
            }
            
            print(f"📊 파싱 결과 - 특약: {len(filtered_data['recommended_agreements'])}개, 법령: {len(filtered_data['legal_basis'])}개, 판례: {len(filtered_data['case_basis'])}개")
            
            # 6. Pydantic 모델로 변환
            result = ContractOutput(**filtered_data)
            print("✅ ContractOutput 객체 생성 성공")
            return result
            
        except json.JSONDecodeError as e:
            print(f"❌ JSON 파싱 실패: {str(e)}")
            print(f"📄 문제가 된 텍스트: {text[:500]}...")
            raise ValueError(f"JSON 파싱 실패: {str(e)}")
        except Exception as e:
            print(f"❌ 전체 파싱 실패: {str(e)}")
            print(f"📄 원본 텍스트: {text[:500]}...")
            raise ValueError(f"파싱 실패: {str(e)}")
    
    def get_format_instructions(self) -> str:
        """출력 형식 지시사항"""
        return """
반드시 다음 JSON 형식으로만 응답하세요 (마크다운 블록 사용 금지):

{
  "recommended_agreements": [
    {
      "reason": "특약사항 이유",
      "suggested_revision": "제안하는 특약 조항",
      "negotiation_points": "협상 포인트"
    }
  ],
  "legal_basis": [
    {
      "law_id": "012345",
      "law": "법령명 제○조 제○항",
      "explanation": "법령 설명",
      "content": "법령 원문"
    }
  ],
  "case_basis": [
    {
      "case_id": "2024다315046",
      "case": "판례명",
      "explanation": "판례 설명 (임차인 관점에서)",
      "link": "판례 링크"
    }
  ]
}

중요: 
- 마크다운 ```json 블록을 사용하지 마세요
- 순수 JSON만 출력하세요
- law_id는 반드시 문자열로 작성하세요
- case_id는 반드시 실제 판례 문서ID를 문자열로 작성하세요
"""

class VectorDBManager:
    """벡터 데이터베이스 관리 클래스"""
    
    def __init__(self):
        self.law_db = None
        self.case_db = None
        self._initialize_dbs()
    
    def _initialize_dbs(self):
        """벡터 데이터베이스 초기화"""
        try:
            # 임베딩 모델 설정 (3072 차원 모델 사용)
            embeddings = OpenAIEmbeddings(model="text-embedding-3-large")
            
            # 법령 데이터베이스 연결
            law_db_path = os.getenv("CHROMA_LAW_DB_PATH", "./vectordb/chroma_law/chroma_openai_law")
            law_collection_name = os.getenv("LAW_COLLECTION_NAME", "law_chunks_openai")
            
            if os.path.exists(law_db_path):
                self.law_db = Chroma(
                    persist_directory=law_db_path,
                    embedding_function=embeddings,
                    collection_name=law_collection_name
                )
                print(f"✅ 법령 DB 연결 완료: {law_db_path}")
                print(f"📋 법령 컬렉션명: {law_collection_name}")
                
                # 컬렉션 정보 확인
                try:
                    collection_count = self.law_db._collection.count()
                    print(f"📊 법령 데이터 개수: {collection_count}개")
                except Exception as e:
                    print(f"⚠️ 법령 컬렉션 정보 확인 실패: {e}")
            else:
                print(f"⚠️ 법령 DB 경로를 찾을 수 없습니다: {law_db_path}")
            
            # 판례 데이터베이스 연결 (선택적)
            case_db_path = os.getenv("CHROMA_CASE_DB_PATH", "./vectordb/chroma_case/chroma_openai_case")
            case_collection_name = os.getenv("CASE_COLLECTION_NAME", "case_collection")
            
            if os.path.exists(case_db_path):
                self.case_db = Chroma(
                    persist_directory=case_db_path,
                    embedding_function=embeddings,
                    collection_name=case_collection_name
                )
                print(f"✅ 판례 DB 연결 완료: {case_db_path}")
                print(f"📋 판례 컬렉션명: {case_collection_name}")
                
                # 컬렉션 정보 확인
                try:
                    collection_count = self.case_db._collection.count()
                    print(f"📊 판례 데이터 개수: {collection_count}개")
                except Exception as e:
                    print(f"⚠️ 판례 컬렉션 정보 확인 실패: {e}")
            else:
                print(f"⚠️ 판례 DB 경로를 찾을 수 없습니다: {case_db_path}")
                
        except Exception as e:
            print(f"❌ 벡터DB 초기화 실패: {str(e)}")
    
    def _format_article(self, metadata: Dict) -> str:
        """조문 정보를 한국 법령 체계에 맞게 포맷팅"""
        parts = []
        
        # 조문번호
        if metadata.get("조문번호"):
            parts.append(f"제{metadata['조문번호']}조")
        
        # 항번호
        if metadata.get("항번호") and metadata["항번호"].strip():
            parts.append(f"제{metadata['항번호']}항")
        
        # 호번호  
        if metadata.get("호번호") and metadata["호번호"].strip():
            parts.append(f"제{metadata['호번호']}호")
        
        return " ".join(parts) if parts else ""
    
    async def search_relevant_laws(self, query: str, k: int = 5) -> List[Dict]:
        """관련 법령 검색"""
        if not self.law_db:
            return []
        
        try:
            # 벡터 검색 실행
            search_results = self.law_db.similarity_search_with_score(query, k=k)
            
            relevant_laws = []
            for doc, score in search_results:
                # 거리 기준으로 필터링 (낮을수록 유사함)
                max_distance = float(os.getenv("MAX_DISTANCE", "1.5"))  # 거리 임계값
                print(f"🔍 법령 검색 결과 - 거리: {score:.4f}, 최대거리: {max_distance}")
                
                if score <= max_distance:
                    law_info = {
                        "content": doc.page_content,
                        "metadata": doc.metadata,
                        "distance_score": score,
                        "law_name": doc.metadata.get("법령명", ""),
                        "article": self._format_article(doc.metadata),  # 조문 정보 포맷팅
                        "law_id": doc.metadata.get("법령ID", None),
                        "article_title": doc.metadata.get("조문제목", "")
                    }
                    relevant_laws.append(law_info)
                    print(f"✅ 법령 추가: {law_info.get('law_name', 'Unknown')} {law_info.get('article', '')} - 거리: {score:.4f}")
                else:
                    print(f"❌ 법령 제외: 거리 너무 멀음 ({score:.4f} > {max_distance})")
            
            return relevant_laws
            
        except Exception as e:
            print(f"❌ 법령 검색 실패: {str(e)}")
            return []
    
    async def search_relevant_cases(self, query: str, k: int = 3) -> List[Dict]:
        """관련 판례 검색 - 메타데이터 정보 풍부화"""
        if not self.case_db:
            return []
        
        try:
            search_results = self.case_db.similarity_search_with_score(query, k=k)
            
            relevant_cases = []
            for doc, score in search_results:
                max_distance = float(os.getenv("MAX_DISTANCE", "1.5"))
                if score <= max_distance:
                    case_info = {
                        "content": doc.page_content,
                        "metadata": doc.metadata,
                        "distance_score": score,
                        # 실제 메타데이터에서 추출
                        "case_id": doc.metadata.get("doc_id", None),  # ← doc_id를 case_id로 활용
                        "case_name": doc.metadata.get("case_name", ""),
                        "case_type": doc.metadata.get("case_type", ""),
                        "announce_date": doc.metadata.get("announce_date", ""),
                        "judgement": doc.metadata.get("judgement", ""),
                        "receipt_year": doc.metadata.get("receipt_year", ""),
                        "section": doc.metadata.get("section", "")
                    }
                    relevant_cases.append(case_info)
                    print(f"✅ 판례 추가: [{case_info.get('case_id')}] {case_info.get('case_name')} - 거리: {score:.4f}")
                else:
                    print(f"❌ 판례 제외: 거리 너무 멀음 ({score:.4f} > {max_distance})")
            
            return relevant_cases
            
        except Exception as e:
            print(f"❌ 판례 검색 실패: {str(e)}")
            return []

class ContractService:
    def __init__(self, model_name: str = "claude-sonnet-4-20250514", temperature: float = 0.1):
        """
        계약서 특약사항 생성 서비스
        
        Args:
            model_name: 사용할 Claude 모델명
            temperature: 생성 창의성 조절 (0.0-1.0)
        """
        self.llm = ChatAnthropic(
            model=model_name,
            temperature=temperature,
            max_tokens=4000,
            anthropic_api_key=os.getenv("ANTHROPIC_API_KEY")
        )
        
        # 벡터 데이터베이스 매니저 초기화
        self.vector_db = VectorDBManager()
        
        # 커스텀 OutputParser 설정
        self.output_parser = CustomJSONOutputParser()
        
        # 프롬프트 템플릿 설정
        self.prompt_template = self._create_prompt_template()
        
        # LangChain Chain 구성
        self.chain = self._create_chain()
    
    def _create_prompt_template(self) -> ChatPromptTemplate:
        """
        프롬프트 템플릿 생성 - 프롬프트 엔지니어링 가이드라인 적용
        """
        
        system_prompt = """
### 역할 (Role)
당신은 **25년 경력의 임차인을 위한 부동산 전문 변호사**입니다. 
- 25년간 부동산 임대차법 전문으로 활동한 베테랑 변호사
- 수천 건의 임대차 분쟁 해결 경험 보유
- 임차인의 권익 보호를 최우선으로 하는 전문가
- 실무적이고 현실적인 계약 조건 협상 전문가
- 관련 법령과 판례에 대한 깊은 실무 지식 보유

### 법령 명시 규칙 (Legal Citation Rules)
법령명은 반드시 한국 법령 체계에 따라 정확하게 명시하세요:
- 기본: "○○법 제○조"
- 항 포함: "○○법 제○조 제○항" 
- 호 포함: "○○법 제○조 제○항 제○호"
- 예시: "주택임대차보호법 제16조 제1항 제2호"

### 중요 지시사항
**절대 준수 사항:**
1. **law_id는 반드시 문자열로 작성하세요** ("012345" 형식)
2. **case_id는 반드시 제공된 판례의 실제 문서ID를 사용하세요** ("2024다315046" 형식)
3. **특약사항에서 언급한 모든 법령은 반드시 legal_basis에 포함하세요**
4. **마크다운 ```json 블록을 사용하지 마세요**
5. **순수 JSON만 출력하세요**
6. 제공된 법령 정보의 실제 법령ID를 law_id에 사용하세요
7. 제공된 판례 정보의 실제 문서ID를 case_id에 사용하세요
8. 검색된 법령/판례 내용을 그대로 사용하세요
9. 제공된 법령 정보가 "관련 법령을 찾을 수 없습니다"인 경우에만 legal_basis는 빈 배열 []로 반환하세요
10. 제공된 판례 정보가 "관련 판례를 찾을 수 없습니다"인 경우에만 case_basis는 빈 배열 []로 반환하세요  
11. **반드시 검색된 실제 법령 데이터를 legal_basis에 포함하세요**
12. **절대로 법령이나 판례를 임의로 생성하지 마세요**

### 특약-법령 일관성 규칙
**매우 중요: 특약에서 언급한 법령 = legal_basis 필수 포함**

특약에서 "○○법 제○조"를 언급했다면:
1. 검색 결과에 있는 경우: 그대로 포함
2. 검색 결과에 없는 경우: law_id를 null로 하되 반드시 포함
3. 예외 없이 모든 언급 법령을 legal_basis에 포함

### 특약-법령 매핑 예시
특약사항에서 언급:
"주택임대차보호법 제7조에 따른 법정 한도 내에서만 가능하다"

→ legal_basis에 반드시 포함:
{{
  "law_id": "검색된ID" 또는 null,
  "law": "주택임대차보호법 제7조",
  "explanation": "임대료 증액을 연 5% 이내로 제한하여...",
  "content": "검색된 내용" 또는 "일반적 설명"
}}

### 검색된 법령 활용 예시
검색된 법령이 "법령ID: 012345, 법령명: 공동주택관리법"인 경우:
- law_id: "012345" (반드시 실제 ID 사용)
- law: "공동주택관리법 제20조 제1항" (검색된 조문 그대로)
- content: 검색된 법령 텍스트 그대로 사용

### 검색된 판례 활용 예시
검색된 판례가 "문서ID: 2024다315046, 판례명: 차임증액"인 경우:
- case_id: "2024다315046" (반드시 실제 문서ID 사용)
- case: "차임증액" (검색된 판례명 그대로)
- explanation: 판결요지와 내용을 바탕으로 임차인 관점에서 설명
- link: "case/2024da315046" (문서ID 기반 링크)

### 주요 업무 (Task)
사용자의 요청사항을 분석하여 **임차인에게 유리한 특약사항**을 생성하고, 법적 근거를 제시합니다.

### 대상 청중 (Audience)  
부동산 임대차 계약을 체결하는 **임차인(세입자)**

### 응답 정책 (Policy)
**Style**: 전문적이면서도 이해하기 쉬운 설명
**Constraint**: 
- 각 특약사항당 50-100자 내외로 간결하게 작성
- 법령명은 조, 항, 호까지 정확하게 명시 (예: 주택임대차보호법 제16조 제1항 제2호)
- 법령 해설은 일반인도 이해하기 쉬운 1-2문장으로 요약 설명
- 판례는 실제 존재하는 대표적인 사례 위주로 제시
- 협상 전략은 구체적이고 실행 가능한 방법 제시

### 작업 단계 (Step-by-Step Process)
1. **사용자 요청 분석**: user_query를 분석하여 핵심 니즈 파악
2. **특약사항 생성**: 임차인 보호 관점에서 적절한 특약 조건 도출  
3. **법적 근거 제시**: 관련 법령과 판례를 통한 근거 마련
4. **협상 전략 제안**: 실무적인 협상 포인트 제시

### 출력 형식 예시 (Examples)
{{
  "recommended_agreements": [
    {{
      "reason": "임차인의 원상복구 의무 범위를 명확히 하여 과도한 비용 부담 방지",
      "suggested_revision": "통상적인 사용으로 인한 자연 마모는 원상복구 대상에서 제외한다",
      "negotiation_points": "입주 시 현황 사진 촬영 및 체크리스트 작성으로 입증 자료 확보"
    }}
  ],
  "legal_basis": [
    {{
      "law_id": "012345",
      "law": "공동주택관리법 제20조 제1항",
      "explanation": "법령 설명",
      "content": "법령 원문"
    }}
  ],
  "case_basis": [
    {{
      "case_id": "2024다315046",
      "case": "차임증액",
      "explanation": "임대차 관련 판례로, 임차인의 차임증액 저항권을 보여주는 사례",
      "link": "case/2024da315046"
    }}
  ]
}}

### 입력 데이터 구분자
사용자 요청사항: {user_query}

### 출력 형식 지시
반드시 아래 JSON 형식으로만 응답하세요:
{format_instructions}
"""

        human_prompt = """
임차인을 위한 특약사항을 생성해주세요.

### 사용자 요청사항
{user_query}

### 관련 법령 정보
{law_context}

### 관련 판례 정보  
{case_context}

### 지시사항
1. 위 요청사항들을 분석하여 임차인에게 유리한 특약사항을 생성하세요
2. 제공된 법령과 판례 정보를 적극적으로 활용하세요
3. **판례의 실제 문서ID를 case_id에 반드시 사용하세요**
4. **법령의 실제 법령ID를 law_id에 반드시 사용하세요**
5. 각 특약마다 관련 법령과 판례 근거를 제시하세요  
6. 실제 협상에서 활용할 수 있는 구체적인 전략을 제안하세요
7. 반드시 JSON 형식으로만 응답하세요 (마크다운 블록 금지)

### 판례 활용 가이드
- 제공된 판례 정보에서 문서ID, 판례명, 판결요지를 정확히 활용하세요
- 판례의 임차인 보호 측면을 강조하여 설명하세요
- 판례 링크는 "case/문서ID"에서 특수문자 제거한 형태로 생성하세요

### 일관성 체크리스트
응답 전 다음을 반드시 확인하세요:
□ 특약에서 "○○법 제○조"라고 언급한 모든 법령이 legal_basis에 포함되었는가?
□ legal_basis의 모든 법령이 특약사항 또는 검색 결과와 관련이 있는가?

### 출력 형식
{format_instructions}
"""

        return ChatPromptTemplate.from_messages([
            ("system", system_prompt),
            ("human", human_prompt)
        ])
    
    def _create_chain(self):
        """LangChain Chain 구성"""
        return (
            {
                "user_query": RunnablePassthrough(),
                "law_context": lambda x: x.get("law_context", ""),
                "case_context": lambda x: x.get("case_context", ""),
                "format_instructions": lambda _: self.output_parser.get_format_instructions()
            }
            | self.prompt_template
            | self.llm
            | self.output_parser
        )
    
    async def generate_contract_terms(self, user_queries: List[str]) -> ContractOutput:
        """
        특약사항 생성 메인 함수
        
        Args:
            user_queries: 사용자 요청사항 리스트
            
        Returns:
            ContractOutput: 생성된 특약사항 및 법적 근거
        """
        try:
            # 1. 사용자 쿼리 통합
            combined_query = " ".join(user_queries)
            query_text = "\n".join([f"- {query}" for query in user_queries])
            
            # 2. 관련 법령 검색
            print(f"🔍 법령 검색 중: {combined_query}")
            relevant_laws = await self.vector_db.search_relevant_laws(
                combined_query, 
                k=int(os.getenv("VECTOR_SEARCH_K", "5"))
            )
            
            # 3. 관련 판례 검색  
            print(f"🔍 판례 검색 중: {combined_query}")
            relevant_cases = await self.vector_db.search_relevant_cases(
                combined_query,
                k=3  # 판례는 3개로 제한
            )
            
            # 4. 검색 결과 포맷팅
            law_context = self._format_law_context(relevant_laws)
            case_context = self._format_case_context(relevant_cases)
            
            print(f"📚 검색된 법령: {len(relevant_laws)}개")
            print(f"⚖️ 검색된 판례: {len(relevant_cases)}개")
            
            # 5. Chain 실행 (검색 결과 포함)
            result = await self.chain.ainvoke({
                "user_query": query_text,
                "law_context": law_context,
                "case_context": case_context
            })
            
            enhanced_result = await self.complete_missing_laws(result)
             
            print("✅ 특약사항 생성 성공!")
            return enhanced_result
            
        except Exception as e:
            print(f"❌ 특약사항 생성 실패: {str(e)}")
            # 에러 처리 - 기본 응답 반환
            return self._create_fallback_response(user_queries, str(e))
    
    def _format_law_context(self, laws: List[Dict]) -> str:
        """법령 검색 결과를 프롬프트용으로 포맷팅"""
        if not laws:
            return "관련 법령을 찾을 수 없습니다."
        
        formatted_laws = []
        for i, law in enumerate(laws, 1):
            law_text = f"""
[법령 {i}]
법령명: {law.get('law_name', '알 수 없음')}
조항: {law.get('article', '알 수 없음')}
조문제목: {law.get('article_title', '알 수 없음')}
내용: {law.get('content', '')[:500]}...
거리점수: {law.get('distance_score', 0):.4f}
법령ID: {law.get('law_id', 'N/A')}
"""
            formatted_laws.append(law_text.strip())
        
        return "\n\n".join(formatted_laws)
    
    def _format_case_context(self, cases: List[Dict]) -> str:
        """판례 검색 결과를 프롬프트용으로 포맷팅"""
        if not cases:
            return "관련 판례를 찾을 수 없습니다."
        
        formatted_cases = []
        for i, case in enumerate(cases, 1):
            case_text = f"""
[판례 {i}]
판례명: {case.get('case_name', '알 수 없음')}
문서ID: {case.get('case_id', 'N/A')}
사건유형: {case.get('case_type', '알 수 없음')}
판결일: {case.get('announce_date', '알 수 없음')}
접수년도: {case.get('receipt_year', '알 수 없음')}
판결섹션: {case.get('section', '알 수 없음')}
판결요지: {case.get('judgement', '알 수 없음')[:100]}...
내용: {case.get('content', '')[:400]}...
거리점수: {case.get('distance_score', 0):.4f}
"""
            formatted_cases.append(case_text.strip())
        
        return "\n\n".join(formatted_cases)
    
    def _create_fallback_response(self, user_queries: List[str], error_msg: str) -> ContractOutput:
        """에러 발생 시 기본 응답 생성"""
        
        fallback_agreement = RecommendedAgreement(
            reason="시스템 오류로 인한 기본 특약사항입니다.",
            suggested_revision="상기 요청사항에 대해서는 임대인과 별도 협의하여 결정한다.",
            negotiation_points="구체적인 조건과 비용은 계약 시 상호 협의하여 정한다."
        )
        
        fallback_legal = LegalBasis(
            law_id=None,
            law="주택임대차보호법",
            explanation="세입자의 권리를 보호하고 안정적인 주거생활을 보장하기 위한 기본 법령입니다.",
            content="임차인의 권리와 의무에 관한 기본 사항을 규정함"
        )
        
        fallback_case = CaseBasis(
            case_id=None,
            case="관련 판례 검토 필요",
            explanation="구체적인 사안에 따라 판례 검토가 필요합니다.",
            link="case/review_needed"
        )
        
        return ContractOutput(
            recommended_agreements=[fallback_agreement],
            legal_basis=[fallback_legal],
            case_basis=[fallback_case]
        )
    def extract_mentioned_laws(self, recommended_agreements) -> Set[str]:
        """생성된 특약사항에서 언급된 법령 추출"""
        
        mentioned_laws = set()
        
        # 법령 패턴 정규식들
        law_patterns = [
            r'주택임대차보호법\s*제\s*\d+조(?:의\d+)?',    # 주택임대차보호법 제7조, 제3조의2
            r'민법\s*제\s*\d+조(?:의\d+)?',              # 민법 제623조
            r'상가건물임대차보호법\s*제\s*\d+조(?:의\d+)?', # 상가건물임대차보호법 제10조
            r'부동산등기법\s*제\s*\d+조(?:의\d+)?',        # 부동산등기법 제8조
            r'건축법\s*제\s*\d+조(?:의\d+)?',            # 건축법 제11조
            r'집합건물법\s*제\s*\d+조(?:의\d+)?',         # 집합건물법 제15조
        ]
        
        # 모든 특약사항 텍스트 수집
        all_special_text = ""
        for agreement in recommended_agreements:
            agreement_dict = agreement.dict()
            # recommended_agreements의 필드들 확인 후 적절한 필드 사용
            text_fields = [
                agreement_dict.get('reason', ''),
                agreement_dict.get('suggested_revision', ''),
                agreement_dict.get('negotiation_points', '')
            ]
            all_special_text += " ".join(text_fields) + " "
            
        print(f"🔍 특약 텍스트에서 법령 추출 중...")
        print(f"📝 특약 내용: {all_special_text[:200]}...")
        
        # 각 패턴으로 법령 추출
        for pattern in law_patterns:
            matches = re.findall(pattern, all_special_text, re.IGNORECASE)
            for match in matches:
                # 공백 정규화
                normalized_law = re.sub(r'\s+', ' ', match.strip())
                mentioned_laws.add(normalized_law)
                print(f"  📋 발견된 법령: {normalized_law}")
        
        return mentioned_laws

    async def complete_missing_laws(self, result: ContractOutput) -> ContractOutput:
        """특약에서 언급된 법령이 legal_basis에 누락되었는지 확인하고 보완"""
        
        print("🔍 특약-법령 일관성 검증 시작")
        
        # 1. 생성된 특약에서 실제로 언급된 법령들 추출
        mentioned_laws = self.extract_mentioned_laws(result.recommended_agreements)
        
        if not mentioned_laws:
            print("📋 특약에서 법령 언급 없음 - 검증 완료")
            return result
        
        # 2. 현재 legal_basis에 포함된 법령들
        existing_laws = {basis.law for basis in result.legal_basis}
        print(f"📚 현재 legal_basis 법령: {list(existing_laws)}")
        
        # 3. 누락된 법령들 찾기
        missing_laws = mentioned_laws - existing_laws
        
        if missing_laws:
            print(f"🎯 누락된 법령 발견: {list(missing_laws)}")
            
            # 4. 누락된 법령들을 직접 검색해서 추가
            for missing_law in missing_laws:
                print(f"🔍 누락 법령 검색 중: {missing_law}")
                
                # 🚀 _search_by_metadata_filter 직접 호출
                search_result = await self._search_by_metadata_filter(missing_law)
                
                if search_result:
                    # 검색 성공 - 실제 DB 데이터로 LegalBasis 생성
                    print(f"  ✅ 메타데이터 검색 성공: {search_result['law_name']}")
                    
                    legal_basis = LegalBasis(
                        law_id=search_result.get('metadata', {}).get('법령ID'),
                        law=missing_law,
                        explanation=f"{missing_law}에 따른 임차인 권익 보호 규정",
                        content=search_result.get('content', '')[:300] + "..." if len(search_result.get('content', '')) > 300 else search_result.get('content', '')
                    )
                    
                    result.legal_basis.append(legal_basis)
                    print(f"✅ 누락 법령 추가 완료: {missing_law}")
            
        return result

    async def _search_by_metadata_filter(self, law_name: str):
        """동적 법령 검색 - 하드코딩 없이 모든 법령을 찾을 수 있는 범용 로직"""
        
        try:
            # ChromaDB에서 메타데이터 기반 검색
            collection = self.vector_db.law_db._collection
            
            # 🔧 법령명 분해 (동적 파싱)
            import re
            law_match = re.match(r'([가-힣]+법)\s*제(\d+)조(?:의(\d+))?(?:\s*제(\d+)항)?(?:\s*제(\d+)호)?', law_name)
            
            if law_match:
                base_law = law_match.group(1)     # "주택임대차보호법"
                article_num = law_match.group(2)  # "7"
                sub_article = law_match.group(3)  # None 또는 "2" (조의2)
                paragraph = law_match.group(4)    # None 또는 "1" (항)
                item = law_match.group(5)         # None 또는 "3" (호)
                
                print(f"    동적 파싱 결과:")
                print(f"      법령명: {base_law}")
                print(f"      조: {article_num}")
                print(f"      조의: {sub_article if sub_article else 'None'}")
                print(f"      항: {paragraph if paragraph else 'None'}")
                print(f"      호: {item if item else 'None'}")
                
                # 🔧 1단계: 법령명으로 모든 조문 가져오기 (하드코딩 없음!)
                condition = {"법령명": {"$eq": base_law}}
                
                try:
                    print(f"      🔍 1단계: '{base_law}' 전체 조문 검색...")
                    
                    # 🚀 핵심: limit 없이 또는 매우 크게 설정해서 모든 조문 가져오기
                    all_results = []
                    batch_size = 1000
                    offset = 0
                    
                    while True:
                        results = collection.get(
                            where=condition,
                            limit=batch_size,
                            offset=offset,
                            include=['documents', 'metadatas']
                        )
                        
                        if not results['documents']:
                            break
                            
                        all_results.extend(zip(results['documents'], results['metadatas']))
                        offset += batch_size
                        
                        # 안전장치: 너무 많으면 중단 (무한루프 방지)
                        if len(all_results) > 5000:
                            print(f"         ⚠️ 결과가 너무 많음 ({len(all_results)}개) - 처음 5000개만 처리")
                            break
                    
                    print(f"         ✅ 총 {len(all_results)}개 조문 가져옴")
                    
                    if all_results:
                        # 🔧 2단계: for문으로 조/항/호 동적 매칭
                        print(f"      🎯 2단계: 조({article_num})/항({paragraph})/호({item}) 동적 매칭...")
                        
                        candidates = []
                        
                        for doc, metadata in all_results:
                            db_조문번호 = metadata.get('조문번호', '')
                            db_항번호 = metadata.get('항번호', '')
                            db_호번호 = metadata.get('호번호', '')
                            조문제목 = metadata.get('조문제목', '')
                            
                            # 🎯 동적 매칭 로직
                            match_score = 0
                            match_info = []
                            
                            # 조문 매칭
                            if db_조문번호 == article_num:
                                match_score += 100
                                match_info.append(f"조:{db_조문번호}")
                            else:
                                continue  # 조문이 안 맞으면 skip
                            
                            # 항 매칭 (요청한 경우에만)
                            if paragraph:
                                if db_항번호 == paragraph:
                                    match_score += 50
                                    match_info.append(f"항:{db_항번호}")
                                elif db_항번호:  # 다른 항이면 점수 낮춤
                                    match_score += 10
                                    match_info.append(f"항:{db_항번호}(다름)")
                                else:  # 항 없으면 기본조문
                                    match_score += 30
                                    match_info.append("기본조문")
                            else:
                                # 항을 요청하지 않았으면 기본조문 우선
                                if not db_항번호:
                                    match_score += 50
                                    match_info.append("기본조문")
                                elif db_항번호 == "1":
                                    match_score += 30
                                    match_info.append(f"항:{db_항번호}")
                                else:
                                    match_score += 10
                                    match_info.append(f"항:{db_항번호}")
                            
                            # 호 매칭 (요청한 경우에만)
                            if item:
                                if db_호번호 == item:
                                    match_score += 30
                                    match_info.append(f"호:{db_호번호}")
                                elif db_호번호:
                                    match_score += 5
                                    match_info.append(f"호:{db_호번호}(다름)")
                            else:
                                # 호를 요청하지 않았으면 호 없는 게 우선
                                if not db_호번호:
                                    match_score += 20
                            
                            candidates.append({
                                'doc': doc,
                                'metadata': metadata,
                                'match_score': match_score,
                                'match_info': match_info,
                                'description': f"제{db_조문번호}조" + 
                                            (f" 제{db_항번호}항" if db_항번호 else "") + 
                                            (f" 제{db_호번호}호" if db_호번호 else "") +
                                            f" '{조문제목}'"
                            })
                        
                        if candidates:
                            # 매칭 점수 순으로 정렬
                            candidates.sort(key=lambda x: x['match_score'], reverse=True)
                            
                            print(f"         🏆 매칭 결과 Top 5:")
                            for i, candidate in enumerate(candidates[:5], 1):
                                print(f"           {i}. {candidate['description']} (점수: {candidate['match_score']}, 매칭: {candidate['match_info']})")
                            
                            # 최고 점수 선택
                            best = candidates[0]
                            print(f"         🎯 최종 선택: {best['description']}")
                            print(f"         📋 상세 정보:")
                            print(f"            법령ID: {best['metadata'].get('법령ID')}")
                            print(f"            내용: {best['doc'][:150]}...")
                            
                            return {
                                'content': best['doc'],
                                'metadata': best['metadata'],
                                'law_name': best['metadata'].get('법령명', base_law),
                                'distance_score': 0.0,
                                'match_score': best['match_score']
                            }
                        else:
                            print(f"         ❌ 매칭되는 조문 없음")
                    
                except Exception as e:
                    print(f"      ❌ 검색 실패: {e}")
            
            return None
            
        except Exception as e:
            print(f"    동적 검색 오류: {e}")
            return None
    
# 비동기 함수로 서비스 인스턴스 생성 및 실행
async def create_contract_service() -> ContractService:
    """ContractService 인스턴스 생성"""
    return ContractService()


# 메인 생성 함수 (FastAPI에서 호출할 함수)
async def generate_special_terms(user_queries: List[str]) -> ContractOutput:
    """
    특약사항 생성 메인 함수
    
    Args:
        user_queries: 사용자 요청사항 리스트
        
    Returns:
        ContractOutput: 생성된 특약사항
    """
    service = await create_contract_service()
    return await service.generate_contract_terms(user_queries)
