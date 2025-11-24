# Relatório de Testes – Sistema de Cálculo de IMC (Parte 1)

HealthTech Solutions – Aplicativo de Monitoramento de Saúde

---

## 0. Contexto do Sistema

O módulo analisado é um programa de linha de comando responsável por:

- Ler **peso (kg)** e **altura (m)** informados pelo usuário.
- Calcular o **IMC (Índice de Massa Corporal)**.
- Classificar o IMC em faixas textuais (magreza, saudável, sobrepeso, obesidade etc.).

Trata-se de um componente central de um futuro aplicativo de monitoramento de saúde. Logo, **erros de cálculo, de classificação ou de interpretação dos dados podem impactar diretamente decisões do usuário sobre sua própria saúde**, o que exige cuidado redobrado com:

- Validação de dados de entrada.
- Tratamento de erros.
- Clareza das mensagens.
- Correção das faixas de classificação.

O código analisado é o seguinte (resumo funcional):

- `calcularPeso(double peso, double altura)`: calcula IMC.
- `classificarIMC(double imc)`: converte IMC em rótulo textual.
- `programaIMC(String versao)`: fluxo de entrada/saída com o usuário.
- `main(String[] args)`: ponto de entrada que chama `programaIMC`.

---

## 1. Teste Exploratório: Avaliação Geral

### 1.1 Cenários testados (peso em kg, altura em m)

**Entradas válidas (normais)**

- (70, 1.75) → IMC ≈ 22,86 → Classificação exibida: “Saudável”.
- (90, 1.80) → IMC ≈ 27,78 → Classificação exibida: “Sobrepeso”.

**Entradas com decimais**

- Altura = `1.92` → funciona normalmente.
- Altura = `1,92` → lança `NumberFormatException` (falha ao converter texto para número via `Double.parseDouble`).

**Entradas inválidas e extremas**

- Peso = `-10` → IMC negativo, programa aceita e calcula (resultado logicamente inválido).
- Altura = `0` → divisão por zero → resultado `Infinity`.
- Entrada com letras (`abc`) → `NumberFormatException`.
- Valores extremos (500, 0.5) → IMC extremamente alto, sem qualquer alerta ou validação.

### 1.2 Experiência do usuário (usabilidade)

**Pontos positivos**

- Interface de texto simples e direta.
- Mensagens básicas (“Insira o seu peso...”, “Insira a sua altura...”) são compreensíveis.
- Exibição do IMC com duas casas decimais é adequada.

**Pontos negativos**

- Não há indicação de que o separador decimal deve ser **ponto** (`.`) e não **vírgula** (`,`), formato usual no Brasil.
- Qualquer erro de digitação (ex.: `1,92`, letras, vazio) causa exceção não tratada e encerra o programa.
- Não há possibilidade de correção da entrada sem reiniciar o programa.
- Uso de **dois `Scanner` diferentes** para ler peso e altura é desnecessário e pode gerar confusão/manutenção mais difícil.

Conclusão: **a funcionalidade básica existe**, mas o programa é **frágil** e **pouco amigável** para um usuário comum.

---

## 2. Identificação de Problemas (Funcionais, Limites e Usabilidade)

### 2.1 Erros funcionais principais

1. **Separador decimal com vírgula não funciona**

   - Sintoma: entrada `1,92` para altura ou peso gera `NumberFormatException`.
   - Causa raiz: uso direto de `Double.parseDouble()` sem normalizar vírgulas para ponto.
   - Impacto: usuários brasileiros tendem a usar vírgula; o sistema se torna pouco utilizável.
   - **Prioridade:** Alta.

2. **Possibilidade de divisão por zero (altura = 0)**

   - Sintoma: altura = `0` resulta em `peso / (0 * 0)` → `Infinity`.
   - Causa: ausência total de validação de domínio para altura.
   - Impacto: resultado matematicamente inválido, pode quebrar módulos futuros ou gerar interpretações completamente erradas.
   - **Prioridade:** Alta.

3. **Aceitação de valores negativos (peso e altura)**

   - Exemplos:
     - Peso = -10, altura = 1.75 → IMC negativo.
     - Peso = 70, altura = -1.75 → IMC negativo ou valor sem sentido.
   - Causa: não há qualquer validação para evitar valores ≤ 0.
   - Impacto: resultados absurdos do ponto de vista médico.
   - **Prioridade:** Média/Alta.

