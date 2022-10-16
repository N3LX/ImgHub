# Start the database

docker-compose up -d

# Run tests

cd ../../imghub-api
mvn clean test

# Stop the database and remove its containers

cd ../docker/TEST
docker-compose down