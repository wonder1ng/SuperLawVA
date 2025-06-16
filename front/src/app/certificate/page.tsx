// page.tsx
"use client";

import DocumentIcon from "@/components/icons/Document";
import InfoIcon from "@/components/icons/Info";
import TwoStarIcon from "@/components/icons/TwoStar";
import Modal from "@/components/Modal";
import SubmitButton from "@/components/SubmitButton";
import { useRouter } from "next/navigation";
import { useState } from "react";
import Image from "next/image";

function StartPage() {
  const router = useRouter();
  const [modalOpen, setModalOpen] = useState(false);

  return (
    <>
      <main className="flex flex-col items-center h-full bg-white">
        <div className="h-52 w-full" />
        <SubmitButton
          width={17}
          height={3.2}
          background="rgba(255, 69, 58, 0.2)"
          fontSize={1.2}
          fontWeight={700}
          icon={<InfoIcon color="red" width={1.2} height={1.2} />}
        >
          <span className="text-red-500">AI로 내용증명서 생성하기</span>
        </SubmitButton>
        <div className="mt-8 text-center text-[2.6rem]/[3.1rem] font-bold">
          상황에 맞는 <span className="text-good">내용증명서</span>를
          <br />
          자동으로 생성하세요
        </div>
        <Image
          src="/ai_document.png"
          alt="vector Icon"
          width={250}
          height={250}
          className="mt-16"
        />
        <div className="text-[#9ca3af] mt-16 text-center text-[1.3rem] font-bold">
          AI가 귀하의 상황을 분석하여 법적 효력이 있는
          <br />
          내용증명서를 자동 작성합니다.
        </div>
        <SubmitButton
          width={26}
          height={5.5}
          fontSize={1.8}
          className="mt-16 flex items-center justify-center gap-x-2 whitespace-nowarp"
          onClick={() => setModalOpen(true)}
          icon={<TwoStarIcon width={2.4} height={2.4} color="#FFFFFF" />}
        >
          생성하기
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
            업로드하는 파일이 맞으신가요?
          </div>
          <div className="flex flex-col gap-8 p-8 justify-center items-center w-full border-[1.5px] border-[#c6c6c8] rounded-[20px]">
            <DocumentIcon color="#6000ff" />
            <span className="text-[1.6rem] font-medium">
              부동산임대차 계약서.pdf
            </span>
          </div>
          <div className="flex font-bold px-8">
            <span className="flex-1 text-[1.6rem] text-[#2b2b2b]">
              계약 유형
            </span>
            <span className="flex-1 text-[1.4rem] text-[#5c5c5c]">
              부동산(전세) 계약서
            </span>
          </div>
          <div className="flex font-bold px-8">
            <span className="flex-1 text-[1.6rem] text-[#2b2b2b]">
              계약 일자
            </span>
            <span className="flex-1 text-[1.4rem] text-[#5c5c5c]">
              2018.08.28.
            </span>
          </div>
          <div className="flex font-bold px-8">
            <span className="flex-1 text-[1.6rem] text-[#2b2b2b]">
              건물 유형
            </span>
            <span className="flex-1 text-[1.4rem] text-[#5c5c5c]">
              오피스텔
            </span>
          </div>
          <div className="flex w-full gap-8 justify-between">
            <SubmitButton
              width={16}
              height={5}
              fontSize={1.6}
              fontWeight={500}
              fontColor="#1e1e1e"
              background="white"
              borderColor="#5c5c5c"
              onClick={() => {
                setModalOpen(false);
                router.push("/main/certificate");
              }}
            >
              다시 업로드
            </SubmitButton>
            <SubmitButton
              width={16}
              height={5}
              fontSize={1.6}
              fontWeight={500}
              onClick={() => {
                setModalOpen(false);
                router.push("certificate/starting");
              }}
            >
              네, 맞아요
            </SubmitButton>
          </div>
        </div>
      </Modal>
    </>
  );
}

export default StartPage;
