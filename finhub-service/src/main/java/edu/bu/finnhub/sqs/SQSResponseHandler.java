package edu.bu.finnhub.sqs;

import edu.bu.FinnhubResponseHandler;
import java.io.IOException;
import org.tinylog.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

/** This class is to write to the SQS messages from Finhub */
public class SQSResponseHandler implements FinnhubResponseHandler {
  private static final String QUEUE_URL =
      "https://sqs.us-east-2.amazonaws.com/183631322250/GabrielBrown-Standard1";

  SqsClient sqsClient = SqsClient.builder().region(Region.US_EAST_2).build();

  @Override
  public void enqueue(String response) throws IOException {
    SendMessageRequest sendMessageRequest =
        SendMessageRequest.builder().queueUrl(QUEUE_URL).messageBody(response).build();

    SendMessageResponse sendMessageResponse = sqsClient.sendMessage(sendMessageRequest);

    Logger.info("Message sent to SQS queue. Message ID: " + sendMessageResponse.messageId());
  }
}
