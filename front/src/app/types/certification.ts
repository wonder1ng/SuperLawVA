// src/types/certification.ts
export interface PersonInfo {
  name: string;
  address: string;
  detail_address: string;
}

export interface Law {
  law_id: number;
  law: string;
  explanation: string;
  content: string;
}

export interface CourtCase {
  case_id: number;
  case: string;
  explanation: string;
  link: string;
}

export interface Certification {
  _id: number;
  title: string;
  date_created: string;
  sender_name: string;
  sender_address: string;
  sender_detail_address: string;
  recipient_name: string;
  recipient_address: string;
  recipient_detail_address: string;
  body: string;
  strategy_summary: string | null;
  followup_strategy: string;
  legal_basis: Law[];
  case_basis: CourtCase[];
}