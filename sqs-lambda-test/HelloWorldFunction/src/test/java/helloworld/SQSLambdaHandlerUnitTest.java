package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SQSLambdaHandlerUnitTest {

    @Test
    public void testHandlerWithSingleMessage() {
        // Create a mock SQSEvent
        SQSEvent event = new SQSEvent();
        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody("{\"name\":\"John\",\"email\":\"john@example.com\"}");

        event.setRecords(Collections.singletonList(message));

        // Instantiate handler and context
        SQSLambdaHandler handler = new SQSLambdaHandler();
        Context mockContext = mock(Context.class);

        // Invoke
        String result = handler.handleRequest(event, mockContext);

        // Assert
        assertEquals("Processed 1 messages.", result);
    }
}