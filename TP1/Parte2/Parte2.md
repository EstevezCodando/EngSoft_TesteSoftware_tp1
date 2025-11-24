# Parte 2 — Testes Baseados em Propriedades (Property-Based Testing)

### Ex1.1 Conceito e Diferenças em Relação aos Testes Tradicionais

Nos testes tradicionais (ex.: JUnit), o desenvolvedor define **exemplos específicos** que representam cenários pontuais. Cada teste cobre apenas um conjunto de entradas, e a eficácia depende do quanto o programador conseguir antecipar os casos relevantes.

Os **testes baseados em propriedades (Property-Based Testing – PBT)** adotam outra abordagem:  
em vez de testar valores isolados, o desenvolvedor formula **propriedades invariantes** que o sistema deve sempre satisfazer, independentemente da entrada.

A ferramenta (como **Jqwik**) então gera automaticamente **centenas ou milhares de valores**, explorando uma grande variedade de cenários — incluindo valores extremos, inesperados ou difíceis de prever.

Assim, enquanto os testes tradicionais respondem a:

> **"Funciona para este exemplo?"**

os testes baseados em propriedades respondem a:

> **"Funciona para todos os valores possíveis dentro desse domínio?"**

---

### Ex1.2 Vantagens dos Testes Baseados em Propriedades

#### ✔ Geração automática e massiva de dados

Ferramentas como Jqwik criam automaticamente combinações diversas de entradas, cobrindo cenários que dificilmente seriam testados manualmente.

#### ✔ Identificação de casos inesperados

Como as entradas são geradas aleatoriamente e reduzidas sistematicamente (shrinking), o PBT encontra **contraexemplos mínimos**, revelando erros que passariam despercebidos nos testes tradicionais.

#### ✔ Maior cobertura com menos código

Uma única propriedade pode validar **centenas de cenários** em um único teste, aumentando cobertura estrutural sem multiplicar métodos de teste.

#### ✔ Testes mais alinhados à modelagem matemática

Em casos como o cálculo de IMC — uma fórmula determinística — o PBT é particularmente eficaz, pois permite testar o comportamento lógico do sistema em todo o domínio de entrada.

---

### Ex1.3. Exemplo aplicado ao cálculo do IMC

Considere a função:

```java
public static double calcularPeso(double peso, double altura) {
    return peso / (altura * altura);
}
```

Uma propriedade fundamental é:

#### ⭐ **Propriedade IMC ≥ 0**

> Para qualquer **peso > 0** e **altura > 0**, o valor calculado do IMC deve ser sempre **maior ou igual a zero**.

Essa propriedade é universal — ela independe de valores específicos — portanto encaixa-se perfeitamente para ser testada com Jqwik.

#### ✔ Implementação da propriedade usando Jqwik

```java
import net.jqwik.api.*;
import static org.assertj.core.api.Assertions.assertThat;

class IMCPropertyTests {

    @Property
    void imcNuncaDeveSerNegativo(
        @ForAll @Positive double peso,
        @ForAll @Positive double altura
    ) {
        double imc = CalculoImcApplication.calcularPeso(peso, altura);
        assertThat(imc).isGreaterThanOrEqualTo(0.0);
    }
}
```

#### ✔ Por que essa abordagem é superior à versão com arrays fixos?

- Não depende de valores arbitrados pelo programador.
- Explora automaticamente dezenas de combinações de peso e altura.
- Usa shrinking para identificar contraexemplos mínimos, caso exista um erro.
- Aumenta cobertura sem aumentar complexidade do código.

---

### Ex1.4. Síntese

Os testes baseados em propriedades ampliam a robustez e a confiabilidade do sistema, permitindo validar **características universais** do cálculo do IMC com muito mais eficiência do que testes exemplares tradicionais. No contexto deste projeto, a propriedade "IMC ≥ 0" é essencial e representa fielmente a natureza matemática da operação.

## Ex2.1

Link github :

```java
class IMCPropertyTests {

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
```

## Ex4

### Ex4.1 Teste Sem Restrições de Entrada

O teste abaixo executa o cálculo de IMC com valores totalmente aleatórios gerados pelo Jqwik:

```java
@Property
void testIMCComValoresAleatorios(@ForAll double peso, @ForAll double altura) {
    double imc = CalculoImcApplication.calcularPeso(peso, altura);
    assertThat(imc).isGreaterThanOrEqualTo(0.0);
}
```

Esse teste assume que todas as combinações possíveis de `peso` e `altura` devem resultar em um IMC maior ou igual a zero — o que é propositalmente falso.  
O objetivo é permitir que o Jqwik encontre automaticamente **contraexemplos**, casos que violam a propriedade declarada.

---

### Ex4.2 Contraexemplo Encontrado

Durante a execução, o Jqwik identificou o seguinte contraexemplo mínimo:

- **peso:** 80.0
- **altura:** 0.0
- **resultado do IMC:** `NaN` (divisão por zero)

Isso ocorre porque:

IMC = 80 / (0²) = 80 / 0 → operação inválida → `NaN`.

`NaN` não satisfaz:

```java
assertThat(imc).isGreaterThanOrEqualTo(0.0);
```

---

### Ex4.3 Impacto da Falha no Programa

#### Problema 1 — Não há validação de entrada

O método:

```java
public static double calcularPeso(double peso, double altura) {
    return peso / (altura * altura);
}
```

