"use client";

import CustomStyledProps from "@/app/types/CustomStyledProps";

export default function StyledDiv({
  width = "100%",
  height = 5.5,
  fontColor = "#ffffff",
  fontWeight = 700,
  fontSize = 2.2,
  gap = 0.8,
  background = "#6000ff",
  borderRadius = 50,
  borderColor,
  children,
  icon,
  className = "flex justify-center items-center",
}: CustomStyledProps) {
  const border = borderColor ? "0.15rem solid " + borderColor : "none";

  return (
    <div
      className={className}
      style={{
        width: typeof width === "number" ? `${width}rem` : width,
        height: typeof height === "number" ? `${height}rem` : height,
        background: background,
        border: border,
        color: fontColor,
        fontWeight: fontWeight,
        fontSize: typeof fontSize === "number" ? `${fontSize}rem` : fontSize,
        borderRadius:
          typeof borderRadius === "number" ? `${borderRadius}px` : borderRadius,
        gap: typeof gap === "number" ? `${gap}rem` : gap,
      }}
    >
      {icon && <span>{icon}</span>}
      {children}
    </div>
  );
}
