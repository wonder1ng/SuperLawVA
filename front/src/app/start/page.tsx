"use client";

import SubmitButton from "@/components/SubmitButton";
import { useRouter } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import { useEffect, useState } from "react";

function StartPage() {
  const router = useRouter();

  const [showFirst, setShowFirst] = useState(true);
  const [showSecond, setShowSecond] = useState(false);
  const handleClick = () => {
    router.push("/login");
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      setShowFirst(false);
    }, 300); // 3초 뒤에 첫 main 사라짐

    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      setShowSecond(true);
    }, 1200); // 3초 뒤에 첫 main 사라짐

    return () => clearTimeout(timer);
  }, []);

  return (
    <>
      <AnimatePresence>
        {showFirst && (
          <motion.main
            key="first"
            className="w-full mt-72 h-[45%] flex flex-col justify-center items-center"
            initial={{ opacity: 1 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 1.5 }} // 사라지는 데 3초
          >
            <div className="absolute top-0 w-full h-[45%] mt-72 flex flex-col items-center gap-4">
              <span className="font-semibold text-[4rem] tracking-[-0.04em] bg-gradient-to-r from-[#6000FF] to-[#E100FF] bg-clip-text text-transparent">
                Super LawVA
              </span>
              <span className="font-medium text-[1.4rem] tracking-[-0.04em]">
                Law Virtual Assistant
              </span>
            </div>
          </motion.main>
        )}
      </AnimatePresence>

      {showSecond && (
        <AnimatePresence>
          <motion.main
            key="second"
            className="absolute top-0 w-full mt-72 h-[calc(100%-36rem)] flex flex-col justify-center items-center gap-8"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 2 }} // 나타나는 데 3초
          >
            <span className="min-w-28 max-h-24 mx-72 mt-52">
              <img src="/logo.svg" className="w-full h-full my-px" alt="" />
            </span>
            <div className="w-full flex flex-col items-center justify-center">
              <span className="font-semibold text-[4rem] tracking-[-0.04em]">
                Super LawVA
              </span>
              <span className="text-[1.7rem] leading-8 text-center">
                임대차 계약, 분쟁 대신 분석을.
                <br />
                AI 기반의 쉽고 명확한 계약서 분석
              </span>
            </div>
            <div
              className="w-full px-14 mt-96 flex self-end"
              onClick={handleClick}
            >
              <SubmitButton>시작하기</SubmitButton>
            </div>
          </motion.main>
        </AnimatePresence>
      )}
    </>
  );
}

export default StartPage;
