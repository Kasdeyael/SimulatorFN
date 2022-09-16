# SimulatorFN
This project is a simulator for fake news diffusion using Repast Simphony, including a tool for the management of the validation results in a database and a parser for datasets to obtain the percentages of spread of a rumor or fake news article. In this file we give context to each folder. The full instructions for installation or use are in the manuals of the memoria file (only in Spanish).

*This file can also be read in [Spanish](README.sp.md).*

## SimulatorFN
Contained in this folder we have the fake news diffusion simulator implemented in the folder *simulator*. Simply run the *.bat* or *.command* file in accordance with the OS you have.

## Database
The database files are included in the "base de datos" folder. It contains a docker-compose file to deploy the container in Docker. The folder *mysql/init* contains only the best results. To start the database, it is recommended to load only the best results. To load only the database structure, remove all files from *mysql/init* except for *init.sql*. 

Before running the database, it's recommended to change the passwords of the *docker-compose.yml* file. After this configuration, simply run the command `docker compose up -d` from the directory with the *docker-compose.yml* file and let it load for a couple of minutes to make sure it has initialized.

## ManagerSim

The *.jar* is provided in the folder *Ejecutable* and the source code is zipped in *Codigo Fuente*. It can be run in command line or directly through a GUI by using the *.bat* or *.command* files provided. It uses the file *batchParams.xml* to establish the range of the parameters to sweep for the batch runs and the *db.properties* file for configuration purposes. Some of the fields in *db.properties* need to be modified to use the batch run menu, and if you changed the database passwords they must be updated here too. For Linux users they also must copy the *batch_runner.jar* after installing the Repast Simphony platform in eclipse, it can be obtained through the Repast Simphony Suite. Detailed information can be found in the manual for this tool.

## Parser datasets

In this folder we have the runnable parser in *Ejecutable*, the anonymized datasets used are in *Datasets anonimizados* and the source code can be located in *Codigo Fuente*. Original datasets cannot be shared due to the terms on distribution from Twitter.

The *parser.jar* can be run in the command line to produce the percentages of diffusion used for the validation. It can be run specifying the next options:

- `-f`. Takes an argument, the location of the dataset file to parse.
- `-l` or `-t`. We use `-l` if the dataset is labeled and it does not need arguments. We use `-t` if the dataset was extracted directly from Twitter, in which case it needs the argument of the interval, in minutes, corresponding with the time unit to parse.

For example, the next commands are valid:
```
java -jar parser.jar -f palinAnonimized.csv -l
java -jar parser.jar -f popeCoronaTwitter.json -t 1440
```

## Smoothing script

In the folder *Script de suavizado* we have the smoothing script project in R. This project can be imported and run directly, or it can be executed using a container, with the provided dockerfile. It is necessary to specify a volume to be able to access the smoothed datasets and the curves it produces. It can be built with: 

```
docker build -t name dockerfile_location
```

We specify a name in `name` and where the dockerfile is located in `dockerfile_location`. After it completes the process, it can be run as:

```
docker run --rm -v location_volume:/usr/local/src/script/output name
```

`location_volume` will be the location where we mount the output volume.
