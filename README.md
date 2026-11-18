# ImageLite API

REST API for ImageLite, an image gallery. Users sign up and log in, upload images with a name and tags, and search them by text and format. Authentication uses JWT.

This is the backend of the ImageLite project. The frontend lives in [image-lite-frontend](https://github.com/PedroSgorla/image-lite-frontend), and the [imageliteapp](https://github.com/PedroSgorla/imageliteapp) repository brings both together with a `docker-compose.yml`, so you can run the whole project with one command.

## Features

- Sign up and log in. Passwords are stored hashed with BCrypt, and a successful login returns a JWT.
- Upload images (PNG, GIF or JPEG) with a name and tags.
- Search images by text and filter them by format.
- Serve each stored image through its own URL.

## Tech stack

Java 17, Spring Boot 4.1, Spring Web MVC, Spring Data JPA, Spring Security, JWT ([jjwt](https://github.com/jwtk/jjwt) 0.12), PostgreSQL, Lombok and Maven.

## API endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/v1/users` | no | Create a user |
| `POST` | `/v1/users/auth` | no | Log in and receive a token |
| `POST` | `/v1/images` | **JWT** | Upload an image |
| `GET` | `/v1/images` | no | Search images |
| `GET` | `/v1/images/{id}` | no | Download one image |

Authenticated requests send the token in the `Authorization: Bearer <token>` header.

### Examples

Create a user. It returns `201`, or `409` with `{"error": "User already exists!"}` if the email is taken:

```bash
curl -X POST http://localhost:8080/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Pedro", "email": "pedro@example.com", "password": "secret"}'
```

Log in. It returns `200` with `{"accessToken": "..."}`, or `401` if the credentials are wrong:

```bash
curl -X POST http://localhost:8080/v1/users/auth \
  -H "Content-Type: application/json" \
  -d '{"email": "pedro@example.com", "password": "secret"}'
```

Upload an image. It is a `multipart/form-data` request with the fields `file`, `name` and `tags` (comma-separated). It returns `201` with the image URL in the `Location` header:

```bash
curl -X POST http://localhost:8080/v1/images \
  -H "Authorization: Bearer <token>" \
  -F "file=@photo.jpg" \
  -F "name=nature" \
  -F "tags=landscape,lake"
```

Search images. Both query parameters are optional: `query` is the text to look for, and `extension` is `PNG`, `JPEG` or `GIF`:

```bash
curl "http://localhost:8080/v1/images?query=nature&extension=JPEG"
```

Each result looks like this:

```json
{
  "url": "http://localhost:8080/v1/images/<id>",
  "name": "nature",
  "extension": "JPEG",
  "size": 54750,
  "uploadDate": "18/11/2026"
}
```

`size` is in bytes. The maximum upload size is 20 MB.

## Running the project

### With Docker (recommended)

Use the [imageliteapp](https://github.com/PedroSgorla/imageliteapp) repository. Its `docker-compose.yml` starts PostgreSQL, pgAdmin, this API and the frontend together. The README there explains the steps.

### Without Docker

You need JDK 17 and a PostgreSQL database. The default configuration expects PostgreSQL on `localhost:5432` with a database called `imagelite`, user `postgres` and password `postgres`. One way to get that is a throwaway container:

```bash
docker run --name imagelite-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=imagelite \
  -p 5432:5432 -d postgres:16
```

Then start the API with the Maven wrapper that comes with the project:

```bash
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`. Hibernate creates and updates the tables (`tb_user` and `tb_image`) on startup.

To build a runnable jar instead:

```bash
./mvnw clean package -DskipTests
java -jar target/imageliteapi-0.0.1-SNAPSHOT.jar
```

## Configuration

The settings are in `src/main/resources/application.yml`.

| Setting | Default | Notes |
|---|---|---|
| Database URL | `jdbc:postgresql://localhost:5432/imagelite` | |
| Database user and password | `postgres` / `postgres` | Local development values |
| Schema management | `ddl-auto: update` | Tables are created automatically |
| Upload limit | 20 MB | Per file and per request |

The `production` profile, which the Dockerfile activates, builds the database URL from the `POSTGRES_HOST` environment variable instead of `localhost`. This is how the API finds the `db` container in Docker Compose.

## Project structure

```
src/main/java/com/github/pedrosgorla/imageliteapi/
├── application/   controllers, DTOs, mappers and service implementations
│   ├── images/
│   └── users/
├── domain/        entities, enums, exceptions and service interfaces
├── infra/         repositories and search specifications
├── config/        Spring Security setup and the JWT filter
└── jwt/           token creation and validation
```

## Known issues and limitations

- **Searching by tags does not work yet.** The query only matches the image name. `ImageSpecs.tagsLike` compares against the `name` column instead of `tags`.
- **The JWT signing key is generated in memory** every time the API starts. Restarting the API invalidates every token, so users have to log in again. Running more than one instance would not work either.
- **Reading images is public.** `GET /v1/images` and `GET /v1/images/{id}` need no token, so image URLs can be used directly in `<img>` tags. Only uploads require authentication.
- **Images are stored in the database** as binary data, which is simple but not meant for large volumes.
- **The database credentials in `application.yml` are for local development only.**
