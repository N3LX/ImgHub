# Build imghub-api

cd ../../imghub-api
mvn clean package spring-boot:repackage -DskipTests
docker build -t imghub-api:prod .

# Run the application
cd ../docker/PROD
docker-compose up -d