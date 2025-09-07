#!/bin/bash

echo " Setting up Infrastructure (MySQL + Kafka)..."

echo " Starting Docker services..."
docker-compose up -d

echo "Waiting for services to be ready..."
sleep 30


echo " Creating Kafka topic: wallet-activity..."
docker exec web3-kafka kafka-topics --create \
    --topic wallet-activity \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --partitions 3

echo " Verifying setup..."
docker exec web3-kafka kafka-topics --list --bootstrap-server localhost:9092
docker exec web3-mysql mysql -u root -prootpassword -e "SELECT 'MySQL is ready!' as status;"

echo ""
echo " Infrastructure setup complete!"
echo ""
echo " Services running:"
echo "- MySQL: localhost:3306 (root/rootpassword)"
echo "- Kafka: localhost:9092"
echo "- Kafka UI: http://localhost:8081"
echo ""
echo "▶  Now run your Spring Boot app with:"
echo "   ./mvnw spring-boot:run"
echo ""
echo " Test Kafka manually:"
echo "Producer: docker exec -it web3-kafka kafka-console-producer --topic wallet-activity --bootstrap-server localhost:9092"
echo "Consumer: docker exec -it web3-kafka kafka-console-consumer --topic wallet-activity --bootstrap-server localhost:9092 --from-beginning"