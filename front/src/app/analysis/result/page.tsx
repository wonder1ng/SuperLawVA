"use client";

import { useRouter } from "next/navigation";
import StatusIcon from "@/components/icons/Status";
import { useEffect, useRef, useState } from "react";
import BackHeader from "@/components/BackHeader";
import InfoIcon from "@/components/icons/Info";
import ArrowLeftIcon from "@/components/icons/ArrowLeft";
import ArrowRightIcon from "@/components/icons/ArrowRight";
import DivBox from "@/components/DivBox";
import MagnifyingGlassIcon from "@/components/icons/MagnifyingGlass";
import Article, { Agreement } from "@/app/types/Article";
import DocumentIcon from "@/components/icons/Document";
import Modal from "@/components/Modal";
import QuestionMarkIcon from "@/components/icons/QuestionMark";
import BookIcon from "@/components/icons/Book";
import ScalesIcon from "@/components/icons/Scales";
import ArrowDownIcon from "@/components/icons/ArrowDownIcon";

function AnalysisResultPage() {
  const router = useRouter();
  const [modalOpen, setModalOpen] = useState(false);
  const [activeTitle, setActiveTitle] = useState(false);
  // const [articles, setArticles] = useState<string[]>([]);
  const [articleDetailArrayIndex, setArticleDetailArrayIndex] =
    useState<number>(0);
  const [agreementDetailArrayIndex, setAgreementDetailArrayIndex] =
    useState<number>(0);
  const containerRef = useRef<HTMLDivElement | null>(null);
  // const [articles, setArticles] = useState<Article[]>([])
  const [articles, setArticles] = useState<Article[]>([
    {
      result: true,
      title: "제 6조: 채무불이행과 손해배상",
      content:
        "세입자가 계약을 어기면 보증금을 돌려주지 않고, 추가로 월세 2개월치를 배상금으로 낸다",
      suggestedRevision:
        "계약 위반으로 실제 손해가 생겼을 시 보증금에서 차감하고, 나머지는 돌려준다",
      reason:
        "증금도 안 돌려주고 월세 2개월치까지 더 내라는 건 너무 과해요. 실제 피해보다 훨씬 큰 돈을 요구하는 거예요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "소득세법 시행령 제122조 제 1항",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울중앙법 2029가합18",
        },
        {
          caseId: 2,
          case: "부산지법 1818가합18",
        },
      ],
    },
    {
      result: false,
      title: "제 7조: 계약 해지 사유",
      content: "임차인은 정당한 사유 없이 계약을 해지할 수 없다",
      suggestedRevision: "정당한 사유가 있다면 계약 해지가 가능하다",
      reason: "계약 해지가 불가하면 세입자 권리가 심각하게 제한돼요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "주택임대차보호법 제6조",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울고법 2028나7890",
        },
      ],
    },
    {
      result: false,
      title: "제 6조: 채무불이행과 손해배상",
      content:
        "세입자가 계약을 어기면 보증금을 돌려주지 않고, 추가로 월세 2개월치를 배상금으로 낸다",
      suggestedRevision:
        "계약 위반으로 실제 손해가 생겼을 시 보증금에서 차감하고, 나머지는 돌려준다",
      reason:
        "증금도 안 돌려주고 월세 2개월치까지 더 내라는 건 너무 과해요. 실제 피해보다 훨씬 큰 돈을 요구하는 거예요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "소득세법 시행령 제122조 제 1항",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울중앙법 2029가합18",
        },
        {
          caseId: 2,
          case: "부산지법 1818가합18",
        },
      ],
    },
    {
      result: true,
      title: "제 7조: 계약 해지 사유",
      content: "임차인은 정당한 사유 없이 계약을 해지할 수 없다",
      suggestedRevision: "정당한 사유가 있다면 계약 해지가 가능하다",
      reason: "계약 해지가 불가하면 세입자 권리가 심각하게 제한돼요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "주택임대차보호법 제6조",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울고법 2028나7890",
        },
      ],
    },
    {
      result: false,
      title: "제 6조: 채무불이행과 손해배상",
      content:
        "세입자가 계약을 어기면 보증금을 돌려주지 않고, 추가로 월세 2개월치를 배상금으로 낸다",
      suggestedRevision:
        "계약 위반으로 실제 손해가 생겼을 시 보증금에서 차감하고, 나머지는 돌려준다",
      reason:
        "증금도 안 돌려주고 월세 2개월치까지 더 내라는 건 너무 과해요. 실제 피해보다 훨씬 큰 돈을 요구하는 거예요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "소득세법 시행령 제122조 제 1항",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울중앙법 2029가합18",
        },
        {
          caseId: 2,
          case: "부산지법 1818가합18",
        },
      ],
    },
    {
      result: true,
      title: "제 7조: 계약 해지 사유",
      content: "임차인은 정당한 사유 없이 계약을 해지할 수 없다",
      suggestedRevision: "정당한 사유가 있다면 계약 해지가 가능하다",
      reason: "계약 해지가 불가하면 세입자 권리가 심각하게 제한돼요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "주택임대차보호법 제6조",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울고법 2028나7890",
        },
      ],
    },
  ]);

  // const [agreements, setAgreements] = useState<Agreement[]>([]);

  const [agreements, setAgreements] = useState<Agreement[]>([
    {
      result: true,
      content: "임차인은 정당한 사유 없이 계약을 해지할 수 없다",
      suggestedRevision: "정당한 사유가 있다면 계약 해지가 가능하다",
      reason: "계약 해지가 불가하면 세입자 권리가 심각하게 제한돼요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "주택임대차보호법 제6조",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울고법 2028나7890",
        },
      ],
    },
    {
      result: true,
      content:
        "세입자가 계약을 어기면 보증금을 돌려주지 않고, 추가로 월세 2개월치를 배상금으로 낸다",
      suggestedRevision:
        "계약 위반으로 실제 손해가 생겼을 시 보증금에서 차감하고, 나머지는 돌려준다",
      reason:
        "증금도 안 돌려주고 월세 2개월치까지 더 내라는 건 너무 과해요. 실제 피해보다 훨씬 큰 돈을 요구하는 거예요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "소득세법 시행령 제122조 제 1항",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울중앙법 2029가합18",
        },
        {
          caseId: 2,
          case: "부산지법 1818가합18",
        },
      ],
    },
    {
      result: false,
      content: "임차인은 정당한 사유 없이 계약을 해지할 수 없다",
      suggestedRevision: "정당한 사유가 있다면 계약 해지가 가능하다",
      reason: "계약 해지가 불가하면 세입자 권리가 심각하게 제한돼요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "주택임대차보호법 제6조",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울고법 2028나7890",
        },
      ],
    },
    {
      result: false,
      content:
        "세입자가 계약을 어기면 보증금을 돌려주지 않고, 추가로 월세 2개월치를 배상금으로 낸다",
      suggestedRevision:
        "계약 위반으로 실제 손해가 생겼을 시 보증금에서 차감하고, 나머지는 돌려준다",
      reason:
        "증금도 안 돌려주고 월세 2개월치까지 더 내라는 건 너무 과해요. 실제 피해보다 훨씬 큰 돈을 요구하는 거예요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "소득세법 시행령 제122조 제 1항",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울중앙법 2029가합18",
        },
        {
          caseId: 2,
          case: "부산지법 1818가합18",
        },
      ],
    },
    {
      result: true,
      content: "임차인은 정당한 사유 없이 계약을 해지할 수 없다",
      suggestedRevision: "정당한 사유가 있다면 계약 해지가 가능하다",
      reason: "계약 해지가 불가하면 세입자 권리가 심각하게 제한돼요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "주택임대차보호법 제6조",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울고법 2028나7890",
        },
      ],
    },
    {
      result: false,
      content:
        "세입자가 계약을 어기면 보증금을 돌려주지 않고, 추가로 월세 2개월치를 배상금으로 낸다",
      suggestedRevision:
        "계약 위반으로 실제 손해가 생겼을 시 보증금에서 차감하고, 나머지는 돌려준다",
      reason:
        "증금도 안 돌려주고 월세 2개월치까지 더 내라는 건 너무 과해요. 실제 피해보다 훨씬 큰 돈을 요구하는 거예요.",
      negotiationPoints: "손해 범위를 구체화하여 분쟁 요소를 방지해요.",
      legalBasis: {
        lawId: 123,
        law: "소득세법 시행령 제122조 제 1항",
      },
      caseBasis: [
        {
          caseId: 1,
          case: "서울중앙법 2029가합18",
        },
        {
          caseId: 2,
          case: "부산지법 1818가합18",
        },
      ],
    },
  ]);

  const titleArray = ["계약 조항 분석 결과", "특약 사항 분석 결과"].map(
    (value, index) => (
      <li
        key={index}
        className="w-full h-20 px-10 py-6  flex items-center text-[1.8rem] text-[#3a3a40] font-semibold border border-[#d7d7d7] rounded-[50px] bg-white"
      >
        <div className="w-8 flex justify-center items-center flex-shrink-0">
          {!activeTitle ? (
            <ArrowLeftIcon
              color="white"
              className="cursor-not-allowed pointer-events-none"
            />
          ) : (
            <ArrowLeftIcon
              width={2}
              height={2}
              onClick={() => setActiveTitle(!activeTitle)}
              className="z-10 cursor-pointer"
            />
          )}
        </div>
        <div className="flex justify-center items-center gap-4 px-4 flex-grow overflow-hidden">
          <span className="flex-shrink-0">
            <MagnifyingGlassIcon />
          </span>
          <span className="truncate">{value}</span>
        </div>
        <div className="w-8 flex justify-center items-center flex-shrink-0">
          {activeTitle ? (
            <ArrowRightIcon
              color="white"
              className="cursor-not-allowed pointer-events-none"
            />
          ) : (
            <ArrowRightIcon
              width={2}
              height={2}
              onClick={() => setActiveTitle(!activeTitle)}
              className="z-10 cursor-pointer"
            />
          )}
        </div>
      </li>
    )
  );

  const articleArray = articles.map(({ title, result }, index) => {
    return (
      <li
        key={index}
        className="w-full mx-8 py-6 px-8 flex items-center bg-white border border-[#f3f4f6] rounded-[20px]"
        onClick={
          result
            ? undefined
            : () => {
                const falseIndex = articleFalseArray.findIndex(
                  (i) => i === index
                );
                setArticleDetailArrayIndex(falseIndex);
                setModalOpen(true);
              }
        }
      >
        <span className="flex flex-1 gap-4 items-center overflow-hidden text-[1.4rem] font-medium">
          <span
            className={`flex-shrink-0 w-[1.4rem] h-[1.4rem] rounded-full bg-[#${
              result ? "32D74B" : "FF9500"
            }]`}
          />
          {title}
        </span>

        {!result && <DocumentIcon width={1.6} height={1.6} color="black" />}
      </li>
    );
  });

  const agreementArray = agreements.map(({ content, result }, index) => {
    return (
      <li
        key={index}
        className="w-full mx-8 py-6 px-8 flex items-center bg-white border border-[#f3f4f6] rounded-[20px]"
        onClick={
          result
            ? undefined
            : () => {
                const falseIndex = agreementFalseArray.findIndex(
                  (i) => i === index
                );
                setAgreementDetailArrayIndex(falseIndex);
                setModalOpen(true);
              }
        }
      >
        <span className="flex flex-1 gap-4 items-center overflow-hidden text-[1.4rem] font-medium">
          <span
            className={`flex-shrink-0 w-[1.4rem] h-[1.4rem] rounded-full bg-[#${
              result ? "32D74B" : "FF9500"
            }]`}
          />
          <span className="truncate pr-4">{index + 1 + ". " + content}</span>
        </span>

        {!result && <DocumentIcon width={1.6} height={1.6} color="black" />}
      </li>
    );
  });

  const articleFalseArray = articles.reduce<number[]>(
    (p, { result }, index) => (result ? p : [...p, index]),
    []
  );
  const agreementFalseArray = agreements.reduce<number[]>(
    (p, { result }, index) => (result ? p : [...p, index]),
    []
  );

  const articleDetailArray = articles.map(
    (
      {
        title,
        result,
        content,
        reason,
        suggestedRevision,
        negotiationPoints,
        legalBasis,
        caseBasis,
      },
      index
    ) => {
      return result ? (
        ""
      ) : (
        <div
          key={index}
          className={`inline-block w-[90%] h-full py-8 mx-2${
            index === articleFalseArray[0]
              ? " ml-[6.5rem]"
              : index === articleFalseArray.at(-1)
              ? " mr-[6.5rem]"
              : ""
          } rounded-[40px] snap-center align-top bg-white overflow-y-auto max-h-full`}
        >
          <div className="flex flex-col justify-center items-center px-8">
            <div className="w-full flex items-center text-[1.4rem] font-medium text-[#FF9500]">
              <span className="flex-shrink-0 w-[1.4rem] h-[1.4rem] rounded-full bg-[#FF9500]" />
              &nbsp;- 확인 필요 계약 조항
              {` (${articleFalseArray.findIndex((v) => v === index) + 1}/${
                articleFalseArray.length
              })`}
            </div>
            <hr className="w-[calc(100%+4rem)] my-4 -mx-4 border-0 border-t border-t-[#F3F4F6]" />
            <div className="w-full flex flex-col justify-center items-center gap-12">
              <div className="w-full flex flex-col justify-center items-center gap-4">
                <div className="w-full text-[1.6rem] text-start">
                  <span className="font-semibold">
                    {title.split("조")[0] + "조"}
                  </span>
                  {title.split("조")[1].trim()}
                </div>
                <div className="w-full flex gap-4 text-[1.2rem] text-black/70 border border-[#F3F4F6] rounded-[20px]">
                  <span className="px-10 flex items-center border border-[#F3F4F6] rounded-[20px] text-[1.2rem] font-semibold">
                    원본
                  </span>
                  <span className="my-6 text-wrap pr-4">{content}</span>
                </div>
                <div className="w-full flex gap-4 bg-main/5 text-[1.2rem] border border-main rounded-[20px]">
                  <div className="px-10 flex items-center bg-main/10 border border-main rounded-[20px] text-main text-[1.2rem] font-semibold">
                    제안
                  </div>
                  <span className="my-6 text-wrap pr-4">
                    {suggestedRevision}
                  </span>
                </div>
              </div>
              <div className="w-full flex flex-col gap-4">
                <div className="flex items-center gap-2 text-[1.6rem] font-semibold">
                  <QuestionMarkIcon />
                  이유
                </div>
                <DivBox className="w-full py-6 px-4 text-wrap">{reason}</DivBox>
              </div>
              <div className="w-full flex flex-col gap-4">
                <div className="flex items-center gap-2 text-[1.6rem] font-semibold">
                  <InfoIcon />
                  협상 전략 및 법적 영향
                </div>
                <DivBox className="w-full py-6 px-4 text-wrap">
                  {negotiationPoints}
                </DivBox>
              </div>
              <div className="w-full flex flex-col gap-4">
                <div className="flex items-center justify-between text-[1.6rem] font-semibold">
                  <div className="flex items-center gap-2">
                    <ScalesIcon />
                    참고한 법령
                  </div>
                  <ArrowDownIcon className="mr-[2.1rem]" />
                </div>
                <DivBox className="w-full py-6 px-4 flex justify-between items-center text-wrap">
                  {legalBasis.law}
                  <ArrowDownIcon className="mr-4" />
                </DivBox>
              </div>
              <div className="w-full flex flex-col gap-4">
                <div className="flex items-center justify-between text-[1.6rem] font-semibold">
                  <div className="flex items-center gap-2">
                    <BookIcon />
                    참고한 판례
                  </div>
                  <ArrowDownIcon className="mr-[2.1rem]" />
                </div>
                {caseBasis.map(({ case: caseName }, index) => (
                  <DivBox
                    key={index}
                    className="w-full py-6 px-4 flex justify-between items-center text-wrap"
                  >
                    {caseName}
                    <ArrowDownIcon className="mr-4" />
                  </DivBox>
                ))}
              </div>
            </div>
          </div>
        </div>
      );
    }
  );

  const agreementDetailArray = agreements.map(
    (
      {
        result,
        content,
        reason,
        suggestedRevision,
        negotiationPoints,
        legalBasis,
        caseBasis,
      },
      index
    ) => {
      return result ? (
        ""
      ) : (
        <div
          key={index}
          className={`inline-block w-[90%] h-full py-8 mx-2${
            index === agreementFalseArray[0]
              ? " ml-[6.5rem]"
              : index === agreementFalseArray.at(-1)
              ? " mr-[6.5rem]"
              : ""
          } rounded-[40px] snap-center align-top bg-white overflow-y-auto max-h-full`}
        >
          <div className="flex flex-col justify-center items-center px-8">
            <div className="w-full flex items-center text-[1.4rem] font-medium text-[#FF9500]">
              <span className="flex-shrink-0 w-[1.4rem] h-[1.4rem] rounded-full bg-[#FF9500]" />
              &nbsp;- 확인 필요 특약 사항
              {` (${agreementFalseArray.findIndex((v) => v === index) + 1}/${
                agreementFalseArray.length
              })`}
            </div>
            <hr className="w-[calc(100%+4rem)] my-4 -mx-4 border-0 border-t border-t-[#F3F4F6]" />
            <div className="w-full flex flex-col justify-center items-center gap-12">
              <div className="w-full flex flex-col justify-center items-center gap-4">
                <div className="w-full text-[1.6rem] text-start">
                  <span className="font-semibold">{index + 1 + "번 "}</span>
                  특약 사항
                </div>
                <div className="w-full flex gap-4 text-[1.2rem] text-black/70 border border-[#F3F4F6] rounded-[20px]">
                  <span className="px-10 flex items-center border border-[#F3F4F6] rounded-[20px] text-[1.2rem] font-semibold">
                    원본
                  </span>
                  <span className="my-6 text-wrap pr-4">{content}</span>
                </div>
                <div className="w-full flex gap-4 bg-main/5 text-[1.2rem] border border-main rounded-[20px]">
                  <div className="px-10 flex items-center bg-main/10 border border-main rounded-[20px] text-main text-[1.2rem] font-semibold">
                    제안
                  </div>
                  <span className="my-6 text-wrap pr-4">
                    {suggestedRevision}
                  </span>
                </div>
              </div>
              <div className="w-full flex flex-col gap-4">
                <div className="flex items-center gap-2 text-[1.6rem] font-semibold">
                  <QuestionMarkIcon />
                  이유
                </div>
                <DivBox className="w-full py-6 px-4 text-wrap">{reason}</DivBox>
              </div>
              <div className="w-full flex flex-col gap-4">
                <div className="flex items-center gap-2 text-[1.6rem] font-semibold">
                  <InfoIcon />
                  협상 전략 및 법적 영향
                </div>
                <DivBox className="w-full py-6 px-4 text-wrap">
                  {negotiationPoints}
                </DivBox>
              </div>
              <div className="w-full flex flex-col gap-4">
                <div className="flex items-center justify-between text-[1.6rem] font-semibold">
                  <div className="flex items-center gap-2">
                    <ScalesIcon />
                    참고한 법령
                  </div>
                  <ArrowDownIcon className="mr-[2.1rem]" />
                </div>
                <DivBox className="w-full py-6 px-4 flex justify-between items-center text-wrap">
                  {legalBasis.law}
                  <ArrowDownIcon className="mr-4" />
                </DivBox>
              </div>
              <div className="w-full flex flex-col gap-4">
                <div className="flex items-center justify-between text-[1.6rem] font-semibold">
                  <div className="flex items-center gap-2">
                    <BookIcon />
                    참고한 판례
                  </div>
                  <ArrowDownIcon className="mr-[2.1rem]" />
                </div>
                {caseBasis.map(({ case: caseName }, index) => (
                  <DivBox
                    key={index}
                    className="w-full py-6 px-4 flex justify-between items-center text-wrap"
                  >
                    {caseName}
                    <ArrowDownIcon className="mr-4" />
                  </DivBox>
                ))}
              </div>
            </div>
          </div>
        </div>
      );
    }
  );

  useEffect(() => {
    if (modalOpen && containerRef.current) {
      const container = containerRef.current;
      const target = container.children[
        activeTitle ? agreementDetailArrayIndex : articleDetailArrayIndex
      ] as HTMLElement | undefined;
      if (target) {
        container.scrollTo({
          left: target.offsetLeft - container.offsetLeft,
          behavior: "instant",
        });
      }
    }
  }, [
    modalOpen,
    articleDetailArrayIndex,
    agreementDetailArrayIndex,
    activeTitle,
  ]);

  return (
    <>
      <div className="h-20 w-full flex flex-col justify-center items-center">
        <StatusIcon className="mt-[1.4rem]" />
      </div>
      <BackHeader to="/">월세 임대차 계약서</BackHeader>
      <main className="flex flex-col items-center mt-[3rem] gap-12 h-auto">
        <div className="w-full my-12 flex flex-col gap-4 justify-center items-center">
          <div className="mx-16 px-8 py-6 gap-12 flex justify-center items-center bg-white rounded-[20px]">
            <span className="w-full flex-1 text-[1.4rem] font-medium text-[#32D74B]">
              <span className="inline-block align-middle w-[1.4rem] h-[1.4rem] rounded-full bg-[#32D74B]" />
              &nbsp;- 문제 없음
            </span>
            <span className="w-full flex-1 text-[1.4rem] font-medium text-[#FF9500]">
              <span className="inline-block align-middle w-[1.4rem] h-[1.4rem] rounded-full bg-[#FF9500]" />
              &nbsp;- 확인 필요
            </span>
          </div>
          <div className="w-full p-8 flex flex-col gap-12 items-center justify-center bg-white opacity-70 rounded-[50px]">
            <span className="text-center text-[1.8rem] font-semibold">
              {activeTitle ? "특약 사항" : "계약 조항"} 목록
            </span>
            <ul className="w-full flex flex-col gap-4 items-center justify-center">
              {activeTitle ? agreementArray : articleArray}
            </ul>
          </div>
        </div>
        <DivBox className="mb-6 w-full flex flex-col gap-12">
          <div className="flex w-full justify-between items-center">
            {titleArray[Number(activeTitle)]}
          </div>
        </DivBox>
      </main>
      <Modal
        isOpen={modalOpen}
        setIsOpen={setModalOpen}
        clickOutsideClose={true}
        isCenter={true}
        ref={containerRef}
      >
        {activeTitle ? agreementDetailArray : articleDetailArray}
      </Modal>
    </>
  );
}

export default AnalysisResultPage;