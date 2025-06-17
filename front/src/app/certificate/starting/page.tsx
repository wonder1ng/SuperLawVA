// page.tsx
"use client";

import SubmitButton from "@/components/SubmitButton";
import StatusIcon from "@/components/icons/Status";
import { useState } from "react";
import Modal from "@/components/Modal";
import BackHeader from "@/components/BackHeader";
import AnalyzeIcon from "@/components/icons/Analysis";
import MagicTwoStarIcon from "@/components/icons/MagicTwoStar";
import InfoIcon from "@/components/icons/Info";
import { useRouter } from "next/navigation";

function StartPage() {
  const router = useRouter();
  const [purpose, setPurpose] = useState<string>("");
  const [story, setStory] = useState<string>("");
  const maxLenPurpose = 200;
  const maxLenStory = 1000;

  const [purposeOpen, setPurposeOpen] = useState(false);
  const [storyOpen, setStoryOpen] = useState(false);

  const canSubmit = purpose.trim().length > 0 && story.trim().length > 0;

  const handleClick = () => {
    if (!canSubmit) return;
    router.push("loading");
  };

  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="mt-[1.4rem]" />
      </div>
      <BackHeader>내용증명서</BackHeader>
      <main className="flex flex-col mt-[3.6rem] w-full">
        <div className="flex flex-col text-center font-sans">
          <div className="font-bold text-[2.2rem]">
            <span
              className="
              bg-gradient-to-r 
              from-[#6000FF] 
              via-[#E100FF] 
              to-[#E100FF] 
              bg-clip-text text-transparent"
            >
              법·판례&nbsp;
            </span>
            기반으로
          </div>
          <div className="font-bold text-[2.2rem]">
            당신의 내용증명서를 설득력있게
          </div>
          <div className="mt-2 flex flex-col text-[1.1rem]">
            <span className="font-medium text-subText">
              내용증명: 어떤 내용을 보냈는지 우체국이 공식적으로 증명해 주는
              편지
            </span>
          </div>
          <div className="w-full h-243 bg-white rounded-t-[40px] mt-15">
            <div className="mx-10 mt-15 flex flex-col gap-4 text-[1.7rem]">
              <span className="ml-5 flex items-center gap-3 font-bold">
                <AnalyzeIcon width={1.6} height={1.6} color="#6000FF" />
                작성하시는 목적을 간단하게 입력해주세요
              </span>
              <div className="relative">
                <textarea
                  className="w-full h-30 rounded-[20px] border border-[#eeeeee] bg-[#fafafa]
                    p-6 text-[1.4rem] resize-none outline-none placeholder:text-gray-400"
                  placeholder="ex) 보증금을 돌려받고 싶습니다."
                  value={purpose}
                  readOnly
                  onClick={() => {
                    setPurposeOpen(true);
                  }}
                  maxLength={maxLenPurpose}
                />
                <span className="absolute bottom-6 right-5 text-[1rem] text-gray-400">
                  {purpose.length}/{maxLenPurpose}
                </span>
              </div>
            </div>
            <div className="mx-10 mt-15 flex flex-col gap-4 text-[1.7rem]">
              <span className="ml-5 flex items-center gap-3 font-bold">
                <InfoIcon width={1.6} height={1.6} color="#6000FF" />
                당신의 상황을 상세히 이야기 해주세요
              </span>
              <div className="relative">
                <textarea
                  className="w-full h-85 rounded-[20px] border border-[#eeeeee] bg-[#fafafa]
                    p-6 text-[1rem] resize-none outline-none placeholder:text-gray-400"
                  placeholder="ex) 계약 종료일이 다가오는데 집주인이 보증금 반환에 대해 아무런 언급이 없습니다.
                    연락도 잘 되지 않아 불안한 상황입니다."
                  value={story}
                  readOnly
                  onClick={() => {
                    setStoryOpen(true);
                  }}
                  maxLength={maxLenStory}
                />
                <span className="absolute bottom-6 right-5 text-[1rem] text-gray-400">
                  {story.length}/{maxLenStory}
                </span>
              </div>
            </div>
            <div
              className="mt-20 text-[1.1rem]             
                bg-gradient-to-r 
                from-[#6000FF] 
                via-[#E100FF] 
                to-[#E100FF] 
                bg-clip-text text-transparent"
            >
              Tip: 자세히 입력할수록 완성도 있는 내용증명서가 작성됩니다.
            </div>
            <div className="flex justify-center">
              <SubmitButton
                width={30}
                height={5.5}
                fontSize={1.8}
                className={`mt-3 flex items-center justify-center gap-x-2 whitespace-nowrap ${
                  !canSubmit ? "opacity-40 cursor-not-allowed" : ""
                }`}
                disabled={!canSubmit}
                onClick={handleClick}
                icon={
                  <MagicTwoStarIcon width={2.4} height={2.4} color="#FFFFFF" />
                }
              >
                생성하기
              </SubmitButton>
            </div>
          </div>
        </div>
      </main>

      <Modal
        isOpen={purposeOpen}
        setIsOpen={setPurposeOpen}
        clickOutsideClose={true}
      >
        <div className="mx-10 mt-10 flex flex-col gap-4 text-[1.7rem]">
          <span className="ml-5 flex items-center gap-3 font-bold">
            <AnalyzeIcon width={1.6} height={1.6} color="#6000FF" />
            작성하시는 목적을 간단하게 입력해주세요
          </span>
          <div className="relative">
            <textarea
              className="w-full h-30 rounded-[20px] border border-[#eeeeee] bg-[#fafafa]
                   p-6 text-[1.4rem] resize-none outline-none placeholder:text-gray-400"
              placeholder="ex) 보증금을 돌려받고 싶습니다."
              value={purpose}
              onChange={(e) => setPurpose(e.target.value)}
              maxLength={maxLenPurpose}
              autoFocus
            />
            <span className="absolute bottom-6 right-5 text-[1rem] text-gray-400">
              {purpose.length}/{maxLenPurpose}
            </span>
          </div>
        </div>
        <div className="text-[1.8rem] font-bold">
          <button
            disabled={purpose.trim() === ""}
            className={`w-[399px] h-[60px]
                ${
                  purpose.trim() === ""
                    ? "bg-gray-300 text-white"
                    : "bg-main text-white"
                }`}
            onClick={() => setPurposeOpen(false)}
          >
            작성하기
          </button>
        </div>
      </Modal>

      <Modal
        isOpen={storyOpen}
        setIsOpen={setStoryOpen}
        clickOutsideClose={true}
      >
        <div className="mx-10 mt-8 flex flex-col gap-4 text-[1.7rem]">
          <span className="ml-5 flex items-center gap-3 font-bold">
            <InfoIcon width={1.6} height={1.6} color="#6000FF" />
            당신의 상황을 상세히 이야기 해주세요
          </span>
          <div className="relative">
            <textarea
              className="w-full h-70 rounded-[20px] border border-[#eeeeee] bg-[#fafafa]
                  p-6 text-[1rem] resize-none outline-none placeholder:text-gray-400"
              placeholder="ex) 계약 종료일이 다가오는데 집주인이 보증금 반환에 대해 아무런 언급이 없습니다.
                  연락도 잘 되지 않아 불안한 상황입니다."
              value={story}
              onClick={() => {
                setStoryOpen(true);
              }}
              onChange={(e) => setStory(e.target.value)}
              maxLength={maxLenStory}
              autoFocus
            />
            <span className="absolute bottom-6 right-5 text-[1rem] text-gray-400">
              {story.length}/{maxLenStory}
            </span>
          </div>
        </div>
        <div className="text-[1.8rem] font-bold">
          <button
            disabled={story.trim() === ""}
            className={`w-[399px] h-[60px]
                ${
                  story.trim() === ""
                    ? "bg-gray-300 text-white"
                    : "bg-main text-white"
                }`}
            onClick={() => setStoryOpen(false)}
          >
            작성하기
          </button>
        </div>
      </Modal>
    </>
  );
}

export default StartPage;
