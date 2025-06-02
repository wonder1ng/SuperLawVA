import requests
import xml.etree.ElementTree as ET
import pandas as pd
import time
import os
from dotenv import load_dotenv
from config import RAW_CASE_LIST_DIR, get_dated_filename

# ✅ 인증키 불러오기
load_dotenv()
OC = os.getenv("OC")
if not OC:
    raise ValueError("❌ .env 파일에 OC 키가 없습니다!")

# ✅ API 파라미터 설정
QUERY = "임대차"
TYPE = "XML"
DISPLAY = 100
MAX_PAGES = 1000
SLEEP_SEC = 0.5

# ✅ 수집 함수 , 국가법령정보 api 사용
def fetch_case_list_page(page: int):
    url = (
        f"http://www.law.go.kr/DRF/lawSearch.do"
        f"?OC={OC}&target=prec&type={TYPE}&query={QUERY}&search=2&display={DISPLAY}&page={page}"
    )
    response = requests.get(url, timeout=10)
    response.encoding = "utf-8"
    root = ET.fromstring(response.text)
    items = root.findall("prec")

    cases = []
    for item in items:
        cases.append({
            '판례일련번호': item.findtext('판례일련번호'),
            '사건명': item.findtext('사건명'),
            '사건번호': item.findtext('사건번호'),
            '선고일자': item.findtext('선고일자'),
            '법원명': item.findtext('법원명'),
            '판결유형': item.findtext('판결유형')
        })
    return cases

# ✅ 전체 수집 실행
def collect_case_list(start_page=1):
    os.makedirs(RAW_CASE_LIST_DIR, exist_ok=True)
    all_cases = []

    for page in range(start_page, MAX_PAGES + 1):
        print(f"📄 {page} 페이지 수집 중...")
        try:
            cases = fetch_case_list_page(page)
            if not cases:
                print("✅ 수집 완료 (더 이상 없음)")
                break
            all_cases.extend(cases)

            if page % 10 == 0:
                temp_path = os.path.join(RAW_CASE_LIST_DIR, "case_list_temp.csv")
                pd.DataFrame(all_cases).to_csv(temp_path, index=False, encoding="utf-8-sig")
                print(f"💾 {page}페이지까지 임시 저장 완료")

            time.sleep(SLEEP_SEC)

        except Exception as e:
            print(f"❌ {page}페이지에서 오류: {e}")
            break

    filename = get_dated_filename("case_list")
    final_path = os.path.join(RAW_CASE_LIST_DIR, filename)
    pd.DataFrame(all_cases).to_csv(final_path, index=False, encoding="utf-8-sig")
    print(f"🎉 최종 저장 완료: {final_path}")

if __name__ == "__main__":
    collect_case_list(start_page=1)
