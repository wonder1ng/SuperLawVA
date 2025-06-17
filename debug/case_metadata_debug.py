"""
Description: 
판례 ChromaDB의 메타데이터 구조를 분석하여 필드 구성, 샘플 값, 값 분포, 문서 길이, 검색 결과 등을 출력하고  
향후 contract_service.py 개선을 위한 제안사항까지 포함하는 종합 분석 스크립트

Author: ooheunsu  
Date: 2025-06-16  
Requirements: chromadb, langchain_openai, langchain_chroma, python-dotenv
"""
import os
import chromadb
from langchain_openai import OpenAIEmbeddings
from langchain_chroma import Chroma
from dotenv import load_dotenv
from collections import Counter, defaultdict
import json

# 환경변수 로드
load_dotenv()

def analyze_case_metadata():
    """판례 DB 메타데이터 구조 상세 분석"""
    print("🔍 === 판례 DB 메타데이터 구조 분석 ===\n")
    
    # 환경변수 설정
    case_db_path = os.getenv("CHROMA_CASE_DB_PATH", "./vectordb/chroma_case/chroma_openai_case")
    case_collection_name = os.getenv("CASE_COLLECTION_NAME", "langchain")
    
    try:
        # ChromaDB 직접 연결
        client = chromadb.PersistentClient(path=case_db_path)
        collection = client.get_collection(name=case_collection_name)
        
        total_count = collection.count()
        print(f"📊 총 판례 데이터 수: {total_count:,}개\n")
        
        # 샘플 데이터 가져오기 (100개)
        sample_size = min(100, total_count)
        print(f"🔬 분석 샘플 수: {sample_size}개\n")
        
        sample_data = collection.get(limit=sample_size, include=["metadatas", "documents"])
        
        if not sample_data['metadatas']:
            print("❌ 메타데이터가 없습니다.")
            return
        
        # 1. 메타데이터 필드 분석
        print("📋 === 메타데이터 필드 분석 ===")
        all_fields = set()
        field_types = defaultdict(set)
        field_samples = defaultdict(list)
        
        for metadata in sample_data['metadatas']:
            if metadata:
                for key, value in metadata.items():
                    all_fields.add(key)
                    field_types[key].add(type(value).__name__)
                    if len(field_samples[key]) < 5:  # 각 필드당 최대 5개 샘플
                        field_samples[key].append(value)
        
        print(f"발견된 메타데이터 필드 수: {len(all_fields)}개")
        print("필드명 목록:")
        for i, field in enumerate(sorted(all_fields), 1):
            types = ', '.join(field_types[field])
            print(f"  {i:2d}. {field} ({types})")
        print()
        
        # 2. 각 필드별 상세 분석
        print("🔍 === 필드별 상세 분석 ===")
        for field in sorted(all_fields):
            print(f"\n📌 필드: '{field}'")
            print(f"   데이터 타입: {', '.join(field_types[field])}")
            print(f"   샘플 값들:")
            for i, sample in enumerate(field_samples[field], 1):
                sample_str = str(sample)
                if len(sample_str) > 100:
                    sample_str = sample_str[:100] + "..."
                print(f"     {i}. {sample_str}")
        
        # 3. 판례 내용(documents) 분석
        print(f"\n\n📄 === 판례 내용(Documents) 분석 ===")
        if sample_data['documents']:
            doc_lengths = [len(doc) if doc else 0 for doc in sample_data['documents']]
            print(f"문서 개수: {len(sample_data['documents'])}개")
            print(f"평균 길이: {sum(doc_lengths)/len(doc_lengths):.0f}자")
            print(f"최소 길이: {min(doc_lengths)}자")
            print(f"최대 길이: {max(doc_lengths)}자")
            
            # 첫 번째 문서 샘플
            if sample_data['documents'][0]:
                print(f"\n📝 첫 번째 문서 샘플 (처음 500자):")
                print(f"'{sample_data['documents'][0][:500]}...'")
        
        # 4. 특정 필드 값 분포 분석
        print(f"\n\n📊 === 주요 필드 값 분포 분석 ===")
        
        # case_type 분포
        if 'case_type' in all_fields:
            case_types = [m.get('case_type') for m in sample_data['metadatas'] if m and m.get('case_type')]
            type_counter = Counter(case_types)
            print(f"\n🏛️ case_type 분포 (상위 10개):")
            for case_type, count in type_counter.most_common(10):
                print(f"   {case_type}: {count}건")
        
        # announce_date 년도 분포
        if 'announce_date' in all_fields:
            dates = [m.get('announce_date') for m in sample_data['metadatas'] if m and m.get('announce_date')]
            years = []
            for date in dates:
                try:
                    if isinstance(date, str) and len(date) >= 4:
                        years.append(date[:4])
                except:
                    pass
            if years:
                year_counter = Counter(years)
                print(f"\n📅 announce_date 년도 분포 (상위 10개):")
                for year, count in sorted(year_counter.most_common(10)):
                    print(f"   {year}년: {count}건")
        
        # case_name 길이 분포
        if 'case_name' in all_fields:
            case_names = [m.get('case_name') for m in sample_data['metadatas'] if m and m.get('case_name')]
            name_lengths = [len(str(name)) for name in case_names if name]
            if name_lengths:
                print(f"\n📋 case_name 길이 분포:")
                print(f"   평균 길이: {sum(name_lengths)/len(name_lengths):.1f}자")
                print(f"   최소 길이: {min(name_lengths)}자")
                print(f"   최대 길이: {max(name_lengths)}자")
                
                # 가장 긴 case_name 샘플
                max_idx = name_lengths.index(max(name_lengths))
                print(f"   가장 긴 사건명: '{case_names[max_idx]}'")
        
        # 5. LangChain Chroma로 연결 테스트
        print(f"\n\n🔗 === LangChain 연결 테스트 ===")
        try:
            embeddings = OpenAIEmbeddings(model="text-embedding-3-large")
            case_db = Chroma(
                persist_directory=case_db_path,
                embedding_function=embeddings,
                collection_name=case_collection_name
            )
            
            # 테스트 검색
            test_queries = ["임대차", "계약", "보증금", "손해배상"]
            for query in test_queries:
                try:
                    results = case_db.similarity_search_with_score(query, k=2)
                    print(f"\n🔍 '{query}' 검색 결과: {len(results)}건")
                    for i, (doc, score) in enumerate(results, 1):
                        case_name = doc.metadata.get('case_name', 'Unknown')
                        doc_id = doc.metadata.get('doc_id', 'Unknown')
                        print(f"   {i}. [{doc_id}] {case_name} (거리: {score:.4f})")
                        print(f"      내용: {doc.page_content[:100]}...")
                except Exception as e:
                    print(f"❌ '{query}' 검색 실패: {e}")
        
        except Exception as e:
            print(f"❌ LangChain 연결 실패: {e}")
        
        # 6. 메타데이터 구조 요약
        print(f"\n\n📋 === 메타데이터 구조 요약 ===")
        print("현재 contract_service.py에서 활용 가능한 필드:")
        useful_fields = []
        if 'doc_id' in all_fields:
            useful_fields.append("doc_id → case_id로 활용 가능")
        if 'case_name' in all_fields:
            useful_fields.append("case_name → case로 활용 가능")
        if 'case_type' in all_fields:
            useful_fields.append("case_type → 사건 분류에 활용")
        if 'announce_date' in all_fields:
            useful_fields.append("announce_date → 판결 날짜 정보")
        if 'judgement' in all_fields:
            useful_fields.append("judgement → 판결 요지")
        
        for field in useful_fields:
            print(f"  ✅ {field}")
        
        # 7. contract_service.py 개선 제안
        print(f"\n💡 === contract_service.py 개선 제안 ===")
        print("1. _format_case_context() 메소드 개선:")
        print("   - doc_id를 case_id로 활용")
        print("   - case_name을 case로 활용") 
        print("   - case_type, announce_date 추가 정보 제공")
        print("   - judgement 내용으로 더 정확한 판례 설명 생성")
        print("\n2. search_relevant_cases() 메소드 개선:")
        print("   - 메타데이터 필터링 추가 (예: 최근 5년 판례만)")
        print("   - case_type별 가중치 적용")
        
    except Exception as e:
        print(f"❌ 분석 실패: {e}")
    
    print(f"\n🔍 === 판례 DB 메타데이터 분석 완료 ===")

if __name__ == "__main__":
    analyze_case_metadata()