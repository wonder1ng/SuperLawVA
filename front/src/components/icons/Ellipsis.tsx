import IconOptions from "@/app/types/IconOptions";

const EllipsisIcon = ({
  width = 2,
  height = 2,
  color = "#6000FF",
  className,
}: IconOptions) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 19 19"
    width={typeof width == "number" ? width + "rem" : width}
    height={typeof height == "number" ? height + "rem" : height}
    color={color}
    fill="none"
  >
    {color === "active" ? (
      <>
        <path
          d="M9.5 7.5C10.6046 7.5 11.5 8.39543 11.5 9.5C11.5 10.6046 10.6046 11.5 9.5 11.5C8.39543 11.5 7.5 10.6046 7.5 9.5C7.5 8.39543 8.39543 7.5 9.5 7.5Z"
          stroke="url(#paint0_linear_1872_1326)"
          strokeOpacity="0.7"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M16.5 7.5C17.6046 7.5 18.5 8.39543 18.5 9.5C18.5 10.6046 17.6046 11.5 16.5 11.5C15.3954 11.5 14.5 10.6046 14.5 9.5C14.5 8.39543 15.3954 7.5 16.5 7.5Z"
          stroke="url(#paint1_linear_1872_1326)"
          strokeOpacity="0.7"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M2.5 7.5C3.60457 7.5 4.5 8.39543 4.5 9.5C4.5 10.6046 3.60457 11.5 2.5 11.5C1.39543 11.5 0.5 10.6046 0.5 9.5C0.5 8.39543 1.39543 7.5 2.5 7.5Z"
          stroke="url(#paint2_linear_1872_1326)"
          strokeOpacity="0.7"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <defs>
          <linearGradient
            id="paint0_linear_1872_1326"
            x1="8.7"
            y1="8.7"
            x2="10.3"
            y2="10.3"
            gradientUnits="userSpaceOnUse"
          >
            <stop stopColor="#6000FF" />
            <stop offset="1" stopColor="#E100FF" />
          </linearGradient>
          <linearGradient
            id="paint1_linear_1872_1326"
            x1="15.7"
            y1="8.7"
            x2="17.3"
            y2="10.3"
            gradientUnits="userSpaceOnUse"
          >
            <stop stopColor="#6000FF" />
            <stop offset="1" stopColor="#E100FF" />
          </linearGradient>
          <linearGradient
            id="paint2_linear_1872_1326"
            x1="1.7"
            y1="8.7"
            x2="3.3"
            y2="10.3"
            gradientUnits="userSpaceOnUse"
          >
            <stop stopColor="#6000FF" />
            <stop offset="1" stopColor="#E100FF" />
          </linearGradient>
        </defs>
      </>
    ) : (
      <>
        <path
          d="M9.5 7.5C10.6046 7.5 11.5 8.39543 11.5 9.5C11.5 10.6046 10.6046 11.5 9.5 11.5C8.39543 11.5 7.5 10.6046 7.5 9.5C7.5 8.39543 8.39543 7.5 9.5 7.5Z"
          stroke={color}
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M16.5 7.5C17.6046 7.5 18.5 8.39543 18.5 9.5C18.5 10.6046 17.6046 11.5 16.5 11.5C15.3954 11.5 14.5 10.6046 14.5 9.5C14.5 8.39543 15.3954 7.5 16.5 7.5Z"
          stroke={color}
          strokeLinecap="round"
          strokeLinejoin="round"
        />
        <path
          d="M2.5 7.5C3.60457 7.5 4.5 8.39543 4.5 9.5C4.5 10.6046 3.60457 11.5 2.5 11.5C1.39543 11.5 0.5 10.6046 0.5 9.5C0.5 8.39543 1.39543 7.5 2.5 7.5Z"
          stroke={color}
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </>
    )}
  </svg>
);

export default EllipsisIcon;
