# Blog API — Posts API com Autenticação JWT

API REST para um blog pessoal, construída com **Spring Boot**. Permite cadastro e verificação de contas por e-mail, login com autenticação stateless via **JWT**, e CRUD de posts com paginação por cursor.

## Stack

- **Java 17**
- **Spring Boot 3.5.7** (Web, Data JPA, Security, Validation, Mail)
- **PostgreSQL** (via `spring-boot-starter-data-jpa` + driver `org.postgresql`)
- **JJWT** (`io.jsonwebtoken`) para geração e validação de tokens JWT
- **Lombok**
- **Gradle** (com wrapper incluso)

## Funcionalidades

- Cadastro de contas (Sign Up)
- Verificação de conta por código enviado via e-mail (SMTP Gmail)
- Reenvio de código de verificação
- Login com geração de token JWT
- Autenticação stateless via filtro JWT (`Authorization: Bearer <token>`)
- Troca de senha e alteração de nome de usuário
- CRUD de posts (criar, buscar por id, editar, deletar)
- Listagem paginada dos posts de um autor, com paginação baseada em cursor

### Em aberto
- Associação mais completa entre as entidades `User` e `Post`

## Estrutura do projeto

```
src/main/java/com/example/blog/
├── BlogApplication.java        # Classe principal (entry point)
├── config/
│   ├── ApplicationConfiguration.java
│   ├── EmailConfiguration.java # Configuração do JavaMailSender (SMTP Gmail)
│   ├── JwtAuthenticationFilter.java # Filtro que valida o token em cada request
│   └── SecurityConfig.java     # Regras de autorização, CORS e cadeia de filtros
├── controller/
│   ├── AuthenticationController.java # /auth (signup, login, verify, resend, password)
│   ├── UserController.java           # /users (dados do usuário logado, username)
│   ├── PostController.java           # /posts (CRUD de posts)
│   └── GlobalExceptionHandler.java
├── DTOs/                        # Records/DTOs de entrada e saída da API
├── mappers/
│   └── PostMapper.java
├── model/
│   ├── User.java                # Entidade JPA, implementa UserDetails
│   └── Post.java                # Entidade JPA, relacionada a User (author)
├── repository/
│   ├── UserRepository.java
│   └── PostRepository.java
└── service/
    ├── AuthenticationService.java
    ├── UserService.java
    ├── PostService.java
    ├── EmailService.java
    └── JwtService.java
```

## Como rodar

### Pré-requisitos
- JDK 17+
- PostgreSQL rodando (local ou remoto)
- Uma conta Gmail (ou outro SMTP compatível) para envio do e-mail de verificação

### Variáveis de ambiente

O projeto lê a configuração a partir de variáveis de ambiente (ou de um arquivo `.env`/`.env.properties` na raiz, já que `spring.config.import=optional:file:.env[.properties]` está habilitado). São necessárias:

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão JDBC do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET_KEY` | Chave secreta usada para assinar os tokens JWT |
| `SUPPORT_EMAIL` | E-mail usado como remetente (SMTP) |
| `APP_PASSWORD` | Senha/app password do e-mail remetente |

Exemplo de `.env` na raiz do projeto:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/blog
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET_KEY=uma-chave-secreta-bem-grande-em-base64
SUPPORT_EMAIL=seuemail@gmail.com
APP_PASSWORD=sua-app-password-do-gmail
```

> A expiração do token é fixa em `security.jwt.expiration-time=3600000` (1 hora), configurável em `application.properties`.

### Subindo a aplicação

```bash
# Linux/macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

A aplicação sobe por padrão na porta `8080`.

### Rodando os testes

```bash
./gradlew test
```

## Endpoints da API

### Autenticação — `/auth` (público)

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/auth/signup` | Cria uma nova conta |
| `POST` | `/auth/login` | Autentica e retorna um token JWT |
| `POST` | `/auth/verify` | Verifica a conta usando o código enviado por e-mail |
| `POST` | `/auth/resend?email=` | Reenvia o código de verificação |
| `PATCH` | `/auth/password` | Altera a senha do usuário |

### Usuário — `/users` (requer autenticação, exceto onde indicado)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/users/me` | Retorna os dados do usuário autenticado |
| `PATCH` | `/users/username/{username}` | Altera o nome de usuário |

### Posts — `/posts`

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/posts` | Autenticado | Cria um novo post |
| `GET` | `/posts/{authorId}/` | Público | Lista os posts de um autor, paginado por cursor (`size`, `nextCursor`) |
| `GET` | `/posts/{postId}` | Público | Busca um post por id |
| `PATCH` | `/posts/{postId}` | Autenticado | Edita título/conteúdo de um post |
| `DELETE` | `/posts/{postId}` | Autenticado | Remove um post |

Todas as rotas fora de `/auth/**` e dos `GET` em `/posts/**` exigem o header:

```
Authorization: Bearer <token>
```

## Modelo de dados (resumo)

- **User**: `id`, `username`, `email`, `password` (hash), `verificationCode`, `verificationCodeExpiresAt`, `enabled`
- **Post**: `id`, `title`, `content`, `author` (`User`, many-to-one), `createdAt`, `updatedAt`

## Licença

Este projeto está sob a licença [MIT](LICENSE).
