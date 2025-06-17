"use client";

import Image from "next/image";
import Link from "next/link";
import BottomNav from "@/components/BottomNav";
import DocumentIcon from "@/components/icons/Document";
import InfoIcon from "@/components/icons/Info";
import AnalyzeIcon from "@/components/icons/Analysis";

export default function MorePage() {
  return (
    <div className="w-full relative bg-[#f2f1f6] h-[852px] overflow-hidden text-left text-sm text-black font-pretendard">
      <div className="absolute top-0 left-0 bg-white/70 border border-[#f3f4f6] w-[393px] h-[852px]" />

      {/* 상단 로고 영역 */}
      <div className="absolute top-[60px] left-[21px] w-[142px] h-6">
        <div className="absolute top-[0px] left-[0px] w-[142px] h-6 flex items-center">
          <Image
            className="w-6 h-3 object-cover"
            src="/lovalogo.svg"
            alt="logo"
            width={24}
            height={12}
          />
          <div className="ml-2 text-[20px] tracking-[-0.04em] leading-[120%] font-semibold text-transparent !bg-clip-text [background:linear-gradient(90deg,_#6000ff,_#e100ff)] [-webkit-background-clip:text] [-webkit-text-fill-color:transparent] whitespace-nowrap">
            Super Lawva
          </div>
        </div>
      </div>

      {/* 사용자 정보 영역 */}
      <div className="absolute top-[132px] left-[107px] w-[114px] h-[45px]">
        <div className="absolute top-[0px] left-[0px] w-[114px] h-[45px]">
          <div className="absolute top-[0px] left-[0px] text-[20px] leading-5 font-semibold">
            홍길동
          </div>
          <div className="absolute top-[33px] left-[0px] w-[114px] h-3 text-[12px] text-[#9ca3af]">
            <div className="absolute top-[0px] left-[0px] leading-[100%] font-medium">
              sibal_zip@gmail.com
            </div>
          </div>
        </div>
      </div>

      {/* 프로필 이미지 영역 */}
      <div className="absolute top-[127px] left-[39px] w-[52px] h-[50px]">
        <div className="absolute top-[0px] left-[0px] rounded-[43.5px] bg-[#f3f4f6] w-[52px] h-[50px]" />
        <Image
          className="absolute w-[48.27%] top-[calc(50%_-_12.75px)] right-[26.25%] left-[25.48%] max-w-full overflow-hidden h-[25.9px]"
          src="/my.svg"
          alt="profile"
          width={25.1}
          height={25.9}
        />
      </div>

      {/* 사용자 정보 박스 */}
      <div className="absolute top-[117px] left-[21px] rounded-[20px] border border-[#c6c6c8] border-solid w-[337px] h-[70px]" />

      {/* 내 문서함 영역 */}
      <div className="absolute top-[217px] left-[21px] text-[20px] leading-5 font-semibold">
        내 문서함
      </div>

      {/* 문서함 아이템들 */}
      <div className="absolute top-[267px] left-[21px] w-[337px] h-[46px] text-[12px] text-black/60">
        <div className="absolute top-[0px] left-[0px] w-[337px] h-[46px]">
          <div className="absolute top-[0px] left-[0px] rounded-[20px] border border-[#c6c6c8] border-solid w-[337px] h-[46px]" />
          <div className="absolute top-[16px] left-[12px] w-[14px] h-[14px] flex items-center justify-center">
            <DocumentIcon color="#32d74b" width={14} height={14} />
          </div>
          <div className="absolute top-[16px] left-[31px] tracking-[-0.2px] font-medium">
            계약서 초안
          </div>
        </div>
        <Image
          className="absolute w-[3.56%] top-[calc(50%_-_3px)] right-[7.12%] left-[89.32%] max-w-full overflow-hidden h-1.5 object-contain"
          src="/add.svg"
          alt="arrow"
          width={12}
          height={6}
        />
      </div>

      {/* 내용증명서 아이템 */}
      <div className="absolute top-[324px] left-[21px] w-[337px] h-[46px] text-[12px] text-black/60">
        <div className="absolute top-[0px] left-[0px] w-[337px] h-[46px]">
          <div className="absolute top-[0px] left-[0px] rounded-[20px] border border-[#c6c6c8] border-solid w-[337px] h-[46px]" />
          <div className="absolute top-[16px] left-[12px] w-[14px] h-[14px] flex items-center justify-center">
            <InfoIcon color="#ff453a" width={14} height={14} />
          </div>
          <div className="absolute top-[16px] left-[31px] tracking-[-0.2px] font-medium">
            생성된 내용증명서
          </div>
        </div>
        <Image
          className="absolute w-[3.56%] top-[calc(50%_-_3px)] right-[7.12%] left-[89.32%] max-w-full overflow-hidden h-1.5 object-contain"
          src="/add.svg"
          alt="arrow"
          width={12}
          height={6}
        />
      </div>

      {/* 분석 결과 아이템 */}
      <div className="absolute top-[381px] left-[21px] w-[337px] h-[46px] text-[12px] text-black/60">
        <div className="absolute top-[0px] left-[0px] w-[337px] h-[46px]">
          <div className="absolute top-[0px] left-[0px] rounded-[20px] border border-[#c6c6c8] border-solid w-[337px] h-[46px]" />
          <div className="absolute top-[16px] left-[12px] w-[14px] h-[14px] flex items-center justify-center">
            <AnalyzeIcon color="#0a84ff" width={14} height={14} />
          </div>
          <div className="absolute top-[16px] left-[31px] tracking-[-0.2px] font-medium">
            계약서 분석 결과
          </div>
        </div>
        <Image
          className="absolute w-[3.56%] top-[calc(50%_-_3px)] right-[7.12%] left-[89.32%] max-w-full overflow-hidden h-1.5 object-contain"
          src="/add.svg"
          alt="arrow"
          width={12}
          height={6}
        />
      </div>

      {/* 자주 묻는 질문 영역 */}
      <div className="absolute top-[457px] left-[21px] text-[20px] leading-5 font-semibold">
        자주 묻는 질문
      </div>

      {/* FAQ 이미지 영역 - 가로 스크롤 */}
      <div className="absolute top-[506px] left-[19px] w-[393px] overflow-x-auto flex gap-4 pb-4">
        <div className="flex flex-col gap-2 flex-shrink-0">
          <div className="relative w-[160px] h-[160px] rounded-[20px] overflow-hidden">
            <Image
              src="/more1.png"
              alt="FAQ 1"
              width={160}
              height={160}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="text-[14px] font-semibold">
            집주인이 보증금을 안줘요
          </div>
          <div className="text-[8px] text-black/50">Q&A</div>
        </div>

        <div className="flex flex-col gap-2 flex-shrink-0">
          <div className="relative w-[160px] h-[160px] rounded-[20px] overflow-hidden">
            <Image
              src="/more2.png"
              alt="FAQ 2"
              width={160}
              height={160}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="text-[14px] font-semibold">임대차 계약이 뭔가요?</div>
          <div className="text-[8px] text-black/50">용어 설명</div>
        </div>

        <div className="flex flex-col gap-2 flex-shrink-0">
          <div className="relative w-[160px] h-[160px] rounded-[20px] overflow-hidden">
            <Image
              src="/more3.png"
              alt="FAQ 3"
              width={160}
              height={160}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="text-[14px] font-semibold">계약이 뭔가요?</div>
          <div className="text-[8px] text-black/50">계약서 분석</div>
        </div>
      </div>

      {/* 하단 네비게이션 */}
      <BottomNav />
    </div>
  );
}
