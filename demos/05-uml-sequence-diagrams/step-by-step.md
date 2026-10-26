# AWS S3 + CloudFront + OAC (Secure Static Website)

This project demonstrates how to deploy a **secure static website** using:

- Amazon S3 (private bucket)
- CloudFront (CDN)
- Origin Access Control (OAC)
- AWS CLI (100% CLI-based setup)

---

## 📌 Key Points

Deploying a website, but on implementing real security best practices:

- Secure an S3 bucket by disabling all public access
- Use CloudFront with Origin Access Control (OAC)
- Connect services using least privilege access
- Deploy a static website using a CDN
- Work entirely with AWS CLI instead of the console

# 🚀 Project Overview

This projectuses a secure architecture where:

- The S3 bucket is **completely private**
- Only CloudFront can access the content using **OAC**
- The website is delivered globally through a CDN
- All resources are created using the **AWS CLI**
  
All resources were created using the AWS CLI to simulate real-world scenarios and avoid manual configuration errors.

---

## 🔐 Security Design

This architecture follows best practices:

- The S3 bucket is completely private (no public access)
- Only CloudFront can access the bucket using OAC
- Access is restricted using a bucket policy with SourceArn
- IAM permissions follow least privilege

This prevents direct access to S3 and protects the content.

### 🛠️ Tech Stack & Architecture

| Component | Service / Tool |
| :--- | :--- |
| **Cloud Provider** | AWS (Region: us-east-1) |
| **Storage** | Amazon S3 (Private Bucket) |
| **Content Delivery** | Amazon CloudFront (CDN) |
| **Security & Access** | Origin Access Control (OAC) & IAM |
| **Infrastructure** | AWS CLI (100% Terminal-based) |
| **Deployment Flow** | CLI S3 Sync & Distribution Config |


# 🧱 Architecture

User (Browser) → CloudFront (CDN) → S3 Bucket (Private)

---

## 🧱 Architecture Details

Flow:

1. User sends HTTPS request
2. CloudFront receives the request
3. CloudFront uses OAC to access S3
4. S3 returns the content securely
5. CloudFront delivers content to the user

# ⚙️ Step-by-Step Implementation

## 1.We create a bucket with the following command:

```bash
aws s3 mb <bucket_name> --region us-east-1
# or
aws s3api create-bucket
--bucket <bucket_name>
--region us-east-1
```

## 1.1.We will get the following output

```json
{
"Location": "/<bucket_name>",
"BucketArn": "arn:aws:s3:::<bucket_name>"
}
```
## 2.We validate that the bucket has public access blocked with following command: 

```bash
aws s3api get-public-access-block
--bucket <bucket_name>
```

## 2.1. If all is well the system show the following output

```json
{
    "PublicAccessBlockConfiguration": {
        "BlockPublicAcls": true,
        "IgnorePublicAcls": true,
        "BlockPublicPolicy": true,
        "RestrictPublicBuckets": true
    }
}
```
## 3.We upload a website by cloning a repository

```bash
git clone https://github.com/startbootstrap/startbootstrap-creative.git website
```

## 4.From the folder where the website files are located , we upload files to S3 with the following command:

```bash
aws s3 sync ./website s3://<bucket_name>/
```
## 5.We validate that the files were uploaded to the bucket with the following command:

```bash
aws s3 ls s3://<bucket_name>/
```

## 6. We create OAC file with the following command:

```bash
nano oac.json
```

## 6.1.we add the following Content in the file 

```json
{
"Name": "frost-augie-oac",
"Description": "OAC for private S3 bucket",
"OriginAccessControlOriginType": "s3",
"SigningBehavior": "always",
"SigningProtocol": "sigv4"
}
```
## 7. We create the OAC with the following command

```bash
aws cloudfront create-origin-access-control
--origin-access-control-config file://oac.json
```

## 7.1 – the previous command generated the following output

