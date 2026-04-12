# Scanly Backend 🚀

Der Kern-Service für das Scanly SB-Kassen-System. Diese Anwendung stellt die REST-API für die Produktverwaltung, Bestellabwicklung und Zahlungsabwicklung bereit.

---

## 🏛️ System-Architektur

Dieses Repository dient als zentraler Einstiegspunkt für die gesamte Scanly-Anwendung (Multi-Repo-Setup). Über die hier enthaltene `docker-compose.yml` und das `Makefile` lässt sich das gesamte System (Datenbank, Backend und Frontend) orchestrieren.

---

## 🛠️ Tech-Stack
- **Framework:** Spring Boot (Java)
- **Runtime:** Java 21+
- **Database:** PostgreSQL 17
- **Containerization:** Docker / Docker Compose
- **Orchestration:** GNU Make
- **Testing:** JUnit 5, Testcontainers

---

## 🚀 Schnellstart (Empfohlen)

Für den schnellsten Einstieg nutzen wir ein **Makefile**, das alle Docker-Befehle kapselt und eine übersichtliche Erfolgsmeldung ausgibt.

### Voraussetzungen
- Docker & Docker Compose
- [GNU Make](https://www.gnu.org/software/make/) (Standard unter Linux/macOS; unter Windows via Git Bash oder Chocolatey/Winget installierbar)

### Befehle

| Befehl | Beschreibung |
| :--- | :--- |
| `make up` | Baut und startet das gesamte System (Frontend, Backend, DB) im Hintergrund. |
| `make logs` | Zeigt die Live-Logs aller Container an. |
| `make status` | Zeigt den Status aller laufenden Container. |
| `make restart` | Startet das gesamte System neu. |
| `make down` | Stoppt und entfernt alle Container. |

Nach einem `make up` ist das System wie folgt erreichbar:
- **🌍 Frontend:** [http://localhost](http://localhost)
- **⚙️ Backend API:** [http://localhost:8080/api](http://localhost:8080/api)
- **📊 Datenbank:** `localhost:5432`

---

## 💻 Lokale Entwicklung

Falls du nur das Backend lokal (außerhalb von Docker) entwickeln möchtest, aber die Datenbank in Docker benötigst:

1. **Datenbank starten:**
   ```bash
   docker compose up db -d
   ```

2. **Backend starten:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Tests ausführen:**
   (Testcontainers wird verwendet, Docker muss also laufen)
   ```bash
   ./mvnw test
   ```

---

## ⚙️ Konfiguration

Die Konfiguration erfolgt über Umgebungsvariablen. Standardwerte für die lokale Entwicklung findest du in `src/main/resources/application.properties`.

| Variable | Beschreibung | Standard |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | JDBC Connection String | `jdbc:postgresql://db:5432/scanly_db` |
| `SPRING_DATASOURCE_USERNAME` | Datenbank-User | `scanly_user` |
| `SPRING_DATASOURCE_PASSWORD` | Datenbank-Passwort | `scanly123` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate Strategie | `update` |

---

## 📑 API & Dokumentation

Die Backend-Module sind wie folgt aufgeteilt:
- **Produkte:** `/api/products` - Verwaltung des Katalogs.
- **Bestellungen:** `/api/orders` - Checkout-Logik und Warenkorb.
- **Coupons:** `/api/coupons` - Validierung von Rabatten.

Nutze für detaillierte API-Tests die **Bruno-Kollektion** im Verzeichnis `/scanly-bruno`.

---

## 📂 Projekt-Struktur
- `src/main/java/com/scanly/scanlyBackend/controllers`: REST-API Layer.
- `src/main/java/com/scanly/scanlyBackend/services`: Business-Logik.
- `src/main/java/com/scanly/scanlyBackend/repository`: Datenzugriff (JPA).
- `src/main/java/com/scanly/scanlyBackend/dtos`: API-Verträge (DTOs).
- `src/main/java/com/scanly/scanlyBackend/models`: JPA-Entitäten.
