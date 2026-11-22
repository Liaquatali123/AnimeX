from time import monotonic
from typing import Any
from app.core.config import CACHE_TTL_SECONDS
from app.services.tmdb import TmdbProvider, ProviderNotConfigured

_cache: dict[tuple, tuple[float, Any]] = {}

def _cached(key: tuple):
    value = _cache.get(key)
    if value and monotonic() - value[0] < CACHE_TTL_SECONDS:
        return value[1]
    return None

def _save(key: tuple, value: Any):
    _cache[key] = (monotonic(), value)
    return value

def _provider():
    try:
        return TmdbProvider()
    except ProviderNotConfigured:
        return None

async def get_catalog(page: int, limit: int, category: int | None = None):
    key = ("catalog", page, limit, category)
    if (value := _cached(key)) is not None:
        return value
    client = _provider()
    if client is None:
        return {"items": [], "page": page, "limit": limit, "total": 0, "has_next": False, "source": "unconfigured", "message": "Set TMDB_API_KEY on the backend to enable live metadata."}
    try:
        return _save(key, await client.discover(page, limit, category))
    finally:
        await client.close()

async def search_titles(query: str, page: int = 1):
    key = ("search", query.casefold(), page)
    if (value := _cached(key)) is not None:
        return value
    client = _provider()
    if client is None:
        return {"items": [], "page": page, "total": 0, "has_next": False, "source": "unconfigured", "message": "Set TMDB_API_KEY on the backend to enable search."}
    try:
        return _save(key, await client.search(query, page))
    finally:
        await client.close()

async def get_genres():
    key = ("genres",)
    if (value := _cached(key)) is not None:
        return value
    client = _provider()
    if client is None:
        return {"items": [], "source": "unconfigured"}
    try:
        return _save(key, await client.genres())
    finally:
        await client.close()

async def get_title(title_id: str):
    key = ("title", title_id)
    if (value := _cached(key)) is not None:
        return value
    client = _provider()
    if client is None:
        return None
    try:
        return _save(key, await client.detail(title_id))
    finally:
        await client.close()
