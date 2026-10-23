# Suggested approach

Below is a practical deployment sequence using **Amazon ECS on Fargate**, **Amazon ECR**, **RDS for PostgreSQL**, and **CloudFront with a private S3 origin**. I’m assuming the Angular app calls the Spring Boot API over HTTPS, and the API and database are in the same VPC.

```text
Browser
  ├── Angular files → CloudFront → private S3 bucket
  └── API requests → HTTPS → Application Load Balancer → ECS Fargate
                                                        └── RDS PostgreSQL
```

### 1. Plan the network and names

Choose an AWS Region and decide on your domain names, for example `app.example.com` and `api.example.com`. Use one VPC with subnets in at least two Availability Zones:

- **Public subnets:** Application Load Balancer
- **Private application subnets:** ECS tasks
- **Private database subnets:** RDS

Configure routing and outbound access for ECS tasks to pull images and send logs. Common options include a NAT Gateway or the relevant VPC endpoints. Keep the database private; it should not be publicly accessible.

Use separate security groups with these inbound rules:

| Security group | Allow inbound |
|---|---|
| ALB | HTTPS (443) from intended client networks, or the internet if the API is public |
| ECS tasks | Spring Boot container port (for example, 8080) **only from the ALB security group** |
| RDS | PostgreSQL (5432) **only from the ECS task security group** |

### 2. Create the PostgreSQL database

1. Create an RDS DB subnet group using the private database subnets in multiple Availability Zones.
2. Create an RDS for PostgreSQL instance. Set **Public access** to **No**, enable encryption, and configure backups and a suitable retention period.
3. Store the database credentials in AWS Secrets Manager rather than in source code or a Docker image.
4. Note the database endpoint, port, and database name. Configure the RDS security group to accept port 5432 only from the ECS task security group.

### 3. Prepare the Spring Boot application

1. Configure Spring Boot to read its database URL, username, and password from environment variables or a mounted/runtime secret. Do not bake credentials into the image.
2. Configure the application to listen on the port you’ll use in ECS, such as `8080`.
3. Add a health-check endpoint, for example `/actuator/health`, and ensure it returns a successful status when the app is ready to receive traffic.
4. Build and test the application locally, including its database connection.

A minimal Dockerfile for a prebuilt Spring Boot JAR could look like this:

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Adapt the Java version and JAR path to your project.

### 4. Build and push the image to ECR

1. Create an ECR repository, for example `spring-api`.
2. Authenticate Docker, build the image, tag it, and push it. Replace the placeholders with your account ID and Region:

```bash
aws ecr get-login-password --region REGION \
  | docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com

docker build -t spring-api .
docker tag spring-api:latest ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/spring-api:latest
docker push ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/spring-api:latest
```

For repeatable releases, tag images with a version or commit identifier instead of relying only on `latest`.

### 5. Create the ECS service and load balancer

1. Create an ECS cluster using the Fargate launch type.
2. Create an **ECS task execution role** that lets ECS pull from ECR, write logs, and retrieve the specific Secrets Manager secret needed at startup. Keep this distinct from the **task role**, which grants permissions to the application itself if it needs AWS API access.
3. Create a CloudWatch log group for the application logs.
4. Register a task definition with:
   - Fargate compatibility and `awsvpc` networking
   - The ECR image URI and container port, such as `8080`
   - CPU and memory allocations
   - CloudWatch logging
   - Database connection settings and secret references
5. Create an internet-facing Application Load Balancer in the public subnets, with a target group for the ECS task port. Set its health-check path to your health endpoint.
6. Add an HTTPS listener using an ACM certificate for `api.example.com`; redirect HTTP to HTTPS if you also configure port 80.
7. Create an ECS service in the private application subnets and attach it to the target group. Use the ECS task security group described above. Start with a small desired task count, then scale based on load and availability needs.

The ALB should be the only route to the ECS service from the public internet. ECS tasks should not need public IP addresses. If they need outbound access, provide it through NAT or appropriate VPC endpoints.

### 6. Build and deploy the Angular UI

