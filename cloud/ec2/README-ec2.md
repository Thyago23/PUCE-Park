# Despliegue de PucePark en AWS EC2 (IaaS)

Levanta el backend completo (nginx + park-app + users-service + 2 PostgreSQL) en una **instancia EC2**. Cubre Computación en la Nube 4.3 (despliegue en IaaS).

---

## Opción A — Consola de AWS (con user-data)

1. **EC2 → Launch instance.**
2. **AMI:** *Amazon Linux 2023*.
3. **Tipo:** **`t3.small`** (2 GB) mínimo — ideal `t3.medium` (4 GB). *No uses `t2.micro` (1 GB): la compilación de Gradle se queda sin memoria.*
4. **Key pair:** crea/selecciona una (para SSH).
5. **Network / Security group** — reglas de entrada:
   - `SSH (22)` → **Solo tu IP** (My IP).
   - `HTTP (80)` → `0.0.0.0/0` (acceso público a la app por nginx).
   - **No abras** 5434/5435 (las BDs quedan internas).
6. **Advanced details → User data:** pega el contenido de [`user-data.sh`](user-data.sh).
7. **Launch instance.** Espera **~5–8 min** (instala Docker y compila las imágenes).

### Verificar
- Copia la **IPv4 pública** de la instancia.
- `http://<IP_PUBLICA>/` → responde `{"error":"Not found"}` (nginx arriba).
- `http://<IP_PUBLICA>/api/v1/zonas` → **401** (llega a park-app; requiere token).
- `http://<IP_PUBLICA>/users/me` → **401** (llega a users-service).
- Por SSH: `docker compose ps` → 5 contenedores; las apps en `(healthy)`.

---

## Opción B — SSH manual (si no usaste user-data)
```bash
ssh -i tu-llave.pem ec2-user@<IP_PUBLICA>
# dentro de la instancia:
curl -SL https://raw.githubusercontent.com/Thyago23/ae_2026_01_Taco_Cede-o_1462_PucePark/main/cloud/ec2/user-data.sh | sudo bash
```

---

## Conectar la app iOS al backend en EC2
Cambia `PucePark/Resources/config.plist` en el frontend:
```xml
<key>API_BASE_URL</key>   <string>http://<IP_PUBLICA>/api/v1</string>
<key>USERS_BASE_URL</key> <string>http://<IP_PUBLICA>/users</string>
```

> ⚠️ **ATS (App Transport Security):** iOS **bloquea HTTP en claro** hacia IPs/dominios que no sean `localhost`. Para apuntar a `http://<IP_PUBLICA>` tienes 2 opciones:
> 1. **Recomendado:** poner **HTTPS** en la instancia (dominio + certificado, p. ej. Caddy/nginx + Let's Encrypt) y usar `https://...`.
> 2. **Rápido (solo demo):** en el `Info.plist` de la app, agrega una excepción ATS:
>    ```xml
>    <key>NSAppTransportSecurity</key>
>    <dict>
>      <key>NSAllowsArbitraryLoads</key><true/>
>    </dict>
>    ```

---

## Operación
```bash
docker compose ps                 # estado
docker compose logs -f park-app   # logs
docker compose pull; docker compose up -d --build   # actualizar tras cambios
docker compose down               # detener (conserva datos en volúmenes)
```

## Costos
`t3.small` bajo demanda ≈ USD 0.02/h. **Apaga la instancia** cuando no la uses (o usa capa gratuita si aplica). Recuerda liberar la Elastic IP si asignas una.

## Para la sustentación
Muestra `http://<IP_PUBLICA>/api/v1/zonas` respondiendo desde la nube y `docker compose ps` con los contenedores `(healthy)`. Explica: instancia **EC2 (IaaS)**, Docker multicapa, nginx como única puerta (puerto 80), y la página de descarga en **S3** — todo sobre **AWS**.
