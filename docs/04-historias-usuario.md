# PucePark — Historias de Usuario (Backlog)

**Proyecto Integrador P02 · PUCE TEC**
Formato: *Como \<rol\> quiero \<acción\> para \<beneficio\>* + criterios de aceptación (Gherkin).
Roles: **Estudiante** (USER), **Guardia** (GUARD), **Admin** (ADMIN).

| HU | Historia | RF | Prioridad | Puntos |
|----|----------|----|-----------|--------|
| HU-01 | Inicio de sesión único | RF-01 | Alta | 5 |
| HU-02 | Aviso "no estás registrado" | RF-02 | Media | 2 |
| HU-03 | Completar perfil (onboarding) | RF-03 | Alta | 5 |
| HU-04 | Ver zonas con disponibilidad | RF-04 | Alta | 3 |
| HU-05 | Ver mapa de puestos de una zona | RF-05 | Alta | 5 |
| HU-06 | Ocupar un puesto | RF-06 | Alta | 5 |
| HU-07 | Liberar mi puesto | RF-07 | Alta | 3 |
| HU-08 | Guardia: registrar entrada manual | RF-08 | Media | 3 |
| HU-09 | Guardia: forzar liberación | RF-09 | Media | 2 |
| HU-10 | Ver mi historial | RF-10 | Media | 3 |
| HU-11 | Ver estadísticas mensuales | RF-11 | Baja | 2 |
| HU-12 | Ver ranking mensual | RF-12 | Baja | 3 |
| HU-13 | Editar mi perfil | RF-13 | Media | 3 |
| HU-14 | Admin: gestionar zonas y puestos | RF-14 | Media | 5 |

---

## HU-01 · Inicio de sesión único
**Como** estudiante o guardia **quiero** iniciar sesión con mi cuenta PUCE **para** entrar al sistema según mi rol.
- **Dado** que ingreso usuario y contraseña válidos, **cuando** confirmo, **entonces** obtengo un token (Cognito) y entro a la pantalla de Zonas.
- **Dado** credenciales inválidas, **entonces** veo un mensaje de error claro.
- **Dado** un token de guardia, **entonces** la app habilita las funciones de guardia.

## HU-02 · Aviso "no estás registrado"
**Como** usuario autenticado sin cuenta de parqueo **quiero** un aviso claro **para** saber que debo ir a Secretaría.
- **Dado** que mi usuario no tiene perfil en el sistema, **cuando** ingreso, **entonces** veo "No estás registrado, ve a Secretaría" y la opción de cerrar sesión.

## HU-03 · Completar perfil (onboarding)
**Como** usuario nuevo **quiero** completar mi perfil **para** poder usar el parqueo.
- **Dado** un perfil incompleto, **cuando** ingreso, **entonces** se muestra el onboarding y no puedo salir sin guardar.
- **Dado** que ingreso nombre y apellido + placa/cédula + permiso válidos, **cuando** guardo, **entonces** el perfil queda completo y entro a la app.
- **Dado** un nombre de una sola palabra, **entonces** la validación lo rechaza (se exige nombre y apellido).

## HU-04 · Ver zonas con disponibilidad
**Como** estudiante **quiero** ver las zonas y su disponibilidad **para** elegir dónde parquear.
- **Dado** que abro la app, **entonces** veo las zonas con total disponibles/ocupados y barra de ocupación.
- **Dado** que hay un cambio de estado, **cuando** vuelvo a la pestaña, **entonces** los datos se refrescan.

## HU-05 · Ver mapa de puestos de una zona
**Como** estudiante **quiero** ver los puestos de una zona con su estado **para** identificar uno libre.
- **Dado** que abro una zona, **entonces** veo el grid de puestos con colores (verde=disponible, amarillo=mi puesto, rojo=ocupado) y su número (A-01…).
- **Dado** que tengo un puesto activo, **entonces** aparece un botón "Liberar" fijo arriba.

## HU-06 · Ocupar un puesto
**Como** estudiante **quiero** ocupar un puesto disponible **para** reservar mi lugar.
- **Dado** un puesto disponible y yo sin puesto activo, **cuando** confirmo "Ocupar", **entonces** el puesto pasa a ocupado (amarillo para mí) y se registra en el historial.
- **Dado** que ya tengo un puesto activo, **cuando** intento ocupar otro, **entonces** el sistema lo impide con un aviso.
- **Dado** dos usuarios sobre el mismo puesto, **entonces** solo uno lo obtiene (bloqueo de concurrencia).

## HU-07 · Liberar mi puesto
**Como** estudiante **quiero** liberar mi puesto rápidamente **para** dejarlo libre al irme.
- **Dado** que tengo un puesto activo, **cuando** toco "Liberar", **entonces** el puesto queda disponible y se cierra la sesión de parqueo (hora de salida).

## HU-08 · Guardia: registrar entrada manual
**Como** guardia **quiero** registrar la entrada de un vehículo con su placa **para** controlar accesos sin app del conductor.
- **Dado** un puesto disponible, **cuando** ingreso una placa y registro, **entonces** el puesto queda ocupado a nombre del guardia con la placa.
- **Dado** placa vacía, **entonces** la operación se rechaza.

## HU-09 · Guardia: forzar liberación
**Como** guardia **quiero** liberar un puesto ocupado **para** corregir o liberar plazas.
- **Dado** un puesto ocupado, **cuando** fuerzo la liberación, **entonces** queda disponible.

## HU-10 · Ver mi historial
**Como** estudiante **quiero** ver mi historial de parqueos **para** llevar control de mi uso.
- **Dado** que abro Historial, **entonces** veo mis sesiones (activas y completadas) con fechas.

## HU-11 · Ver estadísticas mensuales
**Como** estudiante **quiero** ver mis estadísticas del mes **para** conocer mi uso.
- **Dado** un mes, **entonces** veo horas totales, sesiones y racha.

## HU-12 · Ver ranking mensual
**Como** estudiante **quiero** ver el ranking del mes **para** compararme con otros usuarios.
- **Dado** un mes, **entonces** veo la lista ordenada por horas, con mi nombre resaltado.
- Excluye registros de guardia.

## HU-13 · Editar mi perfil
**Como** usuario **quiero** editar mis datos **para** mantenerlos al día.
- **Dado** el botón de lápiz, **cuando** edito y guardo con datos válidos, **entonces** el perfil se actualiza.

## HU-14 · Admin: gestionar zonas y puestos
**Como** admin **quiero** administrar zonas y puestos **para** mantener el catálogo.
- **Dado** rol ADMIN, **cuando** creo/edito/elimino zonas o puestos, **entonces** el cambio se refleja; un no-admin recibe 401/403.

---

### Definición de Terminado (DoD)
- Código en rama `feature/HU-NN-...` integrado a `develop` por Pull Request revisado.
- Pruebas unitarias del componente en verde (`./gradlew test`).
- Sin errores de arranque; endpoints protegidos por rol.
- Documentación/So criterios de aceptación cumplidos.
