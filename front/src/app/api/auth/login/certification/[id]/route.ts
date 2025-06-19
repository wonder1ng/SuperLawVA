// 예: app/api/certification/[id]/route.ts
import { NextResponse } from "next/server";
export async function GET(request: Request, { params }: { params: { id: string } }) {
  const id = params.id;
  const data = await getCertificationFromDB(id);  // 혹은 mock 데이터
  return NextResponse.json(data);
}