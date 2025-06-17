import IconOptions from "@/app/types/IconOptions";

const CameraIcon = ({
  width = 2,
  height = 2,
  color = "#6000ff",
}: IconOptions) => (
  <svg
    width={typeof width == "number" ? width + "rem" : width}
    height={typeof height == "number" ? height + "rem" : height}
    color={color}
    viewBox="0 0 20 20"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <rect
      x="1"
      y="3"
      width="17"
      height="12"
      rx="1"
      stroke={color}
      strokeWidth="2"
    />
    <circle cx="10" cy="9" r="2.5" stroke={color} />
    <path
      d="M4 3V2C4 1.44772 4.44772 1 5 1H6C6.55228 1 7 1.44772 7 2V3"
      stroke={color}
      strokeLinecap="round"
    />
  </svg>
);

export default CameraIcon;
