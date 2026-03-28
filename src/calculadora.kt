fun main() {
    println("Digite uma operação matemática!! (com no mínimo 9 caracteres)")
    // Pega o que o usuário digitou. O "?:" garante que não dê erro se vier vazio.
    val calculoDigitado = readlnOrNull() ?: ""

    // Conta o tamanho do texto. Se for menor que 9, avisa o erro e para tudo.
    if (calculoDigitado.length < 9) {
        println("Erro: O cálculo tem menos de 9 caracteres!")
        return // Encerra o programa aqui
    }

    // ".replace(" ", "")" tira todos os espaços. Ex: "5 + 2" vira "5+2"
    // O ".replace(Regex...)" acha casos tipo "2(5)" e coloca um vezes no meio: "2*(5)"
    val codigoLimpo = calculoDigitado.replace(" ", "").replace(Regex("(\\d)\\("), "$1*(")

    // O TRY tenta rodar o cálculo. Se o usuário digitar algo muito louco (tipo letras),
    // o CATCH segura o erro e não deixa o programa travar feio na tela.
    try {
        val resultado = resolver(codigoLimpo)
        println("Resultado final: $resultado")
    } catch (e: Exception) {
        println("Erro na expressão: Verifique se você digitou a conta certinha.")
    }
}

// Essa é a função que corta a conta em pedaços até resolver tudo
fun resolver(conta: String): Double {

    // 1. TIRAR PARÊNTESES INÚTEIS
    // Se a conta inteira estiver dentro de parênteses tipo "(5+2)", a gente tira eles
    if (conta.startsWith("(") && conta.endsWith(")")) {
        val mioloDaConta = conta.substring(1, conta.length - 1)

        // Só tira se não for desmanchar a conta (ex: "(5)+(2)" não pode tirar das pontas)
        if (parenteseEquilibrado(mioloDaConta)) {
            return resolver(mioloDaConta) // Roda a função de novo, mas sem os parênteses
        }
    }

    // Variável para saber se estamos lendo algo dentro de um parêntese
    var nivelParenteses = 0

    // 2. PROCURAR SOMA (+) OU SUBTRAÇÃO (-)
    // A gente lê a conta de trás pra frente.
    for (i in conta.indices.reversed()) {
        val letra = conta[i]

        if (letra == ')') nivelParenteses++
        if (letra == '(') nivelParenteses--

        // Só corta a conta se a gente não estiver dentro de um parêntese
        if (nivelParenteses == 0 && (letra == '+' || letra == '-') && i > 0) {

            // Regrinha pra não cortar se for só um número negativo (Ex: 5 * -2)
            val letraAntes = conta[i - 1]
            if (letraAntes != '*' && letraAntes != '/' && letraAntes != '(') {

                // Corta a conta no meio! Pega o lado esquerdo e o lado direito do sinal
                val ladoEsquerdo = conta.substring(0, i)
                val ladoDireito = conta.substring(i + 1)

                // Resolve cada lado separado e junta
                if (letra == '+') return resolver(ladoEsquerdo) + resolver(ladoDireito)
                if (letra == '-') return resolver(ladoEsquerdo) - resolver(ladoDireito)
            }
        }
    }

    // 3. PROCURAR MULTIPLICAÇÃO (*) OU DIVISÃO (/)
    // Se o código chegou até aqui, é porque não achou + nem -. Então procura * ou /
    nivelParenteses = 0
    for (i in conta.indices.reversed()) {
        val letra = conta[i]

        if (letra == ')') nivelParenteses++
        if (letra == '(') nivelParenteses--

        if (nivelParenteses == 0 && (letra == '*' || letra == '/')) {
            val ladoEsquerdo = conta.substring(0, i)
            val ladoDireito = conta.substring(i + 1)

            if (letra == '*') return resolver(ladoEsquerdo) * resolver(ladoDireito)
            if (letra == '/') return resolver(ladoEsquerdo) / resolver(ladoDireito)
        }
    }

    // 4. SOBROU SÓ O NÚMERO
    // Se não achou nenhum sinal de +, -, * ou /, é porque a string só tem um número! (Ex: "5")
    return conta.toDouble()
}

// Função auxiliar pra checar se a pessoa fechou todos os parênteses que abriu
fun parenteseEquilibrado(texto: String): Boolean {
    var balanco = 0
    for (letra in texto) {
        if (letra == '(') balanco++
        if (letra == ')') balanco--
        if (balanco < 0) return false // Fechou um parêntese antes de abrir
    }
    return balanco == 0 // Devolve 'true' se deu tudo certo
}