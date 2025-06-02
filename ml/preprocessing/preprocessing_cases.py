# 최민주 작업 
# 판결문 전처리 코드 파이프라인 입니다. 
# + 아마 '주문'이 nan인 거 제거랑, '청구취지'nan인거 '없음'으로. 하는 것도 추가해야될 걸...?

import os
import pandas as pd
import re
from config import RAW_CASE_DETAIL_DIR, PREPROCESSED_DIR, get_latest_file, get_dated_filename

# 사건명 정제 함수
def clean_case_name(name):
    name = re.sub(r'\[.*?\]|\(.*?\)|\{.*?\}', '', name)  # 괄호 제거
    name = name.replace('"', '')  # 쌍따옴표 제거
    name = re.sub(r'\s+', ' ', name).strip()  # 공백 정리
    return name

#  사건명 기반 임대차 키워드 필터링
def is_related_to_lease(text):
    keywords = ["임대차", "임차", "임대인", "임차인", "임대차보증금", "임대차보증금반환", "보증금", "보증금반환",
                "건물명도", "건물명도등", "건물인도", "차임", "전세", "월세", "전대차", "명도"]
    return any(keyword in text for keyword in keywords)

# 판례내용 정제 함수
def clean_precedent_text(text):
    if pd.isna(text):
        return ""
    text = re.sub(r"<br\s*/?>", "\n", text, flags=re.IGNORECASE)
    text = re.sub(r"\n{2,}", "\n", text)
    text = re.sub(r"[ \t]{2,}", " ", text)
    text = re.sub(r"&nbsp;", " ", text)
    return text.strip()

# 판례내용에서 청구취지, 주문, 이유 분리
def extract_sections(text):
    if pd.isna(text):
        return pd.Series(["0", "", ""])
    text = re.sub(r"<br\s*/?>", "\n", text)
    text = re.sub(r"\n{2,}", "\n", text)

    청구취지 = re.search(r'【\s*(청\s*구\s*취\s*지|청\s*구.*?)[^】]*】(.*?)(?=【)', text, re.DOTALL)
    주문 = re.search(r'【\s*주\s*문\s*】(.*?)(?=【\s*이\s*유\s*】)', text, re.DOTALL)
    이유 = re.search(r'【\s*이\s*유\s*】(.*)', text, re.DOTALL)

    return pd.Series([
        청구취지.group(2).strip() if 청구취지 else "0",
        주문.group(1).strip() if 주문 else "",
        이유.group(1).strip() if 이유 else ""
    ])

# 단기연도 → 서기로 변환
def convert_dangi_to_gregorian(code):
    try:
        year = code[:4]
        if year.isdigit() and int(year) >= 3000:
            return str(int(year) - 2333) + code[4:]
    except:
        pass
    return code

# 접수연도 변환 함수
def convert_year(year_str):
    year = int(year_str)
    if len(year_str) == 4:
        return year
    elif len(year_str) == 2:
        return 2000 + year if year < 30 else 1900 + year
    return None

# 사건유형 분류 딕셔너리
case_type_map = {
    '다': '민사 항소심 사건', '가단': '민사 단독사건', '나': '민사 항소심 사건 (지방법원)',
    '도': '형사 항소심 사건', '가소': '소액 민사사건', '가합': '민사 합의사건',
    '노': '노동 사건 항소', '고합': '형사 합의사건', '누': '행정소송 항소',
    '구합': '행정 합의사건', '카합': '가처분 등 민사 비송 합의사건', '추': '재심청구 사건',
    '다카': '민사 항소 사건 중 특수 케이스', '재나': '재심청구 항소 사건', '구': '행정 1심 사건',
    '행': '행정소송 사건', '민공': '민사 공시송달 사건', '민상': '민사 상고사건', '행상': '행정 상고사건'
}

# 메인 전처리 함수
def preprocess_case_data():
    raw_path = get_latest_file(RAW_CASE_DETAIL_DIR, "case_details")
    df = pd.read_csv(raw_path, encoding="utf-8-sig")

    # 필요 없는 컬럼 제거
    df.drop(columns=["판례일련번호"], inplace=True)

    # 사건명 정제
    df["사건명"] = df["사건명"].apply(clean_case_name)

    # 임대차 키워드 필터링
    df["임대차관련여부"] = df["사건명"].apply(is_related_to_lease)
    df = df[df["임대차관련여부"]].reset_index(drop=True)

    # 판례내용 정제
    df["판례내용"] = df["판례내용"].apply(clean_precedent_text)

    # 텍스트 분리
    df[["청구취지", "주문", "이유"]] = df["판례내용"].apply(extract_sections)

    # 선고일자 결측치 처리
    df['선고일자'] = df['선고일자'].fillna('0000-00-00')

    # 단기연도 변환
    mask = df['사건번호'].str.match(r'^[3-9]\d{3}[가-힣]+')
    df_dangi = df[mask].copy()
    df_dangi['사건번호'] = df_dangi['사건번호'].apply(convert_dangi_to_gregorian)
    df = pd.concat([df[~mask], df_dangi], ignore_index=True)

    # 사건번호 분리
    df[["연도원본", "사건부호", "접수번호"]] = df['사건번호'].str.extract(r'(\d{2,4})([가-힣]+)(\d+)', expand=True)
    df['접수연도'] = df['연도원본'].apply(convert_year)
    df.drop(columns=["연도원본"], inplace=True)

    # 사건유형 매핑
    df['사건유형'] = df['사건부호'].map(case_type_map).fillna('')

    # 불필요한 컬럼 제거 및 정리
    df.drop(columns=["사건부호"], inplace=True)
    if "법원명" in df.columns:
        df.drop(columns=["법원명"], inplace=True)

    # 컬럼 순서 정리
    final_columns = ["사건번호", "사건명", "주문", "청구취지", "이유", "선고일자", "접수연도", "사건유형", "접수번호"]
    df = df[final_columns]

    # 저장
    os.makedirs(PREPROCESSED_DIR, exist_ok=True)
    filename = get_dated_filename("case_details_preprocessed")
    save_path = os.path.join(PREPROCESSED_DIR, filename)
    df.to_csv(save_path, index=False, encoding="utf-8-sig")
    print(f"✅ 전처리 완료: {save_path}")

if __name__ == "__main__":
    preprocess_case_data()
