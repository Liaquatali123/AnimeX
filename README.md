# AnimeX

A native Android anime discovery and personal media app built with Kotlin, Jetpack Compose, Paging 3, Room and Media3. The companion backend is FastAPI and exposes normalized catalog metadata from sources that permit API access.

## Project status

This repository is being initialized. The first milestone is catalog discovery and metadata only. Playback is designed for user-owned files and explicitly authorized streams; the app does not bypass DRM, paywalls, access controls, or scrape protected player links.

## Repository layout

- `backend/` — FastAPI catalog API, provider abstraction, cache and tests.
- `android/` — Android Studio project using Kotlin, Compose, Room, Paging and Media3.
- `docs/` — architecture and development notes.

## Backend quick start

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Open `http://127.0.0.1:8000/docs` for the API docs.

## Android

Open the `android/` directory in Android Studio. Set `API_BASE_URL` in `android/gradle.properties` for your backend. The Android app can also browse locally cached catalog metadata when offline.

## Data and playback

Metadata providers must be used according to their terms and rate limits. Only public metadata and authorized playback sources are supported. Local video playback does not require an account or backend.