import { ReactNode } from "react";

type DivBoxProps = {
  textColor?: string;
  className?: string;
  children: ReactNode;
};

export default function DivBox({
  textColor = "rgba(0, 0, 0, 0.7)",
  className = "",
  children,
}: DivBoxProps) {
  return (
    <div
      className={className}
      style={{
        background: "white",
        border: "1px solid #f3f4f6",
        borderRadius: "20px",
        fontSize: "1.2rem",
        color: textColor,
      }}
    >
      {children}
    </div>
  );
}
