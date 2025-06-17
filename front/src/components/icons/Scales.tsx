import IconOptions from "@/app/types/IconOptions";

const ScalesIcon = ({
  width = 2,
  height = 2,
  color = "#6000FF",
  className,
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
      d="M13.334 13.3334L15.834 6.66675L18.334 13.3334C17.609 13.8751 16.734 14.1667 15.834 14.1667C14.934 14.1667 14.059 13.8751 13.334 13.3334Z"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M1.66602 13.3334L4.16602 6.66675L6.66602 13.3334C5.94102 13.8751 5.06602 14.1667 4.16602 14.1667C3.26602 14.1667 2.39102 13.8751 1.66602 13.3334Z"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M5.83398 17.5H14.1673"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M10 2.5V17.5"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M2.5 5.83332H4.16667C5.83333 5.83332 8.33333 4.99999 10 4.16666C11.6667 4.99999 14.1667 5.83332 15.8333 5.83332H17.5"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

export default ScalesIcon;
