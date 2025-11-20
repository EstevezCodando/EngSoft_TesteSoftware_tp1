package calculoimc.CalculoImc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CalculoImcApplicationImcTests {

    // ---- Testes de cálculo de IMC ----

    @Test
    void deveCalcularImcCorretamenteParaValoresNormais() {
        double imc = CalculoImcApplication.calcularPeso(70.0, 1.75);
        assertEquals(22.86, imc, 0.01);
    }

    @Test
    void deveCalcularImcComAlturaDecimal() {
        double imc = CalculoImcApplication.calcularPeso(90.0, 1.92);
        assertEquals(24.41, imc, 0.01);
    }


    // ---- Testes de classificação de IMC ----

    @Test
    void deveClassificarMagrezaGrave() {
        String classe = CalculoImcApplication.classificarIMC(15.9);
        assertEquals("Magreza grave", classe);
    }

    @Test
    void deveClassificarMagrezaModerada() {
        String classe = CalculoImcApplication.classificarIMC(16.5);
        assertEquals("Magreza moderada", classe);
    }

    @Test
    void deveClassificarMagrezaLeve() {
        String classe = CalculoImcApplication.classificarIMC(17.5);
        assertEquals("Magreza leve", classe);
    }

    @Test
    void deveClassificarSaudavel() {
        String classe = CalculoImcApplication.classificarIMC(22.0);
        assertEquals("Saudável", classe);
    }

    @Test
    void deveClassificarSobrepeso() {
        String classe = CalculoImcApplication.classificarIMC(27.0);
        assertEquals("Sobrepeso", classe);
    }

    @Test
    void deveClassificarObesidadeGrauI() {
        String classe = CalculoImcApplication.classificarIMC(32.0);
        assertEquals("Obesidade Grau I", classe);
    }

    @Test
    void deveClassificarObesidadeGrauII() {
        String classe = CalculoImcApplication.classificarIMC(37.0);
        assertEquals("Obesidade Grau II", classe);
    }

    @Test
    void deveClassificarObesidadeGrauIII() {
        String classe = CalculoImcApplication.classificarIMC(45.0);
        assertEquals("Obesidade Grau III", classe);
    }
}
