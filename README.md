# Enviro365 Investments — Withdrawal Notice System

Junior Developer Assessment (June 2026) — full-stack solution: Spring Boot (Gradle) backend + React (Vite) frontend, backed by an in-memory H2 database.

## Package

The backend package is `com.enviro.assessment.junior.murunwamudzhadzhi`.

## Project structure

```
enviro365-withdrawal-system/
├── backend/    Spring Boot 3 / Java 17 / Gradle / H2
└── frontend/   React 18 / Vite / Axios
```

## Running the backend

Requires Java 17+.

```bash
cd backend
./gradlew bootRun        # or: gradle bootRun
```

- API base URL: `http://localhost:8080/api`
- H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:enviro365db`, user `sa`, no password)
- Two demo investors are seeded on startup (see `config/DataSeeder.java`):
  - **Thandiwe Mokoena** — age > 65, holds a Retirement Annuity + Unit Trust (retirement withdrawals allowed)
  - **Sipho Nkosi** — age < 65, holds a Retirement Annuity + Savings Plan (retirement withdrawals blocked, to demonstrate the age rule)

Run the backend tests:

```bash
./gradlew test
```

## Running the frontend

Requires Node.js 18+.

```bash
cd frontend
npm install
npm run dev
```

- Runs at `http://localhost:5173` and calls the backend at `http://localhost:8080`.
- Start the backend first.

## API documentation

All error responses share one shape:

```json
{
  "timestamp": "2026-09-08T10:15:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Withdrawal amount (950) exceeds the maximum allowed withdrawal of 90% of balance (900.00).",
  "path": "/api/withdrawals",
  "fieldErrors": null
}
```

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/investors` | List investors (id + name), for the UI selector |
| GET | `/api/investors/{investorId}/portfolio` | Investor details + their products/balances |
| POST | `/api/withdrawals` | Submit a withdrawal notice. Body: `{ "productId": 1, "amount": 1000.00, "notes": "optional" }` |
| GET | `/api/withdrawals?investorId={id}` | Withdrawal history for an investor |
| GET | `/api/withdrawals/export?investorId={id}&productId={optional}&from={optional ISO datetime}&to={optional ISO datetime}` | Download a CSV statement, filterable by product and date range |

## Business rules implemented

Enforced server-side in `WithdrawalService`, and mirrored client-side in `WithdrawalForm` for immediate feedback:

1. **Retirement age rule** — withdrawals from a `RETIREMENT_ANNUITY` product are only allowed if the investor's age (derived from date of birth) is greater than 65.
2. **Sufficient balance** — the withdrawal amount may not exceed the product's current balance.
3. **90% cap** — the withdrawal amount may not exceed 90% of the product's current balance, even if the balance itself would cover it.
4. **Error handling & feedback** — every rule violation raises a `BusinessRuleException`, caught centrally by `GlobalExceptionHandler` and returned as a structured 400 response with a human-readable message, which the frontend surfaces directly to the user.

## Advanced requirements implemented (4 of 5)

- **Global exception handling** — `GlobalExceptionHandler` (`@RestControllerAdvice`) covers business-rule violations (400), not-found (404), bean-validation failures (400, with per-field messages) and any unhandled error (500), all returned in one consistent `ErrorResponseDTO` shape.
- **DTO layer** — entities are never returned directly from controllers; `PortfolioDTO`, `ProductDTO`, `WithdrawalRequestDTO`, `WithdrawalResponseDTO` and `ErrorResponseDTO` isolate the API contract from persistence, mapped via `EntityMapper`.
- **Input validation** — Bean Validation (`@NotNull`, `@DecimalMin`) on `WithdrawalRequestDTO`, enforced via `@Valid` in the controller, in addition to the business-rule validation in the service layer.
- **Unit tests** — `WithdrawalServiceTest` (all three business rules, boundary case at exactly 90%, not-found case) and `PortfolioServiceTest`, using JUnit 5 + Mockito.
- UI validation was intentionally left as the 5th, unimplemented advanced item to stay within scope, though the frontend does include basic client-side checks (see `WithdrawalForm.jsx`) ahead of the authoritative server-side validation.

## Design notes / assumptions

- An investor can hold multiple products (`Product`); a withdrawal notice (`WithdrawalNotice`) is always against one specific product, not the investor as a whole.
- Withdrawals are processed synchronously — a notice is only ever persisted once it has passed validation, so there is no separate "pending approval" workflow; `WithdrawalStatus` exists mainly to make the audit trail explicit and to leave room for a future approval step.
- Balances and amounts use `BigDecimal` throughout to avoid floating-point rounding issues with currency.
- CORS is enabled for `localhost:5173` (Vite) and `localhost:3000` on both controllers.

## AI usage disclosure

AI assistance (Claude) was used to scaffold this solution end-to-end — entity/DTO/service/controller layers, the business-rule validation, the React UI, and the unit tests — based on the requirements in this brief. All generated code was reviewed for correctness and is understood and can be walked through/defended in a follow-up interview, per the assessment's AI usage guideline.

## Screenshots

_Add screenshots of the running dashboard, withdrawal form, and history table here before submitting._
