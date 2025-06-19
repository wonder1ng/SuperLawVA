// app/main/certificate/result/page.tsx
"use client";

import Image from "next/image";
import React, { useState } from "react";
import SubmitButton from "@/components/SubmitButton";
import BackHeader from "@/components/BackHeader";
import StatusIcon from "@/components/icons/Status";
import InfoIcon from "@/components/icons/Info";
import AnalysisIcon from "@/components/icons/Analysis";
import MagicTwoStarIcon from "@/components/icons/MagicTwoStar";
import Modal from "@/components/Modal";
import ScalesIcon from "@/components/icons/Scales";

export default function CertificateResult() {
  const [ openOriginal, setOpenOriginal ] = useState(false);

  //const [activeTab, setActiveTab] = useState(0);

  //const containerRef = useRef<HTMLDivElement | null>(null);
  // -> 사용할 때 import React, { useState, useRef } from "react";

  // 하드코드 예시 데이터
  const fakeCert = {
    title: "임대차보증금 반환 촉구서",
    sender_name: "김 법무",
    sender_address: "서울특별시 구구 비둘기야 밥먹자",
    sender_detail_address: "빌드인 빌딩",
    recipient_name: "홍길동",
    recipient_address: "서울특별시 강남구 테헤란로 123",
    recipient_detail_address: "101동 103호",
    date_created: "2026-06-30",
    strategy_summary: `임차인의 전입신고 권리를 법적 근거로 명확히 제시하면서도,
    임대인의 세금 우려사항을 이해하고 상호 협의를 통한 해결책을 제안하는 협력적 접근 전략을 채택하였습니다.
    주택임대차보호법상 대항력 취득 권리와 국세징수법상 확인 권리를 구분하여 설명함으로써 법적 정당성을 확보하되,
    과도한 위협보다는 합리적 해결을 추구하는 균형잡힌 어조를 유지하였습니다.`,
    body: `“귀하의 건강과 평안을 기원합니다.”\n\n본인은 … (중략) … 요청드립니다.`,
    legal_basis: [
      { law_id: 1, law: "주택임대차보호법 제4조", explanation: "임대차 기간 중 계약 해지 불가 규정." },
      { law_id: 2, law: "민법 제390조", explanation: "채무불이행 시 손해배상 책임." },
      { law_id: 3, law: "국세징수법 제96조", explanation: "납세증명 제출 의무 규정." },
    ],
    case_basis: [
      { case_id: 1, case: "대법원 2019다12345", explanation: "계약 위반 시 손해배상 인정 판례." },
      { case_id: 2, case: "서울고법 2018나54321", explanation: "임대차 기간 내 해지 효력 부인 판례." },
      { case_id: 3, case: "대법원 2020다54321", explanation: "임대차보증금 반환 책임 판례." },
    ],
  };

  // 탭별 콘텐츠
  //const tabs = ["본문", "전략", `법령(${fakeCert.legal_basis.length})`, `판례(${fakeCert.case_basis.length})`];

  const originalBody = `“귀하의 건강과 평안을 기원합니다.”

본인은 2025년 7월 1일 귀하와 체결한 서울시 성동구 성수동 101-12 B동 802호 8층 아파트에 대한 전세계약(보증금 80,000,000원, 계약기간 2025.07.01~2027.06.30)의 임차인 김민준입니다.

현재 전입신고를 위해 임대차계약서 사본 교부를 요청드렸으나, 귀하께서 세금 문제를 이유로 계약서 교부 및 전입신고를 거부하고 계신 상황입니다.

이에 다음과 같이 법적 근거를 바탕으로 정중히 요청드립니다.

[법적 근거]
1. 전입신고는 임차인의 법정 권리입니다  
「주택임대차보호법」 제3조에서 규정하는 바와 같이 '임차인은 주택의 인도와 주민등록을 마친 때에 제3자에 대하여 효력이 생긴다'고 명시되어 있어, 전입신고는 임차인의 대항력 취득을 위한 필수 요건이자 법정 권리입니다.  

2. 임대인의 협조 의무  
임대차계약이 적법하게 체결된 이상, 임차인의 전입신고에 필요한 서류 제공은 임대인의 당연한 협조 의무에 해당됩니다.

3. 세금 문제는 별개 사안입니다  
「국세징수법」 제109조 제1항에 따르면 '주거용 건물을 임차하여 사용하려는 자는 해당 건물에 대한 임대인의 국세 체납 여부를 확인할 수 있다'고 규정하고 있으나, 이는 임차인의 권리 보호를 위한 규정이며, 임대인의 세금 문제가 임차인의 전입신고 권리를 제한하는 근거가 될 수 없습니다.

[구체적 요청사항]
1. 임대차계약서 사본 1부 교부  
2. 전입신고에 필요한 임대인 신분증 사본 제공  
3. 전입신고 절차에 대한 적극적 협조

[제안사항]
귀하께서 우려하시는 세금 문제와 관련하여서 다음과 같은 해결방안을 제안드립니다.
- 세무 전문가 상담을 통한 적법한 세무 처리 방안 모색
- 필요 시 임대소득 신고 등 정당한 절차 이행
- 상호 협의를 통한 해결책 마련

[이행 기한]
본 통지서 수령 후 7일 이내(2025년 1월 15일까지)에 상기 요청사항에 대한 이행 또는 협의 의사를 회신하여 주시기 바랍니다.

[결어]
전입신고는 임차인의 정당한 권리이며, 이를 위한 세류 제공은 임대인의 기본적인 협조 의무입니다. 귀하의 세금 문제에 대해서는 충분히 이해하며, 상호 협의를 통해 원만히 해결할 수 있을 것으로 믿습니다. 만약 정당한 사유 없이 계속해서 협조를 거부하실 경우, 부득이하게 관련 기관 신고 및 법적 조치를 검토할 수 밖에 없음을 양해해 주시기 바랍니다. 귀하의 현명한 판단과 적극적인 협조를 기대하며, 조속한 회신을 부탁드립니다.

2025년 1월 8일  
발신인: 김민준 (인)`;


const lawBody = `소득세법 시행령 제122조 제1항  
이 조문은 임대차계약 체결 시 임대인의 납세증명서 제출 의무를 규정하며,
임대인이 미납 국세 및 지방세 현황의 열람에 동의할 경우 납세증명서
제출을 생략할 수 있는 대안을 제공합니다.
이를 통해 임차인은 임대인의 세금 납부 상태를 확인할 권리를 보장받으며,
임대인에게는 절차상 편의를 제공받을 수 있습니다.`;


  return (
    <>
    <div className="flex flex-col min-h-screen bg-[#F4F4F6]">
      {/* ── 헤더 ── */}
      <div className="flex flex-col items-center pt-5">
        <StatusIcon className="mb-1" />
        <BackHeader to="main">내용증명서 생성</BackHeader>
      </div>

      {/* ── 본문 ── */}
      <main className="flex-1 flex flex-col items-center mt-6 pb-8">
        <div className="w-full bg-white rounded-t-[40px] pb-16">
          <h2 className="pt-6 pb-4 text-[1.8rem] font-extrabold text-center">
            {fakeCert.title}
          </h2>
          <hr className="border-t border-gray-200" />

          {/* 기본 정보 */}
          <section className="mt-8 px-8 space-y-6">
            <h3 className="flex items-center gap-3 text-[1.5rem] font-semibold">
              <InfoIcon width={1.6} height={1.6} color="#6000FF" />
              기본 정보
            </h3>
            <div className="grid grid-cols-[98px_1fr] pl-6 rounded-[20px] border border-gray-300">
              <Image src="/certificate.png" alt="미리보기" width={98} height={120} className="object-cover mt-7" />
              <div className="border-l ml-8 border-gray-300 flex flex-col">
                <div className="p-4 border-b border-gray-200 text-[1.25rem]">
                  <p className="font-semibold">보낸 사람</p>
                  <p className="mt-2">이름: {fakeCert.sender_name}</p>
                  <p>주소: {fakeCert.sender_address}</p>
                  <p>상세 주소: {fakeCert.sender_detail_address}</p>
                </div>
                <div className="p-4 border-b border-gray-200 text-[1.25rem]">
                  <p className="font-semibold">받는 사람</p>
                  <p className="mt-2">이름: {fakeCert.recipient_name}</p>
                  <p>주소: {fakeCert.recipient_address}</p>
                  <p>상세 주소: {fakeCert.recipient_detail_address}</p>
                </div>
                <div className="p-4 text-[1.25rem]">작성일: {fakeCert.date_created}</div>
              </div>
            </div>
          </section>

          {/* 요약 */}
          <section className="mt-10 px-8 space-y-6">
            <h3 className="flex items-center gap-3 text-[1.5rem] font-semibold">
              <AnalysisIcon width={1.6} height={1.6} color="#6000FF" />
              내용 요약
            </h3>
            <p className="border border-gray-300 p-6 rounded-[20px] leading-[1.55] text-[1.2rem] whitespace-pre-wrap">
              {fakeCert.strategy_summary}
            </p>
          </section>

          {/* AI 추천 */}
          <section className="mt-10 px-8 space-y-6">
            <h3 className="flex items-center gap-3 text-[1.5rem] font-semibold">
              <MagicTwoStarIcon width={1.6} height={1.6} color="#6000FF" />
              AI 추천
            </h3>
            <div className="flex gap-6">
              <button className="flex-1 flex flex-col items-center gap-4 py-6 rounded-[20px] border border-[#E5E5EA]">
                <Image src="/!아이콘.png" width={12} height={22} alt="" />
                <span className="text-[1.35rem] font-semibold">다음 전략</span>
              </button>
              <button className="flex-1 flex flex-col items-center gap-4 py-6 rounded-[20px] border border-[#E5E5EA]">
                <Image src="/openBook.png" width={22} height={24} alt="" />
                <span className="text-[1.35rem] font-semibold">유사 판례</span>
              </button>
            </div>
          </section>

          {/* 원본보기 → 모달 열기 */}
          <div className="px-8 mt-12">
            <SubmitButton
              width="100%"
              height={5.5}
              fontSize={1.8}
              fontWeight={600}
              onClick={() => setOpenOriginal(true)}
            >
              원본보기
            </SubmitButton>
          </div>
        </div>
      </main>
    </div>

      <Modal
        isOpen={openOriginal}
        setIsOpen={setOpenOriginal}
        clickOutsideClose={true}
        isCenter={true}

      >
    {/* 슬라이드 #0 : 원본 */}
        <div
          className="
            inline-block w-[88%] max-w-md
            h-full snap-x snap-mandatory bg-white rounded-[40px] shadow
            align-top ml-8 overflow-y-auto
            "
          >
          <div className="flex-none flex items-center justify-between px-6 py-4">
            <h3 className="text-[1.7rem] ml-50 mt-8 font-bold">내용증명서</h3>
            <Image
              src="/no.png"
              alt="닫기"
              width={24}
              height={24}
              onClick={() => setOpenOriginal(false)}
              className="mt-4 mr-8 text-gray-500 hover:text-gray-700"
              aria-label="닫기"
            />
          </div>
          {/* 본문 (스크롤 처리) */}
          <div className="px-6 py-4 mt-6 flex-1 overflow-y-auto whitespace-pre-line text-[0.95rem] leading-relaxed">
            {originalBody}
          </div>
          {/* ─── 경고 박스 ─── */}
          <div
            className="
            flex p-3
            mx-6 my-4
            w-[90%]
            bg-[#fefce8]
            rounded-[40px]
            items-center
            text-sm"
            >
            <Image
              src="/warning.png"
              alt="warningIcon"
              width={27}
              height={26}
              className="flex-shrink-0 ml-4"
            />
            <div className="ml-6">
              <span className="text-subText font-normal">
                이 문서는 법률 상담 또는 분쟁 조정을 위한 사전 통지용 문서이며,<br />
                실제 소송 등 법적 절차에 참고될 수 있습니다.
              </span>
            </div>
          </div>
        </div>
        

 {/* ── 관련 법률 모달 ── */}
       
          {/* 헤더 */}
          <div className="
            inline-block 
            w-[88%] h-full
            items-center 
            overflow-y-auto
            bg-white rounded-[40px] 
            px-6 py-4 ml-5 mr-8">
            <div className="relative flex flex-col">
              <div className="absolute mt-8 ml-45">
                <ScalesIcon width={2} height={2} color="#6000FF" />
              </div>
              <h3 className="absolute pl-2 mt-7 ml-55 text-[1.7rem] font-bold">관련 법률</h3>
              <Image
                src="/no.png"
                alt="닫기"
                width={24}
                height={24}
                onClick={() => setOpenOriginal(false)}
                className="mt-6 ml-[28rem] text-gray-500 hover:text-gray-700"
                aria-label="닫기"
              />
            </div>
          <div className="
                relative flex-1
                mt-40 rounded-[20px]
                border border-gray-100
                overflow-y-auto 
                whitespace-pre-line  
                leading-relaxed"
              >
                <div>
                  <div className="
                      border border-gray-100 
                      rounded-[20px] p-4 text-[1.2rem]
                      text-gray-500"
                  >
                    소득세법 시행령 제122조 제1항
                  </div>
                  <div className="p-5">
                    {lawBody}
                  </div>
                </div>
            </div>
                <div className="
                  relative flex-1
                  mt-20 rounded-[20px]
                  border border-gray-100
                  overflow-y-auto 
                  whitespace-pre-line  
                  leading-relaxed"
                >
                  <div>
                    <div className="
                      border border-gray-100 
                      rounded-[20px] p-4 text-[1.2rem]
                      text-gray-500"
                    >
                    조세특례제한법 시행령 제 96조 제 2항
                  </div>
                </div>
              </div>
        </div>
      </Modal>
    </>
  );
}
