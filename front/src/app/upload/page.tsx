// page.tsx
"use client";

import CameraIcon from "@/components/icons/Camera";
import CheckedIcon from "@/components/icons/Checked";
import DocumentIcon from "@/components/icons/Document";
import MagicThreeStarIcon from "@/components/icons/MagicThreeStar";
import PictureIcon from "@/components/icons/Picture";
import Modal from "@/components/Modal";
import StyledDiv from "@/components/StyledDiv";
import SubmitButton from "@/components/SubmitButton";
import { useRouter } from "next/navigation";
import { useState } from "react";

function StartPage() {
  const router = useRouter();
  const [modalOpen, setModalOpen] = useState(false);
  const [step, setStep] = useState<number>(0);

  const handleClick = () => {
    router.push("/login");
  };
  return (
    <>
      <main className="flex flex-col items-center h-full bg-white">
        <div className="h-52 w-full" />
        <StyledDiv
          width={15.2}
          height={3.4}
          background="#faf5ff"
          fontSize={1.2}
          fontWeight={700}
          icon={<MagicThreeStarIcon />}
        >
          <span className="text-good">AI로 계약서 관리하기</span>
        </StyledDiv>
        <div className="mt-8 text-center text-[2.6rem]/[3.1rem] font-bold">
          아무개 님의 문서는
          <br />
          소중하니까
        </div>
        <img
          src="/uploadPage.png"
          alt="Main Icon"
          className="w-[26.5rem] h-[26.5rem] mt-16"
        />
        <div className="mt-16 text-center text-[1.2rem] font-medium">
          당신의 계약서는 안전해야 하니까.
          <br />
          AI가 당신의 계약서를 관리해 드릴게요
        </div>
        <SubmitButton
          width={26}
          height={5.5}
          fontSize={1.8}
          className="mt-16"
          onClick={() => setModalOpen(true)}
        >
          업로드 하기
        </SubmitButton>
      </main>

      {/* 1번 모달 */}
      <Modal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        clickOutsideClose={true}
      >
        {step === 0 && (
          <div className="my-16 flex flex-col gap-12">
            <div className="text-[2rem] font-bold text-center">
              어떤 방식으로 업로드하시겠어요?
            </div>
            <ul
              onClick={() => setStep(1)}
              className="flex flex-col gap-6 px-4 text-[1.8rem] text-[#4e4e4e] font-medium justar"
            >
              <li className="flex gap-4 items-center">
                <DocumentIcon color="#6000ff" />
                <span>파일로 업로드</span>
              </li>
              <li className="flex gap-4 items-center">
                <PictureIcon />
                <span>사진으로 업로드</span>
              </li>
              <li className="flex gap-4 items-center">
                <CameraIcon />
                <span>카메라로 업로드</span>
              </li>
            </ul>
          </div>
        )}
        {step === 1 && (
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
              >
                다시 업로드
              </SubmitButton>
              <SubmitButton
                width={16}
                height={5}
                fontSize={1.6}
                fontWeight={500}
                onClick={() => setStep(2)}
              >
                네, 맞아요
              </SubmitButton>
            </div>
          </div>
        )}
        {step === 2 && (
          <div className="w-full p-16 flex flex-col gap-12">
            <div className="text-[2rem] font-bold text-center">
              계약서 업로드가 완료되었습니다!
            </div>
            <div className="flex justify-center items-center">
              <CheckedIcon width={5} height={5} color="#32d74b" />
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
              >
                홈 화면으로
              </SubmitButton>
              <SubmitButton
                width={16}
                height={5}
                fontSize={1.6}
                fontWeight={500}
                onClick={() => router.push("contract")}
              >
                계약서 확인
              </SubmitButton>
            </div>
          </div>
        )}
      </Modal>
    </>
  );
}

export default StartPage;
