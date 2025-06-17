import IconOptions from "@/app/types/IconOptions";

const BookIcon = ({
  width = 2,
  height = 2,
  color = "#6000FF",
}: IconOptions) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 20 20"
    width={typeof width == "number" ? width + "rem" : width}
    height={typeof height == "number" ? height + "rem" : height}
    color={color}
    fill="none"
  >
    <path
      d="M10 17.5V5.83334"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M13.334 9.99992L15.0007 11.6666L18.334 8.33325"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M18.3327 5V3.33333C18.3327 3.11232 18.2449 2.90036 18.0886 2.74408C17.9323 2.5878 17.7204 2.5 17.4993 2.5H13.3327C12.4486 2.5 11.6008 2.85119 10.9757 3.47631C10.3505 4.10143 9.99935 4.94928 9.99935 5.83333C9.99935 4.94928 9.64816 4.10143 9.02304 3.47631C8.39792 2.85119 7.55007 2.5 6.66602 2.5H2.49935C2.27834 2.5 2.06637 2.5878 1.91009 2.74408C1.75381 2.90036 1.66602 3.11232 1.66602 3.33333V14.1667C1.66602 14.3877 1.75381 14.5996 1.91009 14.7559C2.06637 14.9122 2.27834 15 2.49935 15H7.49935C8.16239 15 8.79828 15.2634 9.26712 15.7322C9.73596 16.2011 9.99935 16.837 9.99935 17.5C9.99935 16.837 10.2627 16.2011 10.7316 15.7322C11.2004 15.2634 11.8363 15 12.4993 15H17.4993C17.7204 15 17.9323 14.9122 18.0886 14.7559C18.2449 14.5996 18.3327 14.3877 18.3327 14.1667V13.0833"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

export default BookIcon;
