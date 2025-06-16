import { IconOptions } from "@/app/types/IconOptions";

interface PasswordVisibilityIconProps {
  width?: number;
  height?: number;
  color?: string;
  className?: string;
  isVisible?: boolean;
}

export default function PasswordVisibilityIcon({ 
  width = 16, 
  height = 16, 
  color = "#86868B",
  className = "",
  isVisible = false
}: PasswordVisibilityIconProps) {
  return (
    <svg
      width={width}
      height={height}
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={className}
    >
      {isVisible ? (
        <path
          d="M12 4.5C7 4.5 2.73 7.61 1 12C2.73 16.39 7 19.5 12 19.5C17 19.5 21.27 16.39 23 12C21.27 7.61 17 4.5 12 4.5ZM12 17C9.24 17 7 14.76 7 12C7 9.24 9.24 7 12 7C14.76 7 17 9.24 17 12C17 14.76 14.76 17 12 17ZM12 9C10.34 9 9 10.34 9 12C9 13.66 10.34 15 12 15C13.66 15 15 13.66 15 12C15 10.34 13.66 9 12 9Z"
          fill={color}
        />
      ) : (
        <path
          d="M12 7C14.76 7 17 9.24 17 12C17 12.65 16.87 13.26 16.64 13.83L19.56 16.75C21.07 15.49 22.26 13.86 23 12C21.27 7.61 17 4.5 12 4.5C10.33 4.5 8.77 4.92 7.4 5.66L10.17 8.43C10.74 8.13 11.35 7 12 7ZM2 4.27L3.28 5.55L3.74 6.01C2.08 7.3 0.78 9 0 12C1.73 16.39 6 19.5 11 19.5C12.55 19.5 14.03 19.2 15.38 18.66L15.8 19.08L17.73 21L19 19.73L3.27 4L2 4.27ZM7.53 9.8L9.08 11.35C9.03 11.56 9 11.78 9 12C9 13.66 10.34 15 12 15C12.22 15 12.44 14.97 12.65 14.92L14.2 16.47C13.53 16.8 12.79 17 12 17C9.24 17 7 14.76 7 12C7 11.21 7.2 10.47 7.53 9.8ZM11.84 9.02L14.99 12.17L15.01 12.01C15.01 10.35 13.67 9.01 12.01 9.01L11.84 9.02Z"
          fill={color}
        />
      )}
    </svg>
  );
} 