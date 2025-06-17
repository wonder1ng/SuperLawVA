import { MouseEventHandler } from "react";

export default interface CustomStyledProps {
  width?: number | string;
  height?: number | string;
  fontColor?: string;
  fontSize?: number | string;
  fontWeight?: number | string;
  gap?: number;
  background?: string;
  borderColor?: string;
  borderRadius?: number | string;
  children?: React.ReactNode;
  icon?: React.ReactNode;
  className?: string;
  disabled?: boolean;
  subStyle?: boolean;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}

export interface StyledInputProps extends CustomStyledProps {
  type?: string;
  placeholder?: string;
  value?: number | string;
  lineHeight?: number | string;
  autoFocus?: boolean;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}
