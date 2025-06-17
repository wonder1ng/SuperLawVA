"use client";

import { useRouter } from "next/navigation";
import SubmitButton from "@/components/SubmitButton";
import StatusIcon from "@/components/icons/Status";
import { useState } from "react";
import BackHeader from "@/components/BackHeader";
import ClockIcon from "@/components/icons/Clock";
import InfoIcon from "@/components/icons/Info";
import MapIcon from "@/components/icons/Map";
import CalendarIcon from "@/components/icons/Calendar";
import PaymentIcon from "@/components/icons/Payment";
import AssetIcon from "@/components/icons/Asset";

function StartPage() {
  const router = useRouter();
  const [activeIndex, setActiveIndex] = useState<number>(0);

  const tabs = ["계약 요약", "계약서 정보", "계약 조건", "특약"];

  const tabContents = [
    <div
      key="0"
      className="flex flex-col gap-12 w-full rounded-[30px] p-12 bg-white font-bold text-[1.6rem]"
    >
      <div className="flex flex-col gap-2">
        <span className="flex gap-2 items-center">
          <ClockIcon />
          계약기간
        </span>
        <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
          2025년 12월 24일 - 2026년 12월 24일
        </span>
      </div>
      <div className="flex flex-col gap-2">
        <span className="flex gap-2 items-center">
          <AssetIcon />
          보증금
        </span>
        <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
          5,000만 원
        </span>
      </div>
      <div className="flex flex-col gap-2">
        <span className="flex gap-2 items-center">
          <PaymentIcon />
          월세
        </span>
        <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
          50만 원
        </span>
      </div>
      <div className="flex flex-col gap-2">
        <span className="flex gap-2 items-center">
          <CalendarIcon />
          납부일
        </span>
        <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
          매달 5일
        </span>
      </div>
      <div className="flex flex-col gap-2">
        <span className="flex gap-2 items-center">
          <MapIcon />
          주소
        </span>
        <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
          서울시 서초구 어딘가
        </span>
      </div>
      <div className="flex flex-col gap-2">
        <span className="flex gap-2 items-center">
          <InfoIcon width={1.4} height={1.4} color="#6000FF" />
          상세 주소
        </span>
        <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
          서울역 1번 출구 길거리
        </span>
      </div>
    </div>,
    <div key="1" className="flex flex-col w-full gap-12">
      <div className="flex flex-col gap-4">
        <span className="text-[1.6rem] font-bold pl-8">1. 부동산 표시</span>
        <div className="flex flex-col gap-12 w-full rounded-[30px] p-12 bg-white font-bold text-[1.6rem]">
          <div className="flex flex-col gap-2">
            주소
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              경기 안성시 죽산면 죽산리 343-1
            </span>
          </div>
          <div className="flex flex-col gap-2">
            상세 주소
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              무지개아파트 상가 A동 1층 103호
            </span>
          </div>
          <div className="flex flex-col gap-2">
            면적
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              100㎡
            </span>
          </div>
          <div className="flex flex-col gap-2">
            구조
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              철근콘크리트
            </span>
          </div>
          <div className="flex flex-col gap-2">
            용도
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              오피스텔
            </span>
          </div>
        </div>
      </div>
      <div className="flex flex-col gap-4">
        <span className="text-[1.6rem] font-bold pl-8">2. 계약 내용</span>
        <div className="flex flex-col gap-12 w-full rounded-[30px] p-12 bg-white font-bold text-[1.6rem]">
          <div className="flex flex-col gap-2">
            보증금
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              1,300만 원
            </span>
          </div>
          <div className="flex flex-col gap-2">
            계약금
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              1,400만 원
            </span>
          </div>
          <div className="flex flex-col gap-2">
            잔금
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              200만 원원
            </span>
          </div>
          <div className="flex flex-col gap-2">
            월세
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              70만 원
            </span>
          </div>
          <div className="flex flex-col gap-2">
            계약일자
            <span className="text-[#4e4e4e] text-[1.2rem] font-medium">
              2025년 03월 14일 - 2027년 3월 13일 (24개월)
            </span>
          </div>
        </div>
      </div>
    </div>,
    <div key="2" className="flex flex-col w-full gap-4">
      <span className="text-[1.6rem] font-bold pl-8">계약 조항</span>
      <div className="flex flex-col gap-2 w-full rounded-[30px] p-12 bg-white font-bold text-[1.2rem]">
        제1조: (목적)
        <span className="font-normal">
          위의 부동산의 임대차에 한하여 임대인과 임차인은 합의에 의하여
          임차보증금 및 차임을 아래와 같이 지불하기로 한다.
        </span>
        제1조: (목적)
        <span className="font-normal">
          위의 부동산의 임대차에 한하여 임대인과 임차인은 합의에 의하여
          임차보증금 및 차임을 아래와 같이 지불하기로 한다.
        </span>
        제 2조 (존속기간)
        <span className="font-normal">
          임대인은 위 부동산을 임대차 목적대로 사용․수익할 수 있는 상태로
          '전입일'까지 임차인에게 인도하며, 임대차 기간은 '계약종료일'까지로
          한다.
        </span>
        제 3조 (용도변경 및 전대 등)
        <span className="font-normal">
          임차인은 임대인의 동의없이 위 부동산의 용도나 구조를 변경하거나
          전대․임차권 양도 또는 담보제공을 하지 못하며 임대차 목적 이외의 용도로
          사용할 수 없다.
        </span>
        제 4조 (계약의 해지)
        <span className="font-normal">
          임차인의 차임연체액이 2기의 차임액에 달하거나 제3조를 위반하였을 때
          임대인은 즉시 본 계약을 해지 할 수 있다.
        </span>
        제 5조 (계약의 종료)
        <span className="font-normal">
          임대차계약이 종료된 경우에 임차인은 위 부동산을 원상으로 회복하여
          임대인에게 계약종료 즉시 반환한다. 이러한 경우 임대인은 보증금을
          임차인에게 즉시 반환하고, 연체 임대료 또는 손해배상금이 있을 때는
          이들과 그 이자를 제하고 그 잔액을 반환한다. 임대인은 부동산이 원상으로
          회복되지 않은 경우 손해배상금을 산정할 때까지 보증금의 반환을 연기할
          수 있으며 임차인이 손해배상금에 합의한 즉시 그 잔액을 반환한다.
          임차인은 정당한 사유 없이 임대인의 정당한 부동산의 상태 조사 및
          손해배상금 산정에 저항할 수 없으며 임대인이 정당한 사유 없이 부동산의
          상태 조사 및 손해배상금 산정을 하지 않는 경우 자체적으로 부동산의 상태
          조사 및 손해배상금 산정을 할 수 있으며 임대인이 정당한 사유 없이
          계약종료일까지 상태 조사 및 손해배상금 산정을 실시하지 않은 경우
          임차인의 상태 조사 및 손해배상금에 따르도록 한다.
        </span>
        제 6조 (계약의 해제)
        <span className="font-normal">
          임차인이 임대인에게 중도금(중도금이 없을 때는 잔금)을 지불하기 전까지,
          임대인은 계약금의 배액을 상환하고, 임차인은 계약금을 포기하고 이
          계약을 해제할 수 있다. 임차인이 전입일 이후 계약만료일 전에 정당한
          사유 없이 계약의 해제를 원할 경우 계약시 지불한 중개보수와 수수료와
          동일한 금액을 손해배상하며 새로운 임차인이 임차 계약을 완료하기 전까지
          최대 3개월 간 임차료를 지불해야 한다. 임대인이 임차인의 전입일 이후
          계약만료일 전에 정당한 사유 없이 계약의 해제를 원할 경우 계약시 지불한
          중개보수와 수수료와 동일한 금액을 손해배상하며 새로운 전입지에 대한
          포장이사 실비를 손해배상한다.
        </span>
        제 7조 (채무불이행과 손해배상)
        <span className="font-normal">
          임대인 또는 임차인이 본 계약상의 내용에 대하여 성실히 협조하고 임해야
          하며 불이행이 있을 경우 그 상대방은 불이행한 자에 대하여 서면으로
          최고하고 계약을 해제 할 수 있다. 그리고 계약 당사자는 계약해제에 따른
          손해배상과 지연 이자를 각각 상대방에 대하여 청구할 수 있다.
        </span>
        제8조(관리비등)
        <span className="font-normal">
          관리비는 월 20만 원이며 여기에는 공용 전기요금, 수도요금, 공용시설
          관리비 등이 포함된다.
        </span>
        제9조(기타 사항)
        <span className="font-normal">
          기타 사항은 민법과 관련법령 등에 따르고 이에 정해지지 않았을 때는 조리
          및 관례에 따른다.
        </span>
      </div>
    </div>,
    <div key="3" className="flex flex-col w-full gap-12">
      <div className="flex flex-col gap-4">
        <span className="text-[1.6rem] font-bold pl-8">1. 기본 특약</span>
        <ol className="list-decimal list-inside flex flex-col gap-4 w-full rounded-[30px] p-8 bg-white font-medium text-[1.2rem]">
          <li>권리관계 유지 및 보증금 보호</li>
          <li>전세자금대출 및 보증금 반환</li>
          <li>하자 및 수리 관련</li>
          <li>현 시설 상태 및 부속물 인도</li>
          <li>위반건축물·불법증축 관련</li>
          <li>임차인·세입자 승계 및 기타</li>
        </ol>
      </div>
      <div className="flex flex-col gap-4">
        <span className="text-[1.6rem] font-bold pl-8">2. 합의한 내용</span>
        <ol className="list-decimal list-inside flex flex-col gap-4 w-full rounded-[30px] p-8 bg-white font-medium text-[1.2rem]">
          <li>집에서 흡연은 가능하다</li>
          <li>주차공간은 1대를 기본 보장한다.</li>
          <li>집주인과 분쟁이 발생할 시 스파링으로 결정한다.</li>
          <li>집을 카페로 만들어도 된다.</li>
          <li>집가고 싶다.</li>
          <li>집에 사람을 초대하면 좋겠다</li>
        </ol>
      </div>
    </div>,
  ];

  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="mt-[1.4rem]" />
      </div>
      <BackHeader to="/">월세 임대차 계약서</BackHeader>
      <main className="flex flex-col items-center mt-[3rem] gap-12 mx-10 h-auto">
        <ul className="flex justify-around items-center w-full h-16 font-medium text-subText text-[1.2rem] bg-white border border-[#cdcdcd] rounded-[50px]">
          {tabs.map((tab, index) => (
            <li
              key={index}
              onClick={() => setActiveIndex(index)}
              className={`flex-1 flex h-full items-center justify-center rounded-[50px]${
                activeIndex === index ? " bg-main text-white" : ""
              }`}
            >
              {tab}
            </li>
          ))}
        </ul>
        {tabContents[activeIndex]}
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
          원본 보기
        </SubmitButton>
        <div className="w-full h-[4.5rem] gap-2 flex justify-center items-center text-white text-[1.6rem] font-semibold bg-gradient-to-br from-[#6000FF] via-[#8a00ff] to-[#E100FF] rounded-[50px] mb-12">
          <img src="/bot.png" alt="" className="w-12 h-12" />
          궁금한 점이 있으면 챗봇을 이용해 보세요!
        </div>
      </main>
    </>
  );
}

export default StartPage;
