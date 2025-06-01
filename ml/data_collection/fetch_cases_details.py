import os
import time
import pandas as pd
import requests
import xml.etree.ElementTree as ET
from dotenv import load_dotenv
from config import RAW_CASE_DETAIL_DIR, RAW_CASE_LIST_DIR, get_dated_filename

# ✅ 환경 변수
load_dotenv()
OC = os.getenv("OC")
if not OC:
    raise ValueError("❌ .env 파일에 OC 키가 없습니다!")

# ✅ 본문 수집 함수
def get_case_detail(case_id: str):
    url = f"https://www.law.go.kr/DRF/lawService.do?OC={OC}&target=prec&ID={case_id}&type=XML"
    res = requests.get(url, timeout=10)
    res.encoding = "utf-8"
    root = ET.fromstring(res.text)
    
    return {
        "판례일련번호": case_id,
        "사건명": root.findtext("사건명"),
        "사건번호": root.findtext("사건번호"),
        "선고일자": root.findtext("선고일자"),
        "판례내용": root.findtext("판례내용")
    }

# ✅ 전체 수집 함수
def collect_case_details(start_idx=0):
    # 최근 저장된 목록 파일 자동 선택
    os.makedirs(RAW_CASE_DETAIL_DIR, exist_ok=True)
    case_list_files = sorted(os.listdir(RAW_CASE_LIST_DIR), reverse=True)
    latest_list_path = os.path.join(RAW_CASE_LIST_DIR, case_list_files[0])

    df_cases = pd.read_csv(latest_list_path, encoding="utf-8-sig")
    print(f"📚 수집 대상 판례 수: {len(df_cases)}건")
    total = len(df_cases)

    collected = []
    for i in range(start_idx, total):
        case_id = df_cases.loc[i, "판례일련번호"]
        try:
            print(f"📥 {i+1}/{total} - 판례 ID: {case_id} 수집 중...")
            detail = get_case_detail(case_id)
            collected.append(detail)
        except Exception as e:
            print(f"⚠️ {case_id} 오류: {e}")
        if (i + 1) % 50 == 0:
            temp_path = os.path.join(RAW_CASE_DETAIL_DIR, "case_detail_temp.csv")
            pd.DataFrame(collected).to_csv(temp_path, index=False, encoding="utf-8-sig")
            print(f"💾 {i+1}건 임시 저장 완료")
        time.sleep(0.3)

    filename = get_dated_filename("case_details")
    final_path = os.path.join(RAW_CASE_DETAIL_DIR, filename)
    pd.DataFrame(collected).to_csv(final_path, index=False, encoding="utf-8-sig")
    print(f"✅ 본문 저장 완료: {final_path}")

if __name__ == "__main__":
    collect_case_details(start_idx=0)
