"""
[routes/analyze_contract.py] - 계약서 검토 자동 분석 API 라우터

공통 서비스들을 사용하는 계약서 검토 API
"""

from fastapi import APIRouter, HTTPException
from services.schema.analyze_schema import ContractAnalysisInput, ContractAnalysisOutput
from services.analyze_contract_chain import run_analysis_chain
import traceback

router = APIRouter()

@router.post("/analyze-contract", response_model=ContractAnalysisOutput)
async def analyze_contract(input_data: ContractAnalysisInput):
    """
    임대차 계약서 JSON을 입력 받아 조항별 위험도 분석을 수행합니다.
    공통 서비스들(검색, 법령분석, 판례분석)을 조립하여 사용합니다.
    
    Args:
        input_data: 
            - contract_data: 임대차 계약서 JSON (전세/월세)
            - debug_mode: 디버깅 모드 (선택)
    
    Returns:
        ContractAnalysisOutput: 계약서 검토 분석 결과
            - _id, user_id, contract_id: 메타 정보
            - articles: 계약 조항별 분석 결과 (result, content, reason, 수정안 등)
            - agreements: 특약사항별 분석 결과
            - recommended_agreements: 추가 권고 특약들
            - legal_basis: 관련 법령 근거 목록
            - case_basis: 관련 판례 근거 목록
            - analysis_metadata: 분석 메타데이터
    """
    try:
        # 입력 데이터 검증
        if not input_data.contract_data:
            raise HTTPException(status_code=400, detail="계약서 데이터가 필요합니다.")
        
        # 계약서 타입 검증 (전세/월세)
        contract_type = input_data.contract_data.get("contract_type")
        if contract_type not in ["전세", "월세"]:
            raise HTTPException(status_code=400, detail="지원되지 않는 계약 유형입니다. (전세/월세만 지원)")
        
        # 계약서에 조항이나 특약이 있는지 확인
        articles = input_data.contract_data.get("articles", [])
        agreements = input_data.contract_data.get("agreements", [])
        
        if not articles and not agreements:
            raise HTTPException(status_code=400, detail="분석할 계약 조항이나 특약사항이 없습니다.")
        
        # 계약서 검토 분석 실행
        result = await run_analysis_chain(input_data)
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
                detail=f"계약서 검토 분석 중 오류가 발생했습니다: {str(e)}"
            )
        else:
            # 일반 모드에서는 간단한 에러 메시지만 제공
            raise HTTPException(
                status_code=500, 
                detail="계약서 검토 분석 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            )

@router.get("/analyze-health")
async def health_check():
    """
    계약서 검토 분석 서비스 상태 확인
    """
    return {
        "status": "healthy",
        "service": "contract_analysis",
        "version": "v2.0.0",
        "message": "계약서 검토 분석 서비스가 정상 작동 중입니다.",
        "features": [
            "임대차 계약 조항 위험도 분석",
            "특약사항 적절성 검토",
            "추가 권고 특약 제안",
            "법령 및 판례 기반 근거 제시"
        ],
        "modules": [
            "shared.document_search",
            "shared.case_processor",
            "shared.contract_parser",
            "shared.formatters"
        ]
    }

@router.get("/analyze-info")
async def analysis_info():
    """
    계약서 검토 분석 기능 상세 정보
    """
    return {
        "service_name": "계약서 검토 분석",
        "description": "임대차 계약서의 조항과 특약사항을 법령 및 판례 기반으로 분석하여 임차인 관점에서의 위험도를 평가합니다.",
        "analysis_criteria": {
            "적절 (result: true)": "법령에 부합하고 균형잡힌 조항, 양 당사자의 권리가 적절히 보호됨",
            "문제 (result: false)": "임대차보호법 위반 소지, 임차인에게 과도한 불이익, 법적 분쟁 가능성"
        },
        "output_components": {
            "articles": "계약 조항별 분석 결과",
            "agreements": "특약사항별 분석 결과", 
            "recommended_agreements": "임차인 보호를 위한 추가 권고 특약",
            "legal_basis": "분석에 사용된 관련 법령들",
            "case_basis": "참고된 관련 판례들"
        },
        "supported_contract_types": ["전세", "월세"],
        "analysis_approach": "RAG 기반 법령 검색 + 문제 조항별 판례 분석",
        "llm_model": "Claude Sonnet 4"
    }