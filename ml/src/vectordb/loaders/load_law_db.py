"""
[load_law_db.py] - 법령 벡터DB 로딩

OpenAI 임베딩 모델과 함께 Chroma DB를 로드하는 함수.
search_law_chain.py 등 외부에서 import하여 사용합니다.
"""

import os
from dotenv import load_dotenv
from langchain.vectorstores import Chroma
from langchain.embeddings import OpenAIEmbeddings

load_dotenv()
openai_key = os.getenv("OPENAI_API_KEY")

def load_law_vectorstore():
    embedding_model = OpenAIEmbeddings(
        model="text-embedding-3-large",
        openai_api_key=openai_key
    )

    vectorstore = Chroma(
        persist_directory="vectordb/chroma_law/chroma_openai_law",
        embedding_function=embedding_model,
        collection_name="law_chunks_openai"  # ✅ 친구가 지정한 컬렉션명!
    )
        # 문서 개수 출력
    #print("💾 법령 벡터스토어에 저장된 문서 수:", vectorstore._collection.count())

    return vectorstore


