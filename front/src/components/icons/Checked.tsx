import IconOptions from "@/app/types/IconOptions";

const CheckedIcon = ({
  width = 2,
  height = 2,
  color = "#6000ff",
}: IconOptions) => (
  <svg
    width={typeof width == "number" ? width + "rem" : width}
    height={typeof height == "number" ? height + "rem" : height}
    color={color}
    viewBox="0 0 14 14"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M1 3.54545L6.05263 8L13 1"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
    />
  </svg>
);

export default CheckedIcon;
