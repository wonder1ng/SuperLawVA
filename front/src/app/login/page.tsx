// page.tsx
"use client";

import { useRouter } from "next/navigation";
import styled from "styled-components";
import StatusIcon from "@/components/icons/Status";
import KakaoIcon from "@/components/icons/sns/Kakao";
import StyledInput from "@/components/StyledInput";
import { useState } from "react";
import GoogleIcon from "@/components/icons/sns/Google";
import NaverIcon from "@/components/icons/sns/Naver";
import AppleIcon from "@/components/icons/sns/Apple";
import BackHeader from "@/components/BackHeader";
import SubmitButton from "@/components/SubmitButton";

function LoginPage() {
  const router = useRouter();
  const [emailValue, setemailValue] = useState("");

  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="mt-[1.4rem]" />
      </div>
      <main className="flex flex-col items-center mt-[3rem] gap-12 mx-10 h-auto">
        <div className="mt-20 w-full gap-4 flex flex-col justify-center items-center">
          <img src="/logo.svg" alt="logo" className="h-16" />
          <span className="h-20 font-semibold text-[4rem] tracking-[-0.04em] bg-gradient-to-r from-[#6000FF] to-[#E100FF] bg-clip-text text-transparent">
            Super LawVA
          </span>
        </div>
        <div className="mt-12 w-full flex flex-col gap-8">
          <div className="flex flex-col gap-4 text-[1.8rem]">
            <span className="font-medium">이메일 주소</span>
            <StyledInput
              type="email"
              placeholder="super@lvw.com"
              onChange={(e) => setemailValue(e.target.value)}
            />
          </div>
          <div className="flex flex-col gap-4 text-[1.8rem]">
            <span className="font-medium">비밀번호</span>
            <StyledInput
              type="password"
              placeholder="대소문자, 숫자, 특수문자 포함하여 8글자 이상"
              onChange={(e) => setemailValue(e.target.value)}
            />
          </div>
          <div className="flex items-center gap-10">
            <div className="flex gap-4">
              <input type="checkbox" name="" id="" className="w-6 h-6" />
              <label htmlFor="" className="text-[1.2rem]">
                아이디 저장
              </label>
            </div>
            {/* checkbox 커스텀 코드 */}
            {/* <div className="grid items-center justify-center">
              <input
                type="checkbox"
                className="peer col-start-1 row-start-1 w-8 h-8 appearance-none rounded border-2 border-gray-300 checked:border-main checked:bg-main dark:border-gray-600 dark:checked:border-main forced-colors:appearance-auto"
              />
              <svg
                viewBox="0 0 14 14"
                fill="none"
                className="invisible col-start-1 row-start-1 stroke-black peer-checked:visible dark:text-violet-300 forced-colors:hidden pointer-events-none"
              >
                <path
                  d="M3 8L6 11L11 3.5"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  stroke="#ffffff"
                ></path>
              </svg>
            </div> */}

            <div className="flex gap-4">
              <input type="checkbox" name="" id="" className="w-6 h-6" />
              <label htmlFor="" className="text-[1.2rem]">
                자동 로그인
              </label>
            </div>
          </div>
          <SubmitButton
            onClick={() => router.push("/")}
            className="mt-8 flex justify-center items-center"
          >
            로그인
          </SubmitButton>
          <div className="flex justify-center gap-4 text-l font-medium">
            <span>아이디 찾기</span>|<span>비밀번호 찾기</span>|
            <span>회원가입</span>
          </div>
        </div>
        <div className="w-full flex flex-col justify-center items-center gap-8">
          <div className="mt-16 w-full flex justify-between items-center gap-4 text-xl">
            {/* <hr className="flex-1 border-[#797979]" />
            <span className="flex-1 text-center">SNS 계정으로 로그인</span>
            <hr className="flex-1 border-[#797979]" /> */}
            <hr className="w-full border-[#797979]" />
            <span className="min-w-44 text-center">SNS 계정으로 로그인</span>
            <hr className="w-full border-[#797979]" />
          </div>
          <div className="flex flex-row gap-12">
            <KakaoIcon />
            <GoogleIcon />
            <NaverIcon />
            <AppleIcon />
          </div>
        </div>
      </main>
    </>
  );
}

export default LoginPage;