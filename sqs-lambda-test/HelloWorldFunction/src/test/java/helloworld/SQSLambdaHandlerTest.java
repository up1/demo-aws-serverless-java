package helloworld;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQSLambdaHandlerTest {

    private static final DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:latest");

    private static final LocalStackContainer localstack = new LocalStackContainer(localstackImage)
            .withServices(LocalStackContainer.Service.SQS);

    private SqsClient sqsClient;
    private String queueUrl;

    @BeforeAll
    void setUp() {
        localstack.start();

        sqsClient = SqsClient.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.SQS))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .region(Region.of(localstack.getRegion()))
                .build();

        CreateQueueResponse createQueueResponse = sqsClient.createQueue(CreateQueueRequest.builder()
                .queueName("test-queue")
                .build());

        queueUrl = createQueueResponse.queueUrl();
    }

    @Test
    void testSendAndReceiveMessage() {
        // Send message
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}")
                .build());

        // Receive message
        ReceiveMessageResponse response = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .build());

        List<Message> messages = response.messages();
        assertFalse(messages.isEmpty());
        System.out.println("Received message: " + response.messages().get(0).body());


        // Simulate Lambda Handler Execution
        SQSLambdaHandler handler = new SQSLambdaHandler();
        String result = handler.handleRequest(messages, null); // Context can be null for testing

        Assertions.assertEquals("Processed 1 messages.", result);
        System.out.println("Lambda result: " + result);
    }

    @AfterAll
    void tearDown() {
        sqsClient.close();
        localstack.stop();
    }
}