import IconOptions from "@/app/types/IconOptions";

const UploadIcon = ({
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
      d="M1 11.9286V19H19V11.9286M4.75 8.71429L10 12.5714M10 12.5714L15.25 8.71429M10 12.5714V1"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
    />
  </svg>
);

export default UploadIcon;
