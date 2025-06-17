"use client";

import CustomStyledProps from "@/app/types/CustomStyledProps";

export default function SubmitButton({
  width = "100%",
  height = 5.5,
  fontColor = "#ffffff",
  fontWeight = 700,
  fontSize = 2.2,
  gap = 0.8,
  background = "#6000ff",
  borderRadius = 50,
  disabled = false,
  children,
  icon,
  className = "flex justify-center items-center",
  borderColor,
  onClick,
  subStyle = false,
}: CustomStyledProps) {
  const buttonBg = disabled
    ? "rgba(128, 128, 128, 0.55)"
    : subStyle
    ? "#F7F9FB"
    : background;

  const border =
    subStyle && !disabled
      ? "0.15rem solid #6000ff"
      : borderColor && !disabled
      ? "0.15rem solid " + borderColor
      : "none";
  const color = disabled
    ? "#ffffff"
    : subStyle
    ? "#5046E5"
    : fontColor || "#ffffff";

  return (
    <button
      className={className}
      disabled={disabled}
      onClick={onClick}
      style={{
        width: typeof width === "number" ? `${width}rem` : width,
        height: typeof height === "number" ? `${height}rem` : height,
        background: buttonBg,
        border: border,
        color: color,
        fontWeight: fontWeight,
        fontSize: typeof fontSize === "number" ? `${fontSize}rem` : fontSize,
        borderRadius:
          typeof borderRadius === "number" ? `${borderRadius}px` : borderRadius,
        gap: typeof gap === "number" ? `${gap}rem` : gap,
        cursor: disabled ? "not-allowed" : "pointer",
        pointerEvents: disabled ? "none" : "auto",
        opacity: disabled ? 0.6 : 1,
      }}
    >
      {icon && <span>{icon}</span>}
      {children}
    </button>
  );
}
