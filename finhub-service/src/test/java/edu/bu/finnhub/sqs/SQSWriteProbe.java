package edu.bu.finnhub.sqs;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.util.List;
import org.tinylog.Logger;

@Tag("Probe")
public class SQSWriteProbe {
    private static final String QUEUE_URL = "https://sqs.us-east-2.amazonaws.com/183631322250/GabrielBrown-Standard1";
    @Test
    public void writeMessage() {
        SqsClient sqsClient = SqsClient.create();
        for (int i = 1; i <= 4; i++) {
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(QUEUE_URL)
                    .messageBody("Hello world from Gabe's Stock App")
                    .delaySeconds(5)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);

            Logger.info("Message sent. ID: " + response.messageId());
        }

        sqsClient.close();

    }

}
