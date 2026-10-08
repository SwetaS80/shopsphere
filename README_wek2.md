# Week 2 — Auth, JWT, RBAC

These are ONLY the new/changed pieces on top of your Week 1 project.
Copy the new files into your existing package structure, then make the
small edits below to your existing files. Nothing else in Week 1 changes.

## 1. New files (copy as-is into matching packages)
- entity/RefreshToken.java
- repository/RefreshTokenRepository.java
- security/JwtService.java
- security/JwtAuthFilter.java
- security/SecurityConfig.java
- service/CustomUserDetailsService.java
- dto/AuthDtos.java
- controller/AuthController.java

## 2. pom.xml — add these two dependency blocks

Add inside your existing <dependencies> ... </dependencies> block:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT support -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

## 3. application.yml — add this block at the bottom

```yaml
app:
  jwt:
    secret: "change-this-to-a-long-random-secret-string-at-least-32-chars"
    access-token-expiry-ms: 900000      # 15 minutes
    refresh-token-expiry-ms: 604800000  # 7 days
```

## 4. UserController.java — hash the password before saving

Find this line in the `register` method:
```java
User saved = userRepository.save(user);
```

Replace it with:
```java
user.setPassword(passwordEncoder.encode(user.getPassword()));
User saved = userRepository.save(user);
```

And add this field + constructor param to the class:
```java
private final PasswordEncoder passwordEncoder;

public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}
```
(add `import org.springframework.security.crypto.password.PasswordEncoder;` at the top)

## 5. ProductController.java — restrict product creation to sellers

Add this annotation directly above the `create` method:
```java
@org.springframework.security.access.prepost.PreAuthorize("hasRole('SELLER')")
```

## 6. OrderController.java — restrict checkout to customers

Add this annotation directly above the `checkout` method:
```java
@org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')")
```

## How to test in Postman after this

1. POST /api/v1/users/register (unchanged - now stores a hashed password)
2. POST /api/v1/auth/login with { "email": "...", "password": "..." }
   → returns { accessToken, refreshToken, role }
3. For every other request (create product, checkout), add header:
   Authorization: Bearer <accessToken>
4. When the access token expires (15 min), POST /api/v1/auth/refresh
   with { "refreshToken": "..." } to get a new pair.
