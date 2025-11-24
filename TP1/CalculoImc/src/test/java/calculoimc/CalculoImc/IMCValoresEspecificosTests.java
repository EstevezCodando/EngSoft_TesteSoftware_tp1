package calculoimc.CalculoImc;

import net.jqwik.api.*;
import static org.assertj.core.api.Assertions.assertThat;

class IMCValoresEspecificosTests {

    // --------------------------------------------------------------------
    // Propriedade: IMC de adultos deve estar entre 10 e 50
    // --------------------------------------------------------------------
    @Example
    void imcAdulto60e170() {
        double imc = CalculoImcApplication.calcularPeso(60.0, 1.70);
        assertThat(imc).isBetween(10.0, 50.0);
    }

    @Example
    void imcAdulto90e180() {
        double imc = CalculoImcApplication.calcularPeso(90.0, 1.80);
        assertThat(imc).isBetween(10.0, 50.0);
    }

    // --------------------------------------------------------------------
    // Propriedade: IMC aumenta quando o peso aumenta (altura fixa)
    // --------------------------------------------------------------------
    @Example
    void imcAumentaComPeso() {
        double altura = 1.75;

        double imc60 = CalculoImcApplication.calcularPeso(60.0, altura);
        double imc80 = CalculoImcApplication.calcularPeso(80.0, altura);

        assertThat(imc60).isLessThan(imc80);
    }

    // --------------------------------------------------------------------
    // Propriedade: IMC diminui quando a altura aumenta (peso fixo)
    // --------------------------------------------------------------------
    @Example
    void imcDiminuiComAltura() {
        double peso = 80.0;

        double imc160 = CalculoImcApplication.calcularPeso(peso, 1.60);
        double imc190 = CalculoImcApplication.calcularPeso(peso, 1.90);

        assertThat(imc160).isGreaterThan(imc190);
    }
}
