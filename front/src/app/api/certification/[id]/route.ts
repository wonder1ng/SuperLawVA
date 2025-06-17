import { NextRequest, NextResponse } from "next/server";
import { Certification }            from "@/types/certification";

/* TODO: 실제 DB 쿼리로 교체 */
export async function GET(
  _req: NextRequest,
  { params }: { params: { id: string } }
) {
  const mock: Certification = {
    _id: +params.id,
    user_id: 123,
    contract_id: 123,
    created_date: "2026-06-30T00:00:00Z",
    title: "계약 이행 증명서",
    receiver: {
      name: "홍길동",
      address: "서울특별시 강남구 테헤란로 123",
      detail_address: "101동 202호"
    },
    sender: {
      name: "김법무",
      address: "서울특별시 서초구 서초대로 456",
      detail_address: "법무빌딩 5층"
    },
    body:
      "본 계약에 따라 임차인은 계약 이행을 성실히 준수하여야 합니다. " +
      "이에 따라 계약 조건을 다시 확인하고 이행을 촉구합니다.",
    strategy_summary:
      "계약 이행 보장을 위해 법적 절차를 최소화함.",
    followup_strategy:
      "계약 갱신 시 동일 조건 유지 및 법적 보장 강화.",
    legal_basis: [
      {
        law_id: 201,
        law: "주택임대차보호법 제4조",
        explanation: "임대차 기간 중 계약 해지 불가에 대한 규정.",
        content: "임대차 기간은 2년 이상으로 정함."
      },
      {
        law_id: 202,
        law: "민법 제390조",
        explanation: "채무불이행에 따른 손해배상.",
        content: "채무자가 이행하지 않을 경우 손해배상 책임을 짐."
      }
    ],
    case_basis: [
      {
        case_id: 301,
        case: "대법원 2019다12345 판결",
        explanation: "계약 위반 시 손해배상 책임 인정.",
        link: "data/case/301"
      },
      {
        case_id: 302,
        case: "서울고등법원 2018나54321 판결",
        explanation: "임대차 기간 내 해지의 효력 부인.",
        link: "data/case/302"
      }
    ],
    generation_time: 42.96,
    user_query:
      "수신인과 발신인 정보를 입력하고 계약서 내용을 기반으로 인증서를 생성해주세요."
  };

  return NextResponse.json(mock, { status: 200 });
}