"""
[services/shared/contract_parser.py] - 계약서 파싱 공통 유틸리티 (수정됨)

전세 계약서의 monthly_rent: null 처리 추가
"""

from typing import Dict, Any, Tuple

def summarize_contract_for_letter(contract_data: dict) -> str:
    """내용증명용 계약서 요약 (기존 summarize_contract_data 함수)"""
    try:
        contract_type = contract_data.get("contract_type", "임대차")
        
        # 부동산 정보
        property_info = contract_data.get("property", {})
        address = property_info.get("address", "")
        detail_address = property_info.get("detail_address", "")
        building_type = property_info.get("building", {}).get("building_type", "")
        
        # 계약 정보
        dates = contract_data.get("dates", {})
        start_date = dates.get("start_date", "")
        end_date = dates.get("end_date", "")
        
        # 지급 정보 (None 처리 추가)
        payment = contract_data.get("payment", {})
        deposit = payment.get("deposit", 0)
        monthly_rent = payment.get("monthly_rent")
        
        # 당사자 정보
        lessor = contract_data.get("lessor", {})
        lessee = contract_data.get("lessee", {})
        lessor_name = lessor.get("name", "임대인")
        lessee_name = lessee.get("name", "임차인")
        
        summary = f"""
계약유형: {contract_type}
부동산: {address} {detail_address} ({building_type})
계약기간: {start_date} ~ {end_date}
보증금: {deposit:,}원
"""
        # 🔧 None 처리 추가
        if monthly_rent:
            summary += f"월세: {monthly_rent:,}원\n"
        else:
            summary += "월세: 전세 (월세 없음)\n"
        
        summary += f"임대인: {lessor_name}\n임차인: {lessee_name}"
        
        return summary
        
    except Exception as e:
        return f"계약 요약 오류: {e}"

def summarize_contract_for_review(contract_data: dict) -> str:
    """계약서 검토용 요약 (조항 중심)"""
    try:
        contract_type = contract_data.get("contract_type", "임대차")
        
        # 기본 정보
        property_info = contract_data.get("property", {})
        address = property_info.get("address", "")
        
        # 조항 정보
        articles = contract_data.get("articles", [])
        agreements = contract_data.get("agreements", [])
        
        summary = f"""
[검토 대상 계약서]
유형: {contract_type}
부동산: {address}

[주요 조항]
- 계약조항: {len(articles)}개
- 특약사항: {len(agreements)}개

[조항 목록]
"""
        for i, article in enumerate(articles, 1):
            summary += f"{i}. {article[:50]}...\n"
        
        if agreements:
            summary += "\n[특약사항]\n"
            for i, agreement in enumerate(agreements, 1):
                summary += f"{i}. {agreement[:50]}...\n"
        
        return summary
        
    except Exception as e:
        return f"검토용 계약 요약 오류: {e}"

def extract_parties_info(contract_data: dict) -> Tuple[Dict[str, Any], Dict[str, Any]]:
    """당사자 정보 추출 (내용증명에서 사용)"""
    lessor = contract_data.get("lessor", {})
    lessee = contract_data.get("lessee", {})
    return lessor, lessee

def extract_contract_clauses(contract_data: dict) -> Dict[str, Any]:
    """계약조항들 추출 (계약서 검토에서 사용)"""
    return {
        "articles": contract_data.get("articles", []),
        "agreements": contract_data.get("agreements", []),
        "payment_terms": contract_data.get("payment", {}),
        "dates": contract_data.get("dates", {}),
        "property_info": contract_data.get("property", {})
    }

def extract_key_contract_info(contract_data: dict) -> Dict[str, Any]:
    """핵심 계약 정보 추출 (모든 기능에서 사용) - None 처리 추가"""
    # 🔧 monthly_rent None 처리
    payment = contract_data.get("payment", {})
    monthly_rent = payment.get("monthly_rent")
    monthly_rent = monthly_rent if monthly_rent is not None else 0
    
    return {
        "contract_type": contract_data.get("contract_type", ""),
        "deposit": payment.get("deposit", 0),
        "monthly_rent": monthly_rent,  # 이제 항상 숫자
        "start_date": contract_data.get("dates", {}).get("start_date", ""),
        "end_date": contract_data.get("dates", {}).get("end_date", ""),
        "address": contract_data.get("property", {}).get("address", ""),
        "lessor_name": contract_data.get("lessor", {}).get("name", ""),
        "lessee_name": contract_data.get("lessee", {}).get("name", "")
    }

# 기존 함수명 호환성 유지
def summarize_contract_data(contract_data: dict) -> str:
    """기존 함수명 호환성 유지"""
    return summarize_contract_for_letter(contract_data)