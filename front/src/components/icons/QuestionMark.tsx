import IconOptions from "@/app/types/IconOptions";

const QuestionMarkIcon = ({
  width = 1.6,
  height = 1.6,
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
      d="M3.75 10.293C3.75 7.98828 4.29688 7.28516 5.35156 6.60156C6.23047 6.03516 6.89453 5.54688 6.89453 4.74609C6.89453 4.02344 6.30859 3.53516 5.58594 3.53516C4.82422 3.53516 4.16016 4.10156 4.14062 4.94141H0.703125C0.742188 1.93359 2.94922 0.664062 5.60547 0.664062C8.53516 0.664062 10.6836 1.97266 10.6836 4.55078C10.6836 6.21094 9.78516 7.20703 8.4375 7.98828C7.42188 8.59375 6.91406 9.16016 6.91406 10.293V10.6055H3.75V10.293ZM5.37109 15.2148C4.31641 15.2148 3.45703 14.375 3.47656 13.3203C3.45703 12.2852 4.31641 11.4453 5.37109 11.4453C6.36719 11.4453 7.26562 12.2852 7.26562 13.3203C7.26562 14.375 6.36719 15.2148 5.37109 15.2148Z"
      fill={color}
    />
  </svg>
);

export default QuestionMarkIcon;
