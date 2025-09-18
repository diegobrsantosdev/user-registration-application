# User Registration Application (Em Desenvolvimento / Under Development)
## 🇧🇷 Descrição (Português)
O _User Registration Application_ é um sistema **CRUD** para gerenciamento de usuários, desenvolvido em **Spring Boot**.
Permite registrar, consultar, atualizar e remover usuários via **API RESTful**.
O projeto usa **H2 Database (in-memory)** para fácil execução local e segue boas práticas de organização,
validação e mapeamento de dados.

> ⚠ **Este projeto está em desenvolvimento e pode sofrer alterações.**
> 

### Tecnologias Utilizadas
- Java 24
- Spring Boot 3
- Spring Data JPA
- Spring MVC
- Spring Security (configuração permissiva por padrão)
- H2 Database (memória)
- Lombok (getters/setters/builders)
- Maven

### Funcionalidades
- **Criar Usuário**: Cadastro com e-mail e CPF únicos.
- **Consultar Usuário**: Busca por ID, e-mail ou CPF.
- **Listar Usuários**: Com paginação.
- **Atualizar Usuário**: Atualização completa por ID.
- **Trocar Senha**: Endpoint dedicado.
- **Excluir Usuário**: Remoção por ID.
- **Console H2**: Acesso ao banco via web.

### Estrutura do Projeto
``` text
src/main/java/com/diegobrsantosdev/user_registration_application
│
├─ UserRegistrationApplication.java      # Classe principal
├─ config/                              # Configurações
├─ controllers/                         # Controllers REST
├─ dtos/                                # DTOs (transferência de dados)
├─ entities/                            # Entidades JPA
├─ exceptions/                          # Exceções e handler global
├─ mappers/                             # Conversão DTO <-> Entity
├─ repositories/                        # Repositórios JPA
├─ services/                            # Camada de serviço
└─ resources/                           # application.properties, etc.
```
### Endpoints Principais

| Método | Endpoint | Função | Parâmetros / Body |
| --- | --- | --- | --- |
| POST | `/users` | Criar novo usuário | JSON body: todos campos obrigatórios |
| GET | `/users/{id}` | Buscar usuário por ID | Path variable: `id` |
| GET | `/users?email=&cpf=` | Buscar usuário por e-mail ou CPF | Query params: e/ou `cpf` `email` |
| GET | `/users/all` | Listar todos usuários | Paginação: ?page=0&size=10 |
| PUT | `/users/{id}` | Atualizar usuário por ID | Path: `id`, JSON body com novos dados |
| PUT | `/users/{id}/password` | Atualizar senha | JSON body: `{ "password": "novaSenha" }` |
| DELETE | `/users/{id}` | Remover usuário por ID | Path variable: `id` |
#### Exemplo de JSON
``` json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "minhasenha123",
  "cpf": "12345678900",
  "phone": "11999999999",
  "address": "Rua Exemplo",
  "number": "123",
  "complement": "",
  "neighborhood": "Centro",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01001000"
}
```
### Como Executar
1. **Clone este repositório**
`git clone ...`
2. **Acesse a pasta do projeto**
`cd user-registration-application`
3. **Execute com Maven**
`./mvnw spring-boot:run` (Linux/Mac) — `mvnw.cmd spring-boot:run` (Windows)
4. **Acesse a API**
[http://localhost:8080/users](http://localhost:8080/users)
5. **Acesse o H2 Console (opcional):**
    - URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - JDBC URL: `jdbc:h2:mem:user`
    - Usuário/Senha: `root` / `root`


> Os endpoints não requerem autenticação neste ambiente.
> Validações e mensagens padronizadas já implementadas.
> Em produção, ajuste as regras de segurança.
> 

## 🇺🇸 Description (English)
_User Registration Application_ is a **CRUD system** for user management, built with **Spring Boot**.
It provides create, read, update, and delete operations through a **RESTful API**, following best practices.
The project uses **H2 Database (in-memory)** for easy local execution and applies proper organization, validation, and data mapping.

> ⚠ **This project is under development and may change.**
> 

### Technologies
- Java 24
- Spring Boot 3
- Spring Data JPA
- Spring MVC
- Spring Security (default permissive config)
- H2 Database (in-memory)
- Lombok (automatic generation of getters/setters/builders)
- Maven

### Features
- **Create User**: User registration with unique email and CPF.
- **Read User**: Search by ID, email or CPF.
- **List Users**: With pagination.
- **Update User**: Full update by ID.
- **Change Password**: Dedicated endpoint.
- **Delete User**: Remove by ID.
- **H2 Console**: Web DB access.

### Project Structure
``` text
src/main/java/com/diegobrsantosdev/user_registration_application
│
├─ UserRegistrationApplication.java      # Main class
├─ config/                              # Configuration
├─ controllers/                         # REST Controllers
├─ dtos/                                # DTOs (data transfer)
├─ entities/                            # JPA Entities
├─ exceptions/                          # Custom exceptions and handler
├─ mappers/                             # DTO <-> Entity conversion
├─ repositories/                        # JPA Repositories
├─ services/                            # Business logic layer
└─ resources/                           # application.properties, etc.
```
### Main Endpoints

| Method | Endpoint | Function | Params / Body |
| --- | --- | --- | --- |
| POST | `/users` | Create new user | JSON body: all required fields |
| GET | `/users/{id}` | Find user by ID | Path variable: `id` |
| GET | `/users?email=&cpf=` | Find user by email or CPF | Query params: and/or `cpf` `email` |
| GET | `/users/all` | List all users | Pagination: `?page=0&size=10` |
| PUT | `/users/{id}` | Update user by ID | Path: `id`, JSON body with new data |
| PUT | `/users/{id}/password` | Change user password | JSON body: `{ "password": "newPassword" }` |
| DELETE | `/users/{id}` | Delete user by ID | Path variable: `id` |
#### Example JSON
``` json
{
  "name": "John Smith",
  "email": "john@email.com",
  "password": "mysecurepassword",
  "cpf": "12345678900",
  "phone": "11987654321",
  "address": "Sample Street",
  "number": "123",
  "complement": "",
  "neighborhood": "Downtown",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01001000"
}
```
### How to Run
1. **Clone this repository**
`git clone ...`
2. **Enter project folder**
`cd user-registration-application`
3. **Run with Maven**
`./mvnw spring-boot:run` (Linux/Mac) — `mvnw.cmd spring-boot:run` (Windows)
4. **Access the API:**
[http://localhost:8080/users](http://localhost:8080/users)
5. **H2 Console (optional):**
    - URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - JDBC URL: `jdbc:h2:mem:user`
    - User/Password: `root` / `root`


> Endpoints are open for development purposes.
> Validation and standardized responses included.
> For production, change security settings accordingly.
>
