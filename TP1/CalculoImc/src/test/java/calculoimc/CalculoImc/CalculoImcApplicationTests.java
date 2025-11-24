package calculoimc.CalculoImc;

import net.jqwik.api.*;
import net.jqwik.api.constraints.Positive;


import static org.assertj.core.api.Assertions.assertThat;


class CalculoImcApplicationTests {


    @Property
    void imcNuncaDeveSerNegativo(
            @ForAll @Positive double peso,
            @ForAll @Positive double altura
    ) {
        double imc = CalculoImcApplication.calcularPeso(peso, altura);
        assertThat(imc).isGreaterThanOrEqualTo(0.0);
    }


    @Property
    void imcPodeSerInvalidoQuandoEntradasSaoInvalidas(@ForAll double peso, @ForAll double altura) {

        // Quando altura é zero → divisão por zero → IMC inválido (NaN ou Infinity)
        if (altura == 0) {
            // esse é um contraexemplo esperado, então o teste NÃO deve falhar
            return;
        }

        // Só validamos a propriedade quando ambas as entradas são válidas
        if (peso > 0 && altura > 0) {
            double imc = CalculoImcApplication.calcularPeso(peso, altura);
            assertThat(imc).isGreaterThanOrEqualTo(0.0);
        }
    }
    // --------------------------------------------------------------------
    // 1. Propriedade fundamental: IMC ≥ 0 para entradas válidas
    //    (peso > 0 e altura > 0)
    // --------------------------------------------------------------------
    @Property
    void imcNuncaDeveSerNegativoQuandoPesoEAlturaSaoPositivos(
            @ForAll @Positive double peso,
            @ForAll @Positive double altura
    ) {
        double imc = CalculoImcApplication.calcularPeso(peso, altura);
        assertThat(imc).isGreaterThanOrEqualTo(0.0);
    }

    // --------------------------------------------------------------------
    // 2. Geradores personalizados: valores extremos e improváveis
    // --------------------------------------------------------------------

    @Provide
    Arbitrary<Double> alturasExtremas() {
        // Inclui alturas muito baixas e muito altas, para testar limites numéricos
        return Arbitraries.of(0.1, 0.5, 1.0, 2.0, 5.0, 10.0);
    }

    @Provide
    Arbitrary<Double> pesosExtremos() {
        // Inclui pesos muito baixos e muito elevados
        return Arbitraries.of(1.0, 5.0, 70.0, 150.0, 300.0, 500.0);
    }

    @Property
    void imcDeveSerNaoNegativoMesmoComValoresExtremos(
            @ForAll("pesosExtremos") double peso,
            @ForAll("alturasExtremas") double altura
    ) {
        double imc = CalculoImcApplication.calcularPeso(peso, altura);
        assertThat(imc).isGreaterThanOrEqualTo(0.0);
    }


    // --------------------------------------------------------------------
    // 4. Valores específicos (exemplos clínicos) usando @Example
    //
    //    @Example é aplicado a MÉTODOS, não a parâmetros.
    //    Cada exemplo abaixo representa um caso clínico típico.
    // --------------------------------------------------------------------

    @Example
    void imcDeveEstarEmFaixaClinicaParaAdulto60kg170() {
        double imc = CalculoImcApplication.calcularPeso(60.0, 1.70);
        assertThat(imc).isBetween(10.0, 50.0);
    }

    @Example
    void imcDeveEstarEmFaixaClinicaParaAdulto90kg180() {
        double imc = CalculoImcApplication.calcularPeso(90.0, 1.80);
        assertThat(imc).isBetween(10.0, 50.0);
    }
}