4. **Faixas de classificação de IMC implementadas de forma incorreta**  
   Trecho do código:

   ```java
   if (imc < 16.0) {
       return "Magreza grave";
   }
   else if (imc == 16.0 || imc < 17.0) {
       return "Magreza moderada";
   }
   else if (imc == 17.0 || imc < 18.5) {
       return "Magreza leve";
   }
   else if (imc == 18.5 || imc < 25.0) {
       return "Saudável";
   }
   ...
   ```

   Problemas:

   - Uso de `==` combinado com `<` na mesma condição, por exemplo: `imc == 16.0 || imc < 17.0` é equivalente a `imc < 17.0`.
   - Isso faz com que a fronteira entre faixas **não esteja bem definida**. Há sobreposição lógica entre algumas condições.
   - O correto seria usar intervalos bem delimitados, do tipo `imc >= limiteInferior && imc < limiteSuperior`.
   - Impacto: potencial para classificações incorretas em valores de fronteira.
   - **Prioridade:** Alta.

5. **Ausência total de testes automatizados**
   - Não há nenhuma classe de teste.
   - Impacto: qualquer alteração futura pode quebrar o comportamento sem ser percebida.
   - **Prioridade:** Alta (visão de qualidade a médio/longo prazo).

---

### 2.2 Falhas de mínimo e máximo (análise minuciosa dos limites)

#### 2.2.1 Peso – limites mínimos

- **Peso = 0 kg**

  - Deveria ser rejeitado: não existe pessoa sem massa.
  - Comportamento atual: aceito; IMC = 0, o que **não é uma interpretação válida de saúde**.
  - Risco: passar a impressão de IMC “ideal” ou “baixo” quando na verdade é um erro de entrada.

- **Peso negativo (ex.: -10 kg)**

  - Fisicamente impossível.
  - Comportamento atual: aceito; IMC negativo é calculado.
  - Risco: resultados absurdos sem qualquer aviso, comprometendo credibilidade do sistema.

- **Peso extremamente baixo, porém positivo (ex.: 0.1 kg)**
  - Não faz sentido para humanos, mas o programa aceita.
  - Ideal: definir um limite mínimo realista, ex.: 1 kg ou 10 kg, a depender da regra de negócio (adultos, crianças, bebês etc.).

#### 2.2.2 Peso – limites máximos

- **Peso muito alto (ex.: 500 kg, 1.000 kg)**
  - O programa aceita qualquer valor.
  - Embora existam casos clínicos extremos, valores como 1.000 kg são praticamente impossíveis.
  - Recomenda-se impor um limite superior, por exemplo **500 kg**, para evitar erros de digitação (ex.: digitar um zero a mais).
  - Sem validação, o sistema pode calcular IMC gigantesco e ainda classificá-lo apenas como “Obesidade Grau III”, sem indicar que o valor é possivelmente fruto de erro de entrada.

#### 2.2.3 Altura – limites mínimos

- **Altura = 0 m**

  - Causa divisão por zero → `Infinity`.
  - Deveria ser bloqueada com mensagem clara antes do cálculo.

- **Altura negativa (ex.: -1.70 m)**

  - Fisicamente impossível.
  - Aceita pelo programa, gera IMC matematicamente definido, mas sem qualquer sentido físico.

- **Altura extremamente baixa mas > 0 (ex.: 0.01 m)**
  - IMC = peso / (0.01²) → valor absurdamente alto, resultado de erro de digitação.
  - Ideal: estabelecer limite mínimo plausível, por exemplo **0.5 m** para humanos.

#### 2.2.4 Altura – limites máximos

- **Altura = 3.0 m, 3.5 m, 5.0 m**
  - Alturas muito acima do possível para um ser humano.
  - O programa aceita normalmente.
  - Sem validação, pode mascarar erros de digitação (ex.: 1.80 → 18.0).
  - Recomenda-se um limite máximo como **2.5 m ou 3.0 m**.

#### 2.2.5 IMC – comportamento em extremos

- Como consequência da falta de validação de peso e altura, o IMC pode assumir valores:

  - **Muito próximos de 0** (peso muito baixo / altura muito alta).
  - **Negativos** (peso ou altura negativos).
  - **Enormes (IMC > 100)** (peso muito alto / altura muito baixa).

