import IconOptions from "@/app/types/IconOptions";

const ResumeIcon = ({
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
    className={className}
  >
    <path
      d="M15.7483 2.89861C14.212 1.66958 12.3031 1 10.3357 1H3.25C2.65326 1 2.08097 1.18964 1.65901 1.52721C1.23705 1.86477 1 2.32261 1 2.8V17.2C1 17.6774 1.23705 18.1352 1.65901 18.4728C2.08097 18.8104 2.65326 19 3.25 19H16.75C17.3467 19 17.919 18.8104 18.341 18.4728C18.7629 18.1352 19 17.6774 19 17.2V9.66425C19 7.03219 17.8036 4.54284 15.7483 2.89861V2.89861Z"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M12.584 2.03906V4.81225C12.584 5.18 12.7648 5.53268 13.0866 5.79272C13.4084 6.05276 13.8449 6.19885 14.3 6.19885H17.732"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M7.75 7.29907H5.5"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M14.5 10.9001H5.5"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
    <path
      d="M14.5 14.5H5.5"
      stroke={color}
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

export default ResumeIcon;
