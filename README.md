# AnimeX

AnimeX is a native Android anime discovery app and personal media player.

## Current implementation

- Kotlin + Jetpack Compose dark OTT-style interface.
- FastAPI metadata backend with TMDB API adapter, search, genres, details and in-process TTL caching.
- Paging 3 catalog and search loading.
- Room favorites and local watch-progress history.
- Coil poster loading.
- Media3 ExoPlayer for user-selected local videos and explicitly authorized HTTPS MP4/HLS URLs.
- Audio-track and subtitle-track selection when those tracks are present in the media.
- GitHub Actions CI for backend tests and Android debug compilation.

## Backend setup

1. Get a TMDB API Read Access Token from your TMDB account.
2. Copy `backend/.env.example` to `backend/.env` and set `TMDB_API_KEY`.
3. Run:

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

API routes:
- `GET /health`
- `GET /v1/catalog?page=1&limit=20`
- `GET /v1/search?q=naruto&page=1`
- `GET /v1/genres`
- `GET /v1/titles/{id}`

Keep the TMDB token on the backend. Never embed it in the Android APK.

## Android setup

Open `android/` in Android Studio with JDK 17 and Android SDK 35. Set the backend's HTTPS base URL in `android/gradle.properties`:

```properties
API_BASE_URL=https://your-api-host.example/
```

The URL must end with a slash. For a physical Android phone, use a reachable HTTPS deployment or a properly configured local development network. The manifest intentionally disallows cleartext HTTP.

Build from the repository root:

```bash
gradle -p android :app:assembleDebug
```

## Playback and rights

The player opens local videos through Android's document picker and accepts direct HTTPS media URLs supplied by the user. Use only streams you own or are authorized to access. AnimeX does not scrape protected players, bypass DRM, defeat authentication, or grant rights to third-party video content. Audio and subtitle menus show only tracks actually present in the selected media.

## Data and attribution

TMDB metadata and images are provided under TMDB's terms. Configure required TMDB attribution in the published app before release. Catalog data remains empty until the backend has a valid provider token; the app does not fabricate titles, posters, audio tracks or streams.