- O método `classificarIMC` pressupõe um domínio de valores de IMC inspirado em tabelas padrão, mas **não verifica se o valor está dentro de uma faixa razoável**. Assim:
  - IMC = -10 → entrará em “Magreza grave”, o que não faz sentido.
  - IMC = 200 → cai na última faixa “Obesidade Grau III”, sem qualquer aviso de que esse valor provavelmente vem de um erro de entrada.

Conclusão: **há graves falhas de domínio nos limites mínimos e máximos de peso, altura e IMC**, que devem ser endereçadas por regras explícitas de validação de entrada.

---

### 2.3 Problemas de usabilidade e robustez

1. **Mensagens de erro inexistentes**

   - Qualquer exceção (`NumberFormatException`, divisão por zero) faz o programa encerrar sem feedback amigável.

2. **Falta de orientação sobre formato de entrada**

   - Usuário brasileiro tende a usar vírgula: `1,75`.
   - O programa exige ponto (`1.75`) sem informar isso.

3. **Interação não resiliente**

   - Errou a digitação? O programa simplesmente quebra; não permite tentar novamente.

4. **Internacionalização e contexto cultural**
   - O código não considera localidade (locale) nem formato regional padrão.
   - Para um produto HealthTech real, isso é crítico.

---

### 2.4 Problemas de design (qualidade de código)

- Responsabilidades misturadas:

  - `programaIMC` faz tudo (entrada, cálculo, saída).
  - Não há separação clara entre **lógica de negócio**, **interface com usuário** e **validação**.

- Uso de dois `Scanner` distintos para `System.in`:

  - Redundante e potencialmente problemático.
  - Melhor usar um único `Scanner` e encapsular a leitura em métodos auxiliares.

- Ausência de testes e de qualquer camada de serviço que facilite o reuso do cálculo em outros contextos (ex.: interface gráfica, API web).

---

## 3. Especificação do Comportamento Esperado (Requisitos)

### 3.1 Requisitos funcionais

1. Ler peso (kg) e altura (m) do usuário via entrada de texto.
2. Aceitar números com **ponto ou vírgula** como separador decimal.
3. Validar que:
   - Peso seja **> 0** e **≤ valor máximo definido** (ex.: 500 kg).
   - Altura seja **≥ limite mínimo plausível** (ex.: 0,5 m) e **≤ limite máximo** (ex.: 2,5–3,0 m).
4. Calcular IMC com a fórmula:

   IMC = peso / (altura × altura)

5. Classificar o IMC em faixas **sem sobreposição e cobrindo todo o domínio válido**.
6. Exibir IMC com duas casas decimais e a classificação textual correspondente.

### 3.2 Regras de entrada e saída

- **Entrada**

  - Peso: número real positivo, com ponto ou vírgula.
  - Altura: número real positivo, com ponto ou vírgula.

- **Saída principal**

  - `Seu índice de massa corporal é: XX.XX kg/m²`
  - `Classificação: <faixa do IMC>.`

- **Saídas em caso de erro** (exemplos)
  - Formato inválido:
    > "Entrada inválida. Digite apenas números, usando vírgula ou ponto como separador decimal."
  - Domínio inválido (peso/altura fora dos limites):
    > "Peso e altura devem ser maiores que zero e dentro de limites plausíveis."

### 3.3 Expectativa de tratamento de erros

- Não encerrar o programa de forma abrupta com stack trace.
- Exibir mensagens claras e permitir **nova tentativa de entrada**.
- Idealmente, encapsular a validação para permitir testes unitários.

---

## 4. Partições Equivalentes para Entrada de Dados

### 4.1 Definição das partições

**Para peso (kg)**

- Válidos: (0, 500]
- Inválidos:
  - ≤ 0 (0, negativos).
  - > 500 (erro provável de digitação ou fora do escopo).

**Para altura (m)**

- Válidos: [0.5, 3.0] (faixa configurável).
- Inválidos:
  - < 0.5 (impossível ou suspeito).
  - ≤ 0 (0 ou negativos).
  - > 3.0 (altura irrealista para humanos).

**Para formato**

- Válidos: `"70"`, `"70.5"`, `"70,5"` (após conversão).
- Inválidos: `"abc"`, `" "`, `"1,2,3"`, `"70kg"`.

