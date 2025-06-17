
export interface Certification {
  _id: number;
  user_id: number;
  contract_id: number;

  created_date: string;     // ISO 8601
  title: string;

  receiver: {
    name:           string;
    address:        string;
    detail_address: string;
  };

  sender: {
    name:           string;
    address:        string;
    detail_address: string;
  };

  body: string;
  strategy_summary:  string | null;
  followup_strategy: string;

  legal_basis: Array<{
    law_id:     number;
    law:        string;
    explanation:string;
    content:    string;
  }>;

  case_basis: Array<{
    case_id:    number;
    case:       string;
    explanation:string;
    link:       string;
  }>;

  generation_time: number;
  user_query:      string;
}