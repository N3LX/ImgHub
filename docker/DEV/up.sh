# Build imghub-api

cd ../../imghub-api
mvn clean package spring-boot:repackage -DskipTests
docker build -t imghub-api:dev .

# Run the application

cd ../docker/DEV
docker-compose up -d