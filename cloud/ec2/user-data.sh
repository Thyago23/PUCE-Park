#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────────
# PucePark — script de arranque para EC2 (Amazon Linux 2023)
# Pégalo en "User data" al lanzar la instancia, o ejecútalo por SSH con sudo.
# Instala Docker + Compose, clona el repo y levanta todo el stack.
# ─────────────────────────────────────────────────────────────────────────────
set -eux

# 1) Paquetes base
dnf update -y
dnf install -y docker git

# 2) Docker
systemctl enable --now docker
usermod -aG docker ec2-user

# 3) Plugin de Docker Compose v2
DOCKER_CONFIG=/usr/local/lib/docker
mkdir -p "$DOCKER_CONFIG/cli-plugins"
curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o "$DOCKER_CONFIG/cli-plugins/docker-compose"
chmod +x "$DOCKER_CONFIG/cli-plugins/docker-compose"

# 4) Swap de 2 GB (la build de Gradle necesita RAM; útil en instancias pequeñas)
if [ ! -f /swapfile ]; then
  dd if=/dev/zero of=/swapfile bs=1M count=2048
  chmod 600 /swapfile
  mkswap /swapfile
  swapon /swapfile
  echo '/swapfile none swap sw 0 0' >> /etc/fstab
fi

# 5) Clonar el repo (público) y levantar el stack
cd /home/ec2-user
if [ ! -d pucepark ]; then
  git clone https://github.com/Thyago23/ae_2026_01_Taco_Cede-o_1462_PucePark.git pucepark
fi
cd pucepark

# (Opcional producción) contraseñas por entorno:
# cat > .env <<EOF
# DB_USER=postgres
# DB_PARK_PASSWORD=CAMBIA_ESTO
# DB_MICRO_PASSWORD=CAMBIA_ESTO
# EOF

# Compila las imágenes y levanta los 5 contenedores (nginx en el puerto 80)
docker compose up -d --build

echo "PucePark desplegado. nginx escuchando en el puerto 80."
