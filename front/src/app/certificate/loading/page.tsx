"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import ProgressRing from "@/components/ProgressRing";
import SubmitButton from "@/components/SubmitButton";
import Image from "next/image";
import MagicTwoStarIcon from "@/components/icons/MagicTwoStar";

function LoadingPage() {
  const router = useRouter();
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const id = setInterval(() => {
      setProgress((p) => Math.min(p + 2, 100));
    }, 120);
    return () => clearInterval(id);
  }, []);

  const goNext = () => router.push("result");

  return (
    <main className="min-h-screen flex flex-col items-center px-6 text-center">
      <div>
        {progress < 100 ? (
          <>
            <h1 className="mt-70 text-[2rem] font-bold mb-2">
              AI가 내용증명서를 생성 중 입니다
            </h1>
            <p className="text-[1.4rem] font-medium">
              문서 내용을 토대로 완벽한 내용증명서를 만들어 드릴게요!
            </p>
          </>
        ) : (
          <>
            <h1 className="mt-70 text-[2rem] font-bold mb-2">
              내용증명서 생성 완료!
            </h1>
            <p className="text-[1.4rem] font-medium">
              내용증명서가 어떻게 완성되었는지 보러 가실까요?
            </p>
          </>
        )}
      </div>
      <div className="mt-25 flex flex-col">
        <ProgressRing
          size={200}
          stroke={5}
          progress={progress}
          from="#6040FF"
          to="#E100FF"
          runningLabel="진행 중"
          doneLabel="완료"
        />
      </div>

      <div className="mt-45">
        {progress >= 100 && (
          <SubmitButton
            width={30}
            height={5.5}
            fontSize={1.7}
            onClick={goNext}
            icon={<MagicTwoStarIcon width={2.4} height={2.4} color="#FFFFFF" />}
          >
            보러가기
          </SubmitButton>
        )}
      </div>
      <div className="fixed bottom-1 mb-20 w-[90%] font-bold h-20 bg-[#fefce8] rounded-[20px] pl-5 pt-3 gap-2 text-sm flex items-start">
        <Image
          src="/warning.png"
          alt="warningIcon"
          width={27}
          height={26}
          className="flex-shrink-0 mt-2"
        />
        <div>
          <p className="text-[1.2rem] text-start">경고</p>
          <span className="text-subText font-normal">
            본 결과는 법령·사례 기반 학습된 AI로, 잘못된 답변을 낼 수도
            있습니다.
          </span>
        </div>
      </div>
    </main>
  );
}

export default LoadingPage;
