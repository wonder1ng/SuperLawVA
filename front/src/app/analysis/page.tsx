// page.tsx
"use client";

import CheckedIcon from "@/components/icons/Checked";
import DocumentIcon from "@/components/icons/Document";
import MagicTwoStarIcon from "@/components/icons/MagicTwoStar";
import Modal from "@/components/Modal";
import StyledDiv from "@/components/StyledDiv";
import SubmitButton from "@/components/SubmitButton";
import { useRouter } from "next/navigation";
import { useState } from "react";

function AnalysisPage() {
  const router = useRouter();
  const [modalOpen, setModalOpen] = useState(false);

  const [selected, setSelected] = useState<number | null>(null);
  const handleSelect = (index: number) => {
    setSelected((prev) => (prev === index ? null : index));
  };

  return (
    <>
      <main className="flex flex-col items-center h-full bg-white">
        <div className="h-52 w-full" />
        <StyledDiv
          width="auto"
          height={3.5}
          background="rgba(10, 132, 255, 0.2)"
          fontSize={1.2}
          fontColor="#0A84FF"
          fontWeight={700}
          borderColor="none"
          className="px-10 flex justify-center items-center "
          icon={<MagicTwoStarIcon width={1.4} height={1.4} color="#0A84FF" />}
        >
          AI로 계약서 분석하기
        </StyledDiv>
        <div className="mt-8 text-center text-[2.6rem]/[3.1rem] font-bold">
          AI 분석으로
          <br />
          분쟁을 미리 예방하세요
        </div>
        <img
          src="/analysisStart.png"
          alt="Main Icon"
          className="w-[26.5rem] h-[26.5rem] mt-16"
        />
        <div className="mt-16 text-center text-[1.2rem] font-medium">
          법령 10만 건, 판례 9만 건 기반 AI가
          <br />
          당신의 계약서 위험 사항을 감지해드리겠습니다.
        </div>
        <SubmitButton
          width={26}
          height={5.5}
          fontSize={1.8}
          className="mt-16"
          onClick={() => setModalOpen(true)}
        >
          시작하기
        </SubmitButton>
        <div
          onClick={() => router.back()}
          className="mt-8 text-[#797979] text-[1.4rem] font-medium"
        >
          ← 다음에 할래요
        </div>
      </main>
      <Modal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        clickOutsideClose={true}
      >
        <div className="w-full p-16 flex flex-col gap-12">
          <div className="text-[2rem] font-bold text-center">
            분석할 계약서를 확인해주세요
          </div>
          <div className="flex flex-col gap-8 p-8 justify-center items-center w-full border-[1.5px] border-[#c6c6c8] rounded-[20px]">
            <DocumentIcon color="#6000ff" />
            <span className="text-[1.6rem] font-medium">
              부동산임대차 계약서.pdf
            </span>
          </div>
          <ul className="w-full px-8 flex flex-col gap-12 items-center text-[#2b2b2b] text-[1.6rem] font-bold">
            {[
              ["계약 유형", "부동산(전세)계약서"],
              ["계약 일자", "2018.08.28."],
              ["건물 유형", "오피스텔"],
            ].map(([value, detail], index) => (
              <li
                key={index}
                className="w-full flex justify-between items-center"
              >
                <span className="flex-1">{value}</span>
                <span className="flex-1 text-[1.4rem] text-[#5c5c5c]">
                  {detail}
                </span>
              </li>
            ))}
          </ul>
          <div className="flex w-full gap-8">
            <SubmitButton
              height={5}
              fontSize={1.6}
              fontWeight={500}
              fontColor="#1e1e1e"
              background="white"
              borderColor="#5c5c5c"
            >
              다시 업로드
            </SubmitButton>
            <SubmitButton
              height={5}
              fontSize={1.6}
              fontWeight={500}
              onClick={() => router.push("analysis/result")}
            >
              네, 맞아요
            </SubmitButton>
          </div>
        </div>
      </Modal>
    </>
  );
}

export default AnalysisPage;
