# ChromeCast simulation

Proyecto de Sistemas distribuidos, Chromecast con grupos multicast/broadcast.

## Funcionalidad

Chromecast es un servidor multicast de contenido multimedia que envía cada 1 segundo el estado actual del mismo. Ofrece reproductor multimedia on-demand de sus clientes. Si un cliente pide una canción o que se detenga la reproducción, todos los clientes obtendrán el mismo resultado, ya que es multicast.

## Consideraciones

Cuando un cliente desea ingresar un comando, entonces el thread que se encuentra escuchando el ChromeCast se detiene para permitir ingresar el input del usuario. Se decidió lo anterior debido a que el ChromeCast envía un mensaje cada 1 segundo, el cual es impreso por la salida estandar de consola, limpiando la línea en donde potencialmente el usuario debe ingresar el comando. Por lo que declaramos dos modos:

1. Escucha thread ChromeCast
2. Modo comandos con Escucha suspendida

## Servidor

### Comandos
El servidor ofrece los siguientes comandos: 

- Play
- Stop
- Pause
- Queue
- Next
- Jump

### Estados

- Play
- Stop
- Pause

## Cliente
