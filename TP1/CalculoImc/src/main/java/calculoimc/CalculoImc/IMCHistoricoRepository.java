package calculoimc.CalculoImc;

/**
 * Simula um repositório externo (ex.: banco de dados)
 * responsável por armazenar resultados de IMC.
 */
public interface IMCHistoricoRepository {

    void salvarResultado(String nomeUsuario, double peso, double altura, double imc);
}
