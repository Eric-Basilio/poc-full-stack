package br.gov.dataprev.poc.api_contador.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import br.gov.dataprev.poc.api_contador.service.ContadorService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = {
    /*"https://solid-umbrella-xpjjqgwrg55fpxg7-5500.app.github.dev",
    "https://solid-umbrella-xpjjqgwrg55fpxg7-8081.app.github.dev",*/
    "http://localhost:5500"
} )
@RestController
public class ContadorController {
    //INICIANDO A INJEÇÃO DE DEPENDÊNCIAS

    // 1 - BUCANDO O SERVIÇO
    private final ContadorService contadorService;

    // INJETANDO ATRIBUTOS DO SERVIÇO NO CONTROLADOR ATRAVÉS DO this.contadorService
    // // O Spring injeta uma instância de ContadorService através do construtor.
    public ContadorController (ContadorService service){
        this.contadorService = service;
    }

    /* MAPEANDO A ROTA DE QUANDO CLICAREM NO BOTÃO.
    PROVAVELMENTE VAI TER UM EVENTLISTENER NO FRONT END
    ESPERANDO ONCLICK QUE VAI ATIVAR ESSA ROTA */ 
    @PostMapping("/incrementar")
        public Integer incrementar(){
            return contadorService.incrementar();
    }
    
    @GetMapping("/contador")
        public Integer getContador(){
            return contadorService.getContador();
        }
    
}