1. Set Angular’s API base URL to `https://api.example.com` (or your chosen API hostname), then build the production bundle:

```bash
ng build --configuration production
```

2. Create an S3 bucket for the build output and **block all public access**.
3. Create a CloudFront distribution with the S3 bucket’s **REST origin** and an **Origin Access Control (OAC)**. Configure the bucket policy to allow `s3:GetObject` only to that CloudFront distribution. OAC lets CloudFront read from a private bucket without making the bucket public. <citation src="7"></citation>
4. Set the default root object to `index.html`. Configure SPA deep-link handling so Angular routes work when refreshed; a CloudFront Function rewrite is one option. If you use custom error responses for this, make sure missing JavaScript or CSS assets don’t silently return `index.html`.
5. Upload the contents of Angular’s generated browser output directory to the bucket. The exact output path can vary by Angular version and build configuration.
6. Give `index.html` a short cache lifetime and hashed assets a long cache lifetime. After deploying a new build, invalidate CloudFront paths if needed—for example, `/index.html`.
7. For a custom UI domain, attach an ACM certificate issued in **us-east-1** to CloudFront and point your DNS record at the distribution.

### 7. Connect the UI to the API

- Configure Spring Boot CORS to allow the exact UI origin, such as `https://app.example.com`; avoid a wildcard origin for a credentialed application.
- Ensure the API uses HTTPS and that Angular calls the correct API hostname.
- Test browser preflight requests if the UI sends authorization headers or uses non-simple HTTP methods.
- Keep secrets and privileged AWS credentials out of Angular: browser-delivered code is visible to users.

### 8. Verify and operate the deployment

1. Confirm ECS tasks start and appear healthy in the ALB target group.
2. Call the API health endpoint through the ALB’s HTTPS hostname.
3. Load the CloudFront URL, test Angular routes directly and after refresh, and verify API calls in the browser.
4. Check CloudWatch logs for startup errors, database connection failures, and health-check failures.
5. Set alarms for important signals such as ECS task restarts, ALB 5xx responses, and RDS storage or CPU pressure.
6. For each release, push a versioned image to ECR, update the ECS task definition, deploy the new service revision, then publish the Angular build and invalidate the relevant CloudFront cache paths.

For a production rollout, define these resources in infrastructure as code (such as AWS CDK, CloudFormation, or Terraform) so the network, permissions, and service configuration can be reviewed and recreated consistently. The key traffic boundaries are ALB → ECS on the app port, and ECS → RDS on PostgreSQL’s port 5432. <citation src="4"></citation>

# More Detailed Scripting

Yes. Below is an AWS CLI deployment **starter template** for the full stack. It creates the services, but it isn’t a single copy-and-run script: you must supply your account-specific values, subnet IDs, certificate ARN, and a few JSON configuration files.

This example assumes the VPC and its public, private-app, and private-database subnets already exist. For production, use infrastructure as code to manage dependencies and updates; CLI-created resources can otherwise be hard to recreate consistently.

Commands use Bash and AWS CLI v2. Set your values first:

```bash
export AWS_REGION=us-west-2
export ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export VPC_ID=vpc-REPLACE
export PUBLIC_SUBNET_A=subnet-REPLACE
export PUBLIC_SUBNET_B=subnet-REPLACE
export APP_SUBNET_A=subnet-REPLACE
export APP_SUBNET_B=subnet-REPLACE
export DB_SUBNET_A=subnet-REPLACE
export DB_SUBNET_B=subnet-REPLACE

export DB_PASSWORD='REPLACE_WITH_A_STRONG_PASSWORD'
export BUCKET_NAME=replace-with-globally-unique-ui-bucket
export API_HOST=api.example.com
export UI_HOST=app.example.com
export ACM_CERT_ARN=arn:aws:acm:us-west-2:ACCOUNT_ID:certificate/REPLACE
```

Use a real password and protect your shell history and terminal logs. For production, prefer generating/storing the database password in Secrets Manager instead of putting it directly in a command.

### 1. Create security groups

