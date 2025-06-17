"""
[generate_letter_chain.py] - 내용증명 생성 체인 (수정된 버전)

공통 서비스들을 조립하여 내용증명 생성 기능을 구현
당사자 주소 정보 매칭 문제 해결
"""

import time
from datetime import datetime
from langchain.prompts import ChatPromptTemplate
from langchain_core.output_parsers import PydanticOutputParser

# 공통 모듈들 import
from services.shared.document_search import DocumentSearchService
from services.shared.contract_parser import summarize_contract_for_letter, extract_parties_info
from services.shared.legal_processor import LegalProcessor, convert_to_legal_basis
from services.shared.case_processor import CaseProcessor, convert_to_case_basis
from services.shared.formatters import DocumentFormatter
from services.shared.llm_config import get_claude_llm_for_letter

# 스키마 import
from services.schema.letter_schema import (
    LetterGenerationInput, 
    LetterGenerationOutput,
    TempLetterOutput
)
from services.schema.shared_schema import PersonInfo, CertificationMetadata

# 내용증명 전용 LLM 설정
llm = get_claude_llm_for_letter()
output_parser = PydanticOutputParser(pydantic_object=TempLetterOutput)

# 내용증명 전용 프롬프트 템플릿 (수정됨)
letter_prompt = ChatPromptTemplate.from_template("""
당신은 25년 경력의 분쟁 해결 전문 변호사로, 내용증명을 통한 분쟁 조기 해결 성공률이 80%에 달하는 전문가입니다.  
법적 효력과 심리적 설득력을 모두 갖춘 전략적인 내용증명 문서를 작성해 주세요.                                                

관련 법령:
{related_laws_str}

관련 판례:
{related_cases_str}

계약 정보:
{contract_summary}

당사자 정보:
{parties_info}

사용자 요청: {user_query}

위 정보를 바탕으로 법적 효력이 있는 내용증명을 작성해주세요.                                                

**⚠️ 당사자 정보 작성 시 필수 준수사항:**
1. **수신인 정보**: 위 "당사자 정보"에서 "임대인" 이름, "임대인 주소", "임대인 상세주소"를 정확히 사용하세요
2. **발신인 정보**: 위 "당사자 정보"에서 "임차인" 이름, "임차인 주소", "임차인 상세주소"를 정확히 사용하세요  
3. **주소 분리**: receiver_address에는 기본 주소만, receiver_detail_address에는 상세주소만 입력하세요
4. **주소 정확성**: 임대부동산 주소와 당사자 실제 주소를 절대 혼동하지 마세요
5. **완전한 정보**: 이름, 주소, 상세주소를 모두 정확히 매칭하여 입력하세요                                                

**⚠️ 법령 인용 시 필수 준수사항:**
1. **내용 명시 의무**: 법령을 인용할 때는 "~에 따르면", "~에서 규정하는 바와 같이" 다음에 반드시 해당 조문의 핵심 내용을 구체적으로 명시하세요
   예시: "「주택임대차보호법」 제4조에 따르면 '임차인은 보증금 반환을 요구할 권리가 있다'고 규정하고 있으므로..."
2. **관련성 검증**: 사용자 상황과 직접 관련 없는 법령은 절대 인용 금지
3. **정확한 인용**: 「전체 법령명」 형식 사용, "동법", "같은 법" 등 축약 표현 금지
4. **제공된 법령만 사용**: 위에 제시된 관련 법령 외에는 인용하지 마세요

**📝 어조 및 강도 설정:**
- 협력적, 정중한 어조
- 단호하지만 합리적 어조  
- 공식적, 객관적 어조

**📋 작성 필수 요소:**
1. 정확한 당사자 정보 (수신인/발신인 이름, 실제 주소)
2. 상황에 맞는 명확한 제목 (예: 보증금 반환 촉구서, 수리 요청서, 계약 해지 통보서)
3. 육하원칙 기반 구체적 사실관계 기술
4. 법적 근거를 포함한 명확한 요구사항
5. 합리적 이행 기한 명시
6. 상호 이익을 고려한 해결책 제안 (협의형인 경우)
7. 미이행시 후속 조치 안내 (강도에 따라 조절)
8. 발송 날짜 및 서명

**⚖️ 법적 안정성 확보:**
- 제시된 관련 법령의 구체적 내용을 정확히 반영
- 과도한 위협이나 불가능한 요구사항 배제
- 실제 우체국 내용증명 우편 발송 가능한 형식과 문체
- 객관적이고 감정적 표현 배제

{format_instructions}
""").partial(format_instructions=output_parser.get_format_instructions())

# 내용증명 전용 체인
letter_chain = letter_prompt | llm | output_parser

