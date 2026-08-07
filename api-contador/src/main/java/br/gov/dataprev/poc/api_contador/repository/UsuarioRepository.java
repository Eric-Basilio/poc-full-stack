package br.gov.dataprev.poc.api_contador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.gov.dataprev.poc.api_contador.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository <Usuario, Long>{
    Optional<Usuario> findByLogin(String login);
     
}
