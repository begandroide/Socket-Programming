# ChromeCast simulation

Proyecto de Sistemas distribuidos, Chromecast con grupos multicast/broadcast.

## Integrantes

**Grupo Nº4**  
Benjamín Gautier Ortiz - 20146003604  
Juan Caniguante - 201673100-8

## Aclaración

La tarea funciona correctamente en las máquinas virtuales designadas.

## Estructura

```bash
user@group:~\ChromeCast> tree ./ -L 1
./
├── bin -> binarios
├── Makefile
├── README.md
└── src -> fuente
```

## Compilación :arrow_right_hook:

- Se debe usar **Java 8**

```bash
user@group:~\ChromeCast> make default
```

Lo anterior dejará en el directorio *./bin* las clases compiladas.

## Uso servidor

```bash
user@group:~\ChromeCast> cd bin 
user@group:~\ChromeCast\bin> java Server <ip_multicast>
ejemplo: java Server 230.0.0.1
```

## Uso cliente

```bash
user@group:~\ChromeCast> cd bin 
user@group:~\ChromeCast\bin> java Client <ip_multicast> <port_number>
ejemplo: java Client 230.0.0.1 5030
```

## Funcionalidad

Chromecast es un servidor multicast de contenido multimedia que envía cada 1 segundo el estado actual del mismo. Ofrece reproductor multimedia on-demand de sus clientes. Si un cliente pide una canción o que se detenga la reproducción, todos los clientes obtendrán el mismo resultado, ya que es multicast.

## Consideraciones (importantes)

Cuando un cliente desea ingresar un comando, entonces el thread que se encuentra escuchando el ChromeCast se detiene para permitir ingresar el input del usuario. Se decidió lo anterior debido a que el ChromeCast envía un mensaje cada 1 segundo, el cual es impreso por la salida estandar de consola, limpiando la línea en donde potencialmente el usuario debe ingresar el comando. Por lo que declaramos dos modos:

1. Escucha thread ChromeCast
2. Modo comandos con Escucha suspendida

**Observación**: Solo se detiene el thread del cliente que escucha al ChromeCast que está en modo comando, por lo que cualquier otro cliente puede escuchar sin inconvenientes el ChromeCast.

## Servidor

### Comandos
El servidor ofrece los siguientes comandos: 

- Play_\<cancion>-\<autor>_\<segundos>
- Stop
- Pause
- Queue
- Queue_\<cancion>-\<autor>_\<segundos>
- Next
- Jump_\<queue_cancion_id>
- Help

### Estados

- Play: en reproducción
- Stop: cola vacía
- Pause: Pausada.

## Cliente

El cliente ingresa por defecto a escuchar el grupo multicast (ChromeCast),
para ingresar comandos debe presionar la tecla "c" y luego Enter.

