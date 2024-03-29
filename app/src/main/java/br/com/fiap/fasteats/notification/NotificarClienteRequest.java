package br.com.fiap.fasteats.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificarClienteRequest {
    private String email;
    private String titulo;
    private String mensagem;
}
