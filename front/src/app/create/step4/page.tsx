"use client";

import { useRouter } from "next/navigation";
import StatusIcon from "@/components/icons/Status";
import { useEffect, useRef, useState } from "react";
import BackHeader from "@/components/BackHeader";
import ArrowLeftIcon from "@/components/icons/ArrowLeft";
import ArrowRightIcon from "@/components/icons/ArrowRight";
import StyledDiv from "@/components/StyledDiv";
import WarningIcon from "@/components/icons/Warning";
import MagicTwoStarIcon from "@/components/icons/MagicTwoStar";
import MagnifyingGlassIcon from "@/components/icons/MagnifyingGlass";
import BulbIcon from "@/components/icons/Bulb";
import ExclamationIcon from "@/components/icons/Exclamation";
import ArrowDownIcon from "@/components/icons/ArrowDownIcon";
import DivBox from "@/components/DivBox";

function ContractCreateNewPage() {
  const router = useRouter();
  const [inputValue, setInputValue] = useState<string>("");
  // const [valueArray, setValueArray] = useState<string[]>([]);
  const [valueArray, setValueArray] = useState<string[]>([
    "집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요",
    "집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요",
    "벽에 선반 달고 싶어요",
    "집에서 담배 피고 싶어요",
    "집에서 친구랑 동거하고 싶어요",
    "집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요집에서 친구랑 동거하고 싶어요",
  ]);
  const [modalOpen, setModalOpen] = useState(false);
  const inputRef = useRef<HTMLInputElement | null>(null);

  const [activeIndex, setActiveIndex] = useState<number>(0);

  const liArray = valueArray.map((value, index) => (
    <li
      key={index}
      className="w-full h-20 px-10 py-6  flex items-center text-[1.4rem] text-[#3a3a40] font-medium border border-[#d7d7d7] rounded-[50px] bg-white"
    >
      <div className="w-8 flex justify-center items-center flex-shrink-0">
        {activeIndex === 0 ? (
          <ArrowLeftIcon
            color="white"
            className="cursor-not-allowed pointer-events-none"
          />
        ) : (
          <ArrowLeftIcon
            width={1.5}
            height={1.5}
            onClick={() => setActiveIndex(activeIndex - 1)}
            className="z-10 cursor-pointer"
          />
        )}
      </div>
      <div className="flex items-center gap-4 px-4 flex-grow overflow-hidden">
        <span className="w-[1.6rem] h-[1.6rem] flex justify-center items-center bg-main text-white rounded-[50px] text-[1rem] flex-shrink-0">
          {index + 1}
        </span>
        <span className="truncate">{value}</span>
      </div>
      <div className="w-8 flex justify-center items-center flex-shrink-0">
        {activeIndex === valueArray.length - 1 ? (
          <ArrowRightIcon
            color="white"
            className="cursor-not-allowed pointer-events-none"
          />
        ) : (
          <ArrowRightIcon
            width={1.5}
            height={1.5}
            onClick={() => setActiveIndex(activeIndex + 1)}
            className="z-10 cursor-pointer"
          />
        )}
      </div>
    </li>
  ));

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
      <BackHeader to="/">임대차 계약서 작성</BackHeader>
      <main className="flex flex-col items-center mt-[3rem] gap-12 h-[calc(100%-11rem)]">
        <div className="px-8 py-12 w-full flex-1 bg-white rounded-t-[50px] backdrop-opacity-70 flex flex-col gap-12 items-center">
          <div className="w-full flex flex-col gap-4 items-start">
            <span className="px-8 flex gap-4 items-center text-[1.8rem] text-[#0f0f0f] font-semibold">
              <MagnifyingGlassIcon />
              실현 가능성
            </span>
            <div className="flex gap-4 w-full">
              <div className="px-4 py-6 min-w-28 flex flex-col items-center justify-center bg-white border border-[#f3f4f6] rounded-[20px] text-[1rem] font-semibold text-[#32d74b]">
                <span className="w-[1.6rem] h-[1.6rem] bg-[#32d74b] rounded-full" />
                가능성 높음
              </div>
              <DivBox className="flex-grow flex items-center px-10 py-6">
                생활 필수품 설치로 대부분 임대인이 수용, 법적으로 안전
              </DivBox>
            </div>
          </div>
          <div className="w-full flex flex-col gap-4 items-start">
            <span className="px-8 flex gap-4 items-center text-[1.8rem] text-[#0f0f0f] font-semibold">
              <MagicTwoStarIcon width={1.6} height={1.6} color="#6000FF" />
              생성된 특약
            </span>
            <DivBox className="flex items-center px-10 py-6">
              안전임차인은 벽면에 못을 사용하여 생활용품을 설치할 수 있으며,
              퇴거 시 직경 5mm 이하 못구멍에 대한 원상복구비는 청구하지 않음
            </DivBox>
          </div>
          <div className="w-full flex flex-col gap-4 items-start">
            <span className="px-8 flex gap-4 items-center text-[1.8rem] text-[#0f0f0f] font-semibold">
              <BulbIcon color="#6000FF" />
              생성된 특약
            </span>
            <DivBox className="flex items-center gap-2 px-10 py-6 w-full">
              <span className="font-semibold">협상 포인트:</span>
              "생활하려면 최소한 시계나 액자는 걸어야죠"
            </DivBox>
            <DivBox className="flex items-center gap-2 px-10 py-6 w-full">
              <span className="font-semibold">주의사항:</span>
              못구멍 크기를 구체적으로 명시해야 분쟁 방지
            </DivBox>
            <DivBox className="flex items-center gap-2 px-10 py-6 w-full">
              <span className="font-semibold">실무 팁:</span>
              계약 전 벽면 상태 사진 촬영 필수
            </DivBox>
          </div>
          <div className="w-full flex flex-col gap-4 items-start">
            <span className="px-8 flex gap-4 items-center text-[1.8rem] text-[#0f0f0f] font-semibold">
              <ExclamationIcon color="#6000FF" />
              참고한 법령
            </span>
            <DivBox className="flex justify-between items-center gap-2 px-10 py-6 w-full">
              소득세법 시행령 제122조 제1항
              <ArrowDownIcon />
            </DivBox>
            <DivBox className="flex justify-between items-center gap-2 px-10 py-6 w-full">
              조세특례제한법 시행령 제 96조 제 2항
              <ArrowDownIcon />
            </DivBox>
          </div>
          <div className="w-full flex flex-col gap-4 items-start">
            <span className="px-8 flex gap-4 items-center text-[1.8rem] text-[#0f0f0f] font-semibold">
              <ExclamationIcon color="#6000FF" />
              참고한 판례
            </span>
            <DivBox className="flex justify-between items-center gap-2 px-10 py-6 w-full">
              서울중앙법 2029가합18
              <ArrowDownIcon />
            </DivBox>
            <DivBox className="flex justify-between items-center gap-2 px-10 py-6 w-full">
              부산지법 181가합18
              <ArrowDownIcon />
            </DivBox>
            <DivBox className="flex justify-between items-center gap-2 px-10 py-6 w-full">
              와우 친구들 집 가고 싶다
              <ArrowDownIcon />
            </DivBox>
          </div>
          <StyledDiv
            width="100%"
            height={4.5}
            background="#fefce8"
            borderColor="#fef9c3"
            className="flex items-center px-8 mx-96"
            icon={
              <div className="w-12 h-12 bg-yellow rounded-full flex justify-center items-center">
                <WarningIcon />
              </div>
            }
          >
            <div className="flex flex-col justify-center text-[1.2rem] font-semibold text-black">
              경고
              <span className="text-[1rem] font-medium text-[#9ca3af]">
                계약서 작성 전에 계약 상대방과 상의하시길 바랍니다.
              </span>
            </div>
          </StyledDiv>
        </div>
        <DivBox className="mb-6 w-full flex flex-col gap-12">
          <div className="flex w-full justify-between items-center">
            {liArray[activeIndex]}
          </div>
        </DivBox>
      </main>
    </>
  );
}

export default ContractCreateNewPage;
