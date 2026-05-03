# Tradelia Server

Backend REST de **Tradelia**, un marketplace de segunda mano donde los usuarios pueden publicar productos y chatear en tiempo real.

> **Despliegue:** la versión en producción está en la rama [`demo`](../../tree/demo).

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat-square&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT%20Bearer-black?style=flat-square)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-blue?style=flat-square)

---

## Qué hace

Este proyecto se encarga de:

- registro y login con JWT Bearer
- publicación, edición y borrado de productos
- filtrado por provincia y ciudad
- subida de imágenes a Cloudinary
- conversaciones entre comprador y vendedor
- mensajería en tiempo real con WebSocket STOMP

---

## Stack

- Java 21
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- MySQL
- WebSocket STOMP
- Lombok
- Cloudinary

---

## Quick Start

### Requisitos

- Java 21
- Maven 3.9+
- MySQL 8
- cuenta de Cloudinary

### Clonar el repositorio

```bash
git clone https://github.com/dannytrejo17/Tradelia-server.git
cd Tradelia-server
```

### Crear la base de datos

```sql
CREATE DATABASE tradelia;
```

### Configuración local

En `src/main/resources/application.properties.example` tienes la plantilla de configuración.

Para desarrollo local, crea `src/main/resources/application.properties` a partir de esa plantilla y rellena tus credenciales de MySQL, JWT y Cloudinary.

**Windows (PowerShell)**

```powershell
Copy-Item src\main\resources\application.properties.example src\main\resources\application.properties
```

**Linux / macOS**

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### Ejecutar la aplicación

```bash
mvn spring-boot:run
```

Servidor REST: `http://localhost:8080`  
WebSocket: `ws://localhost:8080/ws`

---

## Autenticación

La API usa JWT Bearer. Tras hacer login, el cliente debe enviar el token en cada request protegida:

```http
Authorization: Bearer <token>
```

### Registro

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "juan",
  "email": "juan@email.com",
  "password": "miPassword123"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "juan@email.com",
  "password": "miPassword123"
}
```

La respuesta devuelve el token JWT en el body.

---

## Endpoints principales

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

### Productos

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products/filter`
- `GET /api/products/mios`
- `POST /api/products/sell`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

### Conversaciones

- `POST /api/conversations/messages`
- `GET /api/conversations/mine`
- `GET /api/conversations/{id}`
- `GET /api/conversations/{id}/messages`

---

## Chat en tiempo real

El sistema de chat combina REST y WebSocket:

- REST para enviar mensajes, consultar bandeja e historial
- WebSocket STOMP para recibir mensajes en vivo

Endpoint de conexión:

```text
ws://localhost:8080/ws
```

Destino de suscripción:

```text
/topic/conversation.{conversationId}
```

Ejemplo:

```text
/topic/conversation.3
```

---

## Estructura del proyecto

```text
src/main/java/com/tradelia/
├── Controller/
├── Service/
├── Repository/
├── Model/
├── Dto/
├── security/
├── config/
└── exception/
```

---

## Tests

```bash
mvn test
```

Los tests usan H2 en memoria desde `src/test/resources/application.properties`.

---

## Notas

- `application.properties.example` es una plantilla, no debe llevar secretos reales
- las credenciales reales de MySQL, JWT y Cloudinary no deberían subirse al repositorio
- el README puede seguir actualizándose a medida que cierres features pendientes, como la autenticación del WebSocket

---

## Autor

**Danny Trejo**  
GitHub: [@dannytrejo17](https://github.com/dannytrejo17)
