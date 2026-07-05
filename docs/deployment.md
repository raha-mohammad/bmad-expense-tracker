# Deployment

`apps/web` deploys to Vercel and `apps/api` + PostgreSQL deploy to Railway, each via native git integration on merge to `main` (NFR-7, ARCHITECTURE-SPINE.md). Provisioning both is a one-time manual step in each platform's dashboard — there is no CLI/script for it in this repo.

## Vercel (`apps/web`)

1. Import this GitHub repo into a new Vercel project.
2. Set **Root Directory** to `apps/web`. Framework (Next.js) is auto-detected; no build command override needed.
3. No environment variables are required yet — `apps/web` has no backend API calls to configure in Story 1.1. When a later story adds one (starting with Story 1.2's Quick Add), add the backend's public URL as `NEXT_PUBLIC_API_BASE_URL` here.

## Railway (`apps/api` + PostgreSQL)

1. Create a new Railway project. Add a **PostgreSQL** plugin — Railway provisions this on a persistent volume by default, satisfying NFR-7 (do not use a free tier that expires/resets).
2. Add a second service for `apps/api`, deploying from this repo with **Root Directory** set to `apps/api`.
3. Railway injects a `DATABASE_URL` env var as a bare URI (`postgres://user:pass@host:port/db`), **not** a Spring-compatible JDBC URL. Set these three env vars explicitly instead of relying on `DATABASE_URL` directly:
   - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<host>:<port>/<database>` (same host/port/database Railway shows for the Postgres plugin, with the `jdbc:` prefix added)
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
4. Set `APP_CORS_ALLOWED_ORIGINS` to the deployed Vercel origin (e.g. `https://<project>.vercel.app`) once it's known. This can be a comma-separated list if both a preview and production Vercel origin need to be allowed.
5. Confirm `/actuator/health` responds `200` on the deployed Railway URL — Railway's own healthcheck depends on it.

## Local dev (reference)

Local dev doesn't touch either platform: `docker-compose up` runs Postgres 18.4 locally, and `apps/api`'s `application.properties` defaults already point at it. See the root `docker-compose.yml` and `apps/api/src/main/resources/application.properties`.

**Running backend tests requires `docker-compose up` first.** `apps/api`'s test suite (`mvn test`) boots the full Spring context against the same `localhost:5432` datasource as local dev — there's no Testcontainers or embedded database, per the PRD's guidance to avoid heavier test-framework investment at MVP scope. If Postgres isn't running, `mvn test` fails at context startup, not with a clear "start Postgres first" message.
