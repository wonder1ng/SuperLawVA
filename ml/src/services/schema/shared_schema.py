"""
[schema/shared_schema.py] - 공통 스키마 정의

모든 기능에서 사용하는 공통 데이터 구조들
"""

from pydantic import BaseModel
from typing import Optional, List, Dict, Any

# 수신인/발신인 정보 (공통)
class PersonInfo(BaseModel):
    name: str                              # 이름
    address: str                           # 주소
    detail_address: Optional[str] = None   # 상세 주소

# 법적 근거 정보 (최종 출력용)
class LegalBasis(BaseModel):
    law_id: int                            # 법령 ID
    law: str                               # 법령명 (예: "주택임대차보호법 제4조")
    explanation: str                       # 법령 설명
    content: str                           # 법령 내용

# 판례 근거 정보 (최종 출력용)
class CaseBasis(BaseModel):
    case_id: int                           # 판례 ID
    case: str                              # 판례명 (예: "대법원 2019다12345 판결")
    explanation: str                       # 판례 설명
    link: str                              # 판례 링크

# 생성 메타데이터 (공통)
class CertificationMetadata(BaseModel):
    model: str = "Claude Sonnet 4"         # 사용된 모델
    generation_time: float                 # 생성 시간 (초)
    user_agent: str = "Mozilla"            # 사용자 에이전트
    version: str = "v1.2.3"                # 버전

# 중간 처리용 클래스들 (내부 로직에서 사용)
class LegalReference(BaseModel):
    """중간 처리용 법령 참조"""
    title: str                             # 예: "주거급여법 제10조 제1항"
    full_text: Optional[str] = None        # 조문 원문
    summary: Optional[str] = None          # 법령 요약
    law_id: Optional[str] = None           # 실제 법령ID (메타데이터에서 추출)

class CaseSummary(BaseModel):
    """중간 처리용 판례 요약"""
    doc_id: str                            # 사건번호
    case_name: str                         # 사건명
    summary: Optional[str] = None          # 판례 요약
    reference_point: Optional[str] = None  # 참고 포인트
    snippet: Optional[str] = None          # 판례 원문 일부
    case_id: Optional[str] = None          # 실제 판례ID (메타데이터에서 추출)

# 공통 입력 베이스 클래스
class BaseInput(BaseModel):
    """공통 입력 베이스"""
    contract_data: Dict[str, Any]          # 임대차 계약서 JSON
    debug_mode: Optional[bool] = False     # 개발자 디버깅 여부

# 공통 출력 베이스 클래스  
class BaseOutput(BaseModel):
    """공통 출력 베이스"""
    id: Optional[int] = None              # 문서 ID
    user_id: Optional[int] = None          # 사용자 ID
    contract_id: Optional[int] = None      # 계약서 ID
    created_date: Optional[str] = None     # 생성 날짜
    
    legal_basis: List[LegalBasis]          # 법적 근거 목록
    case_basis: List[CaseBasis]            # 판례 근거 목록
    
    certification_metadata: CertificationMetadata  # 생성 메타데이터

# 계약서 검토용 특화 스키마들 (미래 사용)
class ContractClause(BaseModel):
    """계약 조항 정보"""
    clause_id: str                         # 조항 ID
    clause_type: str                       # 조항 유형 (article/agreement)
    content: str                           # 조항 내용
    risk_level: str = "medium"             # 위험도 (high/medium/low)

class ReviewResult(BaseModel):
    """검토 결과"""
    clause: ContractClause                 # 검토 대상 조항
    issues: List[str]                      # 발견된 문제점들
    recommendations: List[str]             # 수정 권장사항들
    related_laws: List[LegalBasis]         # 관련 법령들
    related_cases: List[CaseBasis]         # 관련 판례들

# 특약 생성용 특화 스키마들 (미래 사용)
class ClauseRequest(BaseModel):
    """특약 생성 요청"""
    purpose: str                           # 특약 목적
    requirements: List[str]                # 구체적 요구사항들
    priority: str = "medium"               # 중요도

class GeneratedClause(BaseModel):
    """생성된 특약"""
    title: str                             # 특약 제목
    content: str                           # 특약 내용
    legal_basis: List[LegalBasis]          # 법적 근거
    notes: Optional[str] = None            # 추가 설명