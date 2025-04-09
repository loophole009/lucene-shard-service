# lucene-shard-service
 dynamic lucene index
 
# insert documents:
docker exec -i kafka-server \
  kafka-console-producer --broker-list localhost:9092 --topic shard-1-index < ./sample-docs.json
  
# search documents
curl "http://localhost:9000/search?q=hello";
