# Instructions from AWS documentation

## Create an S3 Bucket

```bash
export BUCKET="leap-frost-augie-s3-demo-bucket"
aws s3 mb s3://"$BUCKET" --region us-east-1
```

## Upload the content to the bucket

Upload the files relative to the current directory

- Navigate to the Demo-UI directory
- Using ng build to create the distributable which should be in /dist/demo-ui/browser


```bash
aws s3 cp ./dist/demo-ui/browser/ s3://"$BUCKET"/ --recursive
#or
aws s3 sync ./dist/demo-ui/browser s3://<bucketname>
```

## Create an Origin Access Control (OAC)
```bash
export OACNAME="oac-for-leap-frost-s3"
aws cloudfront create-origin-access-control --origin-access-control-config Name='"$OACNAME"',SigningProtocol=sigv4,SigningBehavior=always,OriginAccessControlOriginType=s3
```
Note the OAC_ID that is generated

## Create a CloudFront Distribution

 Create a dist-config.json file that sets up the rule    

```json
{
    "CallerReference": "leap-frost-augie-example-$(date +%s)",
    "Origins": {
        "Quantity": 1,
        "Items": [
            {
                "Id": 'S3-leap-frost-augie-s3-demo-bucket',
                "DomainName": "leap-frost-augie-s3-demo.s3.amazonaws.com",
                "S3OriginConfig": {
                    "OriginAccessIdentity": ""
                },
                "OriginAccessControlId": "$OAC_ID"
            }
        ]
    },
    "DefaultCacheBehavior": {
        "TargetOriginId": 'S3-leap-frost-augie-s3-demo-bucket',
        "ViewerProtocolPolicy": "redirect-to-https",
        "AllowedMethods": {
            "Quantity": 2,
            "Items": ["GET", "HEAD"],
            "CachedMethods": {
                "Quantity": 2,
                "Items": ["GET", "HEAD"]
            }
        },
        "DefaultTTL": 86400,
        "MinTTL": 0,
        "MaxTTL": 31536000,
        "Compress": true,
        "ForwardedValues": {
            "QueryString": false,
            "Cookies": {
                "Forward": "none"
            }
        }
    },
    "Comment": "Leap CloudFront distribution for S3 bucket",
    "Enabled": true
}
```
Apply this configuration when creating the OAC

```bash
aws cloudfront create-distribution --distribution-config file://dist-config.json
```
Save the output distribution ID and domain name as environment variables.

```bash
export DISTRIBUTION_ID="ABED234123DFFGXMPL"
export DOMAIN_NAME="augieschau-cloudfron.net"

```

## Update the S3 bucket policy

The bucket-policy.json should look something like this

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowCloudFrontServicePrincipal",
            "Effect": "Allow",
            "Principal": {
                "Service": "cloudfront.amazonaws.com"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::$BUCKET/*",
            "Condition": {
                "StringEquals": {
                    "AWS:SourceArn": "arn:aws:cloudfront::$ACCOUNT_ID:distribution/$DISTRIBUTION_ID"
                }
            }
        }
    ]
}
```

Apply this polisy to the bucket

```bash
aws s3api put-bucket-policy --bucket "$BUCKET" --policy file://bucket-policy3.json
```

## Confirm distribution deployment

```bash
aws cloudfront get-distribution --id $DISTRIBUTION_ID --query 'Distribution.Status'
```

## Access your content through CloudFront

```bash
https://$DOMAIN_NAME/index.html
```

## Clean Up

Clean up

If you created your distribution and S3 bucket only as a learning exercise, delete them so that you no longer accrue charges. Disable and delete the distribution first.

To disable and delete a standard distribution (AWS CLI)
First, disable the distribution.


# Get the current configuration and ETag

```bash
ETAG=$(aws cloudfront get-distribution-config --id $DISTRIBUTION_ID --query 'ETag' --output text)
```

# Create a modified configuration with Enabled=false
```bash
aws cloudfront get-distribution-config --id $DISTRIBUTION_ID | \
jq '.DistributionConfig.Enabled = false' > temp_disabled_config.json
```

# Update the distribution to disable it
```bash
aws cloudfront update-distribution \
    --id $DISTRIBUTION_ID \
    --distribution-config file://<(jq '.DistributionConfig' temp_disabled_config.json) \
    --if-match $ETAG
Wait for the distribution to be disabled.


aws cloudfront wait distribution-deployed --id $DISTRIBUTION_ID
```
Delete the distribution.


# Get the current ETag
```bash
ETAG=$(aws cloudfront get-distribution-config --id $DISTRIBUTION_ID --query 'ETag' --output text)
```

# Delete the distribution
```bash
aws cloudfront delete-distribution --id $DISTRIBUTION_ID --if-match $ETAG
```
To delete an S3 bucket (AWS CLI)
Delete the S3 bucket and its contents. Replace the example bucket name with your own.


# Delete the bucket contents
```bash
aws s3 rm s3://amzn-s3-demo-bucket --recursive
```
# Delete the bucket
```bash
aws s3 rb s3://amzn-s3-demo-bucket
```

To clean up the local files created for this tutorial, run the following commands:


Optionally, you can delete the OAC that you created for this tutorial.

# Get the OAC ETag
```bash
OAC_ETAG=$(aws cloudfront get-origin-access-control --id $OAC_ID --query 'ETag' --output text)
```

# Delete the OAC
```bash
aws cloudfront delete-origin-access-control --id $OAC_ID --if-match $OAC_ETAG
```