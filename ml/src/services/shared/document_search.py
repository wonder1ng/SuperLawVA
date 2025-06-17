"""
[services/shared/document_search.py] - 벡터 검색 공통 서비스

기존 generate_letter_chain.py에서 추출한 DocumentSearchService
모든 기능(내용증명, 계약서검토, 특약생성)에서 사용
"""

import asyncio
from typing import Tuple, List
from vectordb.loaders.load_law_db import load_law_vectorstore
from vectordb.loaders.load_case_db import load_case_vectorstore

class SearchConfig:
    """검색 설정 관리"""
    LAW_SEARCH_LIMIT = 10
    CASE_SEARCH_LIMIT = 20
    CASE_SCORE_THRESHOLD = 0.75
    CASE_RESULT_LIMIT = 3

class DocumentSearchService:
    """벡터 검색 전담 (비동기)"""
    
    def __init__(self):
        self.law_vectorstore = None
        self.case_vectorstore = None
    
    async def load_vectorstores(self):
        """벡터스토어 로드 (비동기)"""
        # 동기 함수들을 별도 스레드에서 실행
        loop = asyncio.get_event_loop()
        self.law_vectorstore = await loop.run_in_executor(None, load_law_vectorstore)
        self.case_vectorstore = await loop.run_in_executor(None, load_case_vectorstore)
    
    async def search_documents(self, user_query: str) -> Tuple[List, List]:
        """법령과 판례 문서 검색 (기본 - 내용증명용)"""
        if not self.law_vectorstore:
            await self.load_vectorstores()
        
        # 벡터 검색을 별도 스레드에서 실행
        loop = asyncio.get_event_loop()
        
        # 법령 검색
        law_docs = await loop.run_in_executor(
            None, 
            lambda: self.law_vectorstore.similarity_search(user_query, k=SearchConfig.LAW_SEARCH_LIMIT)
        )
        
        # 판례 검색 (유사도 필터링)
        case_docs_with_scores = await loop.run_in_executor(
            None,
            lambda: self.case_vectorstore.similarity_search_with_score(user_query, k=SearchConfig.CASE_SEARCH_LIMIT)
        )
        case_docs = [
            doc for doc, score in case_docs_with_scores 
            if score >= SearchConfig.CASE_SCORE_THRESHOLD
        ][:SearchConfig.CASE_RESULT_LIMIT]
        
        return law_docs, case_docs
    
    async def search_laws_only(self, query: str, limit: int = 10) -> List:
        """법령만 검색 (계약서 검토에서 사용)"""
        if not self.law_vectorstore:
            await self.load_vectorstores()
        
        loop = asyncio.get_event_loop()
        law_docs = await loop.run_in_executor(
            None,
            lambda: self.law_vectorstore.similarity_search(query, k=limit)
        )
        return law_docs
    
    async def search_cases_by_issue(self, issue_description: str, limit: int = 5) -> List:
        """문제 상황 기반 판례 검색 (계약서 검토에서 사용)"""
        if not self.case_vectorstore:
            await self.load_vectorstores()
        
        loop = asyncio.get_event_loop()
        case_docs_with_scores = await loop.run_in_executor(
            None,
            lambda: self.case_vectorstore.similarity_search_with_score(issue_description, k=limit)
        )
        # 문제 판례는 더 엄격한 유사도 기준 적용
        case_docs = [
            doc for doc, score in case_docs_with_scores 
            if score >= 0.8  # 더 높은 임계값
        ][:limit]
        
        return case_docs