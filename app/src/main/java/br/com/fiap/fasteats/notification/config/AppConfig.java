package br.com.fiap.fasteats.notification.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppConfig {

    @Value("${aws.sqs.queue.url}")
    private String queueUrl;

}
