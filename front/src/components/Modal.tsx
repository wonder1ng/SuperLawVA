import React, { useEffect } from "react";
import { createPortal } from "react-dom";

interface Props {
  isOpen: boolean;
  setIsOpen: (value: boolean) => void;
  isCenter?: boolean;
  height?: number | string;
  children: React.ReactNode;
  upperChildren?: React.ReactNode;
  clickOutsideClose?: boolean;
  ref?: React.RefObject<HTMLDivElement | null>; // ✅ 추가!
}

const Modal = ({
  isOpen,
  setIsOpen,
  isCenter = false,
  height,
  children,
  upperChildren,
  clickOutsideClose = true,
  ref, // ✅ 추가!
}: Props) => {
  useEffect(() => {
    document.body.style.overflow = isOpen ? "hidden" : "auto";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return createPortal(
    <div
      className={`
        fixed inset-0 z-30 flex justify-center 
        ${isCenter ? "items-center" : "items-end"} 
      `}
    >
      {/* 배경 */}
      <div
        className="absolute inset-0 bg-black opacity-50"
        onClick={() => clickOutsideClose && setIsOpen(false)}
      />

      {/* 상단 children */}
      {upperChildren && (
        <div
          className="z-40 pointer-events-auto"
          onClick={(e) => e.stopPropagation()}
        >
          {upperChildren}
        </div>
      )}

      {/* 메인 모달 */}
      {isCenter ? (
        // ✅ 슬라이딩 모달 (가운데 + 80%)
        <div
          ref={ref} // ✅ 여기 추가!
          onClick={(e) => e.stopPropagation()}
          className={`
            relative z-40 bg-transparent w-full h-4/5
            overflow-x-auto scroll-none snap-x snap-mandatory
            whitespace-nowrap
          `}
          style={{
            scrollbarWidth: "none",
            msOverflowStyle: "none",
          }}
        >
          {children}
        </div>
      ) : (
        // ✅ 일반 모달 (하단 슬라이드업)
        <div
          onClick={(e) => e.stopPropagation()}
          className={`
            relative z-40 bg-white transition-transform duration-300 ease-in-out 
            transform animate-slide-up w-full 
            rounded-t-[50px] max-h-[80%] shadow-[0_-2px_10px_rgba(0,0,0,0.2)] 
            flex flex-col items-center
          `}
          style={{ height }}
        >
          {children}
        </div>
      )}
    </div>,
    document.body
  );
};

export default Modal;
