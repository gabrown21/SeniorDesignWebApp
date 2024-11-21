package edu.bu.sqs;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Tag("Probe")
public class SQSReadProbe {
    private static final String QUEUE_URL = "https://sqs.us-east-2.amazonaws.com/183631322250/GabrielBrown-Standard1";
    private static final String EXPECTED_MESSAGE = "Hello world from Gabe's Stock App";
    @Test
    public void readMessage() {
        SqsClient sqsClient = SqsClient.create();
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .maxNumberOfMessages(10)
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

        if (messages.isEmpty()) {
            Logger.info("No messages in the queue.");
        } else {
            for (Message message : messages) {
                assertEquals(EXPECTED_MESSAGE, message.body());
                Logger.info("Message received: " + message.body());
                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .receiptHandle(message.receiptHandle())
                        .build();
                sqsClient.deleteMessage(deleteMessageRequest);
                Logger.info("Message deleted. ID: " + message.messageId());
            }
        }
        sqsClient.close();

    }
}
