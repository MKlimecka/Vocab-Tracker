# Vocab Tracker

A vocabulary learning application with automatic translation and repetition system.

## Technologies

- **Backend:** Spring Boot 3.2.0, Java 21
- **Database:** PostgreSQL
- **Security:** Spring Security + JWT
- **Translation:** MyMemory Translation API
- **ORM:** Hibernate/JPA
- **Build:** Maven

## Features

- User registration and login (JWT authentication)
- Automatic word translation (MyMemory API)
- Manual translation input
- Status system: NEW → REPEAT → KNOWN → MASTERED
- Daily review with priority-based selection
- Repetition counter (10 → 0)
- Data isolation - each user sees only their own words
- Full CRUD operations on vocabulary

## Requirements

- Java 21+
- PostgreSQL 16+
- Maven 3.6+

## Installation and Setup

### 1. Clone the repository
```bash
git clone https://github.com/MKlimecka/Vocab-Tracker
cd vocab-tracker
```

### 2. Create PostgreSQL database
```sql
CREATE DATABASE vocabtracker;
```

### 3. Configure environment variables

#### Option A: Use example files (recommended)
```bash
# Copy example files
cp .env.example .env
cp src/main/resources/application-dev.example.yaml src/main/resources/application-dev.yaml

# Edit .env and application-dev.yaml with your credentials
```

#### Option B: Create files manually

Create `.env` in root directory:
```env
DB_URL=jdbc:postgresql://localhost:5432/vocabtracker
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_minimum_256_bits
```

Create `src/main/resources/application-dev.yaml`:
```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    open-in-view: false

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

translator:
  api-url: https://api.mymemory.translated.net/get
  source-lang: en
  target-lang: pl
```

