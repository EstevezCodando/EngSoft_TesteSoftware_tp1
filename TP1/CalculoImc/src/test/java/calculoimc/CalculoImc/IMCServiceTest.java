package calculoimc.CalculoImc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do IMCService isolando a dependência
 * IMCHistoricoRepository com Mockito.
 */
@ExtendWith(MockitoExtension.class)
class IMCServiceTest {

    @Test
    void deveCalcularIMCESalvarNoHistoricoSemAcessarFonteExterna() {
        // Arrange (configuração do cenário)
        IMCHistoricoRepository repositorioMock = mock(IMCHistoricoRepository.class);
        IMCService imcService = new IMCService(repositorioMock);

        String nomeUsuario = "João";
        double peso = 80.0;
        double altura = 1.80;

        // Act (ação a ser testada)
        double imcCalculado = imcService.calcularERegistrarIMC(nomeUsuario, peso, altura);

        // Assert 1: verifica o valor do IMC calculado
        double imcEsperado = CalculoImcApplication.calcularPeso(peso, altura);
        assertThat(imcCalculado).isEqualTo(imcEsperado);

        // Assert 2: verifica se o repositório foi chamado corretamente
        ArgumentCaptor<String> nomeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> pesoCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> alturaCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> imcCaptor = ArgumentCaptor.forClass(Double.class);

        verify(repositorioMock, times(1))
                .salvarResultado(
                        nomeCaptor.capture(),
                        pesoCaptor.capture(),
                        alturaCaptor.capture(),
                        imcCaptor.capture()
                );

        assertThat(nomeCaptor.getValue()).isEqualTo(nomeUsuario);
        assertThat(pesoCaptor.getValue()).isEqualTo(peso);
        assertThat(alturaCaptor.getValue()).isEqualTo(altura);
        assertThat(imcCaptor.getValue()).isEqualTo(imcEsperado);
    }
}
