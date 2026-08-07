package br.gov.dataprev.poc.api_contador.service;

import br.gov.dataprev.poc.api_contador.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import br.gov.dataprev.poc.api_contador.model.Usuario;

public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private UsuarioService (UsuarioRepository repository, PasswordEncoder password){
        this.usuarioRepository = repository;
        this.passwordEncoder = password;
    }

    public Usuario registrar(String login, String senha){
        Usuario usuario = new Usuario();
        usuario.setLogin(login);

        // CRIPTOGRAFANDO A SENHA ANTES DE SALVAR O VALOR CRIPTOGRAFADO NO BANCO
        usuario.setSenha(passwordEncoder.encode(senha));

        return usuarioRepository.save(usuario);
    }
}
