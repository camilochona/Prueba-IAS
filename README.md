
BancolAS - Core de Transferencias

Solución Full Stack reactiva para el procesamiento, persistencia y consulta de transferencias financieras, desarrollada con Spring Boot WebFlux y Angular.

1. Decisiones Técnicas y Arquitectura
Para cumplir con los requisitos de negocio y las restricciones operativas (WebFlux), se tomaron las siguientes decisiones de diseño:

* Persistencia Reactiva (R2DBC) y H2 en Memoria: Se optó por utilizar Spring Data R2DBC junto con una base de datos H2 en memoria.

    * Justificación: R2DBC garantiza que toda la cadena de procesamiento de la transferencia sea verdaderamente no bloqueante, aprovechando al máximo el stack reactivo de WebFlux. H2 se eligió para cumplir con la premisa de "versión mínima funcional", permitiendo al evaluador ejecutar el proyecto sin depender de contenedores Docker o configuraciones externas.

* Manejo de Concurrencia (RF04) - Optimistic Locking:

    * Problema: Solicitudes simultáneas podrían leer el mismo saldo y actualizarlo, rompiendo el límite diario de la cuenta.

    * Solución: Se implementó Bloqueo Optimista (Optimistic Locking) agregando un campo @Version en la entidad Account.

    * Validación: Si dos hilos intentan modificar la misma cuenta concurrentemente, la base de datos rechaza la segunda transacción (lanzando OptimisticLockingFailureException). Spring Boot intercepta esta excepción y ejecuta un reintento controlado mediante .retryWhen() para procesar la transacción secuencialmente sin bloquear la tabla a nivel de base de datos.

* Idempotencia y Referencias Repetidas (RF05):

    * Estrategia: Se estableció una restricción UNIQUE en la columna request_reference de la tabla transfer.

    * Flujo: Antes de procesar cualquier deducción, el sistema consulta si la referencia ya existe mediante findByRequestReference. Si existe, se interrumpe el procesamiento y se devuelve el registro histórico intacto. Esta delegación parcial a la base de datos evita condiciones de carrera complejas en la capa de aplicación.

2. Instrucciones de Ejecución

Requisitos previos:
* Java 17 o superior.
* Node.js (v18+) y Angular CLI (v17+).
* Maven.

Ejecución del Backend (Spring Boot WebFlux):

    1. Navegar a la carpeta del backend.

    2. Ejecutar el comando: mvn spring-boot:run

    3. El servidor iniciará de forma reactiva (Netty) en http://localhost:8080.

    4. Nota: La base de datos H2 se inicializa automáticamente con los datos semilla de las cuentas requeridas al arrancar la aplicación.

Ejecución del Frontend (Angular):

    1. Navegar a la carpeta del frontend: cd bancolas-front

    2. Instalar dependencias: npm install

    3. Iniciar el servidor de desarrollo: ng serve

    4. Acceder a la interfaz en http://localhost:4200.

3. Uso de Inteligencia Artificial

Cumpliendo con las directrices de la prueba, se documenta el uso de IA durante el desarrollo:

* Actividades: Brainstorming para estrategias de concurrencia reactiva, generación de la estructura base de los componentes de Angular (HTML/TS) y configuración del entorno de pruebas con WebTestClient.

* Resultados aprovechados: Se adoptó la sugerencia de combinar @Version para el manejo de concurrencia optimista y la delegación de la idempotencia a restricciones SQL (UNIQUE).

* Validación: Se escribieron pruebas de integración (BancolasApplicationTests) que verifican el correcto rechazo de operaciones que superan el límite diario y la correcta autorización de operaciones válidas.

* Seguridad: No se incluyó ninguna credencial, dato real ni código propietario en los prompts suministrados a la herramienta.

