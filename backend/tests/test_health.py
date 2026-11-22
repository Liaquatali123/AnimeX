from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"

def test_catalog_without_provider(monkeypatch):
    from app.services import catalog
    monkeypatch.setattr(catalog, "_provider", lambda: None)
    response = client.get("/v1/catalog")
    assert response.status_code == 200
    assert response.json()["items"] == []
    assert response.json()["source"] == "unconfigured"
