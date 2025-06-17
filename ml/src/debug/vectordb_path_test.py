# 별도 테스트 파일로 저장해서 실행
# test_chroma_direct.py

import os
from pathlib import Path
from langchain_openai import OpenAIEmbeddings
from langchain_chroma import Chroma
from dotenv import load_dotenv

load_dotenv()

def test_chroma_direct():
    """Chroma DB에 직접 접근해서 테스트"""
    
    # 경로 설정
    law_db_path = "vectordb/chroma_law/chroma_openai_law"
    case_db_path = "vectordb/chroma_case/chroma_openai_case"
    
    print(f"법령 DB 경로: {law_db_path}")
    print(f"판례 DB 경로: {case_db_path}")
    print(f"법령 DB 존재: {Path(law_db_path).exists()}")
    print(f"판례 DB 존재: {Path(case_db_path).exists()}")
    
    try:
        # 임베딩 함수 생성
        embedding_function = OpenAIEmbeddings(
            model="text-embedding-3-large",
            api_key=os.getenv("OPENAI_API_KEY")
        )
        
        # 법령 DB 테스트
        print("\n=== 법령 DB 테스트 ===")
        law_vectorstore = Chroma(
            persist_directory=law_db_path,
            embedding_function=embedding_function,
            collection_name="law_chunks_openai"  # 컬렉션명 명시
        )
        
        # 전체 문서 수 확인
        law_collection = law_vectorstore.get()
        print(f"법령 DB 전체 문서 수: {len(law_collection['ids'])}")
        
        if len(law_collection['ids']) > 0:
            print(f"첫 번째 문서 ID: {law_collection['ids'][0]}")
            print(f"첫 번째 문서 내용 (100자): {law_collection['documents'][0][:100]}...")
            
            # 직접 검색 테스트
            results = law_vectorstore.similarity_search("임대차", k=3)
            print(f"'임대차' 검색 결과: {len(results)}개")
            for i, doc in enumerate(results):
                print(f"  결과 {i+1}: {doc.page_content[:50]}...")
        
        # 판례 DB 테스트
        print("\n=== 판례 DB 테스트 ===")
        case_vectorstore = Chroma(
            persist_directory=case_db_path,
            embedding_function=embedding_function
            # 판례는 컬렉션명 없이 시도
        )
        
        case_collection = case_vectorstore.get()
        print(f"판례 DB 전체 문서 수: {len(case_collection['ids'])}")
        
        if len(case_collection['ids']) > 0:
            print(f"첫 번째 문서 ID: {case_collection['ids'][0]}")
            print(f"첫 번째 문서 내용 (100자): {case_collection['documents'][0][:100]}...")
            
            # 직접 검색 테스트
            results = case_vectorstore.similarity_search("임대차", k=3)
            print(f"'임대차' 검색 결과: {len(results)}개")
            for i, doc in enumerate(results):
                print(f"  결과 {i+1}: {doc.page_content[:50]}...")
                
    except Exception as e:
        print(f"오류 발생: {str(e)}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    test_chroma_direct()