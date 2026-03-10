# User Registration API

Português | English

---

## Português

## Descrição

API de cadastro, autenticação e gerenciamento de usuários desenvolvida com **Spring Boot**.

Esta API fornece uma solução completa para:

- registro de usuários
- autenticação baseada em **JWT**
- **Two-Factor Authentication (2FA)**
- controle de acesso baseado em roles (**ADMIN / USER**)

O projeto segue boas práticas de segurança, arquitetura limpa e testes automatizados, sendo adequado para aplicações backend em ambientes reais.

---

![Cadastro de usuário](https://diegobrsantosdev.github.io/user-registration-application/assets/captura22.jpg)


## Principais Funcionalidades

### Autenticação e Segurança

- Autenticação baseada em JWT
- Two-Factor Authentication (TOTP)
- Controle de acesso baseado em roles (ADMIN / USER)
- Endpoints protegidos utilizando Bearer Token
- Senhas criptografadas antes de serem armazenadas no banco de dados
- Configuração de segurança utilizando Spring Security
- Filtro de autenticação JwtAuthenticationFilter
- Dados sensíveis nunca são expostos pela API

![Validação 2FA](https://diegobrsantosdev.github.io/user-registration-application/assets/screenshot.18.jpg)

---

### Gerenciamento de Usuários

- Registro de usuário
- Login com JWT
- Login com Two-Factor Authentication
- Atualização de dados do usuário
- Alteração de senha
- Exclusão da própria conta

### Administração

- Listagem de usuários
- Busca de usuários por ID
- Busca de usuários por email
- Busca de usuários por CPF
- Promoção de usuário para ADMIN
- Exclusão de usuários

### Funcionalidades Adicionais

- Validação de CPF
- Integração para consulta de CEP
- Tratamento global de exceções
- Respostas padronizadas da API
- Arquitetura baseada em DTOs

---

## Arquitetura da API


Client
│
▼
Controllers
│
▼
Services
│
▼
Repositories
│
▼
Database


Fluxo de segurança:


Client Request
│
▼
JwtAuthenticationFilter
│
▼
Spring Security
│
▼
Controller

---

## Endpoints da API

![Swagger](https://diegobrsantosdev.github.io/user-registration-application/assets/captura21.jpg)

---

## Segurança

A segurança da aplicação é implementada utilizando **Spring Security**.

Componentes principais:

- SecurityConfig
- SecurityFilterChain
- JwtAuthenticationFilter
- Autorização baseada em roles (ADMIN / USER)
- Autenticação com Bearer Token em endpoints protegidos

As senhas são criptografadas antes de serem armazenadas no banco de dados.

![Banco de dados](https://diegobrsantosdev.github.io/user-registration-application/assets/captura25.png)

---

## Integração com CEP

A API possui funcionalidade de consulta de CEP com sua própria estrutura:

- Controller
- Service
- DTO
- Exceções específicas
- Handler de resposta

<p align="center">
  <img src="https://diegobrsantosdev.github.io/user-registration-application/assets/captura18.png" alt="Consulta via CEP" width="45%">
  <img src="https://diegobrsantosdev.github.io/user-registration-application/assets/captura18.png" alt="CEP não encontrado" width="45%">
</p>

---

## DTOs

O projeto utiliza **12 classes DTO** para separar o contrato da API dos modelos internos.

---

## Validação

Validadores personalizados garantem a integridade dos dados recebidos.

- Validação de CPF
- Validação de requisições utilizando DTOs

---

## Tratamento de Exceções

A API possui **11 classes de exceção personalizadas**, com um handler global responsável por padronizar as respostas de erro.

Isso garante respostas consistentes e seguras para falhas da aplicação.

---

## Testes

O projeto possui **83 testes automatizados**, utilizam MockitoBean para simulação de dependências, e são organizados em:

- controllers
- services
- security
- dtos
- validators
- integration tests
- cep tests

---

## Execução do Projeto

```bash
Executar utilizando Maven:

mvn spring-boot:run

Execução com Docker:

Build da imagem:

docker build -t user-registration-api .

Executar container:

docker run -p 8080:8080 user-registration-api

```

**Acessar API:**

http://localhost:8080

**Documentação da API**

http://localhost:8080/swagger-ui.html

---

## 📝 Contact

Diego Santos

[E-mail](mailto:diegobrsantosdev@gmail.com)                                  [LinkedIn](https://www.linkedin.com/in/diegobrsantos/)

---

## English

# User Registration API

## Description

User registration, authentication and user management API developed with **Spring Boot**.

This API provides a complete solution for:

- user registration
- **JWT-based authentication**
- **Two-Factor Authentication (2FA)**
- role-based access control (**ADMIN / USER**)

The project follows security best practices, clean architecture and automated testing, making it suitable for backend applications in real-world environments.

---

![User registration](https://diegobrsantosdev.github.io/user-registration-application/assets/captura22.jpg)


## Main Features

### Authentication and Security

- JWT-based authentication
- Two-Factor Authentication (TOTP)
- Role-based access control (ADMIN / USER)
- Protected endpoints using Bearer Token
- Passwords encrypted before being stored in the database
- Security configuration using Spring Security
- Authentication filter JwtAuthenticationFilter
- Sensitive data is never exposed through the API

![2FA validation](https://diegobrsantosdev.github.io/user-registration-application/assets/screenshot.18.jpg)

---

### User Management

- User registration
- Login with JWT
- Login with Two-Factor Authentication
- Update user information
- Change password
- Delete own account

### Administration

- List users
- Find users by ID
- Find users by email
- Find users by CPF
- Promote user to ADMIN
- Delete users

### Additional Features

- CPF validation
- CEP lookup integration
- Global exception handling
- Standardized API responses
- DTO-based architecture

---

## API Architecture


Client
│
▼
Controllers
│
▼
Services
│
▼
Repositories
│
▼
Database


Security flow:


Client Request
│
▼
JwtAuthenticationFilter
│
▼
Spring Security
│
▼
Controller

---

## API Endpoints

![Swagger](https://diegobrsantosdev.github.io/user-registration-application/assets/captura21.jpg)

---

## Security

Application security is implemented using **Spring Security**.

Main components:

- SecurityConfig
- SecurityFilterChain
- JwtAuthenticationFilter
- Role-based authorization (ADMIN / USER)
- Bearer Token authentication for protected endpoints

Passwords are encrypted before being stored in the database.

![Database](https://diegobrsantosdev.github.io/user-registration-application/assets/captura25.png)

---

## CEP Integration

The API includes a CEP lookup feature with its own structure:

- Controller
- Service
- DTO
- Specific exceptions
- Response handler

<p align="center">
  <img src="https://diegobrsantosdev.github.io/user-registration-application/assets/captura18.png" alt="CEP lookup" width="45%">
  <img src="https://diegobrsantosdev.github.io/user-registration-application/assets/captura18.png" alt="CEP not found" width="45%">
</p>

---

## DTOs

The project uses **12 DTO classes** to separate the API contract from internal models.

---

## Validation

Custom validators ensure the integrity of the received data.

- CPF validation
- Request validation using DTOs

---

## Exception Handling

The API contains **11 custom exception classes**, with a global handler responsible for standardizing error responses.

This ensures consistent and secure responses for application failures.

---

## Tests

The project contains **83 automated tests**, using MockitoBean for dependency mocking, organized into:

- controllers
- services
- security
- dtos
- validators
- integration tests
- cep tests

---

## Running the Application

```bash
Run using Maven:

mvn spring-boot:run

Running with Docker:

Build image:

docker build -t user-registration-api .

Run container:

docker run -p 8080:8080 user-registration-api

Access API:

http://localhost:8080

API Documentation

http://localhost:8080/swagger-ui.html
```

## 📝 Contact

Diego Santos

[E-mail](mailto:diegobrsantosdev@gmail.com)                                     [LinkedIn](https://www.linkedin.com/in/diegobrsantos/)


