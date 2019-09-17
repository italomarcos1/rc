package rc1

import java.net.Socket //bibioteca de socket
import java.io.PrintStream
import java.io.InputStreamReader
import java.io.BufferedReader
import io.StdIn._ //import pra efetuar I/O
import scala.concurrent.Future //importação do Future para rodar de forma concorrente
import scala.concurrent.ExecutionContext.Implicits.global //importação extra pro Future funcionar

/// <summary>
/// Objeto que atua como ponto-de-entrada (função Main) da aplicação.
    /// <param name="historico">Descrição do movimento.</param>
    /// <param name="valor">Valor do movimento (utilizar valores neg
/// </summary>
object Client extends App {
  println("Criando socket...")
  val sock = new Socket("localhost", 8080) //cria o socket no localhost porta 8080
  println("Socket criado.")
  val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
  val out = new PrintStream(sock.getOutputStream)
	var stopped = false;   
	
	Future{
		while(!stopped){
			val p = in.readLine()
			println(p)
		}
	}
	
	var input = ""
	while (input != ":quit"){
		val input = readLine
		out.println(input)
	}
	sock.close() //fecha o socket
	stopped = true;
}
