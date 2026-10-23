# S3 Fundamentals

Simple Storage Service, which stores static objects.  While it makes it look like a directory system, there isn't really any such thing.

The primary structure in S3 is a bucket.  A bucket name must be globally unique.

Pricing on storage is based on the availability, speed, storage size, and requests (data transfer out).

So to create a bucket to host a website, named leap-frost-s3-website

```bash
aws s3api create-bucket --bucket leap-frost-s3-website --region us-east-1
aws s3api put-bucket-website --bucket leap-frost-s3-website --website-configuration '{...}'
aws s3api put-bucket-policy --bucket leap-frost-s3-website --policy file://public-read-policy.json
```
This will fail because the default is to block public access to the bucket




## Console script

Using the AWS Management Console

- Create bucket named `leap-frost-augie-s3-demo`
- Edit  `Static Web Hosting` under Properties
  - Enable
  - Host a static website
  - Set `index document` - optionally set `error document`
  - Save Changes
  - Copy the Bucket website endpoint URL
- Select `Objects`
  - Upload `index.html` and any other documents
- Select `Permissions`  
- Edit `Block public access`
  - Unclick `Block all public access`
  - Save changes
- Edit `Bucket policy`
  - Add 
```bash
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::leap-frost-augie-s3-demo-989250983827-us-east-1-an/*"
        }
    ]
}
```
  - Save Changes
- Navigate to the bucket website endpoint

## CLI Script

```bash
#!/usr/bin/env bash
set -euo pipefail

BUCKET="leap-frost-augie-s3-demo"
REGION="us-east-1"  # Change to your preferred AWS Region

# Create the bucket. us-east-1 uses a different create-bucket request.
if [ "$REGION" = "us-east-1" ]; then
  aws s3api create-bucket \
    --bucket "$BUCKET" \
    --region "$REGION"
else
  aws s3api create-bucket \
    --bucket "$BUCKET" \
    --region "$REGION" \
    --create-bucket-configuration LocationConstraint="$REGION"
fi

# Allow the bucket policy to grant public access.
# This changes settings on this bucket only; an account-level block may still apply.
aws s3api put-public-access-block \
  --bucket "$BUCKET" \
  --public-access-block-configuration \
  'BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=false,RestrictPublicBuckets=false'

# Enable static website hosting with index.html as the home page.
aws s3api put-bucket-website \
  --bucket "$BUCKET" \
  --website-configuration \
  '{"IndexDocument":{"Suffix":"index.html"}}'

# Upload the home page.
# aws s3 cp ./index.html "s3://$BUCKET/index.html" \
#   --content-type "text/html"
# aws s3 cp ./page2.html "s3://$BUCKET/page2.html" \
#   --content-type "text/html"
# aws s3 cp ./error.html "s3://$BUCKET/error.html" \
#   --content-type "text/html"

aws s3 cp /dist/demo-ui/browser/ s3://"$BUCKET"/ --recursive

# Grant public read access to index.html only.
cat > /tmp/bucket-policy.json <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadIndexOnly",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::$BUCKET/index.html"
    }
  ]
}
EOF

aws s3api put-bucket-policy \
  --bucket "$BUCKET" \
  --policy file:///tmp/bucket-policy.json

echo "Website endpoint: http://$BUCKET.s3-website-$REGION.amazonaws.com"

```