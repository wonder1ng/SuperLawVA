import IconOptions from "@/app/types/IconOptions";

const PencilIcon = ({
  width = 2,
  height = 2,
  color = "#6000FF",
  className,
}: IconOptions) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 16 16"
    width={typeof width == "number" ? width + "rem" : width}
    height={typeof height == "number" ? height + "rem" : height}
    color={color}
    fill="none"
  >
    <path
      d="M1 14H3.94756L12.0533 5.89475L9.10579 2.94739L1 11.0526V14Z"
      stroke={color}
      strokeWidth="1.2"
    />
    <path
      d="M12.053 5.89472L13.5862 4.36162C14.3673 3.58056 14.3673 2.31416 13.5862 1.5331L13.4672 1.41407C12.6862 0.633085 11.4199 0.633086 10.6389 1.41407L9.10547 2.94736L12.053 5.89472Z"
      fill={color}
      stroke={color}
      strokeWidth="1.2"
    />
  </svg>
);

export default PencilIcon;
