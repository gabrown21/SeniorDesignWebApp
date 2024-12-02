package edu.bu;

import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubParser;
import edu.bu.finhub.FinhubResponse;
import edu.bu.metrics.MetricsTracker;
import java.util.List;
import org.tinylog.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

/** This class is to read from the SQS */
public class SQSQueueReader {
  private static final String QUEUE_URL =
      "https://sqs.us-east-2.amazonaws.com/183631322250/GabrielBrown-Standard1";
  private final DataStore dataStore;
  private final SqsClient sqsClient;
  private final FinhubParser parser;
  private final MetricsTracker metricsTracker;
  private volatile boolean keepRunning = true;

  public SQSQueueReader(DataStore dataStore, MetricsTracker metricsTracker) {
    this.dataStore = dataStore;
    this.metricsTracker = metricsTracker;
    this.sqsClient = SqsClient.builder().region(Region.US_EAST_2).build();
    this.parser = new FinhubParser();
  }

  public void start() {
    new Thread(this::pollQueue).start();
  }

  private void pollQueue() {
    Logger.info("SQSQueueReader started polling messages from the SQS queue.");
    while (keepRunning) {
      try {
        List<Message> messages = recieve();

        if (messages.isEmpty()) {
          Logger.info("No messages in the queue.");
        } else {
          for (Message message : messages) {
            processQueueResponse(message);
            deleteMessage(message);
          }
        }

        Thread.sleep(500);
      } catch (InterruptedException e) {
        Logger.error("SQSQueueReader interrupted: " + e.getMessage());
        Thread.currentThread().interrupt();
        break;
      } catch (Exception e) {
        Logger.error("Unexpected error in SQSQueueReader: " + e.getMessage());
      }
    }
  }

  private void processQueueResponse(Message message) {
    Logger.info("Processing message from SQS: " + message.body());
    List<FinhubResponse> parsedResponses = parser.parse(message.body());
    if (!parsedResponses.isEmpty()) {
      handleParsedResponse(parsedResponses);
    } else {
      Logger.warn("Failed to parse message into FinnhubResponse.");
    }
  }

  private void handleParsedResponse(List<FinhubResponse> parsedResponses) {
    dataStore.update(parsedResponses);
    Logger.info("DataStore updated with new responses.");
    parsedResponses.forEach(response -> metricsTracker.recordUpdate(response.symbol));
  }

  private void deleteMessage(Message message) {
    DeleteMessageRequest deleteRequest =
        DeleteMessageRequest.builder()
            .queueUrl(QUEUE_URL)
            .receiptHandle(message.receiptHandle())
            .build();
    sqsClient.deleteMessage(deleteRequest);
    Logger.info("Message deleted from SQS. ID: " + message.messageId());
  }

  public List<Message> recieve() {
    ReceiveMessageRequest receiveRequest =
        ReceiveMessageRequest.builder()
            .queueUrl(QUEUE_URL)
            .maxNumberOfMessages(5)
            .waitTimeSeconds(2)
            .build();

    return sqsClient.receiveMessage(receiveRequest).messages();
  }
}
