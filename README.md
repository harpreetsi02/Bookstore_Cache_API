# Bookstore Cache API

A Bookstore Catalog REST API built to learn **caching** in Spring Boot: what to cache, how to key it, and how to invalidate it correctly. It also covers JPA relationships, JPQL aggregate queries, validation, global exception handling and OpenAPI docs.

## Tech Stack

- Java 21, Spring Boot 4.1
- Spring Web, Spring Data JPA, Validation
- Spring Cache (default in-memory cache)
- PostgreSQL
- Lombok
- springdoc-openapi (Swagger UI)

## Domain Model

| Entity | Relationships |
|---|---|
| Category | one category has many books |
| Publisher | one publisher has many books |
| Author | many-to-many with Book (join table `book_authors`) |
| Book | many-to-one Category and Publisher (LAZY), many-to-many Authors |
| Review | many-to-one Book (LAZY) |

Relationships are unidirectional (Book knows its Category, not the reverse), so large collections are never loaded by accident. Reverse lookups use repository queries.

## Caching Design

| Cache | Key | Used by |
|---|---|---|
| `books` | book id | get book by id |
| `booksList` | `'all'` | list books |
| `categories` / `categoryList` | id / `'all'` | category reads |
| `publishers` / `publisherList` | id / `'all'` | publisher reads |
| `bookRatings` | book id | average rating and review count |
| `topRatedBooks` | `minReviews-limit` | top rated books |

**Invalidation rules**
- Create, update and delete evict the matching list cache; update refreshes the entry with `@CachePut`.
- Renaming a Category, Publisher or Author evicts the book caches, because their names are copied inside `BookResponse`.
- A new review evicts that book's rating and all top-rated entries.
- Updating or deleting a book also evicts its rating / top-rated entries.
- Only DTOs are cached, never JPA entities.

**Known limitations (addressed in the next projects):** no TTL, no size limit, cache is lost on restart, and it is local to one instance.

## API Overview

| Resource | Endpoints |
|---|---|
| Categories | `POST/GET /api/categories`, `GET/PUT/DELETE /api/categories/{id}` |
| Publishers | `POST/GET /api/publishers`, `GET/PUT/DELETE /api/publishers/{id}` |
| Authors | `POST/GET /api/authors`, `GET/PUT/DELETE /api/authors/{id}` |
| Books | `POST/GET /api/books`, `GET/PUT/DELETE /api/books/{id}` |
| Reviews | `POST/GET /api/books/{id}/reviews` (paginated) |
| Ratings | `GET /api/books/{id}/rating`, `GET /api/books/top-rated?minReviews=2&limit=5` |

Error handling: `404` not found, `409` duplicate or resource in use, `400` validation failure, all in a common `ErrorResponse` format.

## Run Locally

1. Create the database: `CREATE DATABASE bookstore_cache_db;`
2. Set environment variables: `DB_USERNAME` (default `postgres`) and `DB_PASSWORD`
3. Start the app: `./mvnw spring-boot:run`
4. Open Swagger UI: http://localhost:8080/swagger-ui/index.html

Tip: `spring.jpa.show-sql=true` is enabled, so cache hits and misses are visible in the console (a hit runs no SQL).

## What I Learned

- `@Cacheable`, `@CachePut`, `@CacheEvict`, `@Caching`, composite keys and `allEntries`
- Cross-cache invalidation and why cache names should be constants
- LAZY loading, N+1 detection from SQL logs, JPQL `group by` / `having` / constructor expressions
- Bulk delete with `@Modifying` instead of one delete per row