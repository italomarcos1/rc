package rc1

import java.net.ServerSocket
import java.net.Socket
import java.io.PrintStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Thread
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._


/// <summary>
/// Objeto que atua como ponto-de-entrada (função Main) da aplicação.
    /// <param name="historico">Descrição do movimento.</param>
    /// <param name="valor">Valor do movimento (utilizar valores neg
/// </summary>
object Server extends App {

	case class User(name: String, sock: Socket, in: BufferedReader, out: PrintStream)
	val users = new ConcurrentHashMap[String, User].asScala	
	
	Future { 
		println("Aceitando conexões...")		
		checkConnections

	}

	while(true){
		for((name, user) <- users){
			doChat(user)			
		}
	}

	/// <summary>
    /// Método responsável por receber novas conexões (requisições) dos Clients. Roda em uma instância em segundo-plano (Future) de modo que sua execução não causa bloqueio nas operações do servidor.
	/// </summary>
	def checkConnections(): Unit = {
			val ss = new ServerSocket(8080)
			while(true){
		  	val sock = ss.accept()
			println("Socket conectado: "+sock)	
		  	val in = new BufferedReader(new InputStreamReader(sock.getInputStream))
  			val out = new PrintStream(sock.getOutputStream)
				Future{
					out.println("Informe seu nome:")
					val name = in.readLine
					val user = User(name, sock, in, out)
					users += name -> user
									
				}
			}
	}

	/// <summary>
    /// Método responsável por checar se o Client enviou algum texto, usando uma cláusua if-else simples.
	/// </summary>
    /// <param name="in">O objeto BufferedReader recebe a mensagem enviada (string) pelo Client.</param>
	def nonblockingRead(in: BufferedReader): Option[String] = {
		if(in.ready()) Some(in.readLine()) else None
	}

	/// <summary>
    /// Método responsável por enviar as mensagens de um Client para os outros Clients. Invoca o método nonBlockingRead para iterar os usuários que enviaram alguma mensagem.
	/// </summary>
    /// <param name="user">O objeto User recebe os dados do usuário (Client) para enviar a mensagem usando seus dados como remetente da mensagem.</param>
	def doChat(user: User) : Unit = {
		nonblockingRead(user.in).foreach { input =>
			if(input == ":q"){
				user.sock.close()
				users -= user.name
			}
			else{
				println("Um cliente enviou: "+input)	//this
				for((name, u) <- users){
					u.out.println(user.name+" disse: "+input)		
				}			
			}
		}  
	}  
}
