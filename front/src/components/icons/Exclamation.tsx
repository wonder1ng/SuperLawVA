import IconOptions from "@/app/types/IconOptions";

const ExclamationIcon = ({
  width = 1.6,
  height = 1.6,
  color = "#6000FF",
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
      d="M6.08309 0.5H2.41691C1.1623 0.5 0.217479 1.64177 0.452233 2.87422L1.69033 9.37422C1.87001 10.3175 2.69474 11 3.65501 11H4.84499C5.80526 11 6.62999 10.3175 6.80967 9.37422L8.04777 2.87422C8.28252 1.64177 7.33771 0.5 6.08309 0.5Z"
      fill={color}
    />
    <circle cx="4.5" cy="14" r="2" fill={color} />
  </svg>
);

export default ExclamationIcon;
