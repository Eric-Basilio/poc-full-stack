package br.gov.dataprev.poc.api_contador.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import br.gov.dataprev.poc.api_contador.model.Contador;
import br.gov.dataprev.poc.api_contador.repository.ContadorRepository;

@ExtendWith(MockitoExtension.class)
public class ContadorServiceTest {

    @Mock
    private ContadorRepository contadorRepository;

    @InjectMocks
    private ContadorService contadorService;

    @Captor
    private ArgumentCaptor<Contador> contadorCaptor;

    @Test
    void deveIncrementarQuandoContadorJaExiste(){

        // Arrange - preparar o cenário

        // Definir o personagem contador
        Contador contador = new Contador();

        contador.setId(1L);
        contador.setValorAtual(10);

        // Ensinar ao Mock o que responder quando findById(1L) for chamado
        when(contadorRepository.findById(1L))
            .thenReturn(Optional.of(contador));

        // Act  - executar o comportamento que queremos testar

        Integer resultado = contadorService.incrementar();

        // Assert - verificar se tudo aconteceu como esperado

        /* Verifica o retorno do metodo incrementar */
        assertEquals(11, resultado);

        /* Verifica se o objeto recebeu o valor que deveria */
        assertEquals(11, contador.getValorAtual());

        /* Verifica se o método tentou salvar no banco de dados */
        verify(contadorRepository).save(contador);

    }

    

    @Test
    void deveCriarContadorQuandoNaoExiste(){
        
        /* Teste de quando o contador não existir (primeira iniciação do site).
        Comportamento esperado: 
        1 - O repositório vai retornar o <Optional> Contador vazio  
        2 - Cai no else
        3 - Instancia o objeto Contador contador
        4 - Usa o setter do modelo Contador e define valorAtual = 0
        5 - Soma 1 ao valor atual
        6 - Usa o setter do modelo Contador novamente para definir o valorAtual igual ao resultado da soma anterior
        7 - Chama a função save para guardar no banco de dados
        P.S.: o save vai enviar o ID null pois a tarefa de definir o valor fica a cardo do banco de dados.*/ 
        

        // Arrange
        when(contadorRepository.findById(1L))
            .thenReturn(Optional.empty());


        // Act
        // Chamamos a função que queremos testar
        Integer resultado = contadorService.incrementar();


        // Assert

        // verifica se o resultado deu 1
        assertEquals(1, resultado);

        // 1 - Captura o objeto que o Service enviou ao Repository
        // Detalhe: se o Service não chamar save(), o verify falhará e o teste será interrompido. 
        verify(contadorRepository)
            .save(contadorCaptor.capture());

        // 2 - Recupera a referência para o objeto capturado
        Contador contadorSalvo = contadorCaptor.getValue();

        // verifica os valores a partir da referência criada pelo teste        
        assertEquals(1, contadorSalvo.getValorAtual());

        assertNull(contadorSalvo.getId());


    }
    
}
