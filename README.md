# Mamba Wedding – RSVP, Gift Registry and Admin Panel

Este projeto é uma aplicação web completa para gerenciamento de casamento, composta por um site público para convidados e um painel administrativo para os noivos.
O sistema permite confirmação de presença via código, gerenciamento de convidados, lista de presentes, integração com gateway de pagamento e armazenamento híbrido em MySQL e MongoDB.

---

## Estrutura do Repositório

```
mamba-wedding/
 ├── backend/          # Aplicação Spring Boot 
 ├── frontend/         # Aplicação React
 ├── infra/            # Configurações de infraestrutura
 ├── docs/             # Documentação
 └── README.md
```

---

## Tech Stack

### Frontend

* React
* TypeScript
* Tailwind CSS

### Backend

* Java 21
* Spring Boot 4
* Spring Web
* Spring Data JPA
* Spring Data MongoDB
* Spring Security + OAuth2 - Google
* Spring Validation
* Lombok

### Bancos de Dados

* MySQL 8 (dados transacionais)
* MongoDB 8 (recados e logs de eventos)