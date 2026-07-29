# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Core REST API backend for the IMS (Inventory Management System) — part of a multi-repo Supply Chain Management System. Spring Boot 3.2 / Java 17, backed by MongoDB Atlas. Two separate React frontends (`ims_frontend`, `IMS_Admin`) and a Node microservice (`inventory-navigatorAPI`) consume this API. See `../MEMORY.md`-equivalent context: this repo is one of 4 sibling repos in `Supply_Chain_Management_System/`.

## Commands

- Run locally: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`) — starts on port `8080` (Spring default, not overridden).
- Build jar: `./mvnw clean package`
- Run tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=ClassName`
- Run a single test method: `./mvnw test -Dtest=ClassName#methodName`
- Docker: `Dockerfile` present at repo root for containerized builds.

No system-wide Maven is required — always use the `./mvnw` wrapper.

## Architecture

Layered structure under `src/main/java/com/project/ims/`:

- `Controllers/` — one REST controller per domain entity (Admin, Auth, Customer, DeliveryMan, Order, Product, ReturnOrder, RSO (return-supply-order), Supplier, SupplyOrder, W2WOrder (warehouse-to-warehouse), Warehouse, WManager).
- `Services/` + `IServices/` — business logic behind an interface/impl split, one pair per domain entity.
- `Repo/` — Spring Data MongoDB repositories, one per entity.
- `Models/` — MongoDB documents (Admin, Customer, DeliveryMan, Order, Product, Supplier, SupplyOrder, W2WOrder, WareHouse, WareHouse_Manager, User, etc.) plus `GlobalProducts`/`GlobalDistances` for cross-entity lookups.
- `Requests/` / `Responses/` — DTOs for request bodies and API outputs, kept separate from `Models/` (e.g. `AdminAddRequest` vs `AdminOutput`).
- `Security/` — JWT auth: `JwtHelper` (token issuing/parsing), `JwtAuthenticationFilter` (per-request filter), `JwtAuthenticationEntryPoint` (401 handling).
- `Config/` — `SecurityConfig` (filter chain: stateless JWT sessions, `/auth/register` and `/auth/login` public, everything under `/api/**` requires auth), `WebConfig` (CORS — **currently hardcoded to allow only `http://localhost:3000`**, will need to be widened/adjusted when running multiple frontends locally on different ports), `MyConfig`.

### Multi-role domain model

The system models a supply chain with several actor roles, each with its own controller/service/repo/model set: **Admin**, **Supplier**, **WManager** (warehouse manager), **DeliveryMan**, **Customer**. Orders flow through several distinct types:
- `Order` — customer → warehouse order.
- `SupplyOrder` / `RSO` (Return Supply Order) — warehouse ↔ supplier.
- `W2WOrder` — warehouse-to-warehouse transfer.
- `ReturnOrder` — customer return flow.

### External service dependency

`Services/OrderService.java` calls out to the sibling `inventory-navigatorAPI` service for distance calculation between pincodes/warehouses via `RestTemplate`, hardcoded to the **deployed** endpoint `https://inventory-navigatorapi.onrender.com/api/get-distance` (not localhost) — see `distanceApiUrl` field. Point this at a local instance only if actively testing that integration end-to-end.

### Known local-dev gotchas

- `src/main/resources/application.properties` has a **live MongoDB Atlas connection string with embedded credentials committed in plaintext**. Treat this as a secret — do not copy it into new commits/repos, and flag to the user before pushing this file anywhere.
- CORS in `WebConfig` only allows origin `http://localhost:3000`. Both React frontends default to port 3000, so only one can talk to a local backend at a time unless CORS is widened or frontends are run on distinct ports with CORS updated accordingly.
