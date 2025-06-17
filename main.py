"""
Description:  
계약서 특약사항 생성 서비스를 위한 FastAPI 앱 초기화, 환경설정, 예외처리, CORS, 라우터 등록 및 헬스체크 포함한 메인 실행 파일

Author: ooheunsu  
Date: 2025-06-16  
Requirements: fastapi, uvicorn, python-dotenv, logging
"""
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from contextlib import asynccontextmanager
import uvicorn
import logging
from datetime import datetime
import os
from dotenv import load_dotenv

# 환경변수 로드
load_dotenv()

# 라우터 임포트
from contract_router import router as contract_router

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    앱 생명주기 관리
    - 시작 시: 필요한 초기화 작업
    - 종료 시: 리소스 정리
    """
    # 앱 시작 시
    logger.info("🚀 계약서 특약사항 생성 서비스 시작")
    logger.info(f"📅 시작 시간: {datetime.now().isoformat()}")
    
    # 환경변수 검증
    required_env_vars = ["ANTHROPIC_API_KEY", "OPENAI_API_KEY"]
    missing_vars = [var for var in required_env_vars if not os.getenv(var)]
    
    if missing_vars:
        logger.error(f"❌ 필수 환경변수가 없습니다: {missing_vars}")
        raise HTTPException(
            status_code=500,
            detail=f"필수 환경변수가 설정되지 않았습니다: {missing_vars}"
        )
    
    logger.info("✅ 환경변수 검증 완료")
    logger.info("✅ 서비스 초기화 완료")
    
    yield
    
    # 앱 종료 시
    logger.info("🛑 계약서 특약사항 생성 서비스 종료")


# FastAPI 앱 생성
app = FastAPI(
    title="계약서 특약사항 생성 API",
    description="""
    ## 📋 계약서 특약사항 생성 서비스
    
    **25년 경력의 임차인을 위한 부동산 전문 변호사** 페르소나로 
    사용자 요청에 따른 특약사항을 생성합니다.
    
    ### 🎯 주요 기능
    - **특약사항 생성**: 임차인 중심의 맞춤형 특약 조건 제안
    - **법령 근거 제시**: 관련 법령과 상세한 해설 제공
    - **판례 정보**: 관련 판례와 요약 정보 제공
    - **협상 전략**: 실무적인 협상 포인트 제안
    
    ### 📝 입력 형식
    - **필수**: `user_query` (사용자 요청사항 리스트)
    - **선택**: 계약 유형, 부동산 정보, 금액 정보 등
    
    ### 📊 출력 형식
    - **추천 특약사항**: 임차인에게 유리한 조건들
    - **법적 근거**: 관련 법령 해설 (조, 항, 호까지 상세)
    - **판례 정보**: 관련 판례 요약과 링크
    
    ### 🔧 기술 스택
    - **AI 모델**: Claude Sonnet 4
    - **프레임워크**: FastAPI + LangChain
    - **검증**: Pydantic 스키마
    """,
    version="1.0.0",
    contact={
        "name": "계약서 특약사항 생성 서비스",
        "email": "contact@example.com",
    },
    license_info={
        "name": "MIT License",
        "url": "https://opensource.org/licenses/MIT",
    },
    lifespan=lifespan
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 실제 운영 시에는 특정 도메인으로 제한
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 라우터 등록
app.include_router(contract_router)

# 루트 엔드포인트
@app.get("/", tags=["기본"])
async def root():
    """
    서비스 기본 정보 조회
    """
    return {
        "service": "계약서 특약사항 생성 API",
        "version": "1.0.0",
        "description": "25년 경력의 부동산 전문 변호사가 임차인을 위한 특약사항을 생성합니다",
        "ai_model": "Claude Sonnet 4",
        "endpoints": {
            "docs": "/docs",
            "redoc": "/redoc",
            "health": "/api/v1/contract/health",
            "generate": "/api/v1/contract/generate-special-terms",
            "validate": "/api/v1/contract/validate-input"
        },
        "timestamp": datetime.now().isoformat(),
        "status": "🟢 정상 운영 중"
    }


# 전역 예외 처리
@app.exception_handler(Exception)
async def global_exception_handler(request, exc):
    """
    전역 예외 처리기
    """
    logger.error(f"예상치 못한 오류 발생: {str(exc)}", exc_info=True)
    
    return JSONResponse(
        status_code=500,
        content={
            "success": False,
            "message": "서버에서 예상치 못한 오류가 발생했습니다.",
            "error_code": "INTERNAL_SERVER_ERROR",
            "timestamp": datetime.now().isoformat(),
            "path": str(request.url)
        }
    )


# 404 에러 처리
@app.exception_handler(404)
async def not_found_handler(request, exc):
    """
    404 에러 처리기
    """
    return JSONResponse(
        status_code=404,
        content={
            "success": False,
            "message": "요청하신 API 엔드포인트를 찾을 수 없습니다.",
            "error_code": "NOT_FOUND",
            "path": str(request.url),
            "available_endpoints": [
                "/docs",
                "/api/v1/contract/health",
                "/api/v1/contract/generate-special-terms",
                "/api/v1/contract/validate-input"
            ],
            "timestamp": datetime.now().isoformat()
        }
    )


# 헬스체크 (추가)
@app.get("/health", tags=["모니터링"])
async def health_check():
    """
    간단한 헬스체크 엔드포인트
    """
    return {
        "status": "healthy",
        "service": "contract-special-terms",
        "timestamp": datetime.now().isoformat(),
        "version": "1.0.0"
    }


# 메인 실행
if __name__ == "__main__":
    # 환경변수에서 설정 읽기
    host = os.getenv("HOST", "0.0.0.0")
    port = int(os.getenv("PORT", "8000"))
    reload = os.getenv("RELOAD", "True").lower() == "true"
    
    logger.info(f"🌐 서버 시작: {host}:{port}")
    logger.info(f"📖 API 문서: http://{host}:{port}/docs")
    logger.info(f"🔄 자동 리로드: {reload}")
    
    uvicorn.run(
        "main:app",
        host=host,
        port=port,
        reload=reload,
        log_level="info"
    )