# AnimeX architecture

## Milestone 1 — catalog foundation

- FastAPI provides normalized, paginated metadata endpoints.
- Provider adapters are isolated from route handlers.
- Short in-process TTL cache is the zero-infrastructure default; Redis can replace it when deployment scale requires it.
- Android uses Compose for UI, Paging for catalog loading, Room for favorites/history/cache, Retrofit for networking, Coil for posters, and Media3 for local or authorized playback.

## Data integrity

No fabricated catalog records, poster URLs, audio tracks, subtitle tracks, or playback links. Empty provider configuration means an honest empty state.

## Playback boundary

Media3 supports local files and direct streams that the app is authorized to play. The project does not extract protected third-party player URLs or bypass DRM, authentication, or access controls.
