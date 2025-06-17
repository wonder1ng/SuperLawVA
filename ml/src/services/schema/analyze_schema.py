"""
[schema/analyze_schema.py] - 계약서 검토 전용 스키마 (수정됨)

조항별에서 바로 상세 정보 포함하도록 수정
"""

from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any
from services.schema.shared_schema import (
    BaseInput, 
    LegalBasis, 
    CaseBasis, 
    CertificationMetadata
)

# INPUT 정보 (계약서 검토 전용)
class ContractAnalysisInput(BaseInput):
    """계약서 검토 입력 - user_query 없음"""
    pass  # contract_data와 debug_mode만 상속

# 🔧 조항별에서 상세 정보 포함하도록 확장
class ClauseLegalBasis(BaseModel):
    law_id: int
    law: str
    explanation: str = Field(description="법령 설명 (요약)")
    content: str = Field(description="법령 원문")

class ClauseCaseBasis(BaseModel):
    case_id: int
    case: str
    explanation: str = Field(description="판례 설명 (조항과의 연관성)")
    link: str = Field(description="판례 링크")

# 개별 조항 분석 결과 (상세 정보 포함)
class ClauseAnalysis(BaseModel):
    """개별 조항/특약 분석 결과"""
    result: bool = Field(description="문제 여부 (true: 적절, false: 문제)")
    content: str = Field(description="조항 원문")
    reason: str = Field(description="판단 이유")
    suggested_revision: Optional[str] = Field(default=None, description="수정안 (문제시에만)")
    negotiation_points: Optional[str] = Field(default=None, description="협상 포인트 (문제시에만)")
    legal_basis: Optional[ClauseLegalBasis] = Field(default=None, description="관련 법령 상세 정보")
    case_basis: List[ClauseCaseBasis] = Field(default_factory=list, description="관련 판례 상세 정보 목록")

# 추가 권고 특약 (상세 정보 포함)
class RecommendedAgreement(BaseModel):
    """추가 권고 특약"""
    reason: str = Field(description="추가가 필요한 이유")
    suggested_revision: str = Field(description="권고 특약 내용")
    negotiation_points: str = Field(description="협상 방법")
    legal_basis: ClauseLegalBasis = Field(description="관련 법령 상세 정보")
    case_basis: List[ClauseCaseBasis] = Field(default_factory=list, description="관련 판례 상세 정보 목록")

# 분석 메타데이터 (계약서 검토 전용)
class AnalysisMetadata(BaseModel):
    """분석 메타데이터"""
    model: str = "Claude Sonnet 4"
    generation_time: float
    user_agent: str = "Mozilla"
    version: str = "v1.2.3"

# OUTPUT 정보 (계약서 검토 전용) - 전체 요약 제거
class ContractAnalysisOutput(BaseModel):
    """계약서 검토 출력"""
    # 기본 메타데이터
    id: int = Field(description="분석 결과 고유 ID")
    user_id: int = Field(description="사용자 ID")
    contract_id: int = Field(description="계약서 ID")
    created_date: str = Field(description="생성 날짜")
    
    # 분석 결과 (조항별로 상세 정보 포함)
    articles: List[ClauseAnalysis] = Field(description="계약 조항 분석 결과 목록")
    agreements: List[ClauseAnalysis] = Field(description="특약 사항 분석 결과 목록")
    recommended_agreements: List[RecommendedAgreement] = Field(description="추가 권고 특약 목록")
    
    # 🔧 전체 요약은 빈 배열로 (또는 아예 제거 가능)
    # legal_basis: List[LegalBasis] = Field(default_factory=list, description="전체 법령 근거 목록 (미사용)")
    # case_basis: List[CaseBasis] = Field(default_factory=list, description="전체 판례 근거 목록 (미사용)")
    
    # 메타데이터
    analysis_metadata: AnalysisMetadata = Field(description="분석 메타데이터")

# 중간 처리용은 기존 유지...
class ProblematicClause(BaseModel):
    """문제가 있는 조항 정보"""
    clause_type: str = Field(description="조항 유형 (article/agreement)")
    clause_index: str = Field(description="조항 인덱스")
    content: str = Field(description="조항 내용")
    issues: List[str] = Field(description="발견된 문제점들")

class ClauseSearchResult(BaseModel):
    """조항별 검색 결과"""
    clause: ProblematicClause
    related_laws: List[Any] = Field(description="관련 법령 문서들")
    related_cases: List[Any] = Field(description="관련 판례 문서들")
    law_analysis: Optional[Dict[str, str]] = Field(default=None, description="법령 분석 결과")
    case_analysis: Optional[Dict[str, str]] = Field(default=None, description="판례 분석 결과")