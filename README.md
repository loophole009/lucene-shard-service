# lucene-shard-service
 dynamic lucene index
 
# run kafka server:
docker compose up

# run emedding server:
docker build -t async-embedding-service . 
docker run -p 6000:6000 async-embedding-service
 
# insert documents:
docker exec -i kafka-server \
  kafka-console-producer --broker-list localhost:9092 --topic shard-1-index < ./sample-docs.json

# insert emeddings:
docker exec -i kafka-server \
  kafka-console-producer --broker-list localhost:9092 --topic shard-4-index < ./sample-docs.json
  
# search documents
curl "http://localhost:9000/search?q=Cotton";</br>
curl "http://localhost:9000/search?q=women";</br>
curl "http://localhost:9000/search?q=black-sunglasses";


# reference documemtation for lucence in search system
[CS276-lucene.pdf](https://github.com/user-attachments/files/21571359/CS276-lucene.pdf)

<img width="964" height="667" alt="Screenshot 2025-08-04 at 11 32 53 AM" src="https://github.com/user-attachments/assets/870343b5-6a4f-454c-a806-530f7b5537d0" />