class LetterGenerationOrchestrator:
    """내용증명 생성 오케스트레이터 (수정된 버전)"""
    
    def __init__(self):
        self.llm = llm
        self.letter_chain = letter_chain
        
        # 공통 서비스들 조립
        self.search_service = DocumentSearchService()
        self.legal_processor = LegalProcessor(self.llm)
        self.case_processor = CaseProcessor(self.llm)
        self.formatter = DocumentFormatter()
    
    def format_parties_info(self, lessor: dict, lessee: dict) -> str:
        """당사자 정보를 프롬프트용 문자열로 포맷팅"""
        return f"""
【임대인 정보】
- 이름: {lessor.get('name', '미상')}
- 주소: {lessor.get('address', '미상')}
- 상세주소: {lessor.get('detail_address', '')}
- 연락처: {lessor.get('mobile_number', lessor.get('phone_number', '미상'))}

【임차인 정보】
- 이름: {lessee.get('name', '미상')}
- 주소: {lessee.get('address', '미상')}
- 상세주소: {lessee.get('detail_address', '')}
- 연락처: {lessee.get('mobile_number', lessee.get('phone_number', '미상'))}

⚠️ 주의: 위 주소는 당사자들의 실제 거주지 주소입니다. 임대부동산 주소와 혼동하지 마세요!
⚠️ 출력 시 주소와 상세주소를 분리하여 기록하세요!
"""
    
    async def generate_letter(self, input_data: LetterGenerationInput) -> LetterGenerationOutput:
        """메인 실행 함수 (수정된 버전)"""
        start_time = time.time()
        
        try:
            # 1. 공통 유틸 사용 - 계약서 파싱
            contract_summary = summarize_contract_for_letter(input_data.contract_data)
            lessor, lessee = extract_parties_info(input_data.contract_data)
            user_query = input_data.user_query
            
            # 2. 당사자 정보 포맷팅 (새로 추가)
            parties_info = self.format_parties_info(lessor, lessee)
            
            # 3. 공통 서비스 사용 - 문서 검색
            law_docs, case_docs = await self.search_service.search_documents(user_query)
            
            # 4. 공통 유틸 사용 - 프롬프트용 포맷팅
            related_laws_str = self.formatter.format_law_documents(law_docs)
            related_cases_str = self.formatter.format_case_documents(case_docs)
            
            # 5. 내용증명 특화 - LLM 체인 실행 (수정됨)
            temp_result = await self.letter_chain.ainvoke({
                "related_laws_str": related_laws_str,
                "related_cases_str": related_cases_str,
                "contract_summary": contract_summary,
                "parties_info": parties_info,  # 새로 추가된 당사자 정보
                "user_query": user_query
            })
            
            # 6. 내용증명 특화 - 본문에서 법령 추출
            referenced_laws = self.legal_processor.extract_referenced_laws(temp_result.body)
            
            # 7. 공통 서비스 사용 - 법령 분석
            legal_explanations = await self.legal_processor.generate_legal_explanations(
                referenced_laws, law_docs
            )
            legal_basis = convert_to_legal_basis(legal_explanations)
            
            # 8. 공통 서비스 사용 - 판례 분석 (내용증명용)
            case_summaries = await self.case_processor.generate_case_summaries_for_letter(
                case_docs, user_query, contract_summary
            )
            case_basis = convert_to_case_basis(case_summaries)
            
            # 9. 최종 결과 조립
            generation_time = round(time.time() - start_time, 2)
            
            return LetterGenerationOutput(
                id=100,  # 내용증명서 고유 ID
                user_id=input_data.contract_data.get("user_id"),  # 계약서에서 user_id 가져오기
                contract_id=input_data.contract_data.get("_id"),
                created_date=datetime.now().isoformat(),
                title=temp_result.title,
                receiver=PersonInfo(
                    name=temp_result.receiver_name,
                    address=temp_result.receiver_address,
                    detail_address=temp_result.receiver_detail_address or ""
                ),
                sender=PersonInfo(
                    name=temp_result.sender_name,
                    address=temp_result.sender_address,
                    detail_address=temp_result.sender_detail_address or ""
                ),
                body=temp_result.body,
                strategy_summary=temp_result.strategy_summary,
                followup_strategy=temp_result.followup_strategy,
                legal_basis=legal_basis,
                case_basis=case_basis,
                certification_metadata=CertificationMetadata(
                    generation_time=generation_time
                ),
                user_query=user_query
            )
            
        except Exception as e:
            return self._create_fallback_result(input_data, start_time, e)
    
    def _create_fallback_result(self, input_data, start_time, error):
        """에러 시 안전한 폴백 결과 생성"""
        generation_time = round(time.time() - start_time, 2)
        
        return LetterGenerationOutput(
            id=None,
            user_id=None,
            contract_id=None,
            created_date=datetime.now().isoformat(),
            title="내용증명서",
            receiver=PersonInfo(name="수신자", address="주소 확인 필요"),
            sender=PersonInfo(name="발신인", address="주소 확인 필요"),
            body=f"사용자 요청: {input_data.user_query}\n\n시스템 오류로 기본 형식 생성됨",
            strategy_summary="시스템 오류 발생",
            followup_strategy="담당자 문의 바람",
            legal_basis=[],
            case_basis=[],
            certification_metadata=CertificationMetadata(
                generation_time=generation_time
            ),
            user_query=input_data.user_query
        )

# 외부 API 호출용 함수 (기존 호환성 유지)
async def run_letter_chain(input_data: LetterGenerationInput) -> LetterGenerationOutput:
    """기존 함수를 비동기로 변경 (수정된 버전)"""
    orchestrator = LetterGenerationOrchestrator()
    return await orchestrator.generate_letter(input_data)