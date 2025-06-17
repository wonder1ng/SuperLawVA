"""
[services/shared/case_processor.py] - 판례 처리 공통 서비스

기존 generate_letter_chain.py에서 추출한 CaseAnalysisService 클래스
모든 기능에서 사용하는 판례 분석 및 처리 서비스
"""

import asyncio
from typing import List, Dict, Any
from services.schema.shared_schema import CaseSummary, CaseBasis

class CaseProcessor:
    """판례 처리 공통 서비스 (기존 CaseAnalysisService)"""
    
    def __init__(self, llm):
        self.llm = llm
    
    async def analyze_case_for_letter(self, case_doc, user_query: str, contract_summary: str) -> CaseSummary:
        """내용증명용 판례 분석"""
        doc_id = case_doc.metadata.get("doc_id", "미상")
        case_name = case_doc.metadata.get("case_name", "")
        case_text = case_doc.page_content.strip().replace("\n", " ")
        
        # 실제 판례ID 추출
        case_id = case_doc.metadata.get("case_id", "") or case_doc.metadata.get("판례ID", "")
        
        case_prompt = f"""
        다음 판례를 사용자 상황과 연관하여 2-3문장으로 요약하고,
        사용자가 참고해야 할 핵심 포인트를 설명해주세요.
        
        사용자 질문: {user_query}
        계약 정보: {contract_summary}
        판례 원문: {case_text[:1000]}
        
        형식:
        **[요약]**
        판례 핵심 내용
        
        **[참고 포인트]**
        사용자 상황에서의 활용 방법
        """
        
        try:
            response = await self.llm.ainvoke(case_prompt)
            analysis = response.content.strip()
            summary, reference_point = self._parse_case_analysis(analysis)
        except:
            summary = "판례 분석 중 오류가 발생했습니다."
            reference_point = None
        
        return CaseSummary(
            doc_id=doc_id,
            case_name=case_name,
            summary=summary,
            reference_point=reference_point,
            snippet=case_text[:300] + "...",
            case_id=case_id
        )
    
    async def analyze_case_for_review(self, case_doc, problematic_clause: str) -> Dict[str, Any]:
        """계약서 검토용 판례 분석 (문제가 된 사례 중심)"""
        doc_id = case_doc.metadata.get("doc_id", "미상")
        case_name = case_doc.metadata.get("case_name", "")
        case_text = case_doc.page_content.strip().replace("\n", " ")
        case_id = case_doc.metadata.get("case_id", "") or case_doc.metadata.get("판례ID", "")
        
        review_prompt = f"""
        다음 계약 조항과 유사한 문제로 발생한 분쟁 판례를 분석해주세요:
        
        문제 조항: {problematic_clause}
        판례 내용: {case_text[:1000]}
        
        분석 결과를 다음 형식으로 작성해주세요:
        **[분쟁 원인]**
        해당 조항으로 인해 발생한 구체적 문제
        
        **[판결 요지]**
        법원의 판단 및 결론
        
        **[시사점]**
        이 판례가 현재 조항에 주는 교훈
        """
        
        try:
            response = await self.llm.ainvoke(review_prompt)
            analysis = response.content.strip()
            
            # 분석 결과 파싱
            dispute_cause = ""
            judgment = ""
            implications = ""
            
            parts = analysis.split("**[판결 요지]**")
            if len(parts) > 1:
                dispute_cause = parts[0].replace("**[분쟁 원인]**", "").strip()
                remaining = parts[1].split("**[시사점]**")
                if len(remaining) > 1:
                    judgment = remaining[0].strip()
                    implications = remaining[1].strip()
                else:
                    judgment = remaining[0].strip()
            
            return {
                "case_id": case_id,
                "case_name": case_name,
                "doc_id": doc_id,
                "dispute_cause": dispute_cause,
                "judgment": judgment,
                "implications": implications,
                "case_summary": f"{case_name}: {dispute_cause[:100]}..."
            }
            
        except:
            return {
                "case_id": case_id,
                "case_name": case_name,
                "doc_id": doc_id,
                "dispute_cause": "분석 중 오류 발생",
                "judgment": "판결 분석 실패",
                "implications": "전문가 상담 권장",
                "case_summary": "판례 분석 오류"
            }
    
    async def generate_case_summaries_for_letter(self, case_docs: list, user_query: str, contract_summary: str) -> List[CaseSummary]:
        """내용증명용 판례 요약 생성 (병렬 처리)"""
        if not case_docs:
            return []
        
        # 병렬 처리를 위해 모든 판례 분석을 동시에 실행
        tasks = [
            self.analyze_case_for_letter(doc, user_query, contract_summary) 
            for doc in case_docs
        ]
        case_summaries = await asyncio.gather(*tasks)
        return case_summaries
    
    async def generate_case_analyses_for_review(self, case_docs: list, problematic_clauses: List[str]) -> List[Dict[str, Any]]:
        """계약서 검토용 판례 분석 생성"""
        if not case_docs or not problematic_clauses:
            return []
        
        analyses = []
        for case_doc in case_docs:
            for clause in problematic_clauses:
                analysis = await self.analyze_case_for_review(case_doc, clause)
                analyses.append(analysis)
        
        return analyses
    
    def _parse_case_analysis(self, analysis: str) -> tuple:
        """판례 분석 결과 파싱"""
        if "**[참고 포인트]**" in analysis:
            parts = analysis.split("**[참고 포인트]**")
            summary = parts[0].replace("**[요약]**", "").strip()
            reference_point = parts[1].strip() if len(parts) > 1 else None
        else:
            summary = analysis
            reference_point = None
        return summary, reference_point
    
    def convert_to_case_basis(self, case_summaries: List[CaseSummary]) -> List[CaseBasis]:
        """CaseSummary를 CaseBasis로 변환"""
        case_basis = []
        for i, summary in enumerate(case_summaries, 1):
            case_basis.append(
                CaseBasis(
                    case_id=300 + i,
                    case=f"{summary.case_name} ({summary.doc_id})",
                    explanation=summary.summary or "판례 설명",
                    link=f"data/case/{300 + i}"
                )
            )
        return case_basis

    # 기존 함수명 호환성 유지
    async def generate_case_summaries(self, case_docs: list, user_query: str, contract_summary: str) -> List[CaseSummary]:
        """기존 함수명 호환성 유지"""
        return await self.generate_case_summaries_for_letter(case_docs, user_query, contract_summary)
    
    async def _analyze_single_case(self, doc, user_query: str, contract_summary: str) -> CaseSummary:
        """기존 함수명 호환성 유지"""
        return await self.analyze_case_for_letter(doc, user_query, contract_summary)

# 데이터 변환 유틸리티 함수들
def convert_to_case_basis(case_summaries: List[CaseSummary]) -> List[CaseBasis]:
    """내용증명용 CaseBasis 변환 (독립 함수)"""
    case_basis = []
    for summary in case_summaries:
        # 실제 case_id가 있으면 사용, 없으면 0
        case_id = int(summary.case_id) if summary.case_id and summary.case_id.isdigit() else 0
        
        case_basis.append(
            CaseBasis(
                case_id=case_id,
                case=f"{summary.case_name} ({summary.doc_id})",
                explanation=summary.summary or "판례 설명",
                link=f"data/case/{case_id}"
            )
        )
    return case_basis

def convert_to_review_case_format(case_analyses: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    """계약서 검토용 판례 형식 변환"""
    review_cases = []
    for analysis in case_analyses:
        review_cases.append({
            "case_id": analysis.get("case_id", ""),
            "case_title": analysis.get("case_name", ""),
            "dispute_summary": analysis.get("dispute_cause", ""),
            "court_decision": analysis.get("judgment", ""),
            "lessons_learned": analysis.get("implications", ""),
            "relevance_score": "high"  # 실제로는 유사도 점수 계산
        })
    return review_cases