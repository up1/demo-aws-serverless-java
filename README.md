# Demo AWS Serverless + Amazon Simple Queue Service(SQS) + LocalStack

## 1. AWS Serverless Application Model (AWS SAM)
* [Install](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)

```
$sam --version
SAM CLI, version 1.138.0
```

## 2. Initial Java-based Lambda function
```
$sam init --runtime java11 --dependency-manager maven --app-template hello-world --name sqs-lambda-test

$cd sqs-lambda-test

$mvn clean package

# Add JAR file to CLASSPATH
```

## 3. Run local with SAM

```
$sam local invoke SQSLambdaFunction --event events/sqs-event.json
```

## 4. Testing with [LocalStack](https://www.localstack.cloud/)
* Integration or Component testing
  * [LocalStack module in TestContainers](https://java.testcontainers.org/modules/localstack/)
* Unit test
  * JUnit + Mockito
    
```
$mvn clean test
```

