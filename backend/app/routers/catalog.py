from fastapi import APIRouter, HTTPException, Query
from app.services.catalog import get_catalog, get_title

router = APIRouter(tags=["catalog"])

@router.get("/catalog")
async def catalog(page: int = Query(1, ge=1), limit: int = Query(20, ge=1, le=50), category: str | None = None):
    return await get_catalog(page=page, limit=limit, category=category)

@router.get("/titles/{title_id}")
async def title_detail(title_id: str):
    item = await get_title(title_id)
    if item is None:
        raise HTTPException(status_code=404, detail="Title not found")
    return item
