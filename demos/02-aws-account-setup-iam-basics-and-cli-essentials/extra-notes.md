# AWS Basics and CLI Essentials

## Where is it running?

AWS provides different `Region` options, around the world.

- Windows VM : eu-west-1
- Linux VM : eu-west-1
- Lab Exercises : us-east-1
- IAM Service : global

## Availability Zones

So that a single region is not built around a single point of failure there are multiple `Availability Zones` in each Region.

```bash
aws ec2 describe-availability-zones --region us-east-1 --query 'AvailabilityZones[].{Name:ZoneName, State:State, Location: SubGeography[0].Name}' --output table
```
Shows the six AZ's in us-east-1.

The Console is a graphic UI to the AWS universe.  It is an interesting place to explore, but can be overwhelming when so much information is provided.  In the CLI the default values for a lot of these settings are assumed, and the command can be simpler.  This also lends itself better to scripting such as in a Jenkins job.

A few examples of the CLI commands

```bash
aws sts get-caller-identity --profile training --region us-east-1
```
Notice the user-name is the trailing part of the Arn:

## IAM Service

User, Group and Policy control what permissions are active.  Since an internal object does not have a user identity a role can allow temporary permissions to be provided.

```bash
aws iam list-attached-user-policies --user-name AugieSchau --profile training
aws iam list-attached-group-policies --group-name Frost
```

The example code shows least priviledge in action.
