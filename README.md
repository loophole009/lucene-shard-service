# lucene-shard-service
 dynamic lucene index
 
# run kafka server:
docker compose up
 
# insert documents:
docker exec -i kafka-server \
  kafka-console-producer --broker-list localhost:9092 --topic shard-1-index < ./sample-docs.json
  
# search documents
curl "http://localhost:9000/search?q=Cotton";
curl "http://localhost:9000/search?q=women";
