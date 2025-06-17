export default interface Article {
  title: String;
  result: Boolean;
  content: String;
  reason: String;
  suggestedRevision: String;
  negotiationPoints: String;
  legalBasis: {
    lawId: String | Number;
    law: String;
  };
  caseBasis: {
    caseId: String | Number;
    case: String;
  }[];
}

export interface Agreement {
  result: Boolean;
  content: String;
  reason: String;
  suggestedRevision: String;
  negotiationPoints: String;
  legalBasis: {
    lawId: String | Number;
    law: String;
  };
  caseBasis: {
    caseId: String | Number;
    case: String;
  }[];
}
