package calculoimc.CalculoImc;

/**
 * Serviço de cálculo de IMC.
 * Depende de IMCHistoricoRepository (fonte externa simulada).
 */
public class IMCService {

    private final IMCHistoricoRepository historicoRepository;

    public IMCService(IMCHistoricoRepository historicoRepository) {
        this.historicoRepository = historicoRepository;
    }

    /**
     * Calcula o IMC, salva o resultado no repositório
     * e retorna o valor calculado.
     */
    public double calcularERegistrarIMC(String nomeUsuario, double peso, double altura) {
        double imc = CalculoImcApplication.calcularPeso(peso, altura);

        historicoRepository.salvarResultado(nomeUsuario, peso, altura, imc);

        return imc;
    }
}
