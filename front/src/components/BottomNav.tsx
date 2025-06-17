import React, { useEffect, useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import HomeIcon from "./icons/Home";
import ChatIcon from "./icons/Chat";
import UserIcon from "./icons/User";
import EllipsisIcon from "./icons/Ellipsis";

interface NavBtnProps {
  to: string;
  icon: React.ElementType;
  label: string;
}

interface BottomNavProps {
  mainBackGroundColor?: string;
}

const NavBtn: React.FC<NavBtnProps> = ({ to, icon: Icon, label }) => {
  const pathname = usePathname();
  const isActive = pathname === to;

  const gradientStyle = {
    background:
      "linear-gradient(135deg, rgba(96, 0, 255, 0.7) 23.33%, rgba(225, 0, 255, 0.7) 76.67%)",
    WebkitBackgroundClip: "text",
    WebkitTextFillColor: "transparent",
    backgroundClip: "text",
    color: "transparent",
  };

  return (
    <Link href={to} className="flex flex-col items-center gap-1">
      <Icon
        width="2.4rem"
        height="2.4rem"
        color={isActive ? "active" : "#9CA3AF"}
      />
      <span
        className="text-[1.2rem]"
        style={isActive ? gradientStyle : { color: "#9CA3AF" }}
      >
        {label}
      </span>
    </Link>
  );
};

const BottomNav: React.FC<BottomNavProps> = ({ mainBackGroundColor }) => {
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  if (!isClient) return null;

  return (
    <nav
      className="sticky bottom-0 w-full h-28"
      style={{
        backgroundColor: mainBackGroundColor || "white",
      }}
    >
      <div className="h-full border-t border-[#c6c6c8] bg-white pt-4 z-10 rounded-t-[50px]">
        <div className="flex justify-around items-center h-full">
          <NavBtn to="/" icon={HomeIcon} label="홈" />
          <NavBtn to="/register" icon={ChatIcon} label="채팅" />
          <NavBtn to="/more" icon={EllipsisIcon} label="더보기" />
        </div>
      </div>
    </nav>
  );
};

export default BottomNav;
