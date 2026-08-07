package br.gov.dataprev.poc.api_contador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String senha;

    public Long getId(){
        return this.id;
    } 

    public String getLogin(){
        return this.login;
    }
    public void setLogin(String novoLogin){
        login = novoLogin;
    }

    public String getSenha(){
        return this.senha;
    }
    public void setSenha(String novaSenha){
        senha = novaSenha;
    }
    
}
