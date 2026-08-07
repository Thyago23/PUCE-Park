# PucePark — Computación en la Nube

**Proyecto Integrador P02 · PUCE TEC**
Despliegue del sistema en entornos virtualizados (Docker) con arquitectura multicapa.

---

## 1. Conceptos de computación en la nube (criterio 4.1)

**Computación en la nube**: entrega de recursos de cómputo (servidores, almacenamiento, red, bases de datos) bajo demanda a través de Internet, con pago por uso y aprovisionamiento elástico.

**Modelos de servicio:**
- **IaaS** (Infraestructura como Servicio): máquinas virtuales, redes y almacenamiento. *PucePark se despliega sobre este modelo: contenedores Docker que pueden correr en una VM/instancia IaaS (EC2, Compute Engine, etc.).*
- **PaaS**: plataforma gestionada (runtime + BD administrada).
- **SaaS**: software final consumido por el usuario. *La app PucePark sería el SaaS resultante.*

**Virtualización vs contenedores:**
- *Virtualización* (VM): cada instancia incluye su propio SO → mayor aislamiento, más peso.
- *Contenedores* (Docker): comparten el kernel del host → arranque rápido, menor consumo, ideales para microservicios. **PucePark usa contenedores.**

### Escalamiento vertical vs horizontal

| Aspecto | Escalamiento **Vertical** (scale-up) | Escalamiento **Horizontal** (scale-out) |
|---|---|---|
| Qué hace | Añadir más recursos (CPU/RAM) a la **misma** máquina | Añadir **más instancias** de la aplicación |
| Ventajas | Simple, sin cambios de arquitectura; sin sincronización entre nodos | Alta disponibilidad, tolerancia a fallos, crecimiento casi ilimitado, balanceo de carga |
| Desventajas | Límite físico del hardware; punto único de fallo; downtime al escalar | Requiere servicios *stateless* y balanceador; mayor complejidad operativa |
| En PucePark | Subir CPU/RAM al contenedor `park-app` si una zona recibe mucha carga | Levantar **N réplicas** de `park-app`/`users-service` detrás de **nginx** (balanceo), porque son *stateless* (el estado vive en Postgres y en el JWT) |

**Conclusión aplicada:** PucePark está diseñado para **escalamiento horizontal**: los microservicios no guardan estado en memoria (la sesión es el JWT de Cognito y los datos están en la BD), por lo que se pueden replicar y poner detrás de nginx como balanceador. La base de datos escalaría verticalmente o con réplicas de lectura.

## 2. Virtualización / contenedores (criterio 4.2)

Sistema **multicapa y multiplataforma** orquestado con `docker-compose` (5 contenedores):

| Servicio | Imagen/Build | Rol | Puerto |
|---|---|---|---|
| `nginx` | nginx:alpine | Reverse proxy / punto de entrada único | **80 → host** |
| `park-app` | build (Spring Boot/Kotlin) | API de parqueo (`/api/*`) | 8080 (interno) |
| `users-service` | build (Spring Boot/Kotlin) | Microservicio de perfiles (`/users/*`) | 8686 (interno) |
| `db_park` | postgres:16-alpine | BD del parqueo (`puce_park`) | 5434 → host |
| `db_micro` | postgres:16-alpine | BD de usuarios (`puce_micro`) | 5435 → host |

Características de infraestructura implementadas:
- **Red privada** `park_net` (bridge) — los servicios se comunican por nombre de contenedor.
- **Volúmenes persistentes** `db_park_data`, `db_micro_data` — los datos sobreviven a reinicios.
- **Healthchecks** en las BDs (`pg_isready`) + `depends_on: condition: service_healthy`.
- **Multi-stage build** en el Dockerfile (compila con Gradle y empaqueta solo el JRE + jar).
- **Punto de entrada único**: solo nginx publica puerto al host; los servicios usan `expose`.

## 3. Diseño de arquitectura de infraestructura (criterio 4.3)

```
                          ┌──────────────────────────┐
                          │      AWS Cognito         │  (User Pool + JWKS)
                          │  emite y firma los JWT    │
                          └────────────┬─────────────┘
                                       │ el cliente obtiene el token
                                       ▼
   ┌──────────┐   HTTP :80    ┌─────────────────────────────┐
   │  App iOS │ ────────────► │        nginx (reverse proxy) │
   │ (cliente)│               │   punto de entrada único     │
   └──────────┘               └───────┬───────────────┬──────┘
                                       │ /api/*        │ /users/*
                                       ▼               ▼
                          ┌────────────────┐   ┌────────────────────┐
                          │   park-app     │   │   users-service    │
                          │ Spring :8080   │   │ Spring :8686       │
                          │ (zonas,puestos,│   │ (perfiles)         │
                          │  historial)    │   │                    │
                          └───────┬────────┘   └─────────┬──────────┘
                                  │ JDBC                  │ JDBC
                                  ▼                       ▼
                          ┌────────────────┐   ┌────────────────────┐
                          │  db_park       │   │   db_micro         │
                          │  PostgreSQL    │   │   PostgreSQL       │
                          │  (puce_park)   │   │   (puce_micro)     │
                          │  vol: db_park  │   │   vol: db_micro    │
                          └────────────────┘   └────────────────────┘

        Red interna: park_net (bridge)   ·   Único puerto expuesto: 80 (nginx)
```

**Principios de diseño:**
- **Aislamiento por servicio**: cada microservicio tiene su BD; no comparten esquema ni hacen *joins* cruzados.
- **Sin llamadas entre servicios**: la confianza se delega al JWT de Cognito (cada servicio valida por su cuenta).
- **Seguridad perimetral**: solo nginx queda expuesto; las BDs y servicios no son accesibles directamente desde fuera (los puertos 5434/5435 se publican solo para inspección en desarrollo).
- **Preparado para la nube**: este `docker-compose` puede desplegarse en una instancia **IaaS** (AWS EC2 / GCP Compute Engine) tal cual, o migrarse a un orquestador (ECS/Kubernetes) para escalamiento horizontal real replicando `park-app`/`users-service`.

## 4. Ventajas del enfoque para la nube

- **Reproducibilidad**: `docker-compose up -d --build` levanta todo el sistema en cualquier host con Docker.
- **Elasticidad**: servicios stateless → réplicas horizontales bajo nginx.
- **Costo**: contenedores livianos (alpine) → menor consumo que VMs completas.
- **Portabilidad multiplataforma**: mismas imágenes en local, IaaS o nube pública.
