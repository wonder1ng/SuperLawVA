// page.tsx
"use client";

import CheckedIcon from "@/components/icons/Checked";
import DocumentIcon from "@/components/icons/Document";
import Modal from "@/components/Modal";
import StyledDiv from "@/components/StyledDiv";
import SubmitButton from "@/components/SubmitButton";
import { useRouter } from "next/navigation";
import { useState } from "react";

function StartPage() {
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
          width={15.2}
          height={3.4}
          background="rgba(50, 215, 75, 0.2)"
          fontSize={1.2}
          fontColor="#32d74b"
          fontWeight={700}
          borderColor="none "
          icon={<DocumentIcon width={1.4} height={1.4} color="#32d74b" />}
        >
          AI로 계약서 관리하기
        </StyledDiv>
        <div className="mt-8 text-center text-[2.6rem]/[3.1rem] font-bold">
          아무개 님의 계약을
          <br />
          도와드릴게요!
        </div>
        <img
          src="/createStart.png"
          alt="Main Icon"
          className="w-[26.5rem] h-[26.5rem] mt-16"
        />
        <div className="mt-16 text-center text-[1.2rem] font-medium">
          당신의 계약서는 안전해야 하니까.
          <br />
          AI가 당신의 계약을 도와드릴게요
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
        <div className="my-16 flex flex-col gap-12">
          <div className="text-[2rem] font-bold text-center">
            추가할 부동산은 무엇인가요?
          </div>
          <ul className="flex flex-col gap-6 px-4 text-[1.8rem] font-medium">
            {["전세", "반전세, 월세"].map((option, index) => {
              return (
                <li
                  key={index}
                  onClick={() => {
                    sessionStorage.setItem("start", "true");
                    router.push(`create/step1/?rent=${Boolean(index)}`);
                  }}
                  className="flex gap-4 items-center justify-between text-[#4e4e4e]"
                >
                  <span>{option}</span>
                  <CheckedIcon width={1.4} height={1.4} color="#c4c4c5" />
                </li>
              );
            })}
          </ul>
        </div>
      </Modal>
    </>
  );
}

export default StartPage;
