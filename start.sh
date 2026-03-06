cd backend/build/libs
# Run the JAR with Railway's dynamic port
java -Dserver.port=$PORT -jar myapp-0.0.1-SNAPSHOT.jar