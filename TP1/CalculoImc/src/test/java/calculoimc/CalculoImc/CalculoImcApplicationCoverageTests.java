package calculoimc.CalculoImc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes adicionais para aumentar a cobertura da classe CalculoImcApplication.
 * Foco:
 *  - Cobrir todos os ramos de classificarIMC(double)
 *  - (ProgramaIMC e main não são cobertos por serem fortemente acoplados a System.in/out)
 */
class CalculoImcApplicationCoverageTests {

    @Test
    void deveClassificarTodasAsFaixasDeIMC() {
        // < 16.0
        assertEquals("Magreza grave", CalculoImcApplication.classificarIMC(15.9));

        // 16.0 <= imc < 17.0
        assertEquals("Magreza moderada", CalculoImcApplication.classificarIMC(16.0));
        assertEquals("Magreza moderada", CalculoImcApplication.classificarIMC(16.9));

        // 17.0 <= imc < 18.5
        assertEquals("Magreza leve", CalculoImcApplication.classificarIMC(17.0));
        assertEquals("Magreza leve", CalculoImcApplication.classificarIMC(18.4));

        // 18.5 <= imc < 25.0
        assertEquals("Saudável", CalculoImcApplication.classificarIMC(18.5));
        assertEquals("Saudável", CalculoImcApplication.classificarIMC(24.9));

        // 25.0 <= imc < 30.0
        assertEquals("Sobrepeso", CalculoImcApplication.classificarIMC(25.0));
        assertEquals("Sobrepeso", CalculoImcApplication.classificarIMC(29.9));

        // 30.0 <= imc < 35.0
        assertEquals("Obesidade Grau I", CalculoImcApplication.classificarIMC(30.0));
        assertEquals("Obesidade Grau I", CalculoImcApplication.classificarIMC(34.9));

        // 35.0 <= imc < 40.0
        assertEquals("Obesidade Grau II", CalculoImcApplication.classificarIMC(35.0));
        assertEquals("Obesidade Grau II", CalculoImcApplication.classificarIMC(39.9));

        // >= 40.0
        assertEquals("Obesidade Grau III", CalculoImcApplication.classificarIMC(40.0));
        assertEquals("Obesidade Grau III", CalculoImcApplication.classificarIMC(50.0));
    }
}
