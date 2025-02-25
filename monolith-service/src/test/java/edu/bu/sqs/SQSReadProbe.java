package edu.bu.sqs;

import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Tag("Probe")
public class SQSReadProbe {
  private static final String QUEUE_URL =
      "https://sqs.us-east-2.amazonaws.com/183631322250/GabrielBrown-Standard1";
  private static final String EXPECTED_MESSAGE = "Hello world from Gabe's Stock App";

  @Test
  public void readMessage() {
    SqsClient sqsClient = SqsClient.builder().region(Region.US_EAST_2).build();
    boolean keepReading = true;
    while (keepReading) {
      ReceiveMessageRequest receiveMessageRequest =
          ReceiveMessageRequest.builder().queueUrl(QUEUE_URL).maxNumberOfMessages(5).build();

      List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

      if (messages.isEmpty()) {
        Logger.info("No messages in the queue.");
        keepReading = false;
      } else {
        for (Message message : messages) {
          Logger.info("Message received: " + message.body());
          DeleteMessageRequest deleteMessageRequest =
              DeleteMessageRequest.builder()
                  .queueUrl(QUEUE_URL)
                  .receiptHandle(message.receiptHandle())
                  .build();
          sqsClient.deleteMessage(deleteMessageRequest);
          Logger.info("Message deleted. ID: " + message.messageId());
        }
      }
    }
    sqsClient.close();
  }
}