```json
{
    "Location": "https://cloudfront.amazonaws.com/2020-05-31/origin-access-control/E88HMBR8Y95V3",
    "ETag": "ETVPDKIKX0DER",
    "OriginAccessControl": {
        "Id": "E88HMBR8Y95V3",
        "OriginAccessControlConfig": {
            "Name": "frost-augie-oac",
            "Description": "OAC for private S3 bucket",
            "SigningProtocol": "sigv4",
            "SigningBehavior": "always",
            "OriginAccessControlOriginType": "s3"
        }
    }
}
```

## 8.we validate  the  OAC connection with the following command

```bash
aws cloudfront list-origin-access-controls
```
## 8.1.In the output we make note of the Id 

```json
{
    "Location": "https://cloudfront.amazonaws.com/2020-05-31/origin-access-control/E88HMBR8Y95V3",
    "ETag": "ETVPDKIKX0DER",
    "OriginAccessControl": {
        "Id": "E88HMBR8Y95V3",
        "OriginAccessControlConfig": {
            "Name": "frost-augie-oac",
            "Description": "OAC for private S3 bucket",
            "SigningProtocol": "sigv4",
            "SigningBehavior": "always",
            "OriginAccessControlOriginType": "s3"
        }
    }
}
```
In the following steps we generate the distribution between OAC and CloudFront, we will connect CloudFront with S3  with OAC
---

## 9.Create the file distribution.json with the following command: 

```bash
nano distribution.json
```

## 9.1.We Add the following information in the new file, but before we should add the Id that we stored in the step 8.1  in the key "OriginAccessControlId":   

```json
{
"CallerReference": "frost-cloudfront-001",
"Comment": "CloudFront with OAC",
"Enabled": true,
"Origins": {
"Quantity": 1,
"Items": [
{
"Id": "S3Origin",
"DomainName": "<bucket-name>.s3.amazonaws.com",
"S3OriginConfig": {
"OriginAccessIdentity": ""
},
"OriginAccessControlId": "E88HMBR8Y95V3"
}
]
},
"DefaultRootObject": "index.html",
"DefaultCacheBehavior": {
"TargetOriginId": "S3Origin",
"ViewerProtocolPolicy": "redirect-to-https",

"AllowedMethods": {
"Quantity": 2,
"Items": ["GET", "HEAD"]
},

"ForwardedValues": {
"QueryString": false,
"Cookies": {
"Forward": "none"
}
},

"TrustedSigners": {
"Enabled": false,
"Quantity": 0
},

"MinTTL": 0,
"DefaultTTL": 86400,
"MaxTTL": 31536000
}
}
```
Because we are using `OriginAccessControlId` in this json file, the `OriginAccessIdentity` is an empty string.

## 10.We create distribution with the following command:

``` bash
aws cloudfront create-distribution\
--distribution-config file://distribution.json
```

## 11.We run the following command for get DomainName

``` bash
aws cloudfront list-distributions | grep DomainName
```

## 12.We run the following command for get the ARN

``` bash
aws cloudfront list-distributions | grep ARN
```
## 13.We create a new file for add a policy with the following command: 

``` bash
nano bucket-policy.json
```

## 14.We add the policy to the JSON file and we replace the ARN and the domain name 

```json
{
"Version": "2012-10-17",
"Statement": [
{
"Sid": "AllowCloudFrontAccessOnly",
"Effect": "Allow",
"Principal": {
"Service": "cloudfront.amazonaws.com"
},
"Action": "s3:GetObject",
"Resource": "arn:aws:s3:::<bucket-name>/*",
"Condition": {
"StringEquals": {
"AWS:SourceArn": "<replace with the ARN>"
}
}
}
]
}
```
## 14.We apply policy with the following command 

```bash
aws s3api put-bucket-policy
--bucket <buckect_name>
--policy file://bucket-policy.json
```
15 – Final step: open in browser and load the CloudFront domain 
https://d2f0yjlgg96yl7.cloudfront.net

![website](./imagenescloud/2.png)

## ✅ Final Result

- Fully private S3 bucket
- Secure access using CloudFront + OAC
- Static website delivered globally
- Infrastructure created using CLI

---