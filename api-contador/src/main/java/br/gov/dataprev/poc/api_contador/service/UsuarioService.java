package br.gov.dataprev.poc.api_contador.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.Optional;

import br.gov.dataprev.poc.api_contador.model.Usuario;
import br.gov.dataprev.poc.api_contador.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsuarioService (UsuarioRepository repository, PasswordEncoder password, TokenService tokenService){
        this.usuarioRepository = repository;
        this.passwordEncoder = password;
        this.tokenService = tokenService;
    }

    public Usuario registrar(String login, String senha){
        Usuario usuario = new Usuario();
        usuario.setLogin(login);

        // CRIPTOGRAFANDO A SENHA ANTES DE SALVAR O VALOR CRIPTOGRAFADO NO BANCO
        usuario.setSenha(passwordEncoder.encode(senha));

        return usuarioRepository.save(usuario);
    }

    public String realizarLogin(String login, String senha){
        
        // PROCURANDO NO BANCO O USUÁRIO COM O LOGIN PASSADO
        Optional<Usuario> usuarioBuscado = usuarioRepository.findByLogin(login);
        
        // SE NÃO EXISTIR UM USUÁRIO COM ESSE LOGIN RETORNA UM ERRO
        if(usuarioBuscado.isEmpty()){
            throw new RuntimeException("Usuário não encontrado");
        }
        // PEGA DO OPTIONAL OS DADOS DO USUARIO ENCONTRADO
        Usuario usuario = usuarioBuscado.get();

        // VERIFICA NO PASSWORD ENCODER SE A SENHA PASSADA É IGUAL A SENHA SALVA NO BANCO (ESTÁ ENCRIPTOGRAFADA)
        boolean senhaEstaCorreta = passwordEncoder.matches(senha, usuario.getSenha());

        // SE NÃO ESTIVE CORRETA RETORNA UM ERRO
        if(!senhaEstaCorreta){
            throw new RuntimeException("Senha incorreta.");
        }

        return tokenService.gerarToken(usuario.getLogin());
    }
}