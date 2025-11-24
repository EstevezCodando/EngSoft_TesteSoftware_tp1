package calculoimc.CalculoImc;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Positive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes baseados em propriedades (Property-Based Testing)
 * aplicados ao cálculo do IMC utilizando Jqwik.
 */
class IMCPropertyTestsEx2 {

    /**
     * Propriedade fundamental:
     * Para qualquer peso > 0 e altura > 0,
     * o IMC calculado nunca deve ser negativo.
     * Aqui o Jqwik gera automaticamente diversos
     * pares (peso, altura) positivos.
     */
    @Property
    void imcNuncaDeveSerNegativo(
            @ForAll @Positive double peso,
            @ForAll @Positive double altura
    ) {
        double imc = CalculoImcApplication.calcularPeso(peso, altura);
        assertThat(imc)
                .as("IMC deve ser >= 0 para peso=%.2f, altura=%.2f", peso, altura)
                .isGreaterThanOrEqualTo(0.0);
    }
}
