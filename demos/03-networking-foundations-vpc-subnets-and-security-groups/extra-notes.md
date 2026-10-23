# Networking Foundations 

## VPC, Subnet and Security Group

### CIDR

This module first introduces the idea of CIDR notation to define a subset of IP address based upon a starting point.  The starting IP address identifies the lowest IP address available.  The number after the slash indicates how many of the bits in that starting point are fixed and how many are available for assignment.

The traditional representation of the IP address is something like `10.42.0.0/16`.  This is actually 

`0000 1010 0010 1010 0000 0000 0000 0000`

Locking the first 16 bits in place and letting the remaining 16 bits be assigned.  That would provide 65536 addresses.  This set can be divided up into subnets.  Subsets of addresses such as `10.42.1.0/24 allowing for 256 addresses.  The bigger the number after the slash the smaller subnet addresses are available (because we are freezing MORE of the original address in place).

```bash
2^0 = 0
2^1 = 2
2^2 = 4
2^3 = 8
2^4 = 16
2^5 = 32
2^6 = 64
2^7 = 128
```
The Programmer mode of the Windows calculator might be helpful in discussing this.

### VPC

Virtual Private Cloud with an emphasis on "private" by default
A VPC can span availability zones within a region.

```bash
VPC_NAME="leaf-frost-augie-vpc-demo"
CIDR_BLOCK="10.0.0.0/16"
REGION="us-east-1"

VPC_ID=$(aws ec2 create-vpc \
  --cidr-block "$CIDR_BLOCK" \
  --region "$REGION" \
  --query 'Vpc.VpcId' \
  --output text)

aws ec2 create-tags \
  --resources "$VPC_ID" \
  --tags "Key=Name,Value=$VPC_NAME" \
  --region "$REGION"
```

### Subnet

Subnets are assigned to a specific availability zone.  A subnet can be set aside for public or private behavior.  (The actual behavior is provided by other components)

All of this so far is private by default.  In accessible from the outside world.  The Internet Gateway provides the potential to open up the VPC and some of it's subnets to the internet.

Yet even the Internet Gateway doesn't automatically make the subnets visible.  They must provide a route table to points to the Internet Gateway.  As slide 9 shows this is what makes a subnet visible.  A private subnet will not map to the Internet Gateway.


If a private subnet needs to reach out to the internet it has two options.
NAT Gateway (Expensive!) opens the entire internet for outgoing requests
PrivateLink (narrower)

```bash

mapfile -t AZS < <(
  aws ec2 describe-availability-zones \
    --region "$REGION" \
    --filters Name=state,Values=available \
    --query 'AvailabilityZones[].ZoneName' \
    --output text | tr '\t' '\n' | sort | head -n 2
)

