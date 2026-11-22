from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.routers.catalog import router as catalog_router

app = FastAPI(title="AnimeX API", version="0.1.0", description="Normalized anime metadata API")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost", "http://127.0.0.1"],
    allow_credentials=False,
    allow_methods=["GET"],
    allow_headers=["*"],
)
app.include_router(catalog_router, prefix="/v1")

@app.get("/health", tags=["system"])
async def health():
    return {"status": "ok", "service": "animex-api", "version": app.version}