não impede divisão por zero.

#### Problema 2 — O sistema aceita valores fisicamente impossíveis

- altura = 0
- altura negativa
- valores mínimos absurdos (0.01 m)

Isso permite IMCs absurdos ou inválidos.

#### Problema 3 — Classificação pode ser corrompida

`classificarIMC()` espera um valor real.  
`NaN` pode quebrar toda a lógica de classificação.

---

### Ex4.4 Sugestões de Implementação

#### ✔ 1. Validar entradas

```java
public static double calcularPeso(double peso, double altura) {
    if (peso <= 0) {
        throw new IllegalArgumentException("Peso deve ser maior que zero.");
    }
    if (altura <= 0) {
        throw new IllegalArgumentException("Altura deve ser maior que zero.");
    }
    return peso / (altura * altura);
}
```

#### ✔ 2. Tratar exceções no programa principal

```java
try {
    imc = calcularPeso(peso, altura);
    System.out.println("Seu IMC é: " + imc);
} catch (IllegalArgumentException e) {
    System.out.println("Erro: " + e.getMessage());
}
```

#### ✔ 3. Separar cálculo da interface (melhor prática)

Crie uma classe `IMCService` dedicada ao cálculo e validação.

#### ✔ 4. Criar mais propriedades

- se peso ↑ então IMC ↑
- se altura ↑ então IMC ↓
- classificarIMC(imc) ∈ conjunto válido de categorias

---

### Ex4.5 Conclusão

O Jqwik revelou que a propriedade “IMC ≥ 0 para todas as entradas” é falsa fora do domínio físico do problema.  
A contraprovação (altura = 0) expôs falhas importantes:

- falta de validação
- risco de divisão por zero
- inconsistência na classificação

Essa etapa demonstra o poder do Property-Based Testing para identificar fragilidades estruturais no software antes do lançamento.

## Ex5 — Isolando Dependências com Mocks

### 📌 Competência

✔ Isolar a classe sob teste substituindo suas dependências por objetos simulados (mocks).

### 📋 Objetivo

Demonstrar como testar o comportamento de uma classe que possui dependências externas, garantindo que:

- o teste é **totalmente isolado**;
- nenhuma fonte externa (banco de dados, API externa, arquivos) seja acessada;
- apenas a lógica do serviço seja testada.

No contexto do cálculo do IMC, introduzimos uma dependência simulada: um **repositório de histórico**, responsável por armazenar o resultado do cálculo.
Essa dependência será **mockada com Mockito**.

---

## Ex5.1 Identificação das Dependências Externas

O código original do IMC não possui dependências externas.
Portanto, para cumprir o objetivo da atividade, foi adicionada uma interface:

```java
public interface IMCHistoricoRepository {
    void salvarResultado(String nomeUsuario, double peso, double altura, double imc);
}
```

Essa interface simula uma _fonte externa_, como:

- banco de dados,
- arquivo,
- API de cloud,
- serviço de telemetria, etc.

Ela será mockada.

---

## Ex5.2 Serviço testado com Mockito

Criamos a classe `IMCService`, que usa a lógica do sistema e se apoia na dependência externa:

```java
public class IMCService {

    private final IMCHistoricoRepository historicoRepository;

    public IMCService(IMCHistoricoRepository historicoRepository) {
        this.historicoRepository = historicoRepository;
    }

    public double calcularERegistrarIMC(String nomeUsuario, double peso, double altura) {
        double imc = CalculoImcApplication.calcularPeso(peso, altura);

        historicoRepository.salvarResultado(nomeUsuario, peso, altura, imc);

        return imc;
    }
}
```

Esse é o alvo do teste unitário.

---

## Ex5.3 Teste Unitário com Mockito (Isolado)

```java
@ExtendWith(MockitoExtension.class)
class IMCServiceTest {

    @Test
    void deveCalcularIMCESalvarNoHistoricoSemAcessarFonteExterna() {
        // MOCK da dependência externa
        IMCHistoricoRepository repositorioMock = mock(IMCHistoricoRepository.class);

        // Classe sob teste
        IMCService imcService = new IMCService(repositorioMock);

        String nomeUsuario = "João";
        double peso = 80.0;
        double altura = 1.80;

        // Execução
        double imcCalculado = imcService.calcularERegistrarIMC(nomeUsuario, peso, altura);

        // Verificação 1 — valor do IMC
        double imcEsperado = CalculoImcApplication.calcularPeso(peso, altura);
        assertThat(imcCalculado).isEqualTo(imcEsperado);

        // Verificação 2 — chamada ao mock
        verify(repositorioMock, times(1))
                .salvarResultado(nomeUsuario, peso, altura, imcEsperado);
    }
}
```

### O que este teste garante?

- Nenhum banco de dados foi acessado
- O cálculo do IMC foi feito corretamente
- O serviço chamou a dependência exatamente **uma vez**
- Os **parâmetros enviados ao mock** são os parâmetros corretos

Assim, o comportamento é testado **de forma isolada**, como exige o exercício.

---

## Ex5.4 Conclusão

O uso de mocks:

- garante isolamento total da classe sob teste;
- permite validar a interação com dependências externas;
- evita efeitos colaterais;
- torna o teste mais previsível e mais rápido;
- segue as melhores práticas de testes unitários adotadas em ambientes profissionais.
