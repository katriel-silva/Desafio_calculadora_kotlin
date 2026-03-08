fun main (){
    println("Digite uma operação matematica!! (com no minimo 9 caracteres)")
    val calculoDigitado = readlnOrNull() ?: "" // O ?: garante basecamente se a pessoa nao escreveu nada, a variavel vai "guardar um valor vazio"

    //o ".length" conta o tamanho do texto. Se for menor que 9, ele avisa o erro
    if (calculoDigitado.length < 9){
        println("O calculo tem menos de 9 caracteres!")
        return //basicamente encerra o codigo
    }

    // ".replace(" ", ""):"  Pega o texto e troca todos os espaços em branco (" ") por nada (""). Isso junta tudo, transformando 5 + 2 em 5+2.
    //O "Regex" acha qualquer número colado em um parêntese e insere um * no meio.
    val codigoLimpo = calculoDigitado.replace("", " ").replace(Regex("(\\d)\\(照"), "$1*(")

    // TRY / CATCH: A REDE DE SEGURANÇA
    // Serve para o programa não "morrer" se o usuário errar.
    // TRY: "Tenta" rodar o código perigoso (cálculos).
    // CATCH: "Captura" o erro. Se o TRY falhar, o código pula
    //pra cá em vez de travar o computador.
    try {
        val resultado = resolver(codigoLimpo)
        println("Resultado: $resultado")
    }catch (e: Exception) {
        println("Erro na expressão: Verifique os sinais e parênteses.")
    }
}

fun resolver(expressao: String): Double{
    val exp = expressao

    // Tirar parênteses que envolvem toda a conta. Ex: (5+5) -> 5+5
    if (exp.startsWith("(") && exp.endsWith(")")) {
        // Só remove se o parêntese do começo for o par do último
        if (parenteseEquilibrado(exp.substring(1, exp.length - 1))) {
            return resolver(exp.substring(1, exp.length - 1))
        }
    }

    // Procurar o operador (+, -, *, /)
    // Precisamos achar primeiro o + ou -, pois na matemática resolvemos o * e / antes.
    // Como o código quebra a conta, o que ele achar PRIMEIRO é o que será resolvido por ÚLTIMO.

    var indiceOperador = -1
    var prioridadeAtual = 0 // 1 para +/-, 2 para */ /

    var nivelParenteses = 0

    // Varremos a conta de trás para frente
    for (i in exp.indices.reversed()) {
        val char = exp[i]

        // Se estivermos dentro de um parêntese, ignoramos o que está lá dentro por enquanto
        if (char == ')') nivelParenteses++
        else if (char == '(') nivelParenteses--

        if (nivelParenteses == 0) {
            if (char == '+' || char == '-') {
                indiceOperador = i
                prioridadeAtual = 1
                break // Soma e subtração são prioridade para quebrar a conta
            } else if ((char == '*' || char == '/') && prioridadeAtual < 2) {
                indiceOperador = i
                prioridadeAtual = 2
            }
        }
    }

    // achou um operador, corta a conta em duas e resolve cada lado
    if (indiceOperador != -1) {
        val esquerda = exp.substring(0, indiceOperador)
        val direita = exp.substring(indiceOperador + 1)
        val op = exp[indiceOperador]

        return when (op) {
            '+' -> resolver(esquerda) + resolver(direita)
            '-' -> resolver(esquerda) - resolver(direita)
            '*' -> resolver(esquerda) * resolver(direita)
            '/' -> resolver(esquerda) / resolver(direita)
            else -> 0.0
        }
    }

    // Se não há operadores, sobrou apenas o número
    return exp.toDouble()
}

// Função auxiliar para checar se os parênteses estão fechando certinho
fun parenteseEquilibrado(texto: String): Boolean {
    var balanco = 0
    for (char in texto) {
        if (char == '(') balanco++
        if (char == ')') balanco--
        if (balanco < 0) return false
    }
    return balanco == 0
}