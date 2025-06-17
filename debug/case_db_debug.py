"""
Description: 
판례 Chroma 벡터 DB의 상태를 상세히 점검하고, 컬렉션 연결 여부 및 검색 가능 여부를 확인하는 로컬 디버깅 스크립트입니다.  
ChromaDB에 직접 연결하거나 LangChain-Chroma wrapper를 통해 접근하여 상태 정보를 출력합니다.

Author: ooheunsu  
Date: 2025-06-16  
Requirements: chromadb, langchain_openai, langchain_chroma, python-dotenv
"""
import os
import chromadb
from langchain_openai import OpenAIEmbeddings
from langchain_chroma import Chroma
from dotenv import load_dotenv

# 환경변수 로드
load_dotenv()

def debug_case_db():
    """판례 DB 상태 상세 확인"""
    print("🔍 === 판례 DB 디버깅 시작 ===")
    
    # 1. 환경변수 확인
    case_db_path = os.getenv("CHROMA_CASE_DB_PATH", "./vectordb/chroma_case/chroma_openai_case")
    case_collection_name = os.getenv("CASE_COLLECTION_NAME", "case_collection")
    
    print(f"📁 판례 DB 경로: {case_db_path}")
    print(f"📋 판례 컬렉션명: {case_collection_name}")
    print(f"🗂️ 경로 존재 여부: {os.path.exists(case_db_path)}")
    
    # 2. 경로 내 파일들 확인
    if os.path.exists(case_db_path):
        print(f"\n📂 경로 내 파일/폴더 목록:")
        try:
            items = os.listdir(case_db_path)
            for item in items:
                item_path = os.path.join(case_db_path, item)
                if os.path.isdir(item_path):
                    print(f"  📁 {item}/")
                    # 하위 폴더도 확인
                    try:
                        sub_items = os.listdir(item_path)
                        for sub_item in sub_items[:5]:  # 처음 5개만
                            print(f"    📄 {sub_item}")
                        if len(sub_items) > 5:
                            print(f"    ... 및 {len(sub_items)-5}개 더")
                    except:
                        pass
                else:
                    file_size = os.path.getsize(item_path)
                    print(f"  📄 {item} ({file_size} bytes)")
        except Exception as e:
            print(f"❌ 파일 목록 읽기 실패: {e}")
    
    # 3. ChromaDB 직접 연결 시도
    print(f"\n🔗 ChromaDB 직접 연결 시도...")
    try:
        # 직접 ChromaDB 클라이언트로 연결
        client = chromadb.PersistentClient(path=case_db_path)
        collections = client.list_collections()
        
        print(f"📊 발견된 컬렉션 수: {len(collections)}")
        for i, collection in enumerate(collections):
            print(f"  {i+1}. 컬렉션명: '{collection.name}'")
            print(f"     데이터 개수: {collection.count()}")
            print(f"     메타데이터: {collection.metadata}")
            
            # 샘플 데이터 확인 (첫 3개)
            if collection.count() > 0:
                try:
                    sample = collection.peek(limit=3)
                    print(f"     샘플 ID: {sample['ids'][:3] if sample['ids'] else 'None'}")
                    if sample['metadatas']:
                        print(f"     샘플 메타데이터: {sample['metadatas'][0] if sample['metadatas'][0] else 'None'}")
                except Exception as e:
                    print(f"     샘플 데이터 확인 실패: {e}")
            print()
            
    except Exception as e:
        print(f"❌ ChromaDB 직접 연결 실패: {e}")
    
    # 4. LangChain Chroma로 연결 시도 (다양한 컬렉션명으로)
    print(f"\n🔗 LangChain Chroma 연결 시도...")
    try:
        embeddings = OpenAIEmbeddings(model="text-embedding-3-large")
        
        # 기본 컬렉션명으로 시도
        possible_names = [
            case_collection_name,  # 환경변수 값
            "case_collection",     # 기본값
            "case_chunks_openai",  # 법령과 유사한 패턴
            "cases",               # 간단한 이름
            "판례",                # 한글
        ]
        
        for name in possible_names:
            try:
                print(f"  📋 '{name}' 컬렉션으로 연결 시도...")
                case_db = Chroma(
                    persist_directory=case_db_path,
                    embedding_function=embeddings,
                    collection_name=name
                )
                
                count = case_db._collection.count()
                print(f"  ✅ 연결 성공! 데이터 개수: {count}개")
                
                if count > 0:
                    print(f"  🎯 판례 DB 발견! 컬렉션명: '{name}'")
                    
                    # 샘플 검색 테스트
                    try:
                        sample_results = case_db.similarity_search("계약", k=2)
                        print(f"  📝 샘플 검색 결과: {len(sample_results)}개")
                        if sample_results:
                            print(f"  📄 첫 번째 결과 (처음 100자): {sample_results[0].page_content[:100]}...")
                    except Exception as e:
                        print(f"  ⚠️ 샘플 검색 실패: {e}")
                    
                    return name, count  # 성공한 컬렉션 정보 반환
                    
            except Exception as e:
                print(f"  ❌ '{name}' 연결 실패: {e}")
        
    except Exception as e:
        print(f"❌ LangChain Chroma 연결 실패: {e}")
    
    print(f"\n🔍 === 판례 DB 디버깅 완료 ===")
    return None, 0

if __name__ == "__main__":
    debug_case_db()