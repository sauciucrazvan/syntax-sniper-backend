import { useEffect, useState } from "react";

export default function MainScreen() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [score, setScore] = useState(0);
  const [bestScore, setBestScore] = useState(0);
  const [lives, setLives] = useState(3);
  const [data, setData] = useState<{
    code: string;
    language: string;
    options: string[];
  } | null>(null);

  useEffect(() => {
    const fetchSnippet = async () => {
      try {
        setBestScore(
          localStorage.getItem("bestScore")
            ? parseInt(localStorage.getItem("bestScore")!)
            : 0
        );
        setLoading(true);
        setError(null);
        const response = await fetch(`http://localhost:8080/`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error(response.statusText);
        }

        const snippet = await response.json();
        setData(snippet);
      } catch (error: any) {
        setError(error.message || "Something went wrong");
      } finally {
        setLoading(false);
      }
    };

    fetchSnippet();
  }, [score]);

  const handleGuess = (option: string) => {
    if (option === data!.language) {
      setScore(score + 1);
      if (score + 1 > bestScore) {
        localStorage.setItem("bestScore", (score + 1).toString());
        setBestScore(score + 1);
      }
    } else {
      setScore(score - 1);
      setLives(lives - 1);
    }
  };

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (loading || data === null) {
    return <div>Loading...</div>;
  }

  if (lives <= 0) {
    return (
      <section className="pt-4 max-w-[60%] min-w-[60%] flex flex-col gap-4 items-center">
        <div className="--font-fira-mono font-mono text-md">
          Game Over! Your final score: {score}
        </div>

        <button
          onClick={() => {
            setScore(0);
            setLives(3);
            setData(null);
          }}
          className="p-2 rounded-md bg-blue-500 hover:bg-blue-700"
        >
          Play Again
        </button>
      </section>
    );
  }

  return (
    <section className="pt-4 max-w-[60%] flex flex-col gap-4 items-center">
      <div className="--font-fira-mono font-mono text-md">
        Your score: {score} | Best score: {bestScore} | Lives left: {lives}
      </div>

      <code className="w-full p-2">{data.code}</code>

      <div className="flex flex-row gap-1">
        {data.options.map((option: string) => (
          <button
            key={option}
            onClick={() => handleGuess(option)}
            className="p-2 rounded-md bg-blue-500 hover:bg-blue-700 flex-grow"
          >
            {option}
          </button>
        ))}
      </div>
    </section>
  );
}
