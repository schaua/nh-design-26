Below is a Bash demo for a **private, single-AZ PostgreSQL RDS instance**. It assumes you already have a VPC, a DB subnet group containing private subnets in at least two Availability Zones, and a security group that permits inbound TCP 5432 only from your application or demo client. The master password is generated and managed by RDS in Secrets Manager; the CLI command supports creating PostgreSQL instances with a master username and subnet group. <citation src="2"></citation>

```bash
#!/usr/bin/env bash
set -euo pipefail

# Set these for your environment:
: "${AWS_REGION:?Set AWS_REGION}"
: "${DB_SUBNET_GROUP:?Set DB_SUBNET_GROUP}"
: "${DB_SECURITY_GROUP_ID:?Set DB_SECURITY_GROUP_ID}"
: "${DB_IDENTIFIER:=demo-postgres-private}"
: "${DB_NAME:=appdb}"
: "${MASTER_USERNAME:=dbadmin}"

# Create the instance. No password is passed: RDS manages it in Secrets Manager.
aws rds create-db-instance \
  --region "$AWS_REGION" \
  --db-instance-identifier "$DB_IDENTIFIER" \
  --engine postgres \
  --db-instance-class db.t4g.micro \
  --allocated-storage 20 \
  --storage-encrypted \
  --db-name "$DB_NAME" \
  --master-username "$MASTER_USERNAME" \
  --manage-master-user-password \
  --db-subnet-group-name "$DB_SUBNET_GROUP" \
  --vpc-security-group-ids "$DB_SECURITY_GROUP_ID" \
  --no-publicly-accessible \
  --backup-retention-period 7 \
  --deletion-protection

aws rds wait db-instance-available \
  --region "$AWS_REGION" \
  --db-instance-identifier "$DB_IDENTIFIER"

# Get the RDS-managed Secrets Manager ARN and database endpoint.
SECRET_ARN=$(
  aws rds describe-db-instances \
    --region "$AWS_REGION" \
    --db-instance-identifier "$DB_IDENTIFIER" \
    --query 'DBInstances[0].MasterUserSecret.SecretArn' \
    --output text
)

ENDPOINT=$(
  aws rds describe-db-instances \
    --region "$AWS_REGION" \
    --db-instance-identifier "$DB_IDENTIFIER" \
    --query 'DBInstances[0].Endpoint.Address' \
    --output text
)

printf 'Endpoint: %s\nSecret ARN: %s\n' "$ENDPOINT" "$SECRET_ARN"
```

To retrieve the credentials at runtime and connect from a host that can reach the private VPC, use `jq` and `psql`:

```bash
SECRET_JSON=$(aws secretsmanager get-secret-value \
  --region "$AWS_REGION" \
  --secret-id "$SECRET_ARN" \
  --query SecretString \
  --output text)

DB_USER=$(jq -r '.username' <<<"$SECRET_JSON")
DB_PASSWORD=$(jq -r '.password' <<<"$SECRET_JSON")
unset SECRET_JSON

PGPASSWORD="$DB_PASSWORD" psql \
  "host=$ENDPOINT port=5432 dbname=$DB_NAME user=$DB_USER sslmode=require" \
  -c 'SELECT current_user, current_database();'

unset DB_PASSWORD
```

The caller needs permission to describe the RDS instance and retrieve its secret (`secretsmanager:GetSecretValue`); if the secret uses a customer-managed KMS key, it also needs the appropriate KMS decrypt permission. Don’t print or log the secret value. This setup makes the database non-public, but its actual reachability still depends on the subnet route tables and security-group rules.

For teardown after the demo, disable deletion protection, then delete the DB instance and choose whether to retain a final snapshot.