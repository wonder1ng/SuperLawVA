"use client";

import { useRouter } from "next/navigation";
import SubmitButton from "@/components/SubmitButton";
import StatusIcon from "@/components/icons/Status";
import StyledInput from "@/components/StyledInput";
import { useState } from "react";
import BackHeader from "@/components/BackHeader";

function StartPage() {
  const router = useRouter();
  const [emailValue, setEmailValue] = useState("");

  const handleClick = () => {
    router.push("/login");
  };
  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="mt-[1.4rem]" />
      </div>
      <BackHeader to="login">회원가입</BackHeader>
      <main className="flex flex-col items-center mt-[5rem] mx-8 h-auto">
        <div className="mt-12 flex flex-col gap-8">
          <div className="flex gap-4">
            <StyledInput
              type="email"
              width="25rem"
              fontSize={1.6}
              placeholder="이메일 입력"
              onChange={(e) => setEmailValue(e.target.value)}
            />
            <SubmitButton
              width={8}
              height={3}
              fontSize={1.2}
              fontWeight={600}
              disabled={true}
            >
              인증하기
            </SubmitButton>
          </div>
          <StyledInput
            type="text"
            width="34rem"
            fontSize={1.6}
            placeholder="인증 코드 입력"
            onChange={(e) => setEmailValue(e.target.value)}
          />
          <StyledInput
            type="password"
            width="34rem"
            fontSize={1.6}
            placeholder="대소문자, 숫자, 특수문자 포함 8-14글자 입력"
            onChange={(e) => setEmailValue(e.target.value)}
          />
          <StyledInput
            type="password"
            width="34rem"
            fontSize={1.6}
            placeholder="비밀번호 확인"
            onChange={(e) => setEmailValue(e.target.value)}
          />
          <StyledInput
            type="text"
            width="34rem"
            fontSize={1.6}
            placeholder="이름 입력"
            onChange={(e) => setEmailValue(e.target.value)}
          />
        </div>
        <div className="bg-mainL mt-16 p-12 w-[36rem] justify-center rounded-[50px] flex flex-col gap-4">
          <div className="flex mb-4 gap-4 items-center">
            <input type="checkbox" name="" id="" className="w-6 h-6" />
            <label htmlFor="" className="text-[1.8rem] font-bold">
              약관 전체 동의
            </label>
          </div>
          <div className="flex gap-4 items-center">
            <input type="checkbox" name="" id="" className="w-6 h-6" />
            <label htmlFor="" className="text-[1.6rem]">
              이용 약관 동의&nbsp;
              <span className="text-main text-[1.2rem]">본문 보기</span>
            </label>
          </div>
          <div className="flex gap-4 items-center">
            <input type="checkbox" name="" id="" className="w-6 h-6" />
            <label htmlFor="" className="text-[1.6rem]">
              개인정보 수집 및 이용 동의&nbsp;
              <span className="text-main text-[1.2rem]">본문 보기</span>
            </label>
          </div>
          <div className="flex gap-4 items-center">
            <input type="checkbox" name="" id="" className="w-6 h-6" />
            <label htmlFor="" className="text-[1.6rem]">
              마케팅 정보 수신 동의 (선택)&nbsp;
              <span className="text-main text-[1.2rem]">본문 보기</span>
            </label>
          </div>
        </div>
        <div className="flex flex-1 items-end mt-28 mb-8">
          <SubmitButton disabled={true}>가입하기</SubmitButton>
        </div>
      </main>
    </>
  );
}

export default StartPage;
