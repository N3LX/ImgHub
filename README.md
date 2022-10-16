# ImgHub (WIP)

ImgHub is a (work in progress) image hosting web service inspired by sites like Imgur.

</br>

## **Project is not yet complete.**

</br>

## What works

- Image uploading and downloading via API

## Software stack

The whole application is divided into 2 Docker containers:

- Database - Postgres database that is inaccessible outside of the container network
- ImgHub-Api - REST API written in Java with help of Spring Boot and Hibernate

## Requirements

After cloning the repository make sure that you have following applications installed:

- Docker
- docker-compose
- JDK 17 or higher
- Apache Maven

## How to run/stop

Application comes with 3 distinct environments:

- PROD - Production environment that is meant to be deployed as a finished product
- DEV - PROD-like environment with only difference being the database that is being used
- TEST - In this environment you can either run tests or run only backend services (postgres, pgadmin) so that you can run your application within IDE for easier development


## How to run/stop

Application comes with 3 environments, each consisting 3 .sh files in their respective directory in **docker** directory:

- up.sh - Builds docker containers and starts them up
- stop.sh - Stops docker containers but does not delete their data
- down.sh - Stops docker containers, removes them and their data

They can be ran like this from one of the 3 directories (PROD, DEV, TEST)

```
sh up.sh
```

TEST environment also contains tests.sh that, as the name suggests, takes care of running any tests.

## API endpoints

### API exposes the access to database using following URLs (on port 80):

| Endpoint | Description | Method | Parameter
| :--- | :--- | :--- | :--- 
| `/images` | Uploads an image | `POST` | N/A
| `/images/{id}` | Accesses an image | `GET` | ID number matching the image ID in the database


<br/>

## Postman

You will find **ImgHub-API.postman_collection.json** included in this repo, you should be able to import it into your application.

Change the input files for uploading and test the API by yourself.