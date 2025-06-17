"""
[main.py] - FastAPI 서버 진입점 (최종 완성 버전)

이 파일은 전체 AI 법률 시스템의 API 서버 역할을 합니다.
모든 기능별 route들을 등록하고, FastAPI 앱을 실행합니다.

완성된 기능:
- 내용증명 생성 기능 (새로운 임대차 계약서 형식 지원)
- 계약서 검토 분석 기능 (조항별 위험도 분석)

구성:
- FastAPI 앱 인스턴스 생성
- 기능별 라우트 등록 (내용증명, 계약검토, 특약사항)
- CORS 설정 (개발 시 전체 허용, 배포 시 특정 도메인으로 제한)

서버 실행 명령어:
uvicorn main:app --reload
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

# 기능별 라우트 import
from routes.generate_letter import router as letter_router
from routes.analyze_contract import router as analyze_router
#from routes.generate_clause import router as clause_router

# ✅ FastAPI 앱 생성
app = FastAPI(
    title="AI Legal Assistant",
    description="Claude + RAG 기반 법률 자동화 API 시스템 (임대차 전문)",
    version="2.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# ✅ CORS 설정 (개발 환경에서는 * 허용, 배포 시 제한 필요)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 배포 환경에선 ["https://yourdomain.com"] 등으로 변경
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ✅ 기능별 라우터 등록
app.include_router(letter_router, prefix="/api/v2", tags=["내용증명 생성"])
app.include_router(analyze_router, prefix="/api/v2", tags=["계약서 검토"])
#app.include_router(clause_router, prefix="/api/v2", tags=["특약사항 생성"])

# ✅ 루트 경로 - 서버 상태 확인용 (비동기로 변경)
@app.get("/")
async def root():
    return {
        "message": "✅ AI Legal Assistant API is live and running.",
        "version": "2.0.0",
        "features": [
            "임대차 계약서 기반 내용증명 생성",
            "임대차 계약서 조항별 위험도 검토",
            "RAG 기반 법령·판례 검색",
            "Claude Sonnet 4 기반 문서 생성"
        ],
        "available_endpoints": [
            "POST /api/v2/generate-letter - 내용증명 생성",
            "POST /api/v2/analyze-contract - 계약서 검토",
            "GET /docs - API 문서"
        ],
        "docs_url": "/docs"
    }

# ✅ 시스템 상태 확인
@app.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "version": "2.0.0",
        "service": "ai_legal_assistant",
        "active_features": {
            "letter_generation": "✅ 활성화",
            "contract_analysis": "✅ 활성화", 
            "clause_generation": "⏳ 준비중"
        },
        "timestamp": "2025-06-15T00:00:00Z"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)