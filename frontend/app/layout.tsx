import type { Metadata } from "next";
import { Fira_Mono, Roboto } from "next/font/google";
import "./globals.css";

const robotoSans = Roboto({
  variable: "--font-roboto-sans",
  subsets: ["latin"],
});

const firaMono = Fira_Mono({
  variable: "--font-fira-mono",
  subsets: ["latin"],
  weight: ["400", "700"],
});

export const metadata: Metadata = {
  title: "Programming Language Guesser",
  description: "Guess the programming language of a code snippet!",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${robotoSans.variable} ${firaMono.variable} antialiased`}
      >
        {children}
      </body>
    </html>
  );
}