The rules below allow HTTPS to the ALB, ALB-to-app traffic on port 8080, and app-to-database traffic on port 5432. Replace `0.0.0.0/0` if the API should only be reachable from restricted client networks.

```bash
aws ec2 create-security-group \
  --region "$AWS_REGION" --vpc-id "$VPC_ID" \
  --group-name app-alb-sg --description "Public HTTPS ALB"
export ALB_SG=$(aws ec2 describe-security-groups --region "$AWS_REGION" \
  --filters Name=group-name,Values=app-alb-sg Name=vpc-id,Values="$VPC_ID" \
  --query 'SecurityGroups[0].GroupId' --output text)

aws ec2 authorize-security-group-ingress --region "$AWS_REGION" \
  --group-id "$ALB_SG" --protocol tcp --port 443 --cidr 0.0.0.0/0

aws ec2 create-security-group \
  --region "$AWS_REGION" --vpc-id "$VPC_ID" \
  --group-name app-task-sg --description "Spring Boot ECS tasks"
export TASK_SG=$(aws ec2 describe-security-groups --region "$AWS_REGION" \
  --filters Name=group-name,Values=app-task-sg Name=vpc-id,Values="$VPC_ID" \
  --query 'SecurityGroups[0].GroupId' --output text)

aws ec2 authorize-security-group-ingress --region "$AWS_REGION" \
  --group-id "$TASK_SG" --protocol tcp --port 8080 --source-group "$ALB_SG"

aws ec2 create-security-group \
  --region "$AWS_REGION" --vpc-id "$VPC_ID" \
  --group-name app-db-sg --description "PostgreSQL from ECS only"
export DB_SG=$(aws ec2 describe-security-groups --region "$AWS_REGION" \
  --filters Name=group-name,Values=app-db-sg Name=vpc-id,Values="$VPC_ID" \
  --query 'SecurityGroups[0].GroupId' --output text)

aws ec2 authorize-security-group-ingress --region "$AWS_REGION" \
  --group-id "$DB_SG" --protocol tcp --port 5432 --source-group "$TASK_SG"
```

### 2. Create the RDS PostgreSQL database

Create a subnet group using private database subnets. The subnets should be in different Availability Zones.

```bash
aws rds create-db-subnet-group --region "$AWS_REGION" \
  --db-subnet-group-name app-db-subnets \
  --db-subnet-group-description "Private subnets for application database" \
  --subnet-ids "$DB_SUBNET_A" "$DB_SUBNET_B"
```

Create the database. Adjust the instance class and storage to suit your workload and budget.

```bash
aws rds create-db-instance --region "$AWS_REGION" \
  --db-instance-identifier app-postgres \
  --engine postgres --engine-version 16 \
  --db-instance-class db.t4g.micro \
  --allocated-storage 20 --storage-type gp3 \
  --db-name appdb --master-username appadmin \
  --master-user-password "$DB_PASSWORD" \
  --db-subnet-group-name app-db-subnets \
  --vpc-security-group-ids "$DB_SG" \
  --no-publicly-accessible \
  --storage-encrypted \
  --backup-retention-period 7
```

Wait until it’s ready, then retrieve its endpoint:

```bash
aws rds wait db-instance-available --region "$AWS_REGION" \
  --db-instance-identifier app-postgres

export DB_HOST=$(aws rds describe-db-instances --region "$AWS_REGION" \
  --db-instance-identifier app-postgres \
  --query 'DBInstances[0].Endpoint.Address' --output text)
```

For production, use Secrets Manager for credentials and consider enabling deletion protection and Multi-AZ. Avoid putting passwords in shell history.

### 3. Create an ECR repository and push the Spring Boot image

Build the Docker image locally first. Then create the repository and push it:

```bash
aws ecr create-repository --region "$AWS_REGION" \
  --repository-name spring-api \
  --image-scanning-configuration scanOnPush=true

export IMAGE_URI="$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/spring-api:1"

aws ecr get-login-password --region "$AWS_REGION" |
  docker login --username AWS --password-stdin \
  "$ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"

docker build -t spring-api:1 .
docker tag spring-api:1 "$IMAGE_URI"
docker push "$IMAGE_URI"
```

