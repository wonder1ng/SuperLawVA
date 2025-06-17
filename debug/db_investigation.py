# db_investigation.py - 독립 실행 DB 조사 파일
"""
Description:  
주택임대차보호법 관련 Chroma 벡터DB의 검색 성능, 메타데이터 구조, 조문 포함 여부(특히 제7조) 등을 종합적으로 분석하는 독립 실행형 스크립트

Author: ooheunsu  
Date: 2025-06-16  
Requirements: langchain-chroma, langchain-openai, python-dotenv, asyncio
"""
import os
import asyncio
import re
from typing import List, Dict, Any
from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings
from dotenv import load_dotenv

# 환경변수 로드
load_dotenv()

class DatabaseInvestigator:
    """주택임대차보호법 DB 조사 전용 클래스"""
    
    def __init__(self):
        self.embeddings = OpenAIEmbeddings(model="text-embedding-3-large")
        self.law_db = None
        self._initialize_db()
    
    def _initialize_db(self):
        """법령 DB 연결"""
        try:
            law_db_path = os.getenv("CHROMA_LAW_DB_PATH", "./vectordb/chroma_law/chroma_openai_law")
            law_collection_name = os.getenv("LAW_COLLECTION_NAME", "law_chunks_openai")
            
            if os.path.exists(law_db_path):
                self.law_db = Chroma(
                    persist_directory=law_db_path,
                    embedding_function=self.embeddings,
                    collection_name=law_collection_name
                )
                print(f"✅ 법령 DB 연결 성공: {law_db_path}")
                print(f"📋 컬렉션명: {law_collection_name}")
                
                # 총 데이터 개수 확인
                try:
                    collection_count = self.law_db._collection.count()
                    print(f"📊 총 법령 데이터: {collection_count}개")
                except Exception as e:
                    print(f"⚠️ 데이터 개수 확인 실패: {e}")
            else:
                print(f"❌ 법령 DB 경로 없음: {law_db_path}")
                
        except Exception as e:
            print(f"❌ DB 초기화 실패: {e}")
    
    async def search_laws(self, query: str, k: int = 5) -> List[Dict]:
        """법령 검색 (기존 VectorDBManager 방식과 동일)"""
        if not self.law_db:
            return []
        
        try:
            search_results = self.law_db.similarity_search_with_score(query, k=k)
            
            results = []
            for doc, score in search_results:
                max_distance = float(os.getenv("MAX_DISTANCE", "1.5"))
                
                if score <= max_distance:
                    law_info = {
                        "content": doc.page_content,
                        "metadata": doc.metadata,
                        "distance_score": score,
                        "law_name": doc.metadata.get("법령명", ""),
                        "law_id": doc.metadata.get("법령ID", None),
                    }
                    results.append(law_info)
            
            return results
            
        except Exception as e:
            print(f"❌ 검색 오류: {e}")
            return []
    
    async def comprehensive_investigation(self):
        """종합적인 DB 조사"""
        
        print("\n" + "="*80)
        print("🔍 주택임대차보호법 DB 종합 조사 시작")
        print("="*80)
        
        # 1단계: 기본 키워드 검색
        await self._investigate_basic_keywords()
        
        # 2단계: 법령명 변형 검색
        await self._investigate_law_variations()
        
        # 3단계: 내용 기반 검색
        await self._investigate_content_search()
        
        # 4단계: 메타데이터 구조 분석
        await self._investigate_metadata()
        
        # 5단계: 통계 분석
        await self._investigate_statistics()
        
        print("\n" + "="*80)
        print("🎯 조사 결과 요약")
        print("="*80)
        await self._generate_summary()
        
        print("\n" + "="*80)
        print("🔍 DB 조사 완료")
        print("="*80)
    
    async def _investigate_basic_keywords(self):
        """1단계: 기본 키워드 검색"""
        
        print("\n" + "📋 1단계: 기본 키워드 검색")
        print("-" * 50)
        
        keywords = [
            "주택임대차보호법",
            "주택임대차", 
            "임대차보호법",
            "임대료 증액",
            "차임 증액",
            "5퍼센트",
            "100분의 5",
            "연 5%"
        ]
        
        for keyword in keywords:
            print(f"\n🔍 '{keyword}' 검색:")
            
            results = await self.search_laws(keyword, k=5)
            
            if results:
                print(f"  ✅ {len(results)}개 결과 발견")
                for i, result in enumerate(results, 1):
                    law_name = result.get('law_name', '')
                    distance = result.get('distance_score', 0)
                    
                    print(f"    [{i}] {law_name} (거리: {distance:.3f})")
                    
                    # 🎯 주택임대차 관련 상세 분석
                    if any(word in law_name for word in ["주택", "임대차", "임대료"]):
                        print(f"        🎯 관련 법령 발견!")
                        metadata = result.get('metadata', {})
                        print(f"        📋 메타데이터 키: {list(metadata.keys())}")
                        print(f"        📄 내용: {result.get('content', '')[:100]}...")
                        
                        # 특히 제7조 관련 확인
                        content = result.get('content', '')
                        if "제7조" in content or "7조" in content:
                            print(f"        🎯🎯 제7조 관련 내용 발견!")
            else:
                print(f"  ❌ 결과 없음")
    
    async def _investigate_law_variations(self):
        """2단계: 법령명 변형 검색"""
        
        print("\n" + "📋 2단계: 법령명 변형 검색")
        print("-" * 50)
        
        variations = [
            "주택 임대차 보호법",        # 공백 포함
            "주택임대차보호법",          # 표준
            "주택임대차 보호법",         # 부분 공백
            "住宅賃貸借保護法",          # 한자
            "주택임대차보호법률",        # 법률
            "임대차보호법",             # 단축
            "주택임대차법",             # 단축2
            "주택임대보호법",           # 오타
        ]
        
        for variation in variations:
            print(f"\n🔍 변형 '{variation}' 검색:")
            
            # 정확 검색 (따옴표)
            exact_results = await self.search_laws(f'"{variation}"', k=3)
            # 일반 검색
            normal_results = await self.search_laws(variation, k=5)
            
            print(f"  📍 정확 검색: {len(exact_results)}개")
            print(f"  📍 일반 검색: {len(normal_results)}개")
            
            # 결과 분석
            all_results = exact_results + normal_results
            unique_results = self._deduplicate_results(all_results)
            
            if unique_results:
                for i, result in enumerate(unique_results[:3], 1):
                    law_name = result.get('law_name', '')
                    distance = result.get('distance_score', 0)
                    print(f"    [{i}] {law_name} (거리: {distance:.3f})")
                    
                    # 정확한 매치인지 확인
                    if self._is_exact_match(variation, law_name):
                        print(f"        🎯 정확한 매치 발견!")
                        print(f"        📋 전체 메타데이터: {result.get('metadata', {})}")
    
    async def _investigate_content_search(self):
        """3단계: 내용 기반 검색"""
        
        print("\n" + "📋 3단계: 내용 기반 검색")
        print("-" * 50)
        
        # 주택임대차보호법 제7조 실제 내용들
        content_queries = [
            "임대료를 증액하려는 경우",
            "100분의 5를 초과하지 못한다", 
            "임대료의 100분의 5",
            "연 5퍼센트를 초과하지",
            "증액 당시의 임대료",
            "임대료 증액 제한",
            "차임 증액 제한"
        ]
        
        for query in content_queries:
            print(f"\n🔍 내용 검색 '{query}':")
            
            results = await self.search_laws(query, k=5)
            
            if results:
                print(f"  ✅ {len(results)}개 결과")
                for i, result in enumerate(results, 1):
                    law_name = result.get('law_name', '')
                    distance = result.get('distance_score', 0)
                    content = result.get('content', '')
                    
                    print(f"    [{i}] {law_name} (거리: {distance:.3f})")
                    
                    # 내용에서 키워드 확인
                    if query in content:
                        print(f"        🎯 내용에서 키워드 정확 발견!")
                        # 키워드 주변 텍스트 추출
                        start = max(0, content.find(query) - 50)
                        end = min(len(content), content.find(query) + len(query) + 50)
                        context = content[start:end]
                        print(f"        📄 문맥: ...{context}...")
            else:
                print(f"  ❌ 결과 없음")
    
    async def _investigate_metadata(self):
        """4단계: 메타데이터 구조 분석"""
        
        print("\n" + "📋 4단계: 메타데이터 구조 분석")
        print("-" * 50)
        
        try:
            # 샘플 데이터로 메타데이터 구조 파악
            sample_results = await self.search_laws("법", k=20)
            
            if sample_results:
                print(f"✅ 샘플 {len(sample_results)}개로 메타데이터 분석")
                
                # 모든 메타데이터 키 수집
                all_keys = set()
                for result in sample_results:
                    metadata = result.get('metadata', {})
                    all_keys.update(metadata.keys())
                
                print(f"\n📋 발견된 메타데이터 필드들:")
                for key in sorted(all_keys):
                    print(f"  - {key}")
                
                # 법령명 필드 상세 분석
                print(f"\n📝 법령명 관련 필드 분석:")
                law_name_fields = [key for key in all_keys if any(word in key for word in ['법령', 'law', '명', 'name'])]
                print(f"법령명 후보 필드들: {law_name_fields}")
                
                # 샘플 5개 상세 분석
                print(f"\n📊 샘플 메타데이터 상세:")
                for i, result in enumerate(sample_results[:5], 1):
                    metadata = result.get('metadata', {})
                    print(f"  [{i}] 현재 law_name: {result.get('law_name', 'N/A')}")
                    
                    for key, value in metadata.items():
                        if any(word in key for word in ['법령', 'law', '명', 'name', '제목']):
                            print(f"      {key}: {value}")
                    print()
            else:
                print("❌ 샘플 결과 없음")
                
        except Exception as e:
            print(f"❌ 메타데이터 분석 오류: {e}")
    
    async def _investigate_statistics(self):
        """5단계: 통계 분석"""
        
        print("\n" + "📋 5단계: 전체 통계 분석")
        print("-" * 50)
        
        housing_keywords = ["주택", "임대", "임차", "전세", "월세", "보증금"]
        
        for keyword in housing_keywords:
            print(f"\n📊 '{keyword}' 관련 법령 통계:")
            
            try:
                results = await self.search_laws(keyword, k=30)  # 많이 검색
                print(f"  총 {len(results)}개 발견")
                
                # 법령별 분류
                law_stats = {}
                for result in results:
                    law_name = result.get('law_name', '')
                    
                    # 기본 법령명 추출
                    base_law = re.match(r'^([가-힣]+법)', law_name)
                    if base_law:
                        base_name = base_law.group(1)
                        if base_name in law_stats:
                            law_stats[base_name] += 1
                        else:
                            law_stats[base_name] = 1
                
                # 상위 법령들 출력
                sorted_laws = sorted(law_stats.items(), key=lambda x: x[1], reverse=True)
                print(f"  상위 법령들:")
                for law, count in sorted_laws[:8]:
                    print(f"    {law}: {count}개")
                    
                    # 🎯 주택임대차보호법 특별 체크
                    if "주택임대차보호법" in law:
                        print(f"      🎯🎯 주택임대차보호법 발견! {count}개 조문")
                        
                        # 관련 조문들 상세 분석
                        housing_results = [r for r in results if "주택임대차보호법" in r.get('law_name', '')]
                        print(f"      관련 조문들:")
                        for hr in housing_results[:5]:
                            hn = hr.get('law_name', '')
                            print(f"        - {hn}")
                            
            except Exception as e:
                print(f"  ❌ '{keyword}' 통계 오류: {e}")
    
    async def _generate_summary(self):
        """조사 결과 요약"""
        
        print("\n🎯 핵심 발견사항:")
        
        # 주택임대차보호법 직접 검색
        housing_law_results = await self.search_laws("주택임대차보호법", k=10)
        
        if housing_law_results:
            print(f"✅ '주택임대차보호법' 검색 결과: {len(housing_law_results)}개")
            
            # 제7조 관련 찾기
            article_7_found = False
            for result in housing_law_results:
                law_name = result.get('law_name', '')
                content = result.get('content', '')
                
                if "제7조" in law_name or "제7조" in content or "7조" in content:
                    print(f"🎯 제7조 관련 발견: {law_name}")
                    print(f"   거리: {result.get('distance_score', 0):.3f}")
                    print(f"   메타데이터: {result.get('metadata', {})}")
                    article_7_found = True
            
            if not article_7_found:
                print("❌ 주택임대차보호법 제7조 관련 내용 찾을 수 없음")
                
                print("\n💡 대안 검색 시도:")
                alternative_queries = ["임대료 증액", "100분의 5", "연 5퍼센트"]
                for alt_query in alternative_queries:
                    alt_results = await self.search_laws(alt_query, k=3)
                    if alt_results:
                        print(f"  '{alt_query}' 검색 결과:")
                        for ar in alt_results:
                            print(f"    - {ar.get('law_name', '')} (거리: {ar.get('distance_score', 0):.3f})")
        else:
            print("❌ '주택임대차보호법' 검색 결과 없음")
        
        print("\n📋 권장 해결 방안:")
        print("1. DB에 주택임대차보호법 제7조가 실제로 존재하는지 확인")
        print("2. 검색되지 않는다면 검색 알고리즘 개선 필요")
        print("3. 아예 없다면 방안1(정확한 정보 제공) 적용 권장")
    
    def _deduplicate_results(self, results: List[Dict]) -> List[Dict]:
        """결과 중복 제거"""
        seen = set()
        unique = []
        
        for result in results:
            law_name = result.get('law_name', '')
            if law_name not in seen:
                seen.add(law_name)
                unique.append(result)
        
        return unique
    
    def _is_exact_match(self, query: str, law_name: str) -> bool:
        """정확한 매치인지 확인"""
        query_clean = re.sub(r'[\s\-\.]', '', query.lower())
        law_clean = re.sub(r'[\s\-\.]', '', law_name.lower())
        
        return query_clean in law_clean or law_clean in query_clean

async def main():
    """메인 실행 함수"""
    print("🚀 주택임대차보호법 DB 조사 시작")
    
    investigator = DatabaseInvestigator()
    
    if investigator.law_db is None:
        print("❌ DB 연결 실패 - 조사를 진행할 수 없습니다.")
        print("💡 .env 파일과 DB 경로를 확인해주세요.")
        return
    
    await investigator.comprehensive_investigation()
    
    print("\n✅ 조사 완료! 결과를 바탕으로 contract_service.py 개선 방안을 제안받으세요.")

if __name__ == "__main__":
    asyncio.run(main())