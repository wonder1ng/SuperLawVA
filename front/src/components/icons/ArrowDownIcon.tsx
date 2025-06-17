"use client";

import { useRouter } from "next/navigation";
import type { MouseEventHandler } from "react";
import { LinkIconProps } from "@/app/types/IconOptions";

const ArrowDownIcon = ({
  width = 2,
  height = 1,
  color = "#000000",
  opacity = 1,
  className,
  to,
  onClick,
}: LinkIconProps) => {
  const router = useRouter();

  const handleClick: MouseEventHandler<SVGSVGElement> = (e) => {
    if (onClick) {
      onClick(e); // 외부 핸들러 실행
    } else {
      if (!e.defaultPrevented) {
        if (to) {
          router.push(to); // 지정된 경로로 이동
        } else {
          router.back(); // 경로가 없으면 뒤로가기
        }
      }
    }
  };

  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 20 11"
      width={typeof width == "number" ? width + "rem" : width}
      height={typeof height == "number" ? height + 0.1 + "rem" : height}
      color={color}
      fill="none"
      className={className}
      onClick={handleClick}
      role="button"
      style={{ cursor: "pointer" }}
    >
      <path
        d="M19 1L10 10L1 1"
        stroke={color}
        strokeOpacity={opacity}
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
};
export default ArrowDownIcon;
