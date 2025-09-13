"use client";

import { useState } from "react";
import { start } from "repl";

export default function Home() {
  const [score, setScore] = useState(0);
  const [playing, setPlaying] = useState(false);

  const startGame = () => {
    setScore(0);
    setPlaying(true);
  };

  return (
    <main className="flex min-h-screen flex-col items-center justify-center">
      <h1 className="text-2xl font-bold">GUESS THE PROGRAMMING LANGUAGE</h1>

      <h2>Hello world! Can you guess the programming language?</h2>

      {playing ? (
        <div>
          <p>Your score: {score}</p>
        </div>
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
