import IconOptions from "@/app/types/IconOptions";
import { color } from "framer-motion";

const MagicThreeStarIcon = ({
  width = 2,
  height = 2,
  color = "#5046E5 #9134EB",
}: IconOptions) => {
  const startColor = color.split(" ")[0];
  const endColor = color.split(" ")[1];
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 21 21"
      width={typeof width == "number" ? width + "rem" : width}
      height={typeof height == "number" ? height + "rem" : height}
      color="none"
      fill="none"
    >
      <path
        d="M8.38102 13.5549C8.29799 13.2331 8.13024 12.9394 7.89522 12.7043C7.66019 12.4693 7.36648 12.3016 7.04464 12.2185L1.33924 10.7473C1.2419 10.7197 1.15623 10.6611 1.09523 10.5803C1.03423 10.4996 1.00122 10.4012 1.00122 10.3C1.00122 10.1988 1.03423 10.1004 1.09523 10.0197C1.15623 9.93893 1.2419 9.88031 1.33924 9.85268L7.04464 8.38053C7.36637 8.29758 7.66 8.12997 7.89502 7.89512C8.13003 7.66026 8.29785 7.36675 8.38102 7.04508L9.85224 1.33967C9.87959 1.24195 9.93816 1.15586 10.019 1.09453C10.0999 1.0332 10.1985 1 10.3 1C10.4015 1 10.5002 1.0332 10.581 1.09453C10.6619 1.15586 10.7205 1.24195 10.7478 1.33967L12.2181 7.04508C12.3011 7.36692 12.4689 7.66063 12.7039 7.89565C12.9389 8.13068 13.2326 8.29843 13.5545 8.38146L19.2599 9.85175C19.358 9.87881 19.4445 9.93732 19.5062 10.0183C19.5678 10.0993 19.6012 10.1982 19.6012 10.3C19.6012 10.4018 19.5678 10.5007 19.5062 10.5817C19.4445 10.6627 19.358 10.7212 19.2599 10.7482L13.5545 12.2185C13.2326 12.3016 12.9389 12.4693 12.7039 12.7043C12.4689 12.9394 12.3011 13.2331 12.2181 13.5549L10.7469 19.2603C10.7195 19.358 10.661 19.4441 10.5801 19.5055C10.4993 19.5668 10.4006 19.6 10.2991 19.6C10.1976 19.6 10.0989 19.5668 10.0181 19.5055C9.93723 19.4441 9.87866 19.358 9.85131 19.2603L8.38102 13.5549Z"
        fill="url(#paint0_linear_1885_1452)"
        stroke="url(#paint1_linear_1885_1452)"
        strokeWidth="1.4"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M16.9998 3V6.9999"
        stroke="url(#paint2_linear_1885_1452)"
        strokeWidth="1.4"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path d="M18.9994 5H14.9995Z" fill="url(#paint3_linear_1885_1452)" />
      <path
        d="M18.9994 5H14.9995"
        stroke="url(#paint4_linear_1885_1452)"
        strokeWidth="1.4"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M2.99976 14.9995V16.9995"
        stroke="url(#paint5_linear_1885_1452)"
        strokeWidth="1.4"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path d="M3.99995 15.9995H2Z" fill="url(#paint6_linear_1885_1452)" />
      <path
        d="M3.99995 15.9995H2"
        stroke="url(#paint7_linear_1885_1452)"
        strokeWidth="1.4"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <defs>
        <linearGradient
          id="paint0_linear_1885_1452"
          x1="12.352"
          y1="10.3"
          x2="12.352"
          y2="27.7375"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor={startColor} />
          <stop offset="1" stopColor={endColor} />
        </linearGradient>
        <linearGradient
          id="paint1_linear_1885_1452"
          x1="12.352"
          y1="10.3"
          x2="12.352"
          y2="27.7375"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor={startColor} />
          <stop offset="1" stopColor={endColor} />
        </linearGradient>
        <linearGradient
          id="paint2_linear_1885_1452"
          x1="17.61"
          y1="4.99995"
          x2="17.61"
          y2="8.74986"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor={startColor} />
          <stop offset="1" stopColor={endColor} />
        </linearGradient>
        <linearGradient
          id="paint3_linear_1885_1452"
          x1="17.4405"
          y1="5.5"
          x2="17.4405"
          y2="6.4375"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor={startColor} />
          <stop offset="1" stopColor={endColor} />
        </linearGradient>
        <linearGradient
          id="paint4_linear_1885_1452"
          x1="17.4405"
          y1="5.5"
          x2="17.4405"
          y2="6.4375"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor={startColor} />
          <stop offset="1" stopColor={endColor} />
        </linearGradient>
        <linearGradient
          id="paint5_linear_1885_1452"
          x1="3.61001"
          y1="15.9995"
          x2="3.61002"
          y2="17.8744"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor={startColor} />
          <stop offset="1" stopColor={endColor} />
        </linearGradient>
        <linearGradient
          id="paint6_linear_1885_1452"
          x1="3.22048"
          y1="16.4995"
          x2="3.22048"
          y2="17.437"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor={startColor} />
          <stop offset="1" stopColor={endColor} />
        </linearGradient>
        <linearGradient
          id="paint7_linear_1885_1452"
          x1="3.22048"
          y1="16.4995"
          x2="3.22048"
          y2="17.437"
          gradientUnits="userSpaceOnUse"
        >
          <stop stopColor={startColor} />
          <stop offset="1" stopColor={endColor} />
        </linearGradient>
      </defs>
    </svg>
  );
};
export default MagicThreeStarIcon;
