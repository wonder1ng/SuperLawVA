import os
from datetime import datetime

# 루트 경로
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# 데이터 경로
RAW_DIR = os.path.join(BASE_DIR, "data", "raw")
PREPROCESSED_DIR = os.path.join(BASE_DIR, "data", "preprocessed")

# 세부 디렉토리
RAW_CASE_LIST_DIR = os.path.join(RAW_DIR, "case_list")
RAW_CASE_DETAIL_DIR = os.path.join(RAW_DIR, "case_details")

# 날짜 기반 파일명 생성 함수
def get_dated_filename(prefix: str, extension: str = "csv") -> str:
    today = datetime.today().strftime("%Y%m%d")
    return f"{prefix}_{today}.{extension}"
