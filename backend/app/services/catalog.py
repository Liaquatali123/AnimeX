from functools import lru_cache
from time import monotonic
from typing import Any

# Provider adapter boundary: connect only to metadata APIs whose terms permit access.
# Empty results are intentional until a provider and credentials/configuration are supplied.
_CACHE_TTL_SECONDS = 300
_cache: dict[tuple[int, int, str | None], tuple[float, dict[str, Any]]] = {}

async def get_catalog(page: int, limit: int, category: str | None = None) -> dict[str, Any]:
    key = (page, limit, category)
    now = monotonic()
    cached = _cache.get(key)
    if cached and now - cached[0] < _CACHE_TTL_SECONDS:
        return cached[1]
    # TODO: implement a permitted metadata provider adapter (e.g. TMDB with configured API key).
    payload = {
        "items": [],
        "page": page,
        "limit": limit,
        "total": 0,
        "has_next": False,
        "source": "unconfigured",
        "message": "Configure an authorized metadata provider to populate the catalog.",
    }
    _cache[key] = (now, payload)
    return payload

async def get_title(title_id: str) -> dict[str, Any] | None:
    return None
