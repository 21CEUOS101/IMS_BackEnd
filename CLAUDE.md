# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Core REST API backend for the IMS (Inventory Management System) — part of a multi-repo Supply Chain Management System. Spring Boot 3.2 / Java 17, backed by MongoDB (local instance for dev; originally MongoDB Atlas). Two React frontends (`ims_frontend`, `IMS_Admin`) consume this API. This repo is now the **only** backend service in the system — the two former standalone microservices (`inventory-navigatorAPI` for distance/geocoding, `analytics-api` for dashboard analytics) have been merged in as native Java code and decommissioned; see their `CLAUDE.md` files for details on what moved where.

## Commands

- Run locally: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`) — starts on port `8080`. Requires JDK 17 specifically (Lombok annotation processing on this Spring Boot 3.2 setup does not work on newer JDKs like 25) — set `JAVA_HOME` to a JDK 17 install if the system default is newer.
- Build jar: `./mvnw clean package`
- Run tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=ClassName`
- Run a single test method: `./mvnw test -Dtest=ClassName#methodName`
- Docker: `Dockerfile` present at repo root for containerized builds.
- Local Mongo: `mongodb://localhost:27017/inventory` (see `src/main/resources/application.properties`, copy from `application.properties.example` if missing — this file is gitignored and not tracked).

## Architecture

Layered structure under `src/main/java/com/project/ims/`:

- `Controllers/` — one REST controller per domain entity (Admin, Auth, Customer, DeliveryMan, Order, Product, ReturnOrder, RSO (return-supply-order), Supplier, SupplyOrder, W2WOrder (warehouse-to-warehouse), Warehouse, WManager), plus `AnalyticsController` (dashboard/deliveryman analytics, see below).
- `Services/` + `IServices/` — business logic behind an interface/impl split, one pair per domain entity, plus `DistanceService`/`IDistanceService` and `AnalyticsService`/`IAnalyticsService`.
- `Repo/` — Spring Data MongoDB repositories, one per entity.
- `Models/` — MongoDB documents (Admin, Customer, DeliveryMan, Order, Product, Supplier, SupplyOrder, W2WOrder, WareHouse, WareHouse_Manager, User, etc.) plus `GlobalProducts`/`GlobalDistances` for cross-entity lookups.
- `Requests/` / `Responses/` — DTOs for request bodies and API outputs, kept separate from `Models/` (e.g. `AdminAddRequest` vs `AdminOutput`, `AnalyticsOverview`).
- `Security/` — JWT auth: `JwtHelper` (token issuing/parsing), `JwtAuthenticationFilter` (per-request filter), `JwtAuthenticationEntryPoint` (401 handling).
- `Config/` — `SecurityConfig` (filter chain: stateless JWT sessions, `/auth/register` and `/auth/login` public, everything under `/api/**` requires auth — this includes the analytics/distance endpoints below, so frontend callers must attach a Bearer token), `WebConfig` (CORS, allow-list of local frontend dev ports), `MyConfig`.

### Multi-role domain model

The system models a supply chain with several actor roles, each with its own controller/service/repo/model set: **Admin**, **Supplier**, **WManager** (warehouse manager), **DeliveryMan**, **Customer**. Orders flow through several distinct types:
- `Order` — customer → warehouse order.
- `SupplyOrder` / `RSO` (Return Supply Order) — warehouse ↔ supplier.
- `W2WOrder` — warehouse-to-warehouse transfer.
- `ReturnOrder` — customer return flow.

### Distance / auto-replenishment (`DistanceService`)

`OrderService.handleStock()` calls `DistanceService.calculateDistance(warehouseIdFrom, warehouseIdTo)` when a customer order needs more stock than the target warehouse has, to rank candidate source warehouses by `quantity / distance` before auto-creating a `W2WOrder` transfer. `DistanceService`:
1. Checks `GlobalDistancesRepo` for a cached distance (now a real read-through cache — writes via `.save()` after every fresh calculation).
2. On a cache miss, resolves each warehouse ID to its `pincode` via `WareHouseRepo`, geocodes both pincodes via the public OpenStreetMap Nominatim API (`nominatim.openstreetmap.org/search`, no API key), and computes distance with a Haversine formula.

This used to call out to a separate `inventory-navigatorAPI` service over HTTP; that call had a silent contract mismatch (wrong field names, and warehouse IDs were being sent where pincodes were expected) that meant the whole auto-replenishment-routing feature never actually worked. Now fixed as native Java code — see `inventory-navigatorAPI/CLAUDE.md` for the full history.

### Analytics (`AnalyticsController` / `AnalyticsService`)

- `GET /api/admin/analytics/overview` — dashboard totals (`total_revenue`, `total_orders`, `total_quantity`, `total_customers`) plus month-wise and product-wise revenue breakdowns. Revenue is delivered-orders-only; the other three counts are across all orders (preserved from the original implementation's semantics, even though that's slightly inconsistent). Consumed by `IMS_Admin`'s `Dashboard.jsx`.
- `GET /api/deliveryman/{id}/current-order` — every `shipped`-status order currently assigned to a delivery man, across all 5 order types (`order`, `return-order`, `return-supply-order`, `supply-order`, `w2worder`) — key order matters, the frontend picks the first non-empty one. Consumed by `IMS_Admin`'s `EmployeeProfile.jsx`.

Both endpoints used to be served by a separate `analytics-api` Flask service that queried this backend's own REST API over HTTP using a hardcoded login account. Now implemented as direct repository queries + Java Streams — no self-HTTP-call, no separate hardcoded account. See `analytics-api/CLAUDE.md` for the full history.

### Known local-dev gotchas

- CORS in `WebConfig` is a hardcoded origin allow-list of specific `localhost` ports — a new frontend dev port needs to be added there explicitly.
- `application.properties` is gitignored (not tracked) — copy `application.properties.example` if setting up fresh. An earlier version of this file with a live (now-dead) Atlas credential was committed to git history before the gitignore rule existed; that credential is dead but was never purged from history.
