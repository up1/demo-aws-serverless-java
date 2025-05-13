package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQSLambdaHandlerTest {

    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:latest");

    private final LocalStackContainer localstack = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(LocalStackContainer.Service.SQS);

    private SqsClient sqsClient;
    private String queueUrl;

    @BeforeAll
    void setup() {
        localstack.start();

        sqsClient = SqsClient.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.SQS))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .region(Region.of(localstack.getRegion()))
                .build();

        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName("test-queue")
                .build();

        CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
        queueUrl = createQueueResponse.queueUrl();
    }

    @Test
    void testLambdaHandlerWithLocalStackSQS() {
        // Send a real message into LocalStack's SQS
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("{\"name\":\"Alice\",\"email\":\"alice@example.com\"}")
                .build());

        // Receive the message from the queue
        ReceiveMessageResponse received = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(1)
                .build());

        List<Message> awsMessages = received.messages();
        Assertions.assertFalse(awsMessages.isEmpty());

        // Convert AWS SDK message to SQSEvent.SQSMessage
        SQSEvent.SQSMessage lambdaMessage = new SQSEvent.SQSMessage();
        lambdaMessage.setBody(awsMessages.get(0).body());

        SQSEvent lambdaEvent = new SQSEvent();
        lambdaEvent.setRecords(Collections.singletonList(lambdaMessage));

        // Call Lambda Handler
        SQSLambdaHandler handler = new SQSLambdaHandler();
        Context mockContext = Mockito.mock(Context.class);
        String result = handler.handleRequest(lambdaEvent, mockContext);

        assertEquals("Processed 1 messages.", result);
        System.out.println("Lambda Handler result: " + result);
    }

    @AfterAll
    void cleanup() {
        if (sqsClient != null) sqsClient.close();
        localstack.stop();
    }
}