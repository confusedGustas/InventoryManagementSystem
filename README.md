# Inventory Management System

A full-stack inventory management application with multi-tenant support, role-based access control, analytics, and AI-powered inventory insights.

Built with **Java Spring Boot**, **Angular**, **PostgreSQL**, and **OpenAI** integration.

## Features

- Multi-tenant architecture with company-scoped data isolation
- Role-based access control with four user roles: PLATFORM_ADMIN, COMPANY_ADMIN, COMPANY_USER, COMPANY_FINANCE
- JWT authentication and stateless session management
- Inventory and item management with rich metadata (SKU, category, brand, dimensions, serial number, etc.)
- AI-powered inventory analysis — detects low stock, overstock, and depletion risks
- AI-assisted item creation from images
- PDF export for AI-generated analysis reports
- Analytics dashboard with stock health, category, brand, and location insights
- API key management per company for OpenAI integration
- Database schema management with Liquibase migrations

## Tech Stack

**Backend**
- Java 21
- Spring Boot 4.0.5
- Spring Security with JWT (JJWT)
- Spring Data JPA + Liquibase
- PostgreSQL
- OpenPDF (report generation)
- Maven

**Frontend**
- Angular 20
- TypeScript 5.9
- Tailwind CSS
- RxJS

**Infrastructure**
- Docker (PostgreSQL)

## Requirements

- Java 21+
- Maven
- Node.js 18+
- Docker

## Setup

**1. Clone the repository**

```bash
git clone https://github.com/confusedGustas/InventoryManagementSystem.git
cd InventoryManagementSystem
```

**2. Start the database**

```bash
docker compose up -d
```

This starts a PostgreSQL instance on port `5432` with database `inventory`.

**3. Configure environment variables**

Create a `.env` file in the project root:

```
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=postgres
JWT_SECRET=your_32_byte_hex_secret
```

**4. Run the backend**

```bash
./mvnw spring-boot:run
```

The backend starts on `http://localhost:8080`.

**5. Run the frontend**

```bash
cd frontend
npm install
npm start
```

The frontend starts on `http://localhost:4200` and proxies API requests to `http://localhost:8080`.

## AI Features

AI analysis requires an OpenAI API key. You can add one through the Settings page once logged in. The system uses `gpt-5-mini` for inventory analysis and image-based item suggestions.

## User Roles

| Role | Access |
|------|--------|
| PLATFORM_ADMIN | Full access, company management |
| COMPANY_ADMIN | Company-scoped admin, user management |
| COMPANY_USER | Inventory and item operations |
| COMPANY_FINANCE | Finance-scoped views |

## License

[MIT](LICENSE)
