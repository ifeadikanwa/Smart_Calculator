import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val numOfElements = scanner.nextInt()
    val arr = mutableListOf<Int>()
    repeat(numOfElements) {
        arr.add(scanner.nextInt())
    }

    val numOfShifts = scanner.nextInt() % arr.size

    repeat(numOfShifts) {
        val temp = arr[0]
        for (i in arr.size - 1 downTo 0) {
            when (i) {
                arr.size - 1 -> {
                    arr[0] = arr[i]
                }
                0 -> {
                    arr[i + 1] = temp
                }
                else -> {
                    arr[i + 1] = arr[i]
                }
            }

        }
    }

    for (num in arr) {
        print("$num ")
    }
}
