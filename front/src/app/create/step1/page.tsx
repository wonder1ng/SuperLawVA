"use client";

import { useRouter } from "next/navigation";
import SubmitButton from "@/components/SubmitButton";
import StatusIcon from "@/components/icons/Status";
import { useEffect, useState } from "react";
import BackHeader from "@/components/BackHeader";
import StyledInput from "@/components/StyledInput";
import Modal from "@/components/Modal";
import ArrowLeftIcon from "@/components/icons/ArrowLeft";
import ArrowRightIcon from "@/components/icons/ArrowRight";

function ContractCreateNewPage() {
  const router = useRouter();

  useEffect(() => {
    // 클라이언트 환경에서만 실행됨
    if (sessionStorage.getItem("start") !== "true") {
      router.replace("/");
    }
  }, [router]);

  const [modalOpen, setModalOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState<number>(0);
  const [address, setAddress] = useState<string>("");
  const [detailAddress, setDetailAddress] = useState<string>("");
  const [area, setArea] = useState<number | "">("");
  const [structure, setStructure] = useState<string>("");
  const [purpose, setPurpose] = useState<string>("");
  const [deposit, setDeposit] = useState<number | "">("");
  const [downPayment, setDownPayment] = useState<number | "">("");
  const [balance, setBalance] = useState<number | "">("");
  const [rent, setRent] = useState<number | "">("");
  const [contractDate, setContractDate] = useState<Date | "">("");

  const tabsVariable = [
    address,
    detailAddress,
    area,
    structure,
    purpose,
    deposit,
    downPayment,
    balance,
    rent,
    contractDate,
  ];
  const tabsSetFunction = [
    setAddress,
    setDetailAddress,
    setArea,
    setStructure,
    setPurpose,
    setDeposit,
    setDownPayment,
    setBalance,
    setRent,
    setContractDate,
  ];

  const tabTitles = [
    "주소를",
    "상세 주소를",
    "공급(임대) 면적을",
    "건물 구조를",
    "건물 용도를",
    "보증금을",
    "계약금을",
    "잔금을",
    "월세를",
    "계약일자를",
  ];
  const tabContents = [
    <input
      type="text"
      name=""
      id=""
      placeholder="계약하려는 건물의 주소를 입력해주세요."
      onChange={(e) => setAddress(e.target.value)}
      value={address}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="text"
      name=""
      id=""
      placeholder="계약하려는 건물의 상세 주소를 입력해주세요."
      onChange={(e) => setDetailAddress(e.target.value)}
      value={detailAddress}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="number"
      name=""
      id=""
      onChange={(e) => setArea(Number(e.target.value))}
      value={area ?? ""}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="text"
      name=""
      id=""
      placeholder="ex) 철근콘크리트"
      onChange={(e) => setStructure(e.target.value)}
      value={structure}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="text"
      name=""
      id=""
      placeholder="ex) 오피스텔, 아파트, 상가시설"
      onChange={(e) => setPurpose(e.target.value)}
      value={purpose}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="number"
      name=""
      id=""
      onChange={(e) => setDeposit(Number(e.target.value))}
      value={deposit ?? ""}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="number"
      name=""
      id=""
      onChange={(e) => setDownPayment(Number(e.target.value))}
      value={downPayment ?? ""}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="number"
      name=""
      id=""
      onChange={(e) => setBalance(Number(e.target.value))}
      value={balance ?? ""}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="number"
      name=""
      id=""
      onChange={(e) => setRent(Number(e.target.value))}
      value={rent ?? ""}
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
    <input
      type="date"
      name=""
      id=""
      onChange={(e) => setContractDate(new Date(e.target.value))}
      value={
        contractDate === "" ? "" : contractDate.toISOString().split("T")[0]
      }
      className="w-full h-full px-12 text-[1.2rem] font-medium placeholder:text-subText"
    />,
  ];

  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="mt-[1.4rem]" />
      </div>
      <BackHeader>임대차 계약서 작성</BackHeader>
      <main className="flex flex-col items-center mt-[3rem] gap-12 mx-10 h-auto">
        <div key="1" className="flex flex-col w-full gap-12">
          <div className="flex flex-col gap-4">
            <span className="text-[1.6rem] font-bold pl-8">1. 부동산 표시</span>
            <div className="flex flex-col gap-8 w-full rounded-[30px] p-12 bg-white font-bold text-[1.6rem]">
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(0);
                }}
              >
                주소
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="주소를 입력해주세요"
                  onChange={(e) => setAddress(e.target.value)}
                  value={address}
                ></StyledInput>
              </div>
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(1);
                }}
              >
                상세 주소
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="상세한 주소를 입력해주세요"
                  onChange={(e) => setDetailAddress(e.target.value)}
                  value={detailAddress}
                ></StyledInput>
              </div>
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(2);
                }}
              >
                면적
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="면적을 입력해주세요 (㎡)"
                  onChange={(e) => setArea(Number(e.target.value))}
                  value={area ?? ""}
                ></StyledInput>
              </div>
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(3);
                }}
              >
                구조
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="ex) 철근콘크리트"
                  onChange={(e) => setStructure(e.target.value)}
                  value={structure}
                ></StyledInput>
              </div>
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(4);
                }}
              >
                용도
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="ex) 오피스텔, 아파트"
                  onChange={(e) => setPurpose(e.target.value)}
                  value={purpose}
                ></StyledInput>
              </div>
            </div>
          </div>
          <div className="flex flex-col gap-4">
            <span className="text-[1.6rem] font-bold pl-8">2. 계약 내용</span>
            <div className="flex flex-col gap-12 w-full rounded-[30px] p-12 bg-white font-bold text-[1.6rem]">
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(5);
                }}
              >
                보증금
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="보증금 금액을 입력해주세요"
                  onChange={(e) => setDeposit(Number(e.target.value))}
                  value={deposit ?? ""}
                ></StyledInput>
              </div>
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(6);
                }}
              >
                계약금
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="계약금 금액을 입력해주세요"
                  onChange={(e) => setDownPayment(Number(e.target.value))}
                  value={downPayment ?? ""}
                ></StyledInput>
              </div>
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(7);
                }}
              >
                잔금
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="잔금 금액을 입력해주세요"
                  onChange={(e) => setBalance(Number(e.target.value))}
                  value={balance ?? ""}
                ></StyledInput>
              </div>
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(8);
                }}
              >
                월세
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="웰세 금액을 입력해주세요"
                  onChange={(e) => setRent(Number(e.target.value))}
                  value={rent ?? ""}
                ></StyledInput>
              </div>
              <div
                className="flex flex-col gap-2"
                onClick={() => {
                  setModalOpen(true);
                  setActiveIndex(9);
                }}
              >
                계약일자
                <StyledInput
                  width="100%"
                  fontSize={1.2}
                  lineHeight="100%"
                  placeholder="계약 일자를 입력해주세요"
                  onChange={(e) => setContractDate(new Date(e.target.value))}
                  value={
                    contractDate === ""
                      ? ""
                      : contractDate.toISOString().split("T")[0]
                  }
                ></StyledInput>
              </div>
            </div>
          </div>
        </div>
        <div className="flex flex-col gap-4 mb-12 items-center text-[1.2rem] text-[#6000ff] font-medium">
          <SubmitButton
            width={30}
            height={5}
            fontSize={1.8}
            fontWeight={500}
            disabled={true}
          >
            다음
          </SubmitButton>
          <span onClick={() => router.push("step2")}>skip→</span>
        </div>
      </main>
      <Modal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        clickOutsideClose={true}
      >
        <div className="mt-20 mb-6 px-8 w-full flex flex-col gap-12">
          <div className="flex justify-between items-center">
            {activeIndex % 5 === 0 ? (
              <ArrowLeftIcon
                color="white"
                className="cursor-not-allowed pointer-events-none"
              />
            ) : (
              <ArrowLeftIcon
                width={1.5}
                height={1.5}
                onClick={() => setActiveIndex(activeIndex - 1)}
              />
            )}
            <div className="flex flex-col items-center gap-4">
              <span className="text-[2rem] font-bold text-center">
                {tabTitles[activeIndex]} 입력해주세요.
              </span>
              <span className="text-main text-[1.2rem] font-semibold">
                {activeIndex < 5
                  ? "1. 부동산 표시(" + (activeIndex + 1) + "/5)"
                  : "2. 계약 내용(" + (activeIndex - 4) + "/5)"}
              </span>
            </div>
            {activeIndex % 5 === 4 ? (
              <ArrowRightIcon
                color="white"
                className="cursor-not-allowed pointer-events-none"
              />
            ) : (
              <ArrowRightIcon
                width={1.5}
                height={1.5}
                onClick={() => setActiveIndex(activeIndex + 1)}
              />
            )}
          </div>
          <div className="flex flex-col gap-4 items-center text-[1.2rem] font-medium">
            <div className="w-full h-20 bg-white border border-[#d7d7d7] rounded-[50px]">
              {tabContents[activeIndex]}
            </div>
            <button
              onClick={() => {
                tabsSetFunction[activeIndex]("");
                activeIndex % 5 === 4
                  ? setModalOpen(!modalOpen)
                  : setActiveIndex(activeIndex + 1);
              }}
              className="text-main"
            >
              skip→
            </button>
          </div>
        </div>
        <SubmitButton
          className="justify-self-end"
          width="100%"
          height={6}
          fontSize={1.8}
          fontWeight={500}
          disabled={!Boolean(tabsVariable[activeIndex])}
          borderRadius="none"
          onClick={() =>
            activeIndex % 5 === 4
              ? setModalOpen(!modalOpen)
              : setActiveIndex(activeIndex + 1)
          }
        >
          {activeIndex % 5 === 4 ? "확인" : "다음"}
        </SubmitButton>
      </Modal>
    </>
  );
}

export default ContractCreateNewPage;
