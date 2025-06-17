"""
[services/shared/llm_config.py] - LLM 설정 공통 모듈

모든 기능에서 사용하는 LLM 클라이언트 설정
"""

from langchain_anthropic import ChatAnthropic
from langchain_openai import ChatOpenAI

def get_claude_llm(temperature: float = 0, max_tokens: int = 8000):
    """Claude LLM 인스턴스 생성 (기본 설정)"""
    return ChatAnthropic(
        model="claude-sonnet-4-20250514", 
        temperature=temperature,
        max_tokens=max_tokens
    )

def get_claude_llm_for_letter():
    """내용증명용 Claude LLM 설정"""
    return ChatAnthropic(
        model="claude-sonnet-4-20250514", 
        temperature=0,  # 정확한 법적 문서를 위해 낮은 temperature
        max_tokens=8000
    )

def get_claude_llm_for_review():
    """계약서 검토용 Claude LLM 설정"""
    return ChatAnthropic(
        model="claude-sonnet-4-20250514", 
        temperature=0,  # 정확한 분석을 위해 낮은 temperature
        max_tokens=6000
    )

def get_claude_llm_for_clause():
    """특약 생성용 Claude LLM 설정"""
    return ChatAnthropic(
        model="claude-sonnet-4-20250514", 
        temperature=0.3,  # 약간의 창의성을 위해 temperature 증가
        max_tokens=4000
    )

def get_openai_llm(temperature: float = 0, max_tokens: int = 4000):
    """OpenAI LLM 인스턴스 생성 (필요시 사용)"""
    return ChatOpenAI(
        model="gpt-4o",
        temperature=temperature,
        max_tokens=max_tokens
    )

# 기본 LLM 인스턴스 (호환성을 위해)
def get_default_llm():
    """기본 LLM 인스턴스 반환"""
    return get_claude_llm()