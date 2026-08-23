# AppManagePets

Registro de personas y de sus mascotas, con cuentas de usuario y dos niveles de
acceso. Una persona crea su cuenta, completa su ficha y anota sus mascotas; solo
ve **sus** registros. La cuenta de administración ve el catálogo completo.

Unifica dos ejercicios anteriores (`RegistroPersona` y `RegistroMascota`) en una
sola aplicación, ahora con autenticación, validación y control de propiedad.

**Aplicación en vivo:** ver el enlace en el portafolio de
[seiler18.github.io/Curriculo](https://seiler18.github.io/Curriculo/)

## Stack

| Pieza | Qué se usa |
|---|---|
| Backend | Spring Boot 3.3.5, Java 17 |
| Vistas | Thymeleaf + CSS propio (sin framework de front) |
| Seguridad | Spring Security 6, BCrypt, CSRF activo, roles `USER` / `ADMIN` |
| Persistencia | Spring Data JPA · H2 en memoria (local) · PostgreSQL (producción) |
| Build | Maven, Docker multietapa |

## Cómo funciona el control de acceso

Es la parte que importa, así que está concentrada en un sitio y no repartida:

- `Mascota` cuelga de `Dueno`, y `Dueno` cuelga de una cuenta (`app_user`).
- Toda lectura o escritura de una mascota pasa por
  `MascotaService.buscarPropia(id, username)`, que consulta
  `findByIdAndDuenoUsuarioUsername`. Si el id no es del usuario, lanza
  `AccessDeniedException`; **no** devuelve "no encontrada", para no revelar qué
  ids existen probando la URL.
- `/admin/**` lo corta `SecurityConfig` con `hasRole("ADMIN")`.
- Borrar va por `POST` con token CSRF: un enlace `GET` sería además de
  inseguro, rechazado por el filtro.

Hay pruebas para esto en `PropiedadDeMascotasTest` y `SeguridadWebTest`,
incluida la regresión del `403` que devolvía `405`.

## Levantarlo en local

Requiere JDK 17 o superior. No hace falta instalar base de datos: el perfil por
defecto usa H2 en memoria.

```bash
mvn clean package
java -jar target/appmanagepets-1.0.0.jar
```

Queda en <http://localhost:8080>.

Cuentas que se crean al arrancar:

| Usuario | Contraseña | Rol | Ve |
|---|---|---|---|
| `demo` | `demo123` | USER | sus 2 mascotas de ejemplo |
| `admin` | `admin123` | ADMIN | todo |

La contraseña de `admin` en local sale de `app.admin.password`. En producción es
obligatoria por variable de entorno y la app **no arranca sin ella** — es
preferible un despliegue fallido a un `admin/admin123` publicado en internet.

## Desplegarlo

Pensado para Render con el plan gratuito, usando el `Dockerfile` multietapa y el
blueprint `render.yaml`. La base de datos es un PostgreSQL gestionado externo.

Variables de entorno que hay que configurar en el proveedor (los **nombres**;
los valores nunca van al repositorio):

| Variable | Para qué |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | cadena JDBC del PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | usuario de la base |
| `SPRING_DATASOURCE_PASSWORD` | contraseña de la base |
| `APP_ADMIN_PASSWORD` | contraseña de la cuenta de administración |

En `render.yaml` están declaradas con `sync: false`: Render las pide al crear el
servicio y no quedan escritas en el repositorio, que es público.

El plan gratuito duerme la aplicación tras 15 minutos sin visitas; la siguiente
petición tarda entre 30 y 60 segundos en despertarla. Es el compromiso a cambio
de que no cueste nada.

## Estructura

```
src/main/java/cl/jesus/appmanagepets/
├── config/       SecurityConfig · DatosIniciales (siembra idempotente)
├── controllers/  Home · Auth · Perfil · Mascota · Admin
├── entities/     Usuario · Dueno · Mascota
├── repositories/ consultas, incluido el JOIN FETCH del panel de admin
└── services/     UsuarioService (UserDetailsService) · Dueno · Mascota
```

## Autor

Jesús Seiler — [seiler18.github.io/Curriculo](https://seiler18.github.io/Curriculo/)
