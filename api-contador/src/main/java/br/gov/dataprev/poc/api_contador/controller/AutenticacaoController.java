package br.gov.dataprev.poc.api_contador.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.gov.dataprev.poc.api_contador.dto.LoginDTO;
import br.gov.dataprev.poc.api_contador.service.UsuarioService;

@CrossOrigin(origins = {
    "https://solid-umbrella-xpjjqgwrg55fpxg7-5500.app.github.dev",
    "http://localhost:5500"
} )

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {
    private final UsuarioService usuarioService;

    public AutenticacaoController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public String atenticar(@RequestBody LoginDTO loginDTO){
        String tokenGerado = usuarioService.realizarLogin(
            loginDTO.getLogin(),
            loginDTO.getSenha()
        );
        return tokenGerado;
    }
}