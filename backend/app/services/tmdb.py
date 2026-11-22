import httpx
from fastapi import HTTPException
from app.core.config import TMDB_API_KEY, TMDB_BASE_URL, IMAGE_BASE_URL

class ProviderNotConfigured(Exception):
    pass

class TmdbProvider:
    def __init__(self):
        if not TMDB_API_KEY:
            raise ProviderNotConfigured("TMDB_API_KEY is not configured")
        self.client = httpx.AsyncClient(base_url=TMDB_BASE_URL, timeout=12.0, headers={"Authorization": f"Bearer {TMDB_API_KEY}", "accept": "application/json"})

    async def close(self):
        await self.client.aclose()

    async def request(self, path: str, params: dict | None = None):
        try:
            response = await self.client.get(path, params=params or {})
            response.raise_for_status()
            return response.json()
        except httpx.HTTPStatusError as exc:
            raise HTTPException(status_code=502, detail="Metadata provider rejected the request") from exc
        except httpx.HTTPError as exc:
            raise HTTPException(status_code=503, detail="Metadata provider is temporarily unavailable") from exc

    @staticmethod
    def normalize(item: dict) -> dict:
        poster = item.get("poster_path")
        backdrop = item.get("backdrop_path")
        return {
            "id": str(item.get("id", "")),
            "title": item.get("title") or item.get("name") or "Untitled",
            "overview": item.get("overview") or "",
            "poster_url": f"{IMAGE_BASE_URL}{poster}" if poster else None,
            "backdrop_url": f"{IMAGE_BASE_URL}{backdrop}" if backdrop else None,
            "release_date": item.get("release_date") or item.get("first_air_date"),
            "rating": item.get("vote_average"),
            "media_type": item.get("media_type") or "movie",
            "genre_ids": item.get("genre_ids", []),
        }

    async def discover(self, page: int, limit: int, genre: int | None = None):
        params = {"page": page, "include_adult": "false", "sort_by": "popularity.desc", "with_genres": "16", "with_original_language": "ja"}
        if genre is not None:
            params["with_genres"] = f"16,{genre}"
        raw = await self.request("/discover/movie", params)
        items = [self.normalize(x) for x in raw.get("results", [])]
        return {"items": items, "page": raw.get("page", page), "limit": limit, "total": raw.get("total_results", 0), "has_next": raw.get("page", page) < raw.get("total_pages", 0), "source": "tmdb"}

    async def search(self, query: str, page: int = 1):
        raw = await self.request("/search/multi", {"query": query, "page": page, "include_adult": "false"})
        items = [self.normalize(x) for x in raw.get("results", []) if x.get("media_type") in ("movie", "tv")]
        return {"items": items, "page": raw.get("page", page), "total": raw.get("total_results", 0), "has_next": raw.get("page", page) < raw.get("total_pages", 0), "source": "tmdb"}

    async def genres(self):
        raw = await self.request("/genre/movie/list")
        return {"items": raw.get("genres", []), "source": "tmdb"}

    async def detail(self, title_id: str):
        raw = await self.request(f"/movie/{title_id}", {"append_to_response": "videos,credits"})
        item = self.normalize(raw)
        item["genres"] = raw.get("genres", [])
        item["runtime"] = raw.get("runtime")
        item["status"] = raw.get("status")
        item["trailer_key"] = next((v.get("key") for v in raw.get("videos", {}).get("results", []) if v.get("site") == "YouTube" and v.get("type") == "Trailer"), None)
        return item
