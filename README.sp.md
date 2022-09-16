# SimulatorFN
Este proyecto es un simulador para la difusión de noticias falsas usando Repast Simphony. Incluye una herramienta para la gestión de los resultados de la validación en una base de datos y un parseador de los datasets para obtener los porcentajes de difusión de un rumor o de una noticia falsa. En este fichero se da contexto a cada una de las carpetas. Las instrucciones completas de instalación o de uso se encuentran en los manuales de la memoria. 

## SimulatorFN
En esta carpeta se encuentra el simulador comprimido en *simulator.zip*. Simplemente será necesario descomprimir el fichero y ejecutar el archivo con extensión *.bat* o *.command*, en función del sistema operativo que se utilice. 

## Base de datos
En esta carpeta se encuentran los archivos de la base de datos. Contiene un fichero docker-compose para desplegar un contenedor en Docker. La carpeta *mysql/init* contiene únicamente los mejores resultados obtenidos. Para iniciar la base de datos se recomienda cargar solo los mejores resultados. Para cargar solo la estructura de la base de datos se debe eliminar todo el contenido de *mysql/init*, exceptuando el fichero *init.sql*.

Antes de ejecutar la base de datos, es recomendable cambiar las contraseñas del archivo *docker-compose.yml*. Tras esta configuración, simplemente se debe ejecutar el comando: `docker compose up -d`, desde el directorio con el fichero *docker-compose.yml* y esperar un par de minutos para asegurarnos de que haya inicializado.

## ManagerSim
El *.jar* se encuentra en *Ejecutable* y el Código fuente está comprimido en *Codigo Fuente*. Puede ejecutarse en línea de comandos o directamente a través de una interfaz gráfica usando los ficheros *.bat* o *.command*. Se usa el archivo *batchParams.xml* para establecer los rangos de parámetros durante el barrido de la ejecución por lotes y *db.properties* para la configuración. Algunos de los campos en *db.properties* necesitan ser modificados para usar el menú *Batch run*, y si se han cambiado las contraseñas de la base de datos es necesario actualizarlo aquí también. Los usuarios de Linux deben copiar *batch_runner.jar* tras instalar la plataforma de Repast Simphony en eclipse, que se puede obtener desde los instaladores de Repast Simphony. La información detallada se puede encontrar en el manual de esta herramienta.

## Parser datasets
En esta carpeta se ubica el ejecutable del parser en *Ejecutable*, los datasets anonimizados que se han utilizado en *Datasets anonimizados* y el código fuente en *Codigo Fuente*. Los datasets originales no pueden compartirse debido a los términos de distribución de Twitter. 

El *parser.jar* puede ejecutarse en línea de comandos para producir los porcentajes de difusión usados para la validación. Son necesarias las opciones siguientes:
- `-f`. Toma un argumento, que es la ubicación del fichero del dataset a analizar.
- `-l` o `-t`. Se utiliza `-l` si el dataset está etiquetado y no necesita argumentos. Se utiliza `-t` si el dataset se ha extraído directamente de Twitter, en cuyo caso es necesario proporcionar como argumento el intervalo en minutos que corresponde a cada unidad de tiempo.

Por ejemplo, los siguientes comandos son válidos:
```
java -jar parser.jar -f palinAnonimized.csv -l
java -jar parser.jar -f popeCoronaTwitter.json -t 1440
```
## Script de suavizado
En esta carpeta se encuentra el proyecto en R del script de suavizado. Este proyecto se puede importar y ejecutar directamente si se dispone de la plataforma, o se puede utilizar un contenedor, con el dockerfile proporcionado. Con esta segunda opción será necesario especificar un volumen para acceder a los datasets suavizados. Se ejecuta:
```
docker build -t name dockerfile_location
```
Se especifica un nombre en `name` y la localización del dockerfile en `dockerfile_location`. Una vez finalice el proceso, se puede ejecutar como:
```
docker run --rm -v location_volume:/usr/local/src/script/output name
```
`location_volume` será la ubicación del volumen.
