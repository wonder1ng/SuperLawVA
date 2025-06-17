"use client";
import { useEffect, useState } from "react";
import { Certification } from "@/app/types/certification";

export default function ResultPage({ params }: { params: { id: string } }) {
  const [data, setData] = useState<Certification | null>(null);

  useEffect(() => {
    fetch(`/api/certification/${params.id}`)
      .then((r) => r.json())
      .then(setData);
  }, [params.id]);

  if (!data) return <p className="p-10">Loading…</p>;

  /* 예시: receiver·sender 표시 */
  return (
    <main className="p-10">
      <h1 className="text-2xl font-bold">{data.title}</h1>

      <h2 className="mt-6 font-semibold">수신자</h2>
      <p>{data.receiver.name}</p>
      <p>{data.receiver.address} {data.receiver.detail_address}</p>

      <h2 className="mt-6 font-semibold">발신자</h2>
      <p>{data.sender.name}</p>
      <p>{data.sender.address} {data.sender.detail_address}</p>

      <h2 className="mt-6 font-semibold">본문</h2>
      <p className="whitespace-pre-wrap">{data.body}</p>

      {/* TODO: 이후 실제 UI 컴포넌트로 교체 */}
    </main>
  );
}