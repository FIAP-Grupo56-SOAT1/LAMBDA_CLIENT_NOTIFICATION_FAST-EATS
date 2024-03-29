package br.com.fiap.fasteats.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MensagemNotificacaoCliente {
    private Long clienteId;
    private String email;
    private String titulo;
    private String mensagem;
    private StatusMensagemConstants status;

    public MensagemNotificacaoCliente() {
    }

    public MensagemNotificacaoCliente(Long clienteId, String email, String titulo, String mensagem, StatusMensagemConstants status) {
        this.clienteId = clienteId;
        this.email = email;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.status = status;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public StatusMensagemConstants getStatus() {
        return status;
    }

    public void setStatus(StatusMensagemConstants status) {
        this.status = status;
    }

}
