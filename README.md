# Flink Real-Time Movie View Counter

Apache Flink es una de las tecnologías líderes para procesamiento de datos en tiempo real (stream processing). Está diseñado para manejar grandes volúmenes de eventos, con baja latencia y alta tolerancia a fallos.

Este proyecto simula el procesamiento en tiempo real de visualizaciones de películas para alimentar un catálogo. (tipo Netflix). Las tecnologias usadas

- Apache Kafka: para recibir eventos tipo MovieEntity (título, año, género).

- Apache Flink 1.20: como motor de stream processing.

- PostgreSQL: como base de datos destino para almacenar películas.

- Docker Compose: para levantar el entorno completo en local (Zookeeper, Kafka, Postgres, Flink JobManager y TaskManager).

- Java 17

 ![Screenshot from running application](img/architect.png?raw=true "Screenshot")

## Flujo:

Una aplicación publica eventos JSON en Kafka bajo el tópico movies-topic.

Apache Flink lee estos mensajes en tiempo real usando KafkaSource<MovieEntity>,los mensajes se procesan y se persisten en PostgreSQL usando JdbcSink.

Se puede monitorear el job en tiempo real desde el Flink UI (localhost:8081).

## Componentes

1. **event-producer**: 
    Permite enviar eventos al topico de Kafka de informacion de peliculas.
2. **flink-job**: 
    Procesa los eventos y guarda agregados en PostgreSQL. Pasos que realiza:

    - El JobManager analiza el pipeline, divide en tareas.

    - Asigna las tareas a los TaskManagers disponibles.

    - Empieza el procesamiento paralelo del stream (por ejemplo, desde Kafka).

    - Puedes ver el progreso en http://localhost:8081.

3. **api-viewer**: 
    
    El módulo api-viewer está pensado para ser una API REST que expone los resultados procesados por Apache Flink, por ejemplo: Consulta los conteos por película vía API REST.

## Cómo ejecutarlo en local (Standalone)

```bash
docker-compose up -d
```

Luego, construye los servicios Java con Maven y ejecuta los JARs en este orden:

1. event-producer
    Ejecutas la clase Application (main).

    POST: /api/movie
    {
        "title": "titulo",
        "year": 2025,
        "genre": "action"
    }

    Esto publica en Kafka un mensajem en el topico movie-group.

     ![Screenshot from running application](img/post-save-movie.png?raw=true "Screenshot")

2. flink-job
  
        mvn clean install

    Aqui optamos por probar desplegandolo en el pod del job manager o publicando el JAR desde la consola.

        docker cp target/flink-job-0.0.1-SNAPSHOT.jar apache-flink-flink-jobmanager-1:/opt/flink/

        docker exec -it apache-flink-flink-jobmanager-1  flink run -c com.example.flinkjob.FlinkJobApp  /opt/flink/flink-job-0.0.1-SNAPSHOT.jar

    ![Screenshot from running application](img/job-manager.png?raw=true "Screenshot")

    ![Screenshot from running application](img/save-postgres.png?raw=true "Screenshot")


Explora Flink UI:

    http://localhost:8081

 ![Screenshot from running application](img/console-job-manager.png?raw=true "Screenshot")


3. api-viewer

Envía eventos:

    curl -X POST http://localhost:8080/api/view/movie-123

Verifica la API de conteos:

    curl http://localhost:8082/api/views


##  Modo Clúster – JobManager y TaskManager

Aqui optamos por publicar el JAR desde la consola, enviar el jar al JobManager.

## Tecnologia:

 #### flink-jobmanager

 Controla y coordina la ejecución distribuida de los trabajos.. Se encarga de:

- Recibir y planificar los trabajos de Flink (por ejemplo, tu aplicación Java de procesamiento de streams).

- Dividir el trabajo en subtareas.

- Asignar esas subtareas a los TaskManagers.

- Hacer seguimiento de su ejecución.

- Reiniciar tareas si fallan (gracias al soporte de tolerancia a fallos).

- Exponer una UI Web en el puerto 8081 donde puedes ver los trabajos corriendo, su progreso, errores, logs, etc.

#### flink-taskmanager

Este es el trabajador que ejecuta realmente las tareas asignadas. Se encarga de:

- Ejecutar los operadores de procesamiento como .map(), .filter(), .keyBy(), .window(), .aggregate()...

- Manejar buffers y transferencia de datos entre tareas (shuffling).

- Usar su memoria y CPU para procesar los datos en paralelo.

- Reportar su estado al JobManager.

📍 Puedes tener múltiples TaskManagers, y cada uno puede ejecutar varias subtareas en paralelo (slots).