# Kafka Startup

The suggested command is show beow. This configuration creates a single-node Kafka cluster running in KRaft mode (not the legacy ZooKeeper mode), with one node.  The node serves as both broker and controller. The controller manages the Kafka cluster and the broker handles producers and consumers.    

The low replication factors (all set to 1) are appropriate for development but would need to be increased for production resilience.

```bash
docker run -d --name kafka -p PRIVATE_IP:9092:9092 \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://PRIVATE_IP:9092 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@127.0.0.1:9093 \
  -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e CLUSTER_ID=4L6g3nShT-eMCtK--X86sw \
  apache/kafka:latest
```
# Kafka Container Configuration Explained

Looking at each of the above settings

## Docker-level Options
**`docker run -d --name kafka -p PRIVATE_IP:9092:9092`**
- `-d`: Runs the container in detached mode (background)
- `--name kafka`: Names the container "kafka" for easy reference
- `-p PRIVATE_IP:9092:9092`: Maps port 9092 on the host machine to port 9092 in the container (the broker's client port)

## Kafka Broker/Controller Identity
**`-e KAFKA_NODE_ID=1`**
- Assigns a unique numeric ID to this node within the cluster. In a multi-node cluster, each broker would have a different ID (1, 2, 3, etc.)

**`-e KAFKA_PROCESS_ROLES=broker,controller`**
- Defines that this node runs both roles: broker (handles producer/consumer requests) and controller (manages cluster metadata and leader elections). This is KRaft mode (Kafka Raft), which replaces the external ZooKeeper dependency.

## Network Listeners
**`-e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093`**
- Defines internal listeners that the node binds to:
  - `PLAINTEXT://:9092`: Broker clients connect here
  - `CONTROLLER://:9093`: Other controllers/brokers connect here for coordination
- These are bound to all interfaces (`:`) within the container

**`-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://PRIVATE_IP:9092`**
- Tells clients **outside the container** how to reach this broker. Instead of `:9092`, it advertises the actual host's PRIVATE_IP so external clients can connect correctly

**`-e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT`**
- Maps listener names to security protocols:
  - Both use PLAINTEXT (no encryption) for this development setup
  - In production, you'd use SSL/TLS

## Controller Coordination (KRaft Mode)
**`-e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER`**
- Identifies which listener is used for controller-to-controller communication. The `CONTROLLER` listener at `:9093` is designated for this purpose.

**`-e KAFKA_CONTROLLER_QUORUM_VOTERS=1@127.0.0.1:9093`**
- Specifies the voting members of the KRaft quorum:
  - `1` = Node ID 1 (this node)
  - `127.0.0.1:9093` = Connect to controller listener on localhost:9093
- In a 3-node cluster, this would list all three controller endpoints (e.g., `1@host1:9093,2@host2:9093,3@host3:9093`)

**`-e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT`**
- Specifies which listener brokers use to communicate with each other. Uses the `PLAINTEXT` listener on port 9092.

## Replication & Fault Tolerance
**`-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1`**
- Number of replicas for the internal `__consumer_offsets` topic (tracks consumer group progress)
- Set to 1 for single-node development; production would be ≥ 3

**`-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1`**
- Number of replicas for the internal `__transaction_state` topic (tracks transactional state)
- Set to 1 for development

**`-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1`**
- Minimum number of in-sync replicas required for transaction logs before writes succeed
- Set to 1 for single-node; production would typically match replication factor

## Cluster Identity
**`-e CLUSTER_ID=4L6g3nShT-eMCtK--X86sw`**
- A unique identifier for the entire Kafka cluster (base64 encoded)
- Must be the same across all nodes in the same cluster
- In KRaft mode, this replaces ZooKeeper's cluster coordination

---
