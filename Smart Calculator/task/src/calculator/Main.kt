package calculator

import java.math.BigInteger
import java.util.*

val scanner = Scanner(System.`in`)

val variableStorage = mutableMapOf<String, BigInteger>()

fun main() {
    var go = true

    while (go){
        val userInput = scanner.nextLine()

        if (userInput == ""){
            continue
        }
        else if(userInput.substring(0,1) == "/"){
          go = processCommand(userInput)
        }
        else if (userInput.trim().matches(Regex("[a-zA-Z]+"))){
            printVariableValue(userInput)
        }
        else if(userInput.contains("=")){
            if(isValidVariableSyntax(userInput)){
                processVariableInput(userInput)
            }
        }
        else{
            if(isValidExpression(userInput)){
                println(calculator(userInput.split(" ")))
            }
            else {
                println("Invalid expression")
            }
        }

    }
}

fun printVariableValue(userInput: String) {
    if (variableStorage.containsKey(userInput.trim())){
        println(variableStorage[userInput.trim()])
    }
    else {
        println("Unknown variable")
    }
}

fun processVariableInput(userInput: String) {
    val identifier = userInput.substringBefore("=").trim()
    val value = userInput.substringAfter("=").trim()

    if(isNumber(value)){
        variableStorage[identifier] = value.toBigInteger()
    }
    else{
        if (variableStorage.containsKey(value)){
            variableStorage[identifier] = variableStorage[value]!!
        }
        else{
            println("Unknown variable")
        }
    }
}

fun isValidVariableSyntax(userInput: String): Boolean {
    val identifierPattern = Regex("\\s*[a-zA-Z]+\\s*")
    val valuePattern = Regex("\\s*[+-]*[0-9]+\\s*|\\s*[a-zA-Z]+\\s*")

    val identifier = userInput.substringBefore("=")
    val value = userInput.substringAfter("=")

    return if (!identifierPattern.matches(identifier)) {
        println("Invalid identifier")
        false
    }
    else if(!valuePattern.matches(value)){
        println("Invalid assignment")
        false
    }
    else{
        true
    }

}

fun isValidExpression(userInput: String): Boolean {
    val pattern = Regex("[+-]*\\d+|([+-]*[()]*(\\d+|[a-zA-Z]+)(\\s+([+-]+|\\*|/)\\s+[+-]*[(]*(\\d+|[a-zA-Z]+)[)]*)+)")

    if((userInput.contains("(") && !userInput.contains(")")) || (userInput.contains(")") && !userInput.contains("("))){
        return false
    }
    else {
        return pattern.matches(userInput)
    }

}


fun calculator(userInput: List<String>): BigInteger {
    val digitOrVariable = Regex("\\d+|[a-zA-Z]+")
    var formattedExpression = ""

    if (userInput.size == 1){
        formattedExpression = userInput[0]
    }
    else {
        for(input in userInput){
            formattedExpression +=
                if (input.matches(digitOrVariable)){
                input
                }
                else {
                checkForOperator(input)
                }
        }
    }

    val postfix = convertToPostfix(formattedExpression)

    return calculatePostfix(postfix)

}

fun calculate(firstNum: BigInteger, secondNum: BigInteger, operator: Char) : BigInteger{
    return when(operator){
        '+' -> secondNum+firstNum
        '-' -> secondNum-firstNum
        '*' -> secondNum*firstNum
        else -> secondNum/firstNum
    }
}

fun checkForOperator(str: String) :  String{
    val arr = str.toCharArray()
    return when {
        arr[0] == '+' -> {
            "+"
        }
        arr[0] == '*' -> {
            "*"
        }
        arr[0] == '/' -> {
            "/"
        }
        arr[0] == '-' -> {
            if (isEven(arr.size)) "+" else "-"
        }
        arr[0] == '(' ->
            str
        isNumber(arr[0].toString()) ->
            str
        else -> {
           ""
        }
    }
}

fun calculatePostfix(expression: String): BigInteger {
    val stack = Stack<BigInteger>()

    val inputs = expression.split(" ")
    for(input in inputs){
        when {
            isNumber(input) -> stack.push(input.toBigInteger())
            input.matches(Regex("[a-zA-Z]+")) -> stack.push(variableStorage[input])
            else -> {
                val firstNum = stack.pop()
                val secondNum = stack.pop()
                val result = calculate(firstNum, secondNum, input.trim().first())
                stack.push(result)
            }
        }
    }

    return stack.pop()
}

fun convertToPostfix(expression: String) : String{
    val digitOrVariable = Regex("\\d|[a-zA-Z]")
    var result = ""
    val stack = Stack<Char>()

    for(character in expression){
        when {
            digitOrVariable.matches(character.toString()) -> {
                result += character
            }

            character == '(' -> {
                stack.push(character)
            }

            character == ')' -> {
                while (stack.isNotEmpty() && stack.peek() != '('){
                    result += " " + stack.pop()
                }
                stack.pop()
            }
            else -> {
                result+= " "

                if (stack.isEmpty() || stack.peek() == '('){
                    stack.push(character)
                    continue
                }
                else if (stack.isNotEmpty() && precedence(character) > precedence(stack.peek())){
                    stack.push(character)
                    continue
                }

                while (stack.isNotEmpty() && precedence(character) <= precedence(stack.peek())) {
                    result += stack.pop() + " "
                }
                stack.push(character)
            }
        }
    }

    while (stack.isNotEmpty() && stack.peek() != '('){
        result += " " + stack.pop()
    }

    return result.trimEnd()
}

fun precedence(operator: Char) : Int {
    return when(operator) {
        '+' -> 1
        '-' -> 1
        '*' -> 2
        '/' -> 2
        else -> -1
    }
}

fun isNumber(str: String) : Boolean {
    return try {
        str.toBigInteger()
        true
    }
    catch (e: NumberFormatException){
        false
    }
}

fun isEven(number: Int) : Boolean{
    return number % 2 == 0
}

fun processCommand(str: String) : Boolean{
    return when(str){
        "/exit" -> {
            println("Bye!")
            false
        }
        "/help" -> {
            println("The program calculates user input")
            true
        }
        else -> {
            println("Unknown command")
            true
        }
    }
}
