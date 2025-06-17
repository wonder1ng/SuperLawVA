"""
[services/shared/formatters.py] - 문서 포맷팅 유틸리티

기존 generate_letter_chain.py에서 추출한 DocumentFormatter 클래스
검색된 문서들을 프롬프트용 문자열로 변환하는 공통 유틸리티
"""

from typing import List

class DocumentFormatter:
    """검색된 문서를 프롬프트용 문자열로 변환 (기존 클래스)"""
    
    @staticmethod
    def format_law_documents(law_docs: list) -> str:
        """법령 문서 포맷팅 (모든 기능에서 사용)"""
        if not law_docs:
            return "관련 법령을 찾을 수 없습니다."
        
        return "\n".join([
            f"• {doc.metadata.get('법령명', '미상')} 제{doc.metadata.get('조문번호', '?')}조"
            + (f" 제{doc.metadata.get('항번호')}항" if doc.metadata.get("항번호") else "")
            + f": {doc.page_content[:100]}..."
            for doc in law_docs
        ])
    
    @staticmethod
    def format_case_documents(case_docs: list) -> str:
        """판례 문서 포맷팅 (모든 기능에서 사용)"""
        if not case_docs:
            return "관련 판례를 찾을 수 없습니다."
        
        return "\n".join([
            f"• {doc.metadata.get('doc_id', '미상')} ({doc.metadata.get('case_name', '')})"
            for doc in case_docs
        ])
    
    @staticmethod
    def format_law_documents_detailed(law_docs: list) -> str:
        """법령 문서 상세 포맷팅 (계약서 검토용)"""
        if not law_docs:
            return "관련 법령을 찾을 수 없습니다."
        
        formatted = []
        for doc in law_docs:
            law_name = doc.metadata.get('법령명', '미상')
            article = doc.metadata.get('조문번호', '?')
            clause = doc.metadata.get('항번호', '')
            content = doc.page_content[:200]
            
            entry = f"""
【{law_name} 제{article}조{f' 제{clause}항' if clause else ''}】
{content}...
"""
            formatted.append(entry)
        
        return "\n".join(formatted)
    
    @staticmethod
    def format_case_documents_detailed(case_docs: list) -> str:
        """판례 문서 상세 포맷팅 (계약서 검토용)"""
        if not case_docs:
            return "관련 판례를 찾을 수 없습니다."
        
        formatted = []
        for doc in case_docs:
            doc_id = doc.metadata.get('doc_id', '미상')
            case_name = doc.metadata.get('case_name', '')
            content = doc.page_content[:300]
            
            entry = f"""
【{doc_id} - {case_name}】
{content}...
"""
            formatted.append(entry)
        
        return "\n".join(formatted)
    
    @staticmethod
    def format_contract_clauses(clauses: dict) -> str:
        """계약 조항 포맷팅 (계약서 검토용)"""
        formatted = []
        
        # 기본 조항들
        if clauses.get("articles"):
            formatted.append("【계약 조항】")
            for i, article in enumerate(clauses["articles"], 1):
                formatted.append(f"{i}. {article}")
        
        # 특약사항들
        if clauses.get("agreements"):
            formatted.append("\n【특약사항】")
            for i, agreement in enumerate(clauses["agreements"], 1):
                formatted.append(f"{i}. {agreement}")
        
        return "\n".join(formatted)
    
    @staticmethod
    def format_contract_summary(contract_info: dict) -> str:
        """계약 정보 요약 포맷팅"""
        return f"""
【계약 개요】
- 유형: {contract_info.get('contract_type', '미상')}
- 부동산: {contract_info.get('address', '미상')}
- 보증금: {contract_info.get('deposit', 0):,}원
- 월세: {contract_info.get('monthly_rent', 0):,}원 (전세인 경우 0원)
- 계약기간: {contract_info.get('start_date', '')} ~ {contract_info.get('end_date', '')}
- 임대인: {contract_info.get('lessor_name', '')}
- 임차인: {contract_info.get('lessee_name', '')}
"""