package br.gov.dataprev.poc.api_contador.service;

import org.springframework.stereotype.Service;
import br.gov.dataprev.poc.api_contador.repository.ContadorRepository;
import br.gov.dataprev.poc.api_contador.model.Contador;


@Service
public class ContadorService {
 private final ContadorRepository contadorRepository;

 public ContadorService(ContadorRepository repository){
    this.contadorRepository = repository;
 }

}