Use a distinct immutable version tag for each release rather than reusing `1`.

### 4. Create ECS roles and logging

Create the ECS task execution role. This role lets ECS pull from ECR and write logs. The application’s database password should be provided through Secrets Manager; the execution role then needs permission to read that specific secret.

```bash
cat > ecs-trust-policy.json <<'EOF'
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {"Service": "ecs-tasks.amazonaws.com"},
    "Action": "sts:AssumeRole"
  }]
}
EOF

aws iam create-role \
  --role-name ecsTaskExecutionRole \
  --assume-role-policy-document file://ecs-trust-policy.json

aws iam attach-role-policy \
  --role-name ecsTaskExecutionRole \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy
```

Create a log group:

```bash
aws logs create-log-group --region "$AWS_REGION" \
  --log-group-name /ecs/spring-api
```

If using Secrets Manager, create the secret and grant the execution role access to its ARN. For example:

```bash
aws secretsmanager create-secret --region "$AWS_REGION" \
  --name app/postgres \
  --secret-string "{\"username\":\"appadmin\",\"password\":\"$DB_PASSWORD\"}"
```

Restrict the role’s `secretsmanager:GetSecretValue` permission to this secret’s ARN. If the secret uses a customer-managed KMS key, the role also needs permission to decrypt with that key.

### 5. Create the ALB and target group

Create the ALB in public subnets and a target group for Fargate’s `awsvpc` networking. For Fargate, the target type must be `ip`.

```bash
aws elbv2 create-load-balancer --region "$AWS_REGION" \
  --name app-alb --type application --scheme internet-facing \
  --subnets "$PUBLIC_SUBNET_A" "$PUBLIC_SUBNET_B" \
  --security-groups "$ALB_SG"

export ALB_ARN=$(aws elbv2 describe-load-balancers --region "$AWS_REGION" \
  --names app-alb --query 'LoadBalancers[0].LoadBalancerArn' --output text)

aws elbv2 create-target-group --region "$AWS_REGION" \
  --name spring-api-tg --protocol HTTP --port 8080 \
  --target-type ip --vpc-id "$VPC_ID" \
  --health-check-path /actuator/health

export TG_ARN=$(aws elbv2 describe-target-groups --region "$AWS_REGION" \
  --names spring-api-tg --query 'TargetGroups[0].TargetGroupArn' --output text)
```

Create an HTTPS listener. The certificate must be valid for the API hostname and available in the ALB’s Region.

```bash
aws elbv2 create-listener --region "$AWS_REGION" \
  --load-balancer-arn "$ALB_ARN" \
  --protocol HTTPS --port 443 \
  --certificates CertificateArn="$ACM_CERT_ARN" \
  --default-actions Type=forward,TargetGroupArn="$TG_ARN"
```

### 6. Register the ECS task and create the service

Create an ECS cluster:

```bash
aws ecs create-cluster --region "$AWS_REGION" \
  --cluster-name app-cluster
```

Create a task definition file. Replace the execution role ARN and secret ARN. This example uses the RDS endpoint from the earlier command.

```bash
export EXEC_ROLE_ARN=$(aws iam get-role --role-name ecsTaskExecutionRole \
  --query 'Role.Arn' --output text)
export SECRET_ARN=$(aws secretsmanager describe-secret --region "$AWS_REGION" \
  --secret-id app/postgres --query ARN --output text)

cat > task-definition.json <<EOF
{
  "family": "spring-api",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "$EXEC_ROLE_ARN",
  "containerDefinitions": [{
    "name": "spring-api",
    "image": "$IMAGE_URI",
    "essential": true,
    "portMappings": [{"containerPort": 8080, "protocol": "tcp"}],
    "environment": [
      {"name": "SPRING_DATASOURCE_URL", "value": "jdbc:postgresql://$DB_HOST:5432/appdb"},
      {"name": "SPRING_DATASOURCE_DRIVER_CLASS_NAME", "value": "org.postgresql.Driver"}
    ],
    "secrets": [
      {"name": "SPRING_DATASOURCE_USERNAME", "valueFrom": "$SECRET_ARN:username::"},
      {"name": "SPRING_DATASOURCE_PASSWORD", "valueFrom": "$SECRET_ARN:password::"}
    ],
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "/ecs/spring-api",
        "awslogs-region": "$AWS_REGION",
        "awslogs-stream-prefix": "app"
      }
    }
  }]
}
EOF

aws ecs register-task-definition --region "$AWS_REGION" \
  --cli-input-json file://task-definition.json
```

