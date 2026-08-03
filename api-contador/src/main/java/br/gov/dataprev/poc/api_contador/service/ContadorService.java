package br.gov.dataprev.poc.api_contador.service;

import org.springframework.stereotype.Service;
import br.gov.dataprev.poc.api_contador.repository.ContadorRepository;
import br.gov.dataprev.poc.api_contador.model.Contador;
import java.util.Optional;


@Service
public class ContadorService {
 private final ContadorRepository contadorRepository;

 public ContadorService(ContadorRepository repository){
    this.contadorRepository = repository;
 }

 public Integer incrementar(){
   // Primeiro vamos procurar o valor dentro do ID 1 - só pode existir o ID 1
   // Vamos usar o Optional pois pode ser que a linha da tabela inda não tenha sido criada
   // Em resumo, na linha abaixo esperamos receber um o objeto do tipo Contador que tenha ID 1
   // Nessa linha criamos a variável resultadoBusca que procura o objeto Contador com ID 1

   Optional<Contador> resultadoBusca = contadorRepository.findById(1L);

   Contador contador;

   /*Abaixo vamos lidar com as possibilidades de retorno do resultadoBusca: 
   se existir alguma coisa com ID 1 usamos o metodo .get() para receber o objeto 
   dentro da vairável contador recém instanciada.
   Caso não retorne nada, vamos criar o objeto e definir o valorAtual igual a 0.
   Detalhe: como a variável contador é do tipo Contador, ela vai herdar as
   caracteristicas do model Contador.java, ou seja, o contador tem um valor Integer valorAtual e
   um valor Long id
    */

   /* PS: ESTAMOS SEEGUINDO O FLUXO BUSCAR-> ATUALIZAR -> SALVAR */

   //BUSCAR
   if (resultadoBusca.isPresent()){
      contador = resultadoBusca.get();
   } else {
      contador = new Contador();
      contador.setValorAtual(0);
   }

   /* Tendo recebido o valor do contador agora vamos fazer a função do site: somar 1 no contador */

   //ATUALIZAR
   Integer novoValor = contador.getValorAtual() + 1;
   contador.setValorAtual(novoValor);

   /* Acima verificamos o valor atualizado e agora vamos salvar no banco de dados */
   //SALVAR
   this.contadorRepository.save(contador);
   return novoValor;

 }

 public Integer getContador() {
   
   Optional<Contador> resultadoBusca = contadorRepository.findById(1L);
   Contador contador;

   if (resultadoBusca.isPresent()){
      contador = resultadoBusca.get();
   } else {
      contador = new Contador();
      contador.setValorAtual(0);
   }

      return contador.getValorAtual();
 }

}
