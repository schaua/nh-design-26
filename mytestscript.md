# The commands needed for the labs

## Lab 01

- none

## Lab 02

```bash
aws ec2 describe-availability-zones --region us-east-1 --query 'AvailabilityZones[].{Name:ZoneName,State:State}' --output table
aws sts get-caller-identity
aws configure list 
aws iam list-attached-user-policies --user-name AugieSchau
aws s3 ls
aws ec2 describe-vpcs --query 'Vpcs[].{Id:VpcId,Cidr:CidrBlock}' --output table
aws ecs list-clusters
```

## Lab 03
```bash
aws ec2 describe-vpcs
aws ec2 describe-subnets --filters Name=vpc-id,Values=vpc-05c02a848ddc4b989 --query 'Subnets[].{SubnetId:SubnetId}' --output text
aws ec2 describe-route-tables --filters Name=association.subnet-id,Values=[subnet-0708dc1006498df3d,subnet-04b14cf9c655b3370,subnet-07dfbdaa69a9e063d,subnet-01955191a84fbcd06,subnet-07239cebcc76a318d,subnet-0923b3075ea152079]
```

Part 3 looks like it would be better done in AWS Console.  If done with CLI it could look like this:    

```bash
VPC_ID=$(aws ec2 create-vpc \
  --cidr-block 172.0.0.0/16 \
  --query 'Vpc.VpcId' \
  --output text)

read -r AZ1 AZ2 < <(
  aws ec2 describe-availability-zones \
    --query 'AvailabilityZones[?State==`available`].ZoneName' \
    --output text | tr '\t' '\n' | head -n 2
)

aws ec2 create-subnet --vpc-id "$VPC_ID" --availability-zone "$AZ1" --cidr-block 172.0.0.0/24
aws ec2 create-subnet --vpc-id "$VPC_ID" --availability-zone "$AZ1" --cidr-block 172.0.1.0/24
aws ec2 create-subnet --vpc-id "$VPC_ID" --availability-zone "$AZ2" --cidr-block 172.0.2.0/24
aws ec2 create-subnet --vpc-id "$VPC_ID" --availability-zone "$AZ2" --cidr-block 172.0.3.0/24
```

Then create an Internet Gateway and attach it to this VPC   

```bash
IGW_ID=$(aws ec2 create-internet-gateway \
  --query 'InternetGateway.InternetGatewayId' \
  --output text)

aws ec2 attach-internet-gateway \
  --internet-gateway-id "$IGW_ID" \
  --vpc-id "$VPC_ID"
```

Finally set up route table and associate iwith two of the subnets    

```bash
# Look up one subnet in each AZ by its CIDR
SUBNET_AZ1=$(aws ec2 describe-subnets \
  --filters "Name=vpc-id,Values=$VPC_ID" "Name=cidr-block,Values=172.0.0.0/24" \
  --query 'Subnets[0].SubnetId' --output text)

SUBNET_AZ2=$(aws ec2 describe-subnets \
  --filters "Name=vpc-id,Values=$VPC_ID" "Name=cidr-block,Values=172.0.2.0/24" \
  --query 'Subnets[0].SubnetId' --output text)

RTB_ID=$(aws ec2 create-route-table \
  --vpc-id "$VPC_ID" \
  --query 'RouteTable.RouteTableId' \
  --output text)

aws ec2 create-route \
  --route-table-id "$RTB_ID" \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id "$IGW_ID"

aws ec2 associate-route-table --route-table-id "$RTB_ID" --subnet-id "$SUBNET_AZ1"
aws ec2 associate-route-table --route-table-id "$RTB_ID" --subnet-id "$SUBNET_AZ2"

```

Set up security groups    

```bash
SG_HTTP=$(aws ec2 create-security-group \
  --group-name web-http \
  --description "Allow HTTP from anywhere" \
  --vpc-id "$VPC_ID" \
  --query 'GroupId' \
  --output text)

SG_8888=$(aws ec2 create-security-group \
  --group-name app-8888 \
  --description "Allow port 8888 from the HTTP security group" \
  --vpc-id "$VPC_ID" \
  --query 'GroupId' \
  --output text)

aws ec2 authorize-security-group-ingress \
  --group-id "$SG_HTTP" \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0

aws ec2 authorize-security-group-ingress \
  --group-id "$SG_8888" \
  --protocol tcp \
  --port 8888 \
  --source-group "$SG_HTTP"

```

## Lab 04

