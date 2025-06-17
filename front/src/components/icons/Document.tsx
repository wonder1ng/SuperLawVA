import { LinkIconProps } from "@/app/types/IconOptions";

const DocumentIcon = ({
  width = 2,
  height = 2,
  color = "#6000FF",
  onClick,
  className,
}: LinkIconProps) => (
  <svg
    width={typeof width == "number" ? width + "rem" : width}
    height={typeof height == "number" ? height + "rem" : height}
    color={color}
    viewBox="0 0 20 20"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    onClick={onClick}
  >
    <path
      d="M14.3142 3.08276C12.8244 1.74192 10.8909 1 8.88659 1H3C2.46957 1 1.96086 1.18964 1.58579 1.52721C1.21071 1.86477 1 2.32261 1 2.8V17.2C1 17.6774 1.21071 18.1352 1.58579 18.4728C1.96086 18.8104 2.46957 19 3 19H15C15.5304 19 16.0391 18.8104 16.4142 18.4728C16.7893 18.1352 17 17.6774 17 17.2V9.1134C17 6.81336 16.0238 4.6214 14.3142 3.08276V3.08276Z"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M11.2969 2.03906V4.81225C11.2969 5.18 11.4576 5.53268 11.7436 5.79272C12.0297 6.05276 12.4177 6.19885 12.8222 6.19885H15.8729"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M7 7.29883H5"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M13 10.9004H5"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M13 14.5H5"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

export default DocumentIcon;
