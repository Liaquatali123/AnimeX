from dotenv import load_dotenv
import os

load_dotenv()
TMDB_API_KEY = os.getenv("TMDB_API_KEY", "").strip()
TMDB_BASE_URL = "https://api.themoviedb.org/3"
IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
CACHE_TTL_SECONDS = int(os.getenv("CACHE_TTL_SECONDS", "300"))
