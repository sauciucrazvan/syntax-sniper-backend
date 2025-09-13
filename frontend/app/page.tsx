"use client";

import { useState } from "react";
import MainScreen from "./views/main_screen";
import Link from "next/link";

export default function Home() {
  const [playing, setPlaying] = useState(false);

  const startGame = () => {
    setPlaying(true);
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center py-8">
      <h1 className="text-xl font-bold">GUESS THE PROGRAMMING LANGUAGE</h1>

      {playing ? (
        <MainScreen />
      ) : (
        <>
          <p className="pt-4 text-center max-w-md">
            Test your knowledge of programming languages by guessing the
            language of the displayed code snippet.
          </p>

          <button
            onClick={startGame}
            className="mt-4 rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
          >
            Start Game
          </button>
        </>
      )}

      <hr className="my-8 w-1/2 border-t" />

      <footer className="text-center text-sm text-gray-500">
        Made by <Link href="https://razvansauciuc.dev">Răzvan Sauciuc</Link>.
        All rights reserved.
      </footer>
    </main>
  );
}
