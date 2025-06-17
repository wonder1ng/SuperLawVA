# contract_schema.py
"""
Description: 계약서 특약사항 생성 기능의 데이터 스키마 정의 (입력/출력/오류 응답 등)
Author: ooheunsu
Date: 2025-06-16
Requirements: pydantic, typing
"""
from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import datetime


# 법령 정보 스키마
class LegalBasis(BaseModel):
    law_id: Optional[str] = Field(None, description="법령 ID (법령DB에서 가져옴)")
    law: str = Field(..., description="참고한 법령명")
    explanation: str = Field(..., description="법령 해설 내용")
    content: str = Field(..., description="법령 원문")


# 판례 정보 스키마  
class CaseBasis(BaseModel):
    case_id: Optional[str] = Field(None, description="판례 ID (현재는 null)")
    case: str = Field(..., description="참고한 판례명")
    explanation: str = Field(..., description="판례 요약")
    link: str = Field(..., description="원문 혹은 원문 링크")


# 추천 특약사항 스키마
class RecommendedAgreement(BaseModel):
    reason: str = Field(..., description="해당 특약사항의 필요성 및 근거")
    suggested_revision: str = Field(..., description="제안하는 특약사항 내용")
    negotiation_points: str = Field(..., description="협상 전략")


# 입력 스키마 - 필수: user_query, 선택: 기타 계약 정보
class ContractInput(BaseModel):
    user_query: List[str] = Field(..., description="사용자 요청사항 목록", min_items=1)
    
    # 선택적 계약 정보들
    contract_type: Optional[str] = Field(None, description="계약 종류 (전세, 월세)")
    property_address: Optional[str] = Field(None, description="부동산 주소")
    deposit: Optional[int] = Field(None, description="보증금")
    monthly_rent: Optional[int] = Field(None, description="월세")
    contract_period_start: Optional[str] = Field(None, description="계약 시작일")
    contract_period_end: Optional[str] = Field(None, description="계약 종료일")
    
    # 기타 컨텍스트 정보
    additional_context: Optional[Dict[str, Any]] = Field(None, description="추가 계약 정보")


# 출력 스키마 - 3개 필수 섹션
class ContractOutput(BaseModel):
    # 메타데이터
    id: Optional[int] = Field(None, description="응답 고유 ID")
    user_id: Optional[int] = Field(None, description="사용자 ID")
    contract_id: Optional[int] = Field(None, description="계약서 ID")
    created_date: datetime = Field(default_factory=datetime.now, description="생성일시")
    
    # 필수 출력 섹션들
    recommended_agreements: List[RecommendedAgreement] = Field(
        ..., 
        description="추가 조건 목록",
        
    )
    legal_basis: List[LegalBasis] = Field(
        ..., 
        description="관련 법적 해설",
        
    )
    case_basis: List[CaseBasis] = Field(
        ..., 
        description="관련 판례 요약",
        
    )
    
    # 분석 메타데이터
    analysis_metadata: Optional[Dict[str, Any]] = Field(
        default_factory=lambda: {
            "model": "Claude Sonnet 4",
            "version": "v1.0.0"
        },
        description="분석 메타데이터"
    )


# API 응답 래퍼
class ContractResponse(BaseModel):
    success: bool = Field(True, description="처리 성공 여부")
    message: str = Field("특약사항이 성공적으로 생성되었습니다.", description="응답 메시지")
    data: ContractOutput = Field(..., description="생성된 특약사항 데이터")


# 에러 응답 스키마
class ErrorResponse(BaseModel):
    success: bool = Field(False, description="처리 성공 여부")
    message: str = Field(..., description="에러 메시지")
    error_code: Optional[str] = Field(None, description="에러 코드")
    details: Optional[Dict[str, Any]] = Field(None, description="상세 에러 정보")