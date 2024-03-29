package br.com.fiap.fasteats.notification;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

public class NotificationFunctionHandler implements RequestHandler<SQSEvent, String> {

    private ObjectMapper objMapper;

    private final String QUEUE_URL = System.getenv("QUEUE_URL");
    private final String MAIL_PORT = System.getenv("MAIL_PORT");
    private final String SENDER_MAIL = System.getenv("SENDER_MAIL");
    private final String SENDER_MAIL_PASSWORD = System.getenv("SENDER_MAIL_PASSWORD");
    private final String MAIL_HOST = System.getenv("MAIL_HOST");


    @Override
    public String handleRequest(SQSEvent event, Context context) {
        context.getLogger().log("Initiating Message Consumer Function");
        objMapper = new ObjectMapper();
        try {

            for (SQSEvent.SQSMessage msg : event.getRecords()) {
                context.getLogger().log("Message received: " + msg.getBody());
                verifyEmail(msg.getBody(), context);
            }
        } catch (Exception ex) {
            context.getLogger().log("FAILED! : " + ex.getMessage());
        }

        return event.toString();
    }


    public void verifyEmail(String messageSqs, Context context) throws MessagingException {
        MensagemNotificacaoCliente notificarClienteRequest = null;
        try {
            notificarClienteRequest = objMapper.readValue(messageSqs, MensagemNotificacaoCliente.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        context.getLogger().log("Mensagem Object: " + notificarClienteRequest);

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", MAIL_HOST);
        prop.put("mail.smtp.port", MAIL_PORT);
        prop.put("mail.smtp.ssl.trust", MAIL_HOST);

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_MAIL, SENDER_MAIL_PASSWORD);
            }
        });


        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_MAIL));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(notificarClienteRequest.getEmail()));
        message.setSubject(notificarClienteRequest.getTitulo());

        String msg = "Status do pagamento: " + notificarClienteRequest.getStatus() + "<br>" +
                "Mensagem: " + notificarClienteRequest.getMensagem() + "<br>";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);


    }

}
