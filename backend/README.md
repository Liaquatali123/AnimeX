# AnimeX API

FastAPI metadata service backed by the official TMDB API. It does not scrape video pages or extract protected player URLs.

## Configure

Copy `.env.example` to `.env` and set `TMDB_API_KEY` to your TMDB API Read Access Token. Keep this token on the server; never put it in the Android app.

## Run

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Endpoints: `/health`, `/v1/catalog?page=1&limit=20`, `/v1/search?q=naruto`, `/v1/genres`, `/v1/titles/{id}`, `/docs`.

The API returns an honest empty catalog until a provider token is configured. TMDB metadata and images are subject to TMDB terms and attribution requirements. Discovery metadata does not grant streaming rights.
