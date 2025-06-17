"""
[schema/letter_schema.py] - 내용증명 전용 스키마 (수정된 버전)

내용증명 생성 기능에서만 사용하는 전용 스키마들
공통 스키마는 shared_schemas에서 import
"""

from pydantic import BaseModel, Field
from typing import Optional, List
from services.schema.shared_schema import (
    BaseInput, 
    BaseOutput, 
    PersonInfo, 
    LegalBasis, 
    CaseBasis, 
    CertificationMetadata
)

# INPUT 정보 (내용증명 전용)
class LetterGenerationInput(BaseInput):
    """내용증명 생성 입력"""
    user_query: str                        # 사용자의 자연어 질문 (내용증명 특화)

# OUTPUT 정보 (내용증명 전용)
class LetterGenerationOutput(BaseModel):
    # 1순위: 핵심 내용
    title: str                             
    receiver: PersonInfo                   
    sender: PersonInfo                     
    body: str                              
    
    # 2순위: 전략 정보
    strategy_summary: str                  
    followup_strategy: str                 
    
    # 3순위: 참고 자료
    legal_basis: List[LegalBasis]          
    case_basis: List[CaseBasis]            
    
    # 4순위: 메타데이터
    id: Optional[int] = None              
    user_id: Optional[int] = None          
    contract_id: Optional[int] = None      
    created_date: Optional[str] = None     
    certification_metadata: CertificationMetadata
    
    # 5순위: 기록용
    user_query: str

# LLM이 생성하는 임시 출력 스키마 (내부 처리용) - 수정된 버전
class TempLetterOutput(BaseModel):
    """LLM이 생성하는 임시 구조"""
    title: str = Field(description="내용증명 제목")
    receiver_name: str = Field(description="수신인 이름 (계약서의 임대인 이름 정확히 사용)")
    receiver_address: str = Field(description="수신인 기본주소 (계약서 임대인의 address 필드만, detail_address 제외)")
    receiver_detail_address: Optional[str] = Field(description="수신인 상세주소 (계약서 임대인의 detail_address 필드만)", default="")
    sender_name: str = Field(description="발신인 이름 (계약서의 임차인 이름 정확히 사용)")
    sender_address: str = Field(description="발신인 기본주소 (계약서 임차인의 address 필드만, detail_address 제외)")
    sender_detail_address: Optional[str] = Field(description="발신인 상세주소 (계약서 임차인의 detail_address 필드만)", default="")
    body: str = Field(description="내용증명 본문")
    strategy_summary: str = Field(description="전략 요약")
    followup_strategy: str = Field(description="후속 전략")