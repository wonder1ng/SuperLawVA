"use client";

import { useRouter } from "next/navigation";
import SubmitButton from "@/components/SubmitButton";
import StatusIcon from "@/components/icons/Status";
import { useEffect, useRef, useState } from "react";
import BackHeader from "@/components/BackHeader";
import StyledInput from "@/components/StyledInput";
import Modal from "@/components/Modal";
import ArrowLeftIcon from "@/components/icons/ArrowLeft";
import ArrowRightIcon from "@/components/icons/ArrowRight";
import StyledDiv from "@/components/StyledDiv";
import WarningIcon from "@/components/icons/Warning";
import CheckedIcon from "@/components/icons/Checked";
import MagicTwoStarIcon from "@/components/icons/MagicTwoStar";
import CrossIcon from "@/components/icons/Cross";

function ContractCreateNewPage() {
  const router = useRouter();
  const [inputValue, setInputValue] = useState<string>("");
  const [valueArray, setValueArray] = useState<string[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const inputRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    if (modalOpen && inputRef.current) {
      inputRef.current.focus();
    }
  }, [modalOpen, valueArray]);

  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="mt-[1.4rem]" />
      </div>
      <BackHeader>임대차 계약서 작성</BackHeader>
      <main className="flex flex-col items-center mt-[3rem] gap-12 h-[calc(100%-11rem)]">
        <div>
          <div className="flex justify-center items-center">
            <span className="w-full text-[2.4rem] font-bold">
              말로만 한 약속은 없던 일이 돼요.
              <br />
              <span className="text-[rgba(96,0,255,0.7)]">특약</span>
              으로 확실하게&nbsp;
              <span className="text-[rgba(225,0,255,0.7)]">보장</span>
              받으세요.
            </span>
          </div>
          <span className="flex justify-self-end self-end text-[1rem] font-medium text-[rgba(128,128,128,0.55)]">
            특약: 기본 계약서에 없는 추가 약속
          </span>
        </div>
        <div className="h-full flex flex-col items-center w-full gap-4 text-[1.6rem] font-bold ">
          4. 특약 사항
          <div className="px-10 py-12 w-full flex-1 bg-white rounded-t-[50px] backdrop-opacity-70 flex flex-col gap-12 items-center text-[1.8rem] font-semibold">
            <div className="w-full flex flex-col justify-center items-center gap-4">
              당신의 계약은 안전해야 하니까
              <StyledDiv
                width="100%"
                background="white"
                borderColor="#f3f4f6"
                fontSize={1.4}
                fontWeight={700}
                fontColor="black"
                gap={0.5}
                className="flex flex-col justify-center py-6 px-12 h-24"
              >
                기본 특약
                <span className="text-[1rem] font-normal opacity-60">
                  기본적으로 계약서에 들어가는 특약입니다.
                </span>
              </StyledDiv>
            </div>
            <ul className="w-full flex flex-col justify-center items-center gap-4">
              당신의 니즈를 잊지 않도록
              {valueArray.length
                ? valueArray.map((value, index) => (
                    <li
                      key={index}
                      className="w-full h-20 px-12 flex justify-between items-center text-[1.4rem] text-[#3a3a40] font-medium border border-[#d7d7d7] rounded-[50px] bg-white"
                    >
                      <div className="flex gap-4">
                        <span className="w-[1.6rem] h-[1.6rem] flex justify-center items-center bg-main text-white rounded-[50px] text-[1rem]">
                          {index + 1}
                        </span>
                        {value}
                      </div>
                      <div
                        onClick={() =>
                          setValueArray(valueArray.filter((v, i) => i != index))
                        }
                      >
                        <CrossIcon />
                      </div>
                    </li>
                  ))
                : ""}
              <SubmitButton
                width="100%"
                height={5}
                background="white"
                borderColor="#5046E5"
                fontSize={1.4}
                fontWeight={700}
                fontColor="#5046E5"
                gap={0.5}
                className="flex flex-col justify-center items-start py-6 px-12"
                onClick={() => setModalOpen(true)}
              >
                + 추가하기
                <span className="text-[1rem] font-normal">
                  ex&#41; 고양이 키우고 싶어요, 주차 공간이 필요해요
                </span>
              </SubmitButton>
              {valueArray.length === 0 ? (
                <span className="text-[10px] font-normal bg-gradient-to-br from-[rgba(96,0,255,0.7)] to-[rgba(225,0,255,0.7)] bg-clip-text text-transparent">
                  위 버튼을 눌러 요구사항을 입력해 보세요!
                </span>
              ) : (
                ""
              )}
            </ul>
            <SubmitButton
              width="100%"
              height={5.5}
              fontSize={1.8}
              fontWeight={500}
              disabled={valueArray.length === 0}
              className="flex justify-center items-center mb-12 mt-auto"
              icon={<MagicTwoStarIcon />}
              onClick={() => router.push("step4")}
            >
              생성하기
            </SubmitButton>
          </div>
        </div>
      </main>
      <Modal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        clickOutsideClose={true}
        upperChildren={
          <ul
            className={`absolute bottom-[29rem] w-full flex flex-col justify-center items-center gap-4`}
          >
            {valueArray.length
              ? valueArray.map((value, index) => (
                  <li
                    key={index}
                    className="w-[calc(100%-5rem)] h-20 px-12 flex justify-between items-center text-[1.4rem] text-[#3a3a40] font-medium border border-[#d7d7d7] rounded-[50px] bg-white"
                  >
                    <div className="flex gap-4">
                      <span className="w-[1.6rem] h-[1.6rem] flex justify-center items-center bg-main text-white rounded-[50px] text-[1rem]">
                        {index + 1}
                      </span>
                      {value}
                    </div>
                    <div
                      onClick={() =>
                        setValueArray(valueArray.filter((v, i) => i != index))
                      }
                    >
                      <CrossIcon />
                    </div>
                  </li>
                ))
              : ""}
          </ul>
        }
      >
        <div className="mt-16 mb-4 px-8 w-full flex flex-col gap-4">
          <div className="flex flex-col items-center gap-4 mb-8">
            <span className="text-[2rem] font-bold text-center">
              당신의 요구사항을 입력하세요
            </span>
            <span className="text-main text-[1.2rem] font-semibold">
              특약 추가하기
            </span>
          </div>
          <div className="w-full h-20 bg-white border border-[#d7d7d7] rounded-[50px]">
            <input
              type="text"
              name=""
              id=""
              ref={inputRef}
              placeholder="ex) 고양이 키우고 싶어오, 주차 공간이 필요해요"
              onChange={(e) => setInputValue(e.target.value)}
              value={inputValue}
              className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
            />
          </div>
          <button
            className={`flex items-center justify-center gap-4 text-[1.4rem] font-medium${
              valueArray.length === 0
                ? " text-[rgba(128,128,128,0.55)] cursor-not-allowed pointer-events-none"
                : " text-main"
            }`}
            onClick={() => setModalOpen(false)}
          >
            <CheckedIcon
              width={1.6}
              height={1.6}
              color={
                valueArray.length === 0 ? "rgba(128,128,128,0.55)" : "#6000ff"
              }
            />
            완료
          </button>
        </div>
        <SubmitButton
          className="justify-self-end"
          width="100%"
          height={6}
          fontSize={1.8}
          fontWeight={500}
          disabled={inputValue === ""}
          borderRadius="none"
          onClick={() => {
            setValueArray([...valueArray, inputValue]);
            setInputValue("");
          }}
        >
          + 추가하기
        </SubmitButton>
      </Modal>
    </>
  );
}

export default ContractCreateNewPage;
