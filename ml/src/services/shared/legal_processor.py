"""
[services/shared/legal_processor.py] - 법령 처리 공통 서비스

기존 generate_letter_chain.py에서 추출한 LegalAnalysisService 클래스
모든 기능에서 사용하는 법령 분석 및 처리 서비스
"""

import re
from typing import List, Set, Optional, Dict
from services.schema.shared_schema import LegalReference, LegalBasis

class LegalProcessor:
    """법령 처리 공통 서비스 (기존 LegalAnalysisService)"""
    
    def __init__(self, llm):
        self.llm = llm
        self.law_pattern = re.compile(
            r"(?:「)?(?P<law>[가-힣·\w\d\s]{2,}?(법|시행령|시행규칙))(?:」)?\s*제\s*(?P<article>\d+)\s*조(?:\s*제\s*(?P<clause>\d+)\s*항)?(?:\s*제\s*(?P<item>\d+)\s*호)?",
            re.UNICODE
        )
    
    def extract_referenced_laws(self, text: str) -> Set[str]:
        """텍스트에서 인용된 법령 추출 (주로 내용증명에서 사용)"""
        referenced_laws = set()
        
        for match in self.law_pattern.finditer(text):
            law = match.group("law")
            article = match.group("article")
            clause = match.group("clause")
            item = match.group("item")
            
            key = f"{law} 제{article}조"
            if clause:
                key += f" 제{clause}항"
            if item:
                key += f" 제{item}호"
                
            referenced_laws.add(key)
        
        return referenced_laws
    
    async def generate_law_summary(self, full_text: str) -> str:
        """법령 요약 생성 (모든 기능에서 사용)"""
        summary_prompt = f"""
        다음 법령 조문이 사용자에게 제공하는 권리나 법적 보호를 1-2문장으로 요약해주세요:
        "{full_text}"
        """
        try:
            response = await self.llm.ainvoke(summary_prompt)
            return response.content.strip()
        except:
            return "법령 요약 생성 중 오류가 발생했습니다."
    
    def find_law_document(self, reference: str, law_docs: list):
        """참조된 법령에 해당하는 문서 찾기 (모든 기능에서 사용)"""
        match = self.law_pattern.search(reference)
        if not match:
            return None
        
        law_name = match.group("law")
        article = match.group("article")
        clause = match.group("clause")
        
        for doc in law_docs:
            doc_law_name = doc.metadata.get("법령명", "").replace(" ", "")
            doc_article = doc.metadata.get("조문번호", "")
            doc_clause = doc.metadata.get("항번호", "")
            
            if (doc_law_name == law_name.replace(" ", "") and doc_article == article):
                if clause and doc_clause == clause:
                    return doc
                elif not clause:
                    return doc
        
        return None
    
    async def generate_legal_explanations(self, referenced_laws: Set[str], law_docs: list) -> List[LegalReference]:
        """법령 상세 설명 생성 (모든 기능에서 사용)"""
        legal_explanations = []
        
        for law_ref in referenced_laws:
            target_doc = self.find_law_document(law_ref, law_docs)
            if not target_doc:
                continue
            
            full_text = target_doc.page_content.strip().replace("\n", " ")
            summary = await self.generate_law_summary(full_text)
            
            # 실제 법령ID 추출
            law_id = target_doc.metadata.get("법령ID", "") or target_doc.metadata.get("law_id", "")
            
            legal_explanations.append(
                LegalReference(
                    title=law_ref,
                    full_text=full_text,
                    summary=summary,
                    law_id=law_id
                )
            )
        
        return legal_explanations
    
    async def analyze_law_for_review(self, law_doc, clause_text: str) -> Dict[str, str]:
        """계약서 검토용 법령 분석"""
        law_text = law_doc.page_content.strip().replace("\n", " ")
        
        analysis_prompt = f"""
        다음 계약 조항이 법령에 위배되는지 분석해주세요:
        
        계약 조항: {clause_text}
        관련 법령: {law_text}
        
        분석 결과를 다음 형식으로 작성해주세요:
        **위험도**: medium/low
        **문제점**: 구체적인 법적 문제
        **권장사항**: 수정 제안
        """
        
        try:
            response = await self.llm.ainvoke(analysis_prompt)
            content = response.content.strip()
            
            # 분석 결과 파싱
            risk_level = "medium"  # 기본값
            issues = ""
            recommendations = ""
            
            lines = content.split('\n')
            for line in lines:
                if '**위험도**' in line:
                    risk_level = line.split(':')[-1].strip()
                elif '**문제점**' in line:
                    issues = line.split(':')[-1].strip()
                elif '**권장사항**' in line:
                    recommendations = line.split(':')[-1].strip()
            
            return {
                "risk_level": risk_level,
                "issues": issues,
                "recommendations": recommendations,
                "law_summary": await self.generate_law_summary(law_text)
            }
            
        except:
            return {
                "risk_level": "unknown",
                "issues": "분석 중 오류 발생",
                "recommendations": "전문가 상담 권장",
                "law_summary": "법령 요약 실패"
            }
    
    def convert_to_legal_basis(self, legal_explanations: List[LegalReference]) -> List[LegalBasis]:
        """LegalReference를 LegalBasis로 변환 - 실제 법령ID 사용"""
        legal_basis = []
        for ref in legal_explanations:
            # 실제 법령ID가 있으면 사용, 없으면 0
            law_id = int(ref.law_id) if ref.law_id and ref.law_id.isdigit() else 0
            
            legal_basis.append(
                LegalBasis(
                    law_id=law_id,
                    law=ref.title,
                    explanation=ref.summary or "법령 설명",
                    content=ref.full_text or "법령 내용"
                )
            )
        return legal_basis

    # 기존 함수명들 호환성 유지
    def _find_law_document(self, reference: str, law_docs: list):
        """기존 함수명 호환성 유지"""
        return self.find_law_document(reference, law_docs)
    
    async def _generate_law_summary(self, full_text: str) -> str:
        """기존 함수명 호환성 유지"""
        return await self.generate_law_summary(full_text)

# 데이터 변환 유틸리티 함수들
def convert_to_legal_basis(legal_explanations: List[LegalReference]) -> List[LegalBasis]:
    """내용증명용 LegalBasis 변환 (독립 함수)"""
    legal_basis = []
    for ref in legal_explanations:
        # 실제 법령ID가 있으면 사용, 없으면 0
        law_id = int(ref.law_id) if ref.law_id and ref.law_id.isdigit() else 0
        
        legal_basis.append(
            LegalBasis(
                law_id=law_id,
                law=ref.title,
                explanation=ref.summary or "법령 설명",
                content=ref.full_text or "법령 내용"
            )
        )
    return legal_basis

def convert_to_review_format(legal_explanations: List[LegalReference], analysis_results: List[Dict]) -> List[Dict]:
    """계약서 검토용 형식 변환"""
    review_results = []
    for ref, analysis in zip(legal_explanations, analysis_results):
        review_results.append({
            "law_title": ref.title,
            "law_summary": ref.summary,
            "risk_level": analysis.get("risk_level", "medium"),
            "issues": analysis.get("issues", ""),
            "recommendations": analysis.get("recommendations", ""),
            "law_id": ref.law_id
        })
    return review_results