**Note:** JWT_SECRET must be at least 256 bits (43+ characters). You can generate one at [generate-secret.vercel.app](https://generate-secret.vercel.app/32)

### 4. Run the application
```bash
mvn spring-boot:run
```

Application will be available at: `http://localhost:8081`

## API Endpoints

### Authentication (public)

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGci...",
  "email": "user@example.com"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGci...",
  "email": "user@example.com"
}
```

### Vocabulary (requires JWT token)

All endpoints below require header:
```
Authorization: Bearer {token}
```

#### Add word with automatic translation
```http
POST /api/words?word=apple
```

**Response:**
```json
{
  "original": "apple",
  "translations": ["jabłko"],
  "status": "NEW"
}
```

#### Add word with manual translations
```http
POST /api/words/manual?word=apple&translations=jabłko&translations=jabłuszko
```

**Response:**
```json
{
  "original": "apple",
  "translations": ["jabłko", "jabłuszko"],
  "status": "NEW"
}
```

#### Get all words
```http
GET /api/words
```

**Response:**
```json
[
  {
    "original": "apple",
    "translations": ["jabłko"],
    "status": "NEW"
  },
  {
    "original": "book",
    "translations": ["książka"],
    "status": "REPEAT"
  }
]
```

#### Get daily review
```http
GET /api/words/repetition?count=5
```

Returns up to 5 prioritized words for daily review.

**Response:**
```json
[
  {
    "original": "apple",
    "translations": ["jabłko"],
    "status": "NEW"
  },
  {
    "original": "house",
    "translations": ["dom"],
    "status": "NEW"
  }
]
```

#### Update word status
```http
PATCH /api/words/{original}/status?status=KNOWN
```

Example:
```http
PATCH /api/words/apple/status?status=KNOWN
```

Available statuses: `NEW`, `REPEAT`, `KNOWN`, `MASTERED`

**Response:** `204 No Content`

#### Delete word
```http
DELETE /api/words/{original}
```

Example:
```http
DELETE /api/words/apple
```

**Response:** `204 No Content`

#### Clear all words
```http
DELETE /api/words
```

**Response:** `204 No Content`

## Database Schema

### users
| Column   | Type    | Description            |
|----------|---------|------------------------|
| id       | UUID    | Primary key           |
| email    | VARCHAR | Email (unique, login) |
| password | VARCHAR | Hashed password       |
| role     | ENUM    | User role             |

### words
| Column      | Type     | Description                |
|-------------|----------|----------------------------|
| id          | UUID     | Primary key               |
| user_id     | UUID     | FK to users               |
| original    | VARCHAR  | Original word             |
| status      | ENUM     | Learning status           |
| created_at  | DATETIME | Creation timestamp        |
| repetition  | INT      | Repetition counter (10→0) |

### word_translations
| Column      | Type    | Description        |
|-------------|---------|--------------------| 
| word_id     | UUID    | FK to words       |
| translation | VARCHAR | Single translation|

**Relationship:** User (1) → (Many) Words

## Security

- Passwords hashed with BCrypt
- JWT tokens valid for 24 hours
- Stateless session management
- `/api/auth/**` public, all other endpoints require authentication

## Repetition System

1. New word created: `repetition = 10`, `status = NEW`
2. Each review: `repetition - 1` (TODO: implementation pending)
3. When `repetition == 0`: suggest status change to user
4. Status changed: `repetition = 10` (reset counter)

### Daily Review Algorithm

**Priority levels:**
- NEW (priority 1) - highest
- REPEAT (priority 2)
- KNOWN (priority 3)
- MASTERED (priority 4) - lowest

**Selection process:**
1. Sort all user's words by priority (status)
2. Take top `count × 3` words (e.g., 15 for count=5)
3. Randomly shuffle this priority pool
4. Return `count` words (e.g., 5)

This ensures high-priority words appear more frequently while maintaining variety.

## Project Structure
```
src/main/java/org/marta/vocabtracker/
├── auth/
│   ├── controller/        # AuthController
│   ├── dto/               # RegisterRequest, LoginRequest, AuthResponse
│   └── service/           # AuthService
├── jwt/
│   └── JWTService.java    # JWT token generation and validation
├── security/
│   ├── JWTAuthFilter.java # JWT authentication filter
│   └── SecurityConfig.java # Spring Security configuration
├── translation/
│   └── service/           # TranslationService (MyMemory API)
├── user/
│   ├── model/             # UserEntity, Role enum
│   ├── repository/        # UserRepository
│   └── service/           # UserDetailsServiceImpl
└── word/
    ├── controller/        # WordController
    ├── dto/               # WordDTO
    ├── model/             # WordEntity, Status enum
    ├── repository/        # WordRepository
    └── service/           # WordService
```

## Testing with Postman

### 1. Register a new user
```
POST http://localhost:8081/api/auth/register
Body (JSON):
{
  "email": "test@example.com",
  "password": "password123"
}
```

Copy the `token` from response.

### 2. Add words
```
POST http://localhost:8081/api/words?word=apple
Headers:
  Authorization: Bearer {your-token}
```

### 3. Get all your words
```
GET http://localhost:8081/api/words
Headers:
  Authorization: Bearer {your-token}
```

### 4. Get daily review
```
GET http://localhost:8081/api/words/repetition?count=5
Headers:
  Authorization: Bearer {your-token}
```

### 5. Update word status
```
PATCH http://localhost:8081/api/words/apple/status?status=KNOWN
Headers:
  Authorization: Bearer {your-token}
```

### 6. Delete a word
```
DELETE http://localhost:8081/api/words/apple
Headers:
  Authorization: Bearer {your-token}
```

## Roadmap

- [ ] Implement repetition counter decrement on review
- [ ] User statistics endpoint (word count per status)
- [ ] Review history tracking
- [ ] Word categories/tags
- [ ] Export/import vocabulary (CSV, JSON)
- [ ] Frontend (React)
- [ ] Deployment guide
- [ ] Unit and integration tests

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

[MKLimecka](https://github.com/MKlimecka)

## Acknowledgments

- [MyMemory Translation API](https://mymemory.translated.net/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JWT.io](https://jwt.io/)
