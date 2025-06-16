"use client";

import { useRouter } from "next/navigation";
import SubmitButton from "@/components/SubmitButton";
import StatusIcon from "@/components/icons/Status";
import StyledInput from "@/components/StyledInput";
import { useEffect, useState } from "react";
import BackHeader from "@/components/BackHeader";
import AlarmIcon from "@/components/icons/Alarm";
import BottomNav from "@/components/BottomNav";
import ResumeIcon from "@/components/icons/Resume";
import DocumentIcon from "@/components/icons/Document";
import AnalysisIcon from "@/components/icons/Analysis";
import InfoIcon from "@/components/icons/Info";
import UploadIcon from "@/components/icons/Upload";

interface QuickButtonProps {
  bgc: string;
  icon: React.ReactNode;
  title?: string;
  description?: string;
}

function QuickButton({ bgc, icon, title, description }: QuickButtonProps) {
  return (
    <div className="flex flex-col items-center text-center">
      <button
        className="flex justify-center items-center w-20 h-20 rounded-[20px] mb-2"
        style={{ backgroundColor: bgc }}
      >
        {icon && <span>{icon}</span>}
      </button>
      <div className="text-base font-semibold">{title}</div>
      <span className="text-[0.8rem] text-subText">{description}</span>
    </div>
  );
}
function StartPage() {
  const router = useRouter();

  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="w-full mt-[1.4rem]" />
      </div>
      <header className="w-full flex justify-center items-center h-24">
        <div className="w-full bg-white border border-inputBox rounded-[50px] flex justify-between items-center p-8 mx-6 gap-4">
          <span className="flex gap-3">
            <img src="logo.svg" className="w-12" alt="" />
            <span className="font-pretendard font-semibold text-[2rem] leading-[120%] tracking-[-0.04em] bg-gradient-to-r from-[#6000FF] to-[#E100FF] bg-clip-text text-transparent">
              Super LawVA
            </span>
          </span>
          <span className="flex gap-[2.4rem]">
            <AlarmIcon />
            <span className="w-[2.4rem] h-[2.4rem] rounded-full bg-main"></span>
          </span>
        </div>
      </header>
      <main className="w-full flex flex-col items-center h-auto">
        <div className="self-start mx-16 my-20 text-[2rem] font-bold">
          아무개 님의 고민
          <br />'<span className="text-main">로바</span>'에서 도와드릴게요!
        </div>
        <div className="w-full px-8 gap-12 rounded-t-[50px] bg-white flex flex-col items-center">
          <div className="w-full h-[4.4rem] bg-inputBox rounded-[50px] text-subText text-[1.4rem] flex items-center justify-between mt-10 pl-6 pr-4">
            <div className="flex gap-4">
              <AlarmIcon />
              무엇을 도와드릴까요?
            </div>
            <SubmitButton
              width={7}
              height={3}
              fontSize={1.2}
              fontWeight={500}
              borderRadius={50}
              background="#6000FF"
            >
              검색
            </SubmitButton>
          </div>
          <div className="self-start w-full font-semibold text-[1.8rem] px-8 flex flex-col gap-4">
            빠른 작업
            <div className="flex w-full justify-around text-[1.2rem]">
              <QuickButton
                bgc="#32d74b"
                icon={
                  <DocumentIcon
                    color="white"
                    onClick={() => router.push("create")}
                  />
                }
                title="계약서 작성"
                description="안전한 계약을 원해요"
              />
              <QuickButton
                bgc="#0a84ff"
                icon={
                  <AnalysisIcon
                    color="white"
                    onClick={() => router.push("analysis")}
                  />
                }
                title="계약서 분석"
                description="계약을 검토하고 싶어요"
              />
              <QuickButton
                bgc="#ff453a"
                icon={
                  <InfoIcon
                    color="white"
                    onClick={() => router.push("certificate")}
                  />
                }
                title="내용증명"
                description="문제가 발생했어요"
              />
            </div>
          </div>
          <div className="self-start w-full font-semibold text-[1.8rem] px-8 flex flex-col gap-4">
            내 계약서
            <div
              onClick={() => router.push("upload")}
              className="flex flex-col justify-center items-center gap-4"
            >
              <div className="flex flex-col gap-4 py-4 justify-center items-center w-full border-[1.5px] border-[#c6c6c8] rounded-[20px] text-[1.2rem] font-medium">
                <UploadIcon />
                <span>계약서 업로드</span>
                <span className="text-[0.8rem] font-medium text-subText">
                  PDF, 이미지 또는 문서를 업로드하세요
                </span>
              </div>
              <span className="text-main text-[1rem]">
                서비스를 이용하려면 파일을 업로드해주세요!
              </span>
              <SubmitButton
                width={10}
                height={3}
                fontSize={1}
                fontWeight={500}
                borderRadius={"50px"}
                background="#6000FF"
              >
                바로가기
              </SubmitButton>
              <div
                onClick={() => router.push("contract")}
                className="flex items-center gap-4 py-4 px-8 w-full border-[1.5px] border-[#c6c6c8] rounded-[20px] text-[1.2rem] font-medium"
              >
                <QuickButton
                  bgc="rgba(96, 0, 255, 0.5)"
                  icon={<DocumentIcon />}
                />
                <div className="flex justify-between w-full">
                  <div className="flex flex-col gap-[0.2rem] text-[#737373] text-[0.8rem] font-medium">
                    <span className="text-[1.2rem] text-black">
                      월세 임대차 계약서
                    </span>
                    <span className="text-[1rem]">
                      서울시 강남구 테헤란로 123
                    </span>
                    <span>2025.03.22 등록</span>
                    <div className="flex justify-between">
                      <span>보증금: 5000만 원</span>
                      <span>월세: 28만 원</span>
                    </div>
                  </div>
                  <SubmitButton
                    width={4}
                    height={2}
                    fontSize={0.8}
                    fontWeight={500}
                    fontColor="#3c82f6"
                    borderRadius={"50px"}
                    background="#eff6ff"
                    borderColor="#3c82f6"
                  >
                    진행중
                  </SubmitButton>
                </div>
              </div>
              <SubmitButton
                width={10}
                height={3}
                fontSize={1}
                fontWeight={500}
                fontColor="#6000FF"
                borderRadius={"50px"}
                background="#ffffff"
                borderColor="#6000FF"
              >
                추가하기
              </SubmitButton>
            </div>
          </div>
          <div className="self-start w-full font-semibold text-[1.8rem] px-8 flex flex-col gap-4">
            최근 활동
            <div className="flex flex-col justify-center items-center gap-4">
              <div className="flex py-4 justify-between items-center w-full border-[1.5px] border-[#c6c6c8] rounded-[20px] px-[1.5rem]">
                <span className="flex items-center gap-4">
                  <AnalysisIcon color="#0a84ff" />
                  <div>
                    <div className="text-[1.2rem] font-medium">계약서 분석</div>
                    <div className="text-[#7f7f7f] text-[1rem]">
                      {/* <div className="text-subText"> */}
                      전세 임대차 계약서 분석
                    </div>
                  </div>
                </span>
                <span className="text-[0.8rem]">
                  <div className="text-main">자세히 보기 &gt;</div>
                  <div className="text-subText">2025년 6월 6일 화요일</div>
                </span>
              </div>
              <SubmitButton
                width={10}
                height={3}
                fontSize={1}
                fontWeight={500}
                fontColor="#6000FF"
                borderRadius={"50px"}
                background="#ffffff"
                borderColor="#6000FF"
              >
                자세히 보기
              </SubmitButton>
            </div>
          </div>
        </div>
        <div className="h-36 w-full bg-white" />
      </main>
      <BottomNav></BottomNav>
    </>
  );
}

export default StartPage;
