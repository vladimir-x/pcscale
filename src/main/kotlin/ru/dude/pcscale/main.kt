package ru.dude.pcscale

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.*
import java.lang.Exception

import java.util.concurrent.Callable
import kotlin.system.exitProcess


@Command(
    name = "pcscale", mixinStandardHelpOptions = false, version = ["pcscale 1.0"],
    description = ["convert lerua.csv to pc200 format"]
)
class PcScale : Callable<Int> {

    val header =listOf(
        "##@@&&",
        "@",
        "",
        ";<D      код      ;шк                       ;наим                                           ;0;цена  ;0;0;0;0;;;;;;;;;;;;;;10001;;;;"
    )

    @Option(names = ["-i", "--input"], defaultValue = "весовуха.csv", description = ["lerua.csv input file"])
    var inputFile = ""


    @Option(names = ["-o", "--output"], defaultValue = "PCScale.txt", description = ["output pc200"])
    var outputFile = ""


    @Option(names = ["-e", "--encode"], defaultValue = "cp1251", description = ["encoding for lerua.csv and pc200"])
    var encode = "cp1251"


    override fun call(): Int {
        println("current settings: input=$inputFile, output=$outputFile, encode=$encode")
        println("(usage -i lerua.csv -e cp1251 -o PCScale.txt)")

        val r = BufferedReader(InputStreamReader(FileInputStream(inputFile), encode))

        val w = PrintStream(FileOutputStream(outputFile), true, encode)

        println("Start process")

        header.forEach{
            w.println(it)
        }

        var totalLines = 0
        var processedLines = 0
        var skippedLines = 0

        r.lines().skip(1).forEach {
            totalLines++
            val convertLine = convertLine(it)
            if (convertLine != null) {
                processedLines++
                w.println(convertLine)
            } else {
                skippedLines++
            }
        }
        w.close()

        r.close()

        println("Complete $totalLines lines: processed = $processedLines , skipped = $skippedLines")

        return 0
    }

    fun convertLine(line: String): String? {
        try {
            val split = line.split(";")

            if (split[1].isBlank()){
                throw Exception("\"код\" is blank")
            }
            if (split[2].isBlank()){
                throw Exception("\"шк\" is blank")
            }
            if (split[3].isBlank()){
                throw Exception("\"наим\" is blank")
            }
            if (split[4].isBlank()){
                throw Exception("\"цена\" is blank")
            }

            val sb = StringBuilder()
            sb.append("<D            ")
            sb.append(split[1].replace(" ", "").padEnd(14, ' ')).append(";")
            sb.append(split[2].replace(" ", "").padEnd(24, ' ')).append(";")
            sb.append(split[3].padEnd(41, ' ')).append(";")
            sb.append("0;")
            sb.append(split[4].replace(" ", "")).append(";")
            sb.append("0;0;0;0;;;;;10000;;;;;;;;;;;;;;;;;")

            return sb.toString()
        } catch (e: Exception) {
            println("Error on line $line : ${e.message}")
        }
        return null

    }
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(PcScale()).execute(*args))
