import IconOptions from "@/app/types/IconOptions";

const HomeIcon = ({
  width = 2,
  height = 2,
  color = "#9CA3AF",
  className,
}: IconOptions) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    width={typeof width == "number" ? width + "rem" : width}
    height={typeof height == "number" ? height + "rem" : height}
    color={color}
    fill={"none"}
  >
    <path
      d="M15.968 7.3599C15.9884 7.37882 16 7.40538 16 7.43319V17.8999C16 17.9552 15.9552 17.9999 15.9 17.9999H10.3861C10.3309 17.9999 10.2861 17.9552 10.2861 17.8999V11.7337C10.2861 11.6785 10.2414 11.6337 10.1861 11.6337H5.81387C5.75864 11.6337 5.71387 11.6785 5.71387 11.7337V17.8999C5.71387 17.9552 5.6691 17.9999 5.61387 17.9999H0.0999997C0.0447713 17.9999 0 17.9552 0 17.8999V7.43319C0 7.40538 0.0115817 7.37882 0.0319638 7.3599L7.93196 0.0259799C7.97033 -0.0096366 8.02967 -0.00963649 8.06804 0.02598L15.968 7.3599Z"
      fill={color}
    />
  </svg>
);

export default HomeIcon;
