# Despliegue de la página de descarga en AWS S3

**Computación en la Nube (criterio 4.3)** — página web (static website) alojada en **Amazon S3** desde la que se descarga la app: **APK (Android)** e **iOS (simulado)**.

Archivos en `cloud/download-page/`:
- `index.html` — página de descarga
- `PucePark.apk` — APK (placeholder de simulación; reemplázalo por el build real si lo tienes)
- `PucePark.ipa` — paquete iOS (simulación; iOS real se distribuye por App Store / TestFlight)

---

## Opción A — Consola de AWS (más visual)

1. **S3** → **Create bucket**
   - Nombre único, ej. `pucepark-descargas-bstaco`
   - Región: `us-east-1`
   - **Desmarca** "Block all public access" y confirma el aviso.
   - Create bucket.
2. Entra al bucket → pestaña **Properties** → abajo **Static website hosting** → **Edit** → **Enable**
   - Index document: `index.html` → Save.
3. Pestaña **Objects** → **Upload** → arrastra los 3 archivos (`index.html`, `PucePark.apk`, `PucePark.ipa`) → **Upload**.
4. Pestaña **Permissions** → **Bucket policy** → **Edit** → pega (cambia `TU_BUCKET`):
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [{
       "Sid": "PublicReadGetObject",
       "Effect": "Allow",
       "Principal": "*",
       "Action": "s3:GetObject",
       "Resource": "arn:aws:s3:::TU_BUCKET/*"
     }]
   }
   ```
5. La URL está en **Properties → Static website hosting → Bucket website endpoint**:
   `http://TU_BUCKET.s3-website-us-east-1.amazonaws.com`

---

## Opción B — AWS CloudShell (línea de comandos, más rápido)

Abre **CloudShell** (ícono `>_` abajo en la consola). Sube los 3 archivos con **Actions → Upload file** (o `git clone` del repo) y ejecuta:

```bash
BUCKET=pucepark-descargas-bstaco      # nombre único
REGION=us-east-1

aws s3 mb s3://$BUCKET --region $REGION
aws s3 website s3://$BUCKET --index-document index.html

# permitir acceso público
aws s3api put-public-access-block --bucket $BUCKET \
  --public-access-block-configuration BlockPublicAcls=false,IgnorePublicAcls=false,BlockPublicPolicy=false,RestrictPublicBuckets=false

aws s3api put-bucket-policy --bucket $BUCKET --policy "{
  \"Version\":\"2012-10-17\",
  \"Statement\":[{\"Sid\":\"PublicRead\",\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":\"s3:GetObject\",\"Resource\":\"arn:aws:s3:::$BUCKET/*\"}]
}"

# subir la página y los paquetes
aws s3 cp index.html    s3://$BUCKET/ --content-type text/html
aws s3 cp PucePark.apk  s3://$BUCKET/ --content-type application/vnd.android.package-archive
aws s3 cp PucePark.ipa  s3://$BUCKET/ --content-type application/octet-stream

echo "URL: http://$BUCKET.s3-website-$REGION.amazonaws.com"
```

---

## Sobre la "simulación" de iOS
En iOS no se instala por archivo libre como el APK; se distribuye por **App Store** o **TestFlight**. La página ofrece el paquete iOS como **simulación** del flujo de descarga (lo que pidió el profe), con una nota que lo aclara.

## Para la sustentación
Muestra la URL pública de S3, descarga el APK desde la página, y explica: bucket S3 con *static website hosting*, política de acceso público de solo lectura, y que es infraestructura **IaaS/almacenamiento en la nube** de AWS.