### 4.2 Exemplos de casos de teste por categoria

**Válidos**

- CT01: Peso = 70, Altura = 1.75 → IMC ≈ 22,86 → Saudável.
- CT02: Peso = 90, Altura = 1.92 → IMC ≈ 24,43 → Saudável.
- CT03: Peso = 500, Altura = 1.75 → IMC alto, mas dentro do domínio aceito.

**Inválidos (domínio)**

- CT04: Peso = 0, Altura = 1.75 → rejeitar, mensagem de erro.
- CT05: Peso = -10, Altura = 1.75 → rejeitar.
- CT06: Peso = 70, Altura = 0 → rejeitar; evitar divisão por zero.
- CT07: Peso = 70, Altura = 0.49 → rejeitar (abaixo do mínimo).
- CT08: Peso = 70, Altura = 3.5 → rejeitar (acima do máximo).

**Inválidos (formato)**

- CT09: Peso = "abc", Altura = "1.75" → mensagem de formato inválido.
- CT10: Peso = "70,5", Altura = "1,75" → (na versão atual: erro; na versão corrigida: aceito).
- CT11: Peso = "", Altura = "1.75" → erro de entrada vazia.

---

## 5. Análise de Limites (Boundary Value Analysis)

### 5.1 Peso

- Limite inferior teórico: 0 → deve ser inválido.
- Limite imediatamente acima: 0.1 → tecnicamente válido numericamente, mas pode ser filtrado pela regra de negócio.
- Limite superior: 500 kg → válido (limite máximo).
- Acima do limite: 500.1 kg → suspeito; opcionalmente inválido conforme regra.

**Casos de teste típicos de fronteira (peso)**

- CT12: 0 kg (inválido).
- CT13: 0.1 kg (válido ou inválido conforme regra).
- CT14: 500 kg (válido).
- CT15: 500.1 kg (inválido, se definido assim).

### 5.2 Altura

- Limite inferior: 0.5 m (válido).
- Abaixo do limite: 0.49 m (inválido).
- Zero: 0 (inválido, evita divisão por zero).
- Limite superior: 3.0 m (válido).
- Acima do limite: 3.01 m (inválido).

**Casos de teste típicos de fronteira (altura)**

- CT16: 0 m (inválido).
- CT17: 0.49 m (inválido).
- CT18: 0.5 m (válido).
- CT19: 3.0 m (válido).
- CT20: 3.01 m (inválido).

### 5.3 Limites das faixas de IMC

Para garantir que a classificação esteja correta, é necessário testar valores **imediatamente abaixo, exatamente no limite e imediatamente acima** de cada faixa. Exemplos:

- 15.99, 16.0, 16.01
- 16.99, 17.0, 17.01
- 18.49, 18.5, 18.51
- 24.99, 25.0, 25.01
- 29.99, 30.0, 30.01
- 34.99, 35.0, 35.01
- 39.99, 40.0, 40.01

Isso garante que **nenhum valor fique “perdido” entre faixas** e que não haja sobreposição.

---

## 6. Cobertura de Código com JaCoCo

### 6.1 Objetivo

- Medir **quantas linhas, ramos e métodos** de `CalculoIMC` são exercitados pelos testes.
- Detectar caminhos não cobertos, principalmente em:
  - Validação de entradas (que hoje nem existe).
  - Faixas de classificação do IMC.
  - Tratamento de erros (nas versões refatoradas).

### 6.2 Áreas críticas para alta cobertura

1. **`classificarIMC(double imc)`**

   - Todos os `if`/`else if` precisam ser cobertos com valores de teste específicos.

2. **`calcularPeso(double peso, double altura)`**

   - Testar valores normais, limites e casos que levariam a `Infinity`/`NaN`.

3. **Fluxo de entrada/saída (`programaIMC`)**
   - Em uma refatoração, a lógica de leitura/validação deve ser isolada para permitir testes sem depender do console.

### 6.3 Sugestão de metas

- **Cobertura de instruções (lines):** ≥ 90% para lógica de negócio.
- **Cobertura de ramos (branches):** ≥ 80% em `classificarIMC`.

### 6.4 Casos de teste adicionais sugeridos

- Um caso para cada faixa de IMC.
- Casos para limites de IMC (valores das fronteiras).
- Casos de erro de formato e de domínio (na versão refatorada).
