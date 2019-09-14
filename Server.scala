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

	def nonblockingRead(in: BufferedReader): Option[String] = {
		if(in.ready()) Some(in.readLine()) else None
	}

	def doChat(user: User) : Unit = {
		nonblockingRead(user.in).foreach { input =>
			if(input == ":q"){
				user.sock.close()
				users -= user.name
			}
			else{
				println(user.name+" disse: "+input)	//this
				for((name, u) <- users){
					u.out.println(user.name+" disse: "+input)		
				}			
			}
		}  
	}  
}
