import { LinkIconProps } from "@/app/types/IconOptions";
import ArrowLeftIcon from "./icons/ArrowLeft";

interface BackLinkProps extends LinkIconProps {
  children?: string;
}

export default function BackHeader({
  width = 1.5,
  height = 1.5,
  color = "#000000",
  to,
  children,
}: BackLinkProps) {
  return (
    <header className="grid items-center place-items-center relative w-full">
      <ArrowLeftIcon
        width={width}
        height={height}
        color={color}
        to={to}
        className="col-start-1 row-start-1 justify-self-start ml-12"
      />
      {children && (
        <span className="col-start-1 row-start-1 leading-[2.9rem] font-semibold text-[2.4rem]">
          {children}
        </span>
      )}
    </header>
  );
}
