# contract_router.py

"""
Description: FastAPI 라우터 정의 및 HTTP 엔드포인트 구현
Author: ooheunsu
Date: 2025-06-16
Requirements: fastapi, contract_schema, contract_service, logging
"""
from fastapi import APIRouter, HTTPException, Depends, status
from fastapi.responses import JSONResponse
from typing import List, Dict, Any
import time
import logging
from datetime import datetime

from contract_schema import (
    ContractInput, 
    ContractOutput, 
    ContractResponse, 
    ErrorResponse
)
from contract_service import generate_special_terms

# 로거 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# APIRouter 인스턴스 생성
router = APIRouter(
    prefix="/api/v1/contract",
    tags=["계약서 특약사항"]
)


@router.post(
    "/generate-special-terms",
    response_model=ContractResponse,
    status_code=status.HTTP_200_OK,
    summary="계약서 특약사항 생성",
    description="""
    사용자 요청사항을 바탕으로 임차인에게 유리한 특약사항을 생성합니다.
    
    **주요 기능:**
    - 25년 경력 부동산 전문 변호사 페르소나 적용
    - 임차인 권익 보호 중심의 특약사항 생성
    - 관련 법령 및 판례 근거 제시
    - 실무적인 협상 전략 제안
    
    **입력:**
    - user_query (필수): 사용자 요청사항 리스트
    - 기타 계약 정보는 선택사항
    
    **출력:**
    - recommended_agreements: 추천 특약사항들
    - legal_basis: 관련 법령 해설
    - case_basis: 관련 판례 정보
    """,
    responses={
        200: {
            "description": "특약사항 생성 성공",
            "model": ContractResponse
        },
        400: {
            "description": "잘못된 요청 데이터",
            "model": ErrorResponse
        },
        500: {
            "description": "서버 내부 오류",
            "model": ErrorResponse
        }
    }
)
async def create_special_terms(contract_input: ContractInput) -> ContractResponse:
    """
    계약서 특약사항 생성 API
    
    Args:
        contract_input: 사용자 요청사항 및 계약 정보
        
    Returns:
        ContractResponse: 생성된 특약사항 및 법적 근거
        
    Raises:
        HTTPException: 입력 검증 실패 또는 서버 오류
    """
    
    start_time = time.time()
    
    try:
        # 입력 검증
        if not contract_input.user_query:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="user_query는 필수 입력사항입니다."
            )
        
        if len(contract_input.user_query) == 0:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="최소 하나 이상의 요청사항을 입력해주세요."
            )
        
        # 로깅
        logger.info(f"특약사항 생성 요청: {len(contract_input.user_query)}개 항목")
        logger.info(f"요청 내용: {contract_input.user_query}")
        
        # 특약사항 생성 서비스 호출
        result = await generate_special_terms(contract_input.user_query)
        
        # 생성 시간 계산
        generation_time = time.time() - start_time
        
        # 메타데이터 업데이트
        if result.analysis_metadata:
            result.analysis_metadata["generation_time"] = generation_time
        else:
            result.analysis_metadata = {
                "model": "Claude Sonnet 4",
                "generation_time": generation_time,
                "version": "v1.0.0"
            }
        
        # 성공 응답
        response = ContractResponse(
            success=True,
            message="특약사항이 성공적으로 생성되었습니다.",
            data=result
        )
        
        logger.info(f"특약사항 생성 완료: {generation_time:.2f}초 소요")
        return response
        
    except HTTPException:
        # HTTP 예외는 그대로 re-raise
        raise
        
    except Exception as e:
        # 기타 예외 처리
        error_msg = f"특약사항 생성 중 오류 발생: {str(e)}"
        logger.error(error_msg, exc_info=True)
        
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=error_msg
        )


@router.get(
    "/health",
    summary="서비스 상태 확인",
    description="계약서 특약사항 생성 서비스의 상태를 확인합니다."
)
async def health_check():
    """서비스 헬스체크"""
    return {
        "status": "healthy",
        "timestamp": datetime.now().isoformat(),
        "service": "contract-special-terms",
        "version": "v1.0.0"
    }


@router.post(
    "/validate-input",
    summary="입력 데이터 검증",
    description="특약사항 생성 전 입력 데이터의 유효성을 검증합니다."
)
async def validate_contract_input(contract_input: ContractInput):
    """
    입력 데이터 검증
    
    Args:
        contract_input: 검증할 계약 정보
        
    Returns:
        dict: 검증 결과
    """
    
    validation_result = {
        "valid": True,
        "messages": [],
        "suggestions": []
    }
    
    # user_query 검증
    if not contract_input.user_query:
        validation_result["valid"] = False
        validation_result["messages"].append("user_query는 필수 입력사항입니다.")
    elif len(contract_input.user_query) == 0:
        validation_result["valid"] = False
        validation_result["messages"].append("최소 하나 이상의 요청사항을 입력해주세요.")
    
    # 요청사항 품질 검증
    if contract_input.user_query:
        for i, query in enumerate(contract_input.user_query):
            if len(query.strip()) < 5:
                validation_result["suggestions"].append(
                    f"요청사항 {i+1}: 더 구체적으로 작성해주세요. (현재: '{query}')"
                )
    
    # 선택적 정보 확인
    optional_fields = [
        "contract_type", "property_address", "deposit", 
        "monthly_rent", "contract_period_start", "contract_period_end"
    ]
    
    provided_fields = []
    for field in optional_fields:
        if getattr(contract_input, field, None):
            provided_fields.append(field)
    
    if provided_fields:
        validation_result["messages"].append(
            f"추가 정보 제공됨: {', '.join(provided_fields)}"
        )
        validation_result["suggestions"].append(
            "추가 계약 정보를 제공하면 더 정확한 특약사항을 생성할 수 있습니다."
        )
    
    return validation_result