import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import localFont from "next/font/local";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Super Lawva",
  description: "Law Virtual Assistant",
};

const pretendard = localFont({
  src: "fonts/PretendardVariable.woff2",
  display: "swap",
  weight: "45 920",
  variable: "--font-pretendard",
});

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body
        // className={`${pretendard.variable} ${geistSans.variable} ${geistMono.variable} antialiased absolute top-0 left-1/2 -translate-x-1/2 w-full`}
        className={`${pretendard.variable} ${geistSans.variable} ${geistMono.variable} antialiased w-full`}
      >
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full h-full">
          {children}
        </div>
      </body>
    </html>
  );
}
