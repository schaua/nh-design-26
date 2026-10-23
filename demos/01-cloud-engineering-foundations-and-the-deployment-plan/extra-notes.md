# Cloud Engineering Foundations and the Deployment Plan

This Sprint is about moving your lab service (and your project) into the cloud.  For the labs there recommended architecture is illustrated on slide $

S3 to host the code for the angular UI accessed through CloudFront
RDS to host the postgres database
All of the other containers are hosted in ECS

When it comes to your project, something similar might be appropriate, or it might end up that an EC2 Linux instance hosting everthing (like you have been doing so far) is a better solution.

Important things to remember.
S3 is static - it doesn't execute anything, but rather serves up the HTML/CSS/JS that runs in the clients browser.
The 'mission service' is an application running in a JVM which needs to run continuously.
RDS needs to be unreachable anywhere other than the ECS task from the application

The slides mention Kafka staying local, but we have been running it in a container, so it could also potentially be deployed to the cloud.  MSK, the Kafka service in AWS is expensive and complicated, so DO NOT use that service.


