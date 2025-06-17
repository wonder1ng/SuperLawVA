import { MouseEventHandler } from "react";

export default interface IconOptions {
  width?: number | string;
  height?: number | string;
  color?: string;
  fill?: string;
  opacity?: number | string;
  className?: string;
}

export interface LinkIconProps extends IconOptions {
  to?: string; // 경로 인자 (선택적)
  onClick?: MouseEventHandler<SVGSVGElement>; // 외부에서 전달하는 클릭 핸들러
}
