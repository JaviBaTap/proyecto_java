# RutaLimpia - Microservicios

Proyecto de ejemplo con API Gateway y cuatro microservicios Spring Boot.

## Módulos y puertos
- `api_gateway`: 8080
- `auth_service`: 8081
- `solicitudes_service`: 8082
- `flota_service`: 8083
- `rutas_service`: 8084

## Antes de ejecutar
1. Tener Java 21 instalado.
2. Configurar una cola SQS real en AWS. La URL se lee desde la variable de entorno `RUTALIMPIA_SQS_QUEUE_URL`; si no se configura, queda el valor de ejemplo `TU_CUENTA` y SQS no funcionará.
3. Configurar credenciales AWS mediante el mecanismo estándar del SDK (por ejemplo, perfil AWS CLI o variables de entorno).
4. Asegurarse de que la cola exista en la región indicada por `rutalimpia.sqs.region` en `solicitudes_service/src/main/resources/application.properties`.

## Compilar y probar
Desde la carpeta de cada microservicio, ejecutar en Windows:
```powershell
.\mvnw.cmd clean test
.\mvnw.cmd package
```
En Linux/macOS:
```bash
./mvnw clean test
./mvnw package
```

## Gateway
Las rutas públicas están bajo `/api`, por ejemplo `/api/solicitudes`; el Gateway elimina el prefijo `/api` antes de reenviar la solicitud al microservicio.

## Flujo de solicitudes
Al crear una solicitud, se guarda primero en la base de datos y se devuelve un folio. Luego se envía el ID a SQS. El procesador consulta la cola, solicita una asignación al servicio de flota y, si se completa, actualiza la solicitud y agrega un registro al historial. Si falla el procesamiento, el mensaje no se elimina y SQS puede volver a entregarlo.

## Limitaciones importantes
- Cada servicio utiliza H2 en memoria para facilitar pruebas locales; los datos se pierden al reiniciar.
- Los endpoints internos entre servicios deben protegerse adicionalmente antes de desplegar en un entorno real.
- Configurar SQS y probar el flujo asíncrono requiere una cola y credenciales AWS válidas.