if [[ ${#AZS[@]} -ne 2 ]]; then
  echo "Could not find two available Availability Zones in $REGION" >&2
  exit 1
fi

create_subnet() {
  local name="$1" cidr="$2" az="$3"

  aws ec2 create-subnet \
    --region "$REGION" \
    --vpc-id "$VPC_ID" \
    --cidr-block "$cidr" \
    --availability-zone "$az" \
    --tag-specifications \
      "ResourceType=subnet,Tags=[{Key=Name,Value=$name}]" \
    --query 'Subnet.SubnetId' \
    --output text
}

for subnet in \
  "10.0.0.0/18:${AZS[0]}:a" \
  "10.0.64.0/18:${AZS[0]}:b" \
  "10.0.128.0/18:${AZS[1]}:a" \
  "10.0.192.0/18:${AZS[1]}:b"
do
  IFS=: read -r cidr az suffix <<< "$subnet"
  subnet_id=$(create_subnet "lf-aug-${suffix}-${az}" "$cidr" "$az")
  echo "Created $subnet_id: $cidr in $az"
done

```

### Internet Gateway and Route Tables

```bash
mapfile -t AZS < <(
  aws ec2 describe-availability-zones \
    --region "$REGION" \
    --filters Name=state,Values=available \
    --query 'AvailabilityZones[].ZoneName' \
    --output text | tr '\t' '\n' | sort | head -n 2
)

if [[ ${#AZS[@]} -ne 2 ]]; then
  echo "Could not find two available Availability Zones in $REGION" >&2
  exit 1
fi

get_subnet_id() {
  local name="$1"
  aws ec2 describe-subnets \
    --region "$REGION" \
    --filters \
      "Name=vpc-id,Values=$VPC_ID" \
      "Name=tag:Name,Values=$name" \
    --query 'Subnets[0].SubnetId' \
    --output text
}

# Create and attach the internet gateway.
IGW_ID=$(aws ec2 create-internet-gateway \
  --region "$REGION" \
  --query 'InternetGateway.InternetGatewayId' \
  --output text)

aws ec2 create-tags \
  --region "$REGION" \
  --resources "$IGW_ID" \
  --tags Key=Name,Value=lf-aug-igw-demo

aws ec2 attach-internet-gateway \
  --region "$REGION" \
  --internet-gateway-id "$IGW_ID" \
  --vpc-id "$VPC_ID"

# Create the public route table and its default route through the IGW.
PUBLIC_RTB_ID=$(aws ec2 create-route-table \
  --region "$REGION" \
  --vpc-id "$VPC_ID" \
  --query 'RouteTable.RouteTableId' \
  --output text)

aws ec2 create-tags \
  --region "$REGION" \
  --resources "$PUBLIC_RTB_ID" \
  --tags Key=Name,Value=lf-aug-public-rtb

aws ec2 create-route \
  --region "$REGION" \
  --route-table-id "$PUBLIC_RTB_ID" \
  --destination-cidr-block 0.0.0.0/0 \
  --gateway-id "$IGW_ID"

# Create a private route table. It has no internet default route.
PRIVATE_RTB_ID=$(aws ec2 create-route-table \
  --region "$REGION" \
  --vpc-id "$VPC_ID" \
  --query 'RouteTable.RouteTableId' \
  --output text)

aws ec2 create-tags \
  --region "$REGION" \
  --resources "$PRIVATE_RTB_ID" \
  --tags Key=Name,Value=lf-aug-private-rtb

for az in "${AZS[@]}"; do
  PUBLIC_SUBNET_ID=$(get_subnet_id "lf-aug-a-$az")
  PRIVATE_SUBNET_ID=$(get_subnet_id "lf-aug-b-$az")

  if [[ "$PUBLIC_SUBNET_ID" == "None" || "$PRIVATE_SUBNET_ID" == "None" ]]; then
    echo "Could not find both expected subnets in $az" >&2
    exit 1
  fi

  aws ec2 associate-route-table \
    --region "$REGION" \
    --route-table-id "$PUBLIC_RTB_ID" \
    --subnet-id "$PUBLIC_SUBNET_ID"

  aws ec2 associate-route-table \
    --region "$REGION" \
    --route-table-id "$PRIVATE_RTB_ID" \
    --subnet-id "$PRIVATE_SUBNET_ID"

  echo "$az: public=$PUBLIC_SUBNET_ID private=$PRIVATE_SUBNET_ID"
done

echo "Internet gateway: $IGW_ID"
echo "Public route table: $PUBLIC_RTB_ID"
echo "Private route table: $PRIVATE_RTB_ID"

```

### Security Groups

```bash

# Internet-facing group
PUBLIC_SG_ID=$(aws ec2 create-security-group \
  --region "$REGION" \
  --vpc-id "$VPC_ID" \
  --group-name lf-aug-public-http \
  --description "Allow HTTP from the internet" \
  --query 'GroupId' --output text)

aws ec2 create-tags --region "$REGION" --resources "$PUBLIC_SG_ID" \
  --tags Key=Name,Value=lf-aug-public-http

aws ec2 authorize-security-group-ingress \
  --region "$REGION" \
  --group-id "$PUBLIC_SG_ID" \
  --protocol tcp --port 80 \
  --cidr 0.0.0.0/0

# Backend group: HTTP allowed only from resources using the first group
BACKEND_SG_ID=$(aws ec2 create-security-group \
  --region "$REGION" \
  --vpc-id "$VPC_ID" \
  --group-name lf-aug-backend-http \
  --description "Allow HTTP only from lf-aug-public-http resources" \
  --query 'GroupId' --output text)

aws ec2 create-tags --region "$REGION" --resources "$BACKEND_SG_ID" \
  --tags Key=Name,Value=lf-aug-backend-http

aws ec2 authorize-security-group-ingress \
  --region "$REGION" \
  --group-id "$BACKEND_SG_ID" \
  --protocol tcp --port 80 \
  --source-group "$PUBLIC_SG_ID"

echo "Public HTTP SG: $PUBLIC_SG_ID"
echo "Backend HTTP SG: $BACKEND_SG_ID"

```

### Clean Up

Need to clean up in reverse order
```bash
# Find the security groups by name, then delete the backend group first
# because it references the public group.
find_sg() {
  aws ec2 describe-security-groups \
    --region "$REGION" \
    --filters "Name=vpc-id,Values=$VPC_ID" "Name=group-name,Values=$1" \
    --query 'SecurityGroups[0].GroupId' \
    --output text
}

BACKEND_SG=$(find_sg "lf-aug-backend-http")
PUBLIC_SG=$(find_sg "lf-aug-public-http")

if [[ "$BACKEND_SG" != "None" ]]; then
  aws ec2 delete-security-group --region "$REGION" --group-id "$BACKEND_SG"
fi
if [[ "$PUBLIC_SG" != "None" ]]; then
  aws ec2 delete-security-group --region "$REGION" --group-id "$PUBLIC_SG"
fi

# Delete the custom route tables after disassociating their subnets.
for name in lf-aug-public-rtb lf-aug-private-rtb; do
  RTB_ID=$(aws ec2 describe-route-tables \
    --region "$REGION" \
    --filters "Name=vpc-id,Values=$VPC_ID" "Name=tag:Name,Values=$name" \
    --query 'RouteTables[0].RouteTableId' \
    --output text)

  if [[ "$RTB_ID" != "None" ]]; then
    mapfile -t ASSOC_IDS < <(
      aws ec2 describe-route-tables \
        --region "$REGION" \
        --route-table-ids "$RTB_ID" \
        --query 'RouteTables[0].Associations[?Main==`false`].RouteTableAssociationId' \
        --output text | tr '\t' '\n'
    )
    for assoc_id in "${ASSOC_IDS[@]}"; do
      [[ -n "$assoc_id" && "$assoc_id" != "None" ]] &&
        aws ec2 disassociate-route-table \
          --region "$REGION" \
          --association-id "$assoc_id"
    done

    aws ec2 delete-route-table --region "$REGION" --route-table-id "$RTB_ID"
  fi
done

# Delete the subnets created by the earlier script, using their Name tags.
mapfile -t SUBNET_IDS < <(
  aws ec2 describe-subnets \
    --region "$REGION" \
    --filters "Name=vpc-id,Values=$VPC_ID" \
    --query "Subnets[?starts_with(Tags[?Key=='Name']|[0].Value, 'lf-aug-a-') || starts_with(Tags[?Key=='Name']|[0].Value, 'lf-aug-b-')].SubnetId" \
    --output text | tr '\t' '\n'
)
for subnet_id in "${SUBNET_IDS[@]}"; do
  [[ -n "$subnet_id" && "$subnet_id" != "None" ]] &&
    aws ec2 delete-subnet --region "$REGION" --subnet-id "$subnet_id"
done

# Detach and delete the internet gateway created earlier.
IGW_ID=$(aws ec2 describe-internet-gateways \
  --region "$REGION" \
  --filters "Name=attachment.vpc-id,Values=$VPC_ID" "Name=tag:Name,Values=lf-aug-igw-demo" \
  --query 'InternetGateways[0].InternetGatewayId' \
  --output text)

if [[ "$IGW_ID" != "None" ]]; then
  aws ec2 detach-internet-gateway \
    --region "$REGION" \
    --internet-gateway-id "$IGW_ID" \
    --vpc-id "$VPC_ID"
  aws ec2 delete-internet-gateway --region "$REGION" --internet-gateway-id "$IGW_ID"
fi

aws ec2 delete-vpc --region "$REGION" --vpc-id "$VPC_ID"
echo "Deleted VPC $VPC_ID"

```
