# PUCE-Park Backend

Bienvenido al repositorio del backend de PUCE-Park, un sistema diseñado para la gestion de zonas y puestos de parqueo. Este proyecto ha sido desarrollado siguiendo una arquitectura limpia y modular utilizando Kotlin y Spring Boot 3.

## Tecnologias Principales
- Lenguaje: Kotlin
- Framework: Spring Boot 3
- Base de Datos: PostgreSQL
- Seguridad: Spring Security con OAuth2 Resource Server (Validacion de AWS Cognito JWT)
- Despliegue Local: Docker y Docker Compose
- Pruebas: JUnit 5 y MockK

## Estructura del Proyecto

El proyecto se encuentra organizado en paquetes que respetan la separacion de responsabilidades:

- controller: Controladores REST que exponen la API. Unicamente utilizan DTOs y delegan la logica a los servicios.
- service: Interfaces e implementaciones con toda la logica de negocio y el control transaccional (@Transactional).
- repository: Interfaces de Spring Data JPA, incluyendo consultas especificas y bloqueo pesimista (Pessimistic Locking).
- dto: Data Transfer Objects utilizados para manejar la informacion entrante (Requests) y saliente (Responses).
- mapper: Componentes dedicados a la conversion entre entidades de dominio y DTOs.
- entity: Entidades JPA que mapean el modelo de base de datos.
- exception: Clases de excepcion personalizadas y un manejador global (@RestControllerAdvice) para centralizar la respuesta de errores HTTP.

## Requisitos Previos

1. Instalacion de Docker Desktop o Docker Engine.
2. (Opcional) Java 17 y un entorno de desarrollo integrado (IDE) como IntelliJ IDEA o Eclipse, en caso de requerir desarrollo local.

## Instrucciones de Ejecucion Local

Existen dos alternativas para levantar el proyecto en un ambiente local:

### Opcion A: Levantar infraestructura completa (Base de datos y Backend)
Ideal para realizar pruebas directamente contra la API sin compilar el proyecto en un IDE local. Desde la raiz del proyecto, ejecutar:

```bash
docker-compose up -d --build
```

La API quedara expuesta en: http://localhost:8080

### Opcion B: Levantar unicamente la base de datos (Para desarrollo)
Ideal para continuar con el desarrollo del backend. Este comando levantara unicamente PostgreSQL:

```bash
docker-compose up -d db
```

Una vez que el contenedor de PostgreSQL este en ejecucion, puede abrir el proyecto en su IDE de preferencia, sincronizar las dependencias de Gradle e iniciar el servicio ejecutando la clase principal `PuceParkApplication.kt`. El backend se conectara automaticamente a la base de datos local en el puerto 5432 con las credenciales por defecto (postgres / password).

## Documentacion de la API (Endpoints y Roles)

La API cuenta con una configuracion CORS permisiva para integraciones con clientes frontend y dispositivos moviles. El control de acceso se basa en los roles emitidos en los JWT de AWS Cognito a traves del claim `cognito:groups`.

### Endpoints Publicos (Sin token requerido)
- GET /api/v1/zonas : Retorna la lista de todas las zonas de parqueo disponibles.
- GET /api/v1/puestos/zona/{zonaId} : Retorna la lista de puestos asociados a una zona especifica.

### Endpoints para el Rol DRIVER
- POST /api/v1/puestos/{id}/ocupar : Ocupa un puesto de parqueo disponible, generando un registro de historial.
- POST /api/v1/puestos/{id}/liberar : Libera un puesto de parqueo. Solo es valido si el puesto fue ocupado por el mismo usuario autenticado.
- GET /api/v1/perfil/me : Retorna la informacion del perfil del usuario autenticado.
- PUT /api/v1/perfil/me : Actualiza los detalles del perfil del usuario (por ejemplo, modo oscuro, placa del vehiculo).

### Endpoints para el Rol GUARD
- PATCH /api/v1/puestos/{id}/forzar-liberacion : Permite liberar cualquier puesto ocupado, ignorando la validacion de propiedad del registro (orientado a administracion fisica del parqueo).

### Endpoints para el Rol ADMIN
- POST, PUT, DELETE /api/v1/zonas : Administracion del CRUD de Zonas.
- POST, PUT, DELETE /api/v1/puestos : Administracion del CRUD de Puestos de parqueo.

## Detalles Tecnicos Importantes
- Bloqueo Pesimista (Pessimistic Locking): Se implemento `@Lock(LockModeType.PESSIMISTIC_WRITE)` en la tabla de puestos de parqueo para mitigar condiciones de carrera en el evento en el que dos usuarios intenten ocupar un mismo puesto simultaneamente.
- Manejo de Errores Estandarizado: Cualquier regla de negocio rota devuelve estructuras JSON consistentes y codigos HTTP semanticos (200, 201, 204, 400, 401, 403, 404, 409).
