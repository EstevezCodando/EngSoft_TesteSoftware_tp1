package calculoimc.CalculoImc;

import net.jqwik.api.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parte 3 — Geradores personalizados de dados
 * Testes com valores extremos, raros ou improváveis de peso e altura.
 */
class IMCPropertyTestsEx3 {

    // --------------------------------------------------------------------
    // 1. Gerador personalizado de alturas extremas e improváveis
    // --------------------------------------------------------------------
    @Provide
    Arbitrary<Double> alturasExtremas() {
        return Arbitraries.of(
                0.1,   // altura muito baixa (bebê / erro extremo)
                0.5,   // muito baixa
                1.0,   // baixa normal
                2.5,   // acima da média
                5.0,   // altura irreal realista, mas útil para teste
                10.0   // valor absurdo para estressar o algoritmo
        );
    }

    // --------------------------------------------------------------------
    // 2. Gerador personalizado de pesos extremos e improváveis
    // --------------------------------------------------------------------
    @Provide
    Arbitrary<Double> pesosExtremos() {
        return Arbitraries.of(
                1.0,    // peso extremamente baixo
                5.0,    // muito leve
                70.0,   // média adulta
                150.0,  // peso elevado
                300.0,  // muito elevado
                500.0   // extremo superior
        );
    }

    // --------------------------------------------------------------------
    // 3. Teste baseado em propriedades usando geradores personalizados
    // --------------------------------------------------------------------
    @Property
    void imcNaoDeveSerNegativoMesmoComValoresExtremos(
            @ForAll("pesosExtremos") double peso,
            @ForAll("alturasExtremas") double altura
    ) {
        double imc = CalculoImcApplication.calcularPeso(peso, altura);

        // Mesmo com valores bizarros, o IMC matematicamente não pode ser negativo
        assertThat(imc)
                .as("IMC deve ser >= 0 para peso=%.2f kg e altura=%.2f m", peso, altura)
                .isGreaterThanOrEqualTo(0.0);
    }

    // --------------------------------------------------------------------
    // 4. Caso especial: validar consistência do IMC para alturas muito pequenas
    // --------------------------------------------------------------------
    @Property
    void imcDeveSerMuitoAltoParaAlturasExtremamenteBaixas(
            @ForAll("pesosExtremos") double peso
    ) {
        double altura = 0.1; // altura absurda → IMC explode

        double imc = CalculoImcApplication.calcularPeso(peso, altura);

        // IMC será muito grande; queremos apenas garantir que não é negativo e é finito
        assertThat(imc)
                .isFinite()
                .isGreaterThan(0.0);
    }
}
