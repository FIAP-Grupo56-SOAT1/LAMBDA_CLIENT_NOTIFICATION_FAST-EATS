package br.com.fiap.fasteats.notification;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import br.com.fiap.fasteats.notification.config.AppConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component("notificationFunction")
public class NotificationFunction implements Function<Void, Void> {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AmazonSQS amazonSqs;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public Void apply(Void empty) {

        String queueUrl = appConfig.getQueueUrl();

        log.info("Retrieved queues: {}", amazonSqs.listQueues().getQueueUrls().toString());

        List<Message> messageList = this.getQueueMessageByQueueUrl(queueUrl);

        for (Message message : messageList) {
            log.info("Received message: {}", message.toString());
        }

        this.deleteMessagesByQueueUrl(messageList, queueUrl);

        return empty;
    }

    private void deleteMessagesByQueueUrl(List<Message> messageList, String queueUrl) {

        if (messageList == null || messageList.isEmpty()) {
            log.info("Empty list for removal");
            return;
        }

        List<DeleteMessageBatchRequestEntry> deleteMessageEntries = new ArrayList<>();

        messageList.forEach(message ->
                deleteMessageEntries.add(
                        new DeleteMessageBatchRequestEntry(message.getMessageId(), message.getReceiptHandle())
                ));

        DeleteMessageBatchRequest deleteRequest = new DeleteMessageBatchRequest();
        deleteRequest.setQueueUrl(queueUrl);
        deleteRequest.setEntries(deleteMessageEntries);

        DeleteMessageBatchResult result = amazonSqs.deleteMessageBatch(deleteRequest);

        log.info("Successfully removed size: {}", result.getSuccessful().size());
        log.error("Failed to removed size: {}", result.getFailed().size());
    }

    private List<Message> getQueueMessageByQueueUrl(String queueUrl) {
        log.info("Getting messages from queue url: {}", queueUrl);

        ReceiveMessageRequest messageRequest = new ReceiveMessageRequest(queueUrl).
                withWaitTimeSeconds(5).withMaxNumberOfMessages(2);

        List<Message> messages = amazonSqs.receiveMessage(messageRequest).getMessages();

        log.info("Received total messages size: {}", messages.size());

        return messages;
    }

    public void send( NotificarClienteRequest notificarClienteRequest) throws Exception {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            notificarClienteRequest.setEmail("hardtelles@gmail.com");
            helper.setTo(notificarClienteRequest.getEmail());
            helper.setSubject(notificarClienteRequest.getTitulo());
            helper.setText(notificarClienteRequest.getMensagem());

            mailSender.send(message);

            log.info("Email sent successfully to: " + notificarClienteRequest.getEmail());
        } catch (Exception ex) {
            log.error("Error while sending email: ", ex);
            throw new Exception("Error while sending email", ex);
        }
    }


}
