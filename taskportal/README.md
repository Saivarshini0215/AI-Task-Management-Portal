# 🚀 TaskPortal — AI-Powered Task Management Portal

A full-stack task management application with AI-powered summarization, built with Spring Boot and React.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Java 17 |
| Frontend | React 18, Vite, Tailwind CSS |
| Database | PostgreSQL (H2 for dev) |
| Auth | JWT (HS256) |
| AI | Anthropic Claude API |
| State | Zustand |

## Features

- ✅ **JWT Authentication** — Register, Login, Protected APIs
- ✅ **Task Management** — Create, Read, Update, Delete tasks
- ✅ **Task Fields** — Title, Description, Status, Priority, Due Date
- ✅ **Status Tracking** — TODO → IN_PROGRESS → DONE
- ✅ **AI Summarization** — Claude-powered task summaries
- ✅ **Dashboard Stats** — Live task counts by status
- ✅ **Search & Filter** — By status, priority, and text
- ✅ **Responsive UI** — Mobile-friendly dark theme
- ✅ **Clean Architecture** — Layered backend, component-based frontend

## Project Structure

```
taskportal/
├── backend/          # Spring Boot application
│   └── src/main/java/com/taskportal/
│       ├── config/         # Security & CORS config
│       ├── controller/     # REST controllers
│       ├── dto/            # Request/Response DTOs
│       ├── entity/         # JPA entities
│       ├── exception/      # Global error handling
│       ├── repository/     # Data access layer
│       ├── security/       # JWT filter & service
│       └── service/        # Business logic
└── frontend/         # React + Vite + Tailwind
    └── src/
        ├── components/     # Reusable UI components
        ├── pages/          # Page-level components
        ├── services/       # Axios API client
        ├── store/          # Zustand state management
        └── utils/          # Helper utilities
```

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.8+

### 1. Database Setup

```sql
CREATE DATABASE taskportal;
CREATE USER taskportal_user WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE taskportal TO taskportal_user;
```

### 2. Backend Setup

```bash
cd backend

# Copy and configure environment
cp .env.example .env
# Edit .env with your values

# Run the application
./mvnw spring-boot:run
```

The backend will start at `http://localhost:8080`

### 3. Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

The frontend will start at `http://localhost:5173`

## Environment Variables

### Backend (`.env` or system env)

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/taskportal` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | Base64-encoded secret (64+ chars) | (provided) |
| `JWT_EXPIRATION` | Token lifetime in ms | `86400000` (24h) |
| `ANTHROPIC_API_KEY` | Your Anthropic API key | — |
| `CORS_ORIGINS` | Allowed frontend origins | `http://localhost:5173` |

## API Reference

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT |

### Tasks (All require `Authorization: Bearer <token>`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/tasks` | Get all user tasks |
| GET | `/api/tasks/{id}` | Get task by ID |
| POST | `/api/tasks` | Create task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |
| POST | `/api/tasks/{id}/ai-summarize` | Generate AI summary |
| GET | `/api/tasks/stats` | Get task statistics |

### Request/Response Examples

**Register:**
```json
POST /api/auth/register
{
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "password": "secret123"
}
```

**Create Task:**
```json
POST /api/tasks
Authorization: Bearer <token>
{
  "title": "Implement OAuth2 login",
  "description": "Add Google and GitHub OAuth2 providers to the app",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31"
}
```

## Deployment

### Docker Compose (Recommended)

```bash
# Build and start everything
docker-compose up --build
```

See `docker-compose.yml` for configuration.

### Manual Deployment

**Backend:**
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/taskportal-backend-1.0.0.jar
```

**Frontend:**
```bash
cd frontend
npm run build
# Serve the dist/ folder with nginx or similar
```

## Security Notes

- Passwords are hashed with BCrypt (strength 12)
- JWT tokens are HS256 signed
- All task operations are scoped to the authenticated user
- CORS is configured to only allow specified origins
- Input validation on all endpoints

## License

MIT
