package br.gov.dataprev.poc.api_contador.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.gov.dataprev.poc.api_contador.model.Contador;

public interface ContadorRepository extends JpaRepository <Contador, Long>{
    
}
