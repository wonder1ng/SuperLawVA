import IconOptions from "@/app/types/IconOptions";

const CrossIcon = ({
  width = 2,
  height = 2,
  color = "#6000FF",
  // color = "#5E5E5E",
  className,
}: IconOptions) => (
  <svg
    width={typeof width == "number" ? width + "rem" : width}
    height={typeof height == "number" ? height + "rem" : height}
    viewBox="0 0 12 12"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M11 1L1 11"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M1 1L11 11"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

export default CrossIcon;
