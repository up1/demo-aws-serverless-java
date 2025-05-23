# Demo AWS Serverless + Amazon Simple Queue Service(SQS) + LocalStack

## 1. AWS Serverless Application Model (AWS SAM)
* [Install](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)
* [Setup IAM](https://docs.aws.amazon.com/IAM/latest/UserGuide/access-keys_inline-policy.html)

```
$sam --version
SAM CLI, version 1.138.0
```

## 2. Initial Java-based Lambda function
```
$sam init --runtime java11 --dependency-manager maven --app-template hello-world --name sqs-lambda-test


# Build JAR file
$cd sqs-lambda-test
$mvn clean package

# Add JAR file to CLASSPATH
$export CLASSPATH=.:path to jar file:$CLASSPATH
```

## 3. Run local with SAM

```
$sam local invoke SQSLambdaFunction --event events/sqs-event.json
```

Fix bug of docker on MacOS
* https://github.com/aws/aws-sam-cli/issues/5059
```
$sudo ln -sf "$HOME/.docker/run/docker.sock" /var/run/docker.sock
```

## 4. Testing
* Integration or Component testing
  * [LocalStack](https://www.localstack.cloud/)
  * [LocalStack module in TestContainers](https://java.testcontainers.org/modules/localstack/)
* Unit test
  * JUnit + Mockito
    
```
$mvn clean test
```

