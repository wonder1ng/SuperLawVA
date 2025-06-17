import IconOptions from "@/app/types/IconOptions";

const BulbIcon = ({
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
      d="M4.84082 0C7.51393 0.000213365 9.68066 2.16766 9.68066 4.84082C9.68052 6.66003 8.67624 8.24347 7.19238 9.07031V11C7.19238 12.1044 6.29677 12.9998 5.19238 13H4.35156C3.24699 13 2.35156 12.1046 2.35156 11V8.99219C0.942958 8.14595 0.000140711 6.60369 0 4.84082C0 2.16753 2.16753 0 4.84082 0Z"
      fill={color}
    />
  </svg>
);

export default BulbIcon;
