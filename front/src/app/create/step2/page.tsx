"use client";

import { useRouter } from "next/navigation";
import SubmitButton from "@/components/SubmitButton";
import StatusIcon from "@/components/icons/Status";
import { useState } from "react";
import BackHeader from "@/components/BackHeader";
import StyledInput from "@/components/StyledInput";
import Modal from "@/components/Modal";
import ArrowLeftIcon from "@/components/icons/ArrowLeft";
import ArrowRightIcon from "@/components/icons/ArrowRight";
import StyledDiv from "@/components/StyledDiv";
import WarningIcon from "@/components/icons/Warning";
import CheckedIcon from "@/components/icons/Checked";

function ContractCreateNewPage() {
  const router = useRouter();
  const [disable, setDisable] = useState(true);

  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="mt-[1.4rem]" />
      </div>
      <BackHeader>임대차 계약서 작성</BackHeader>
      <main className="flex flex-col items-center mt-[3rem] gap-12 h-auto">
        <StyledDiv
          width="calc(100% - 2.5rem)"
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
              본 조항은 기본 조항입니다. 계약 전에 전문가와 상담을 추천합니다.
            </span>
          </div>
        </StyledDiv>
        <div className="flex flex-col justify-center items-center w-full gap-4 text-[1.6rem] font-bold ">
          3. 계약 조항
          <div className="px-10 py-12 w-full bg-white rounded-[50px] flex flex-col gap-6 justify-center text-[1.2rem] font-medium">
            <div>
              제1조 (목적)
              <div className="font-normal">
                위 부동산의 임대차에 한하여 임대인과 임차인은 합의에 의하여
                임차보증금 및 차임을 아래와 같이 지불하기로 한다.
              </div>
            </div>
            <div>
              제2조 (존속기간)
              <div className="font-normal">
                임대인은 위 부동산을 임대차 목적대로 사용 수익할 수 있는 상태로
                하여 <span className="text-main">2025년 10월 31일</span>까지
                임차인에게 인도하며, 임대차기간은 인도일로부터&nbsp;
                <span className="text-main">24</span>
                개월인 <span className="text-main">2027년 10월 31일</span>
                까지로 한다.
              </div>
            </div>
            <div>
              제3조 (용도변경 및 전대 등)
              <div className="font-normal">
                임차인은 임대인의 동의 없이 위 부동산의 용도나 구조를 변경하거나
                전대, 임차권 양도 또는 담보제공을 하지 못하며 임대차 목적 이외의
                용도로 사용할 수 없다.
              </div>
            </div>
            <div>
              제4조 (계약의 해지)
              <div className="font-normal">
                임차인이 3기의 차임액에 달하도록 연체하거나 제 3조를 위반하였을
                때 임대인은 즉시 본 계약을 해지할 수 있다.
              </div>
            </div>
            <div>
              제5조 (계약의 종료)
              <div className="font-normal">
                임대차계약이 종료된 경우에 임차인은 위 부동산을 원상으로
                회복하여 임대인에게 반환하다. 이러한 경우 임대인은 보증금을
                임차인에게 반환하고, 연체 임대료 또는 손해배상금이 있을 때는
                이들을 제하고 그 잔액을 반환한다.
              </div>
            </div>
            <div>
              제6조 (계약의 해제)
              <div className="font-normal">
                임차인이 임대인에게 중도금(중도금이 없을 때는 잔금)을 지불하기
                전까지, 임대인은 계약금의 배액을 상환하고, 임차인은 계약금을
                포기하고 이 계약을 해제할 수 있다.
              </div>
            </div>
            <div>
              제7조 (채무불이행과 손해배상)
              <div className="font-normal">
                임대인 또는 임차인이 본 게약상의 내용에 대하여 불이행이 있을
                경우 그 상대방은 불이행한 자에 대하여 서면으로 최고하고 계약을
                해제할 수 있다. 그리고 계약 당사자는 계약해제에 따른 손해배상을
                각각 상대방에 대하여 청구할 수 있으며, 손해배상에 대하여 별도의
                약정이 없는 한 계약금을 손해배상의 기준으로 본다.
              </div>
            </div>
            <div>
              제8조 (중개보수)
              <div className="font-normal">
                개업공인중개사는 계약 당사자의 본 계약 불이행에 대하여 책임지지
                않으며, 개업공인중개사의 고의나 과실없이 본 게약이 무효, 취소,
                해제 되어도 중개보수는 지급한다. 중개보수는 본 계약 체결시에
                계약 당사자 쌍방이 각각 지불하되, 확인설명서에 별도의 지급일이
                있으면 그에 따른다. 공동중개인 경우 게약 당사자는 자신이
                중개의뢰한 개업공인중개사에게 각각 중개보수를 지급한다.
              </div>
            </div>
            <div>
              제9조 (중개대상물확인.설명서 교부 등)
              <div className="font-normal">
                개업공인중개사는 중개대상물 확인.설명서를 작성하고
                업무보증관계증서(공제증서 등) 사본을 첨부하여{" "}
                <span className="text-main">2025년 04월 20일</span>
                거래당사자 쌍방에게 교부한다.
              </div>
            </div>
          </div>
        </div>
        <span
          className={`flex self-center justify-self-center gap-4${
            disable ? " text-[rgba(128,128,128,0.55)]" : " text-main"
          }`}
          onClick={() => setDisable(!disable)}
        >
          <CheckedIcon
            color={disable ? "rgba(128, 128, 128, 0.55)" : "#6000ff"}
          />
          모든 내용을 확인했습니다
        </span>
        <SubmitButton
          width="100%"
          height={5.5}
          fontSize={1.8}
          fontWeight={500}
          disabled={disable}
          className="mb-12"
          onClick={() => router.push("step3")}
        >
          다음
        </SubmitButton>
      </main>
    </>
  );
}

export default ContractCreateNewPage;
