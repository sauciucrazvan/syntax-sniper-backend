"use client";

import { useEffect, useState } from "react";
import MainScreen from "./views/main_screen";

export default function Home() {
  const [playing, setPlaying] = useState(false);

  const startGame = () => {
    setPlaying(true);
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center">
      <h1 className="text-xl font-bold">GUESS THE PROGRAMMING LANGUAGE</h1>

      {playing ? (
        <MainScreen />
      ) : (
        <button
          onClick={startGame}
          className="mt-4 rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
        >
          Start Game
        </button>
      )}
    </main>
  );
}
