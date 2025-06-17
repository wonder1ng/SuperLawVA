"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

import SubmitButton from "@/components/submitButton";
import BackHeader   from "@/components/BackHeader";
import StatusIcon   from "@/components/icons/Status";
import InfoIcon     from "@/components/icons/Info";
import AnalyzeIcon  from "@/components/icons/Analyze";
import TwoStarIcon  from "@/components/icons/TwoStar";

type Cert = {
  certification_id: number;
  title:            string;
  date_created:     string;
  recipient_name:   string;
  recipient_address:string;
  recipient_detail_address:string;
  sender_name:      string;
  sender_address:   string;
  sender_detail_address:string;
  summary_text:     string;
  strategy_summary: string | null;
  followup_strategy:string;
  thumbnail_url?:   string;  
  original_url?:    string;   
};

function ResultPage({ params }: { params: { id: string } }) {
  const router            = useRouter();
  const [data,   setData] = useState<Cert | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const controller = new AbortController();

    (async () => {
      try {
        const res = await fetch(
          `/api/certification/${params.id}`,
          { signal: controller.signal }
        );
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        const json: Cert = await res.json();
        setData(json);
      } catch (e) {
        if ((e as Error).name !== "AbortError") {
          console.error(e);
          setError("데이터를 불러오지 못했습니다.");
        }
      }
    })();

    return () => controller.abort();
  }, [params.id]);

  if (error)
    return (
      <main className="flex items-center justify-center min-h-screen">
        <p className="text-red-500">{error}</p>
      </main>
    );

  if (!data) return null; 

  const {
    title, date_created, recipient_name, recipient_address,
    recipient_detail_address, sender_name, sender_address,
    sender_detail_address, summary_text, thumbnail_url, original_url
  } = data;

  return (
    <>
      <div className="flex flex-col items-center">
        <StatusIcon className="mt-5" />
      </div>
      <div className="mt-5">
        <BackHeader>내용증명서 생성</BackHeader>
      </div>

      <main className="flex flex-col items-center min-h-screen mt-5 pb-10">
        <div className="w-full bg-white rounded-t-[40px]">
          <h2 className="py-5 text-[1.6rem] font-extrabold text-center">{title}</h2>
          <hr className="border-t border-gray-200" />

          <section className="mt-7 px-8">
            <h3 className="flex items-center gap-3 text-[1.5rem] font-semibold mb-6">
              <InfoIcon width={1.6} height={1.6} color="#6000FF" /> 기본 정보
            </h3>

            <div className="flex border border-gray-200 rounded-[20px]">
              <div className="flex-1 flex items-center justify-center p-4">
                {thumbnail_url ? (
                  <Image
                    src={thumbnail_url}
                    alt="내용증명서 미리보기"
                    width={160}
                    height={220}
                    className="object-contain rounded-[12px]"
                  />
                ) : (
                  <div className="w-[160px] h-[220px] bg-gray-100 rounded-[12px]" />
                )}
              </div>
              <div className="w-px bg-gray-200 my-0" />

              {/* 정보 블록 */}
              <div className="flex-1 text-[1.24rem] leading-snug">
                {/* 보낸 사람 */}
                <div className="px-4 pt-4 pb-4 space-y-1">
                  <p className="font-semibold">보낸 사람</p>
                  <p className="mt-3">이름&nbsp;:&nbsp;{sender_name}</p>
                  <p>주소&nbsp;:&nbsp;{sender_address}</p>
                  <p>상세 주소&nbsp;:&nbsp;{sender_detail_address}</p>
                </div>
                <hr className="border-t border-gray-200" />

                {/* 받는 사람 */}
                <div className="px-4 pt-4 pb-4 space-y-1">
                  <p className="font-semibold">받는 사람</p>
                  <p className="mt-3">이름&nbsp;:&nbsp;{recipient_name}</p>
                  <p>주소&nbsp;:&nbsp;{recipient_address}</p>
                  <p>상세 주소&nbsp;:&nbsp;{recipient_detail_address}</p>
                </div>
                <hr className="border-t border-gray-200" />

                {/* 작성일 */}
                <div className="px-4 py-4">
                  작성일&nbsp;:&nbsp;{date_created}
                </div>
              </div>
            </div>
          </section>

          {/* ===== 내용 요약 ===== */}
          <section className="mt-8 px-8">
            <h3 className="flex items-center gap-3 text-[1.5rem] font-semibold mb-6">
              <AnalyzeIcon width={1.6} height={1.6} color="#6000FF" /> 내용 요약
            </h3>
            <p className="border border-gray-200 rounded-[20px] p-6 text-[1.2rem] leading-[1.3] whitespace-pre-wrap">
              {summary_text}
            </p>
          </section>
          {/* ===== AI 추천 ===== */}
          <section className="mt-8 px-8">
            <h3 className="flex items-center gap-3 text-[1.5rem] font-semibold mb-6">
              <TwoStarIcon width={1.6} height={1.6} color="#6000FF" /> AI 추천
            </h3>
            <div className="flex gap-6">
              {/* 다음 전략 */}
              <button
                className="flex-1 flex flex-col items-center gap-4 py-6 rounded-[20px] border border-[#E5E5EA]"
              >
                <Image src="/!아이콘.png" alt="" width={12} height={22} />
                <span className="text-[1.35rem] font-semibold">다음 전략</span>
              </button>

              {/* 유사 판례 */}
              <button
                className="flex-1 flex flex-col items-center gap-4 py-6 rounded-[20px] border border-[#E5E5EA]"
              >
                <Image src="/openBook.png" alt="" width={22} height={28} />
                <span className="text-[1.35rem] font-semibold">유사 판례</span>
              </button>
            </div>
          </section>

          {/* 원본보기 버튼 */}
          <div className="mt-14 px-8">
            <SubmitButton
              width="100%"
              height={5.5}
              fontSize={1.8}
              fontWeight={600}
              onClick={() =>
                original_url
                  ? window.open(original_url, "_blank")
                  : router.push(`/main/certificate/original/${data.certification_id}`)
              }
            >
              원본보기
            </SubmitButton>
          </div>
        </div>
      </main>
    </>
  );
}

export default ResultPage;