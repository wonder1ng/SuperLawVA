"use client";

import { StyledInputProps } from "@/app/types/CustomStyledProps";

export default function StyledInput({
  width = "full",
  fontWeight = 400,
  fontSize = "1.4rem",
  type = "text",
  lineHeight = 2,
  className,
  placeholder,
  value,
  autoFocus = false,
  onChange,
}: StyledInputProps) {
  return (
    <div className={`flex flex-col justify-start ${className}`}>
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        autoFocus={autoFocus}
        onChange={onChange}
        className="bg-transparent border-none px-4 outline-none placeholder:text-gray-400"
        style={{
          width: typeof width === "number" ? `${width}rem` : width,
          fontWeight: fontWeight,
          fontSize: typeof fontSize === "number" ? `${fontSize}rem` : fontSize,
          lineHeight:
            typeof lineHeight === "number" ? `${lineHeight}rem` : lineHeight,
        }}
      />
      <div
        className="mt-[0.6rem] border-[0.15rem] border-[#bbb6]"
        style={{
          width: typeof width === "number" ? `${width}rem` : width,
        }}
      />
    </div>
  );
}