Create the service in private application subnets. Set `assignPublicIp` to `DISABLED`; ensure the private subnets have a way to reach ECR and CloudWatch Logs, such as NAT or appropriate VPC endpoints.

```bash
aws ecs create-service --region "$AWS_REGION" \
  --cluster app-cluster --service-name spring-api \
  --task-definition spring-api --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration \
  "awsvpcConfiguration={subnets=[$APP_SUBNET_A,$APP_SUBNET_B],securityGroups=[$TASK_SG],assignPublicIp=DISABLED}" \
  --load-balancers \
  "targetGroupArn=$TG_ARN,containerName=spring-api,containerPort=8080"
```

### 7. Create the private S3 bucket and CloudFront distribution

Create the bucket and block public access. The bucket should be accessed only through CloudFront.

```bash
aws s3api create-bucket --region "$AWS_REGION" \
  --bucket "$BUCKET_NAME" \
  --create-bucket-configuration LocationConstraint="$AWS_REGION"

aws s3api put-public-access-block --region "$AWS_REGION" \
  --bucket "$BUCKET_NAME" \
  --public-access-block-configuration \
  BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true
```

For CloudFront, prepare a distribution JSON file that specifies the S3 REST endpoint as the origin and uses an Origin Access Control (OAC). The OAC is created like this:

```bash
aws cloudfront create-origin-access-control \
  --origin-access-control-config \
  "Name=$BUCKET_NAME-oac,Description=Private S3 origin,SigningProtocol=sigv4,SigningBehavior=always,OriginAccessControlOriginType=s3"
```

The distribution configuration must reference the returned OAC ID. After creating the distribution, add a bucket policy granting `s3:GetObject` only to that distribution’s CloudFront service principal, conditioned on its distribution ARN. Do not make the bucket public.

Upload the Angular production build after the distribution is ready:

```bash
aws s3 sync ./dist/your-angular-app/browser/ "s3://$BUCKET_NAME/" \
  --delete --cache-control "public,max-age=31536000,immutable"

# Give the entrypoint a short cache lifetime so clients pick up new releases.
aws s3 cp ./dist/your-angular-app/browser/index.html \
  "s3://$BUCKET_NAME/index.html" \
  --cache-control "public,max-age=60,must-revalidate"
```

The exact Angular output folder varies by build configuration. Configure the distribution’s default root object as `index.html` and add SPA route handling, such as a CloudFront Function rewrite. For a custom UI domain, the CloudFront certificate must be in `us-east-1`, even if the rest of the stack is in another Region.

### 8. Verify the deployment

```bash
aws ecs describe-services --region "$AWS_REGION" \
  --cluster app-cluster --services spring-api

aws elbv2 describe-target-health --region "$AWS_REGION" \
  --target-group-arn "$TG_ARN"

aws rds describe-db-instances --region "$AWS_REGION" \
  --db-instance-identifier app-postgres \
  --query 'DBInstances[0].Endpoint.Address' --output text

aws logs tail /ecs/spring-api --region "$AWS_REGION" --follow
```

Important omissions to fill in before production: a CloudFront distribution configuration and its matching bucket policy, DNS records, Angular SPA routing behavior, IAM permissions for the secret, alerting, and deletion protection/backup settings. CloudFront distribution configuration is fairly verbose, so validate it with a test distribution before relying on a hand-written JSON file. <citation src="2,3"></citation>