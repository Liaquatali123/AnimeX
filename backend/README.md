# AnimeX API

FastAPI metadata service. It deliberately starts with an empty catalog instead of fabricated titles. Add a provider adapter only for a source whose API terms allow your use case.

Run with `uvicorn app.main:app --reload` from this directory. Health: `/health`; catalog: `/v1/catalog?page=1&limit=20`; OpenAPI: `/docs`.
