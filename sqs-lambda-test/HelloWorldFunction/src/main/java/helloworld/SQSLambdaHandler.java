package helloworld;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.services.sqs.model.Message;

public class SQSLambdaHandler implements RequestHandler<List<Message>, String> {

    @Override
    public String handleRequest(List<Message> messages, Context context) {
        for (Message msg : messages) {
            System.out.println("Received SQS Message: " + msg.body());
        }
        return "Processed " + messages.size() + " messages.";
    }
}
