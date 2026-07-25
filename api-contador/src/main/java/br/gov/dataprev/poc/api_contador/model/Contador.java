package br.gov.dataprev.poc.api_contador.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Contador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer valorAtual;

    // GETTERS E SETTERS DO ID
    public Long getId(){
        return this.id;
    }
    public void setId(Long novoId){
        id = novoId;
    }

    // GETTERS E SETTERS DO VALOR ATUAL
    public Integer getValorAtual(){
        return this.valorAtual;
    }
    public void setValorAtual(Integer novoValor){
        valorAtual = novoValor;
    }

}