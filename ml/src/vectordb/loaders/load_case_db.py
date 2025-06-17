"""
[load_case_db.py] - 판례 벡터DB 로딩

OpenAI 임베딩 모델과 함께 Chroma DB를 로드하는 함수.
search_case_chain.py 등 외부에서 import하여 사용합니다.
"""

import os
from dotenv import load_dotenv
from langchain.vectorstores import Chroma
from langchain.embeddings import OpenAIEmbeddings

load_dotenv()
openai_key = os.getenv("OPENAI_API_KEY")

def load_case_vectorstore():
    embedding_model = OpenAIEmbeddings(
        model="text-embedding-3-large",
        openai_api_key=openai_key
    )

    vectorstore = Chroma(
        persist_directory="vectordb/chroma_case/chroma_openai_case",
        embedding_function=embedding_model
    )

    return vectorstore
