"""
[routes/generate_letter.py] - 내용증명 자동 생성 API 라우터 (모듈화된 버전)

공통 서비스들을 사용하는 내용증명 생성 API
"""

from fastapi import APIRouter, HTTPException
from services.schema.letter_schema import LetterGenerationInput, LetterGenerationOutput
from services.generate_letter_chain import run_letter_chain
import traceback

router = APIRouter()

@router.post("/generate-letter", response_model=LetterGenerationOutput)
async def generate_letter(input_data: LetterGenerationInput):
    """
    임대차 계약서 JSON과 사용자 설명을 입력 받아 내용증명을 생성합니다.
    공통 서비스들(검색, 법령분석, 판례분석)을 조립하여 사용합니다.
    
    Args:
        input_data: 
            - contract_data: 임대차 계약서 JSON (전세/월세)
            - user_query: 사용자 자연어 질문
            - debug_mode: 디버깅 모드 (선택)
    
    Returns:
        LetterGenerationOutput: 새로운 형식의 내용증명 결과
            - _id, user_id, contract_id: 메타 정보
            - title: 내용증명 제목
            - receiver/sender: 수신인/발신인 정보
            - body: 내용증명 본문
            - strategy_summary/followup_strategy: 전략 정보
            - legal_basis: 법적 근거 목록
            - case_basis: 판례 근거 목록
            - certification_metadata: 생성 메타데이터
            - user_query: 사용자 질의
    """
    try:
        # 입력 데이터 검증
        if not input_data.contract_data:
            raise HTTPException(status_code=400, detail="계약서 데이터가 필요합니다.")
        
        if not input_data.user_query.strip():
            raise HTTPException(status_code=400, detail="사용자 질의가 필요합니다.")
        
        # 계약서 타입 검증 (전세/월세)
        contract_type = input_data.contract_data.get("contract_type")
        if contract_type not in ["전세", "월세"]:
            raise HTTPException(status_code=400, detail="지원되지 않는 계약 유형입니다. (전세/월세만 지원)")
        
        # 공통 서비스들을 사용하는 내용증명 생성 실행
        result = await run_letter_chain(input_data)
        return result
     
    except HTTPException:
        # HTTPException은 그대로 재발생
        raise
    except Exception as e:
        # 예상치 못한 에러 처리
        if input_data.debug_mode:
            # 디버그 모드에서는 상세 에러 정보 제공
            traceback.print_exc()
            raise HTTPException(
                status_code=500, 
                detail=f"내용증명 생성 중 오류가 발생했습니다: {str(e)}"
            )
        else:
            # 일반 모드에서는 간단한 에러 메시지만 제공
            raise HTTPException(
                status_code=500, 
                detail="내용증명 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            )

@router.get("/health")
async def health_check():
    """
    내용증명 생성 서비스 상태 확인
    """
    return {
        "status": "healthy",
        "service": "letter_generation",
        "version": "v2.1.0 (modular)",
        "message": "모듈화된 내용증명 생성 서비스가 정상 작동 중입니다.",
        "modules": [
            "shared.document_search",
            "shared.legal_processor", 
            "shared.case_processor",
            "shared.contract_parser",
            "shared.formatters"
        ]
    }