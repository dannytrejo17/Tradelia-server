# Tradelia Server - Modo Demo

La rama `demo` contiene una version de solo lectura pensada para ensenar el proyecto sin depender de datos manuales.

## Que incluye

- 2 cuentas demo precargadas
- 8 productos demo cargados al arrancar
- login normal con JWT Bearer
- catalogo y filtros funcionando
- endpoints de lectura del chat habilitados

## Que esta bloqueado

En modo demo se responde con `403 Forbidden` cuando intentas:

- registrarte
- publicar productos
- editar productos
- eliminar productos
- enviar mensajes

## Cuentas demo

| Rol | Email | Password |
|-----|-------|----------|
| Vendedora | `demo@tradelia.com` | `Demo1234!` |
| Comprador | `buyer@tradelia.com` | `Demo1234!` |

## Como arrancarlo en local

Define estas variables y activa el perfil `demo`.

### Windows PowerShell

```powershell
$env:SPRING_PROFILES_ACTIVE="demo"
$env:JWT_SECRET="clave-secreta-de-al-menos-32-caracteres"
mvn spring-boot:run
```

Si no usas `application.properties`, define tambien las variables o propiedades de base de datos necesarias para MySQL.

## Como funciona internamente

- `DemoModeService` decide si la app esta en demo y bloquea escrituras
- `DemoDataLoader` carga usuarios y productos desde `src/main/resources/data/`
- los datos se guardan en la base de datos normal de la app
- el JSON se usa como semilla, no como fuente directa en cada request

## Archivos clave

- `src/main/resources/application-demo.properties`
- `src/main/resources/data/demo-users.json`
- `src/main/resources/data/demo-products.json`
- `src/main/java/com/tradelia/Service/DemoModeService.java`
- `src/main/java/com/tradelia/Service/DemoDataLoader.java`
