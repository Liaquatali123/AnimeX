from app.services.tmdb import TmdbProvider


def test_normalize_real_metadata_shape():
    item = TmdbProvider.normalize({
        "id": 42,
        "title": "Sample Anime Film",
        "overview": "A metadata test, not a catalog fixture.",
        "poster_path": "/poster.jpg",
        "backdrop_path": None,
        "release_date": "2024-01-01",
        "vote_average": 8.1,
        "genre_ids": [16],
    })
    assert item["id"] == "42"
    assert item["title"] == "Sample Anime Film"
    assert item["poster_url"].endswith("/poster.jpg")
    assert item["genre_ids"] == [16]
