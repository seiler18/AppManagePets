# AppManagePets

App satélite del portafolio. **No se gobierna desde aquí**: los procedimientos
viven en `Curriculo/.claude/skills/` (`revivir-app-java`, `desplegar-app-java`).
Este archivo existe para que una sesión abierta en esta carpeta sepa qué es
esto y qué no debe hacer sola.

## Qué es

Registro de personas y de sus mascotas, con cuentas y dos roles. Unifica dos
ejercicios anteriores (`RegistroPersona` y `RegistroMascota`). El detalle
funcional y de seguridad está en `README.md` — **ese es la fuente de verdad**,
no este archivo.

| Pieza | Qué se usa |
|---|---|
| Backend | Spring Boot 3.3.5, Java 17 |
| Vistas | Thymeleaf + CSS propio, sin framework de front |
| Seguridad | Spring Security 6, BCrypt, CSRF activo, roles `USER` / `ADMIN` |
| Persistencia | Spring Data JPA · H2 en memoria (local) · PostgreSQL (prod) |
| Build | Maven, Docker multietapa |
| Deploy | Render (blueprint `render.yaml`) + Postgres en Neon |

## Levantarlo

```bash
mvn clean package
java -jar target/appmanagepets-1.0.0.jar    # http://localhost:8080
```

No hace falta instalar base de datos: el perfil por defecto usa H2 en memoria.
Cuentas de arranque en local: `demo/demo123` (USER), `admin/admin123` (ADMIN).

## Reglas

1. **`mvn clean package` verde antes de cualquier push.** Las pruebas de
   propiedad (`PropiedadDeMascotasTest`, `SeguridadWebTest`) defienden el
   control de acceso, incluida la regresión del `403` que devolvía `405`. Si
   una falla, el arreglo no es borrarla.
2. **Ninguna credencial en el repo.** Es público. Todo lo sensible va con
   `sync: false` en `render.yaml` y se introduce en el panel de Render. En
   producción `APP_ADMIN_PASSWORD` es obligatoria y la app **no arranca sin
   ella** — eso es deliberado.
3. **El acceso a una mascota pasa siempre por `MascotaService.buscarPropia`.**
   Lanza `AccessDeniedException`, no "no encontrada", para no revelar qué ids
   existen. Cualquier consulta nueva sigue el mismo camino.
4. **Borrar va por `POST` con token CSRF.** Un enlace `GET` sería inseguro y
   además lo rechaza el filtro.
5. **Versiones de Spring sin `<version>` a mano**: se heredan del parent.
6. **El enlace vivo se publica desde el CV**, no desde aquí. Si cambia la URL,
   se actualiza en `Curriculo` y se anota en un hito **de Curriculo**.

## Qué NO va aquí

No lleva `.claude/hitos/` ni `.claude/skills/` propios: es nivel B del
estándar de `Desarrollo/CLAUDE.md`. Su memoria y sus procedimientos son los de
`Curriculo`, que es el proyecto que la publica.

## Convenciones

Español en comentarios, commits y documentación. Commits de una línea con el
qué concreto. Los comentarios explican *por qué*, no *qué*.
