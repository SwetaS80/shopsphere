# ShopSphere — Week 1 (Monolith)

Multi-vendor e-commerce backend. This is the Week 1 scope: plain CRUD +
checkout flow, no auth yet, single Spring Boot service, PostgreSQL only.

## Stack (this week)
- Java 17, Spring Boot 3.3
- Spring Data JPA + PostgreSQL
- Bean Validation

## Run it

1. Start Postgres:
   ```
   docker compose up -d
   ```

2. Run the app:
   ```
   ./mvnw spring-boot:run
   ```
   (If you don't have the Maven wrapper yet, run `mvn spring-boot:run` with
   a local Maven install, or open the project in IntelliJ and run
   `ShopsphereApplication`.)

3. The API is live at `http://localhost:8080`.

## Try it out

**Register a seller**
```
POST /api/v1/users/register
{
  "name": "Acme Seller",
  "email": "seller@acme.com",
  "password": "temporary123",
  "role": "SELLER"
}
```

**Register a customer**
```
POST /api/v1/users/register
{
  "name": "Jane Buyer",
  "email": "jane@example.com",
  "password": "temporary123",
  "role": "CUSTOMER"
}
```

**Create a product** (use the seller's id from above)
```
POST /api/v1/products
{
  "name": "Wireless Mouse",
  "description": "Ergonomic, 2.4GHz",
  "price": 19.99,
  "stock": 50,
  "category": "electronics",
  "sellerId": 1
}
```

**List products**
```
GET /api/v1/products?category=electronics&page=0&size=10
```

**Checkout** (use the customer's id and the product's id)
```
POST /api/v1/orders/checkout
{
  "customerId": 2,
  "items": [
    { "productId": 1, "quantity": 2 }
  ]
}
```

**View a customer's orders**
```
GET /api/v1/orders/customer/2
```

## Project structure
```
src/main/java/com/shopsphere/
  entity/       JPA entities (User, Product, Order, OrderItem)
  repository/   Spring Data repositories
  service/      Business logic (checkout, stock handling)
  controller/   REST endpoints
  dto/          Request/response shapes (never expose entities directly)
  exception/    Global error handling -> clean JSON errors
```

## What's deliberately NOT here yet (coming in later weeks)
- Auth / JWT / RBAC → Week 2
- Redis cart + optimistic locking stress test → Week 3
- Kafka events (order.placed → notifications/inventory-sync) → Week 4
- Dockerizing the app itself + docker-compose for the full stack → Week 5
- Kubernetes manifests → Week 6
- AWS deployment + CI/CD → Week 7
- Observability (logging/metrics/Sentry) → Week 8

## Known rough edges (intentional, for now)
- Passwords are stored in plain text — fixed in Week 2 with BCrypt.
- No pagination validation limits — fine for local dev.
- `ddl-auto: update` is dev-only; switch to Flyway before deploying anywhere real.
