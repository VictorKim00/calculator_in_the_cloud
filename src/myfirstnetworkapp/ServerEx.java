//ServerEx.java
//Author: kimyushin
//Date: 2025.11.10.

package myfirstnetworkapp;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//main class(ThreadPool, create socket, accept())
//Calculator class(4 arithmetic operations(ADD, SUB, MUL, DIV))

public class ServerEx {
	
	private static final int PORT = 9999;
	private static final int THREAD_POOL_NUM = 20;
	
	//server 메인 메서드
	//serverSocket 생성하고 클라이언트 연결을 지속적으로 받아들임
	public static void main(String[] args)
	{
		//ThreadPool 생성(최대 20개 클라이언트 처리가능)
		ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_NUM);
		ServerSocket listener = null;
		try {
			listener = new ServerSocket(PORT);
			System.out.println("=================================");
            System.out.println("Calculator Server Started");
            System.out.println("Port: " + PORT);
            System.out.println("ThreadPool Size: " + THREAD_POOL_NUM);
            System.out.println("=================================\n");
			
			while(true)
			{
				//socket 대기
				System.out.println("Waiting...");
				Socket socket = listener.accept();
				System.out.println("The Calculator server is running...");
				
				//ThreadPool에 새 클라이언트 처리 작업 제출
				threadPool.execute(new ClientHandler(socket));
			}
		} catch(IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if(listener != null)
					listener.close();
				threadPool.shutdown();
			} catch (IOException e) {
				System.out.println("Error" + e.getMessage());
			}
		}
	}
	
}


//각 클라이언트를 처리하는 Handler class
//Runnable 인터페이스를 구현하여 ThreadPool에서 실행됨
class ClientHandler implements Runnable {
	private Socket socket;
	
	//protocol define
	//commands
	private static final String CMD_ADD = "ADD";
	private static final String CMD_SUB = "SUB";
	private static final String CMD_MUL = "MUL";
	private static final String CMD_DIV = "DIV";
	private static final String CMD_QUIT = "QUIT";
    
	
	//response
	private static final String RES_SUCCESS = "200";
	private static final String RES_ERROR = "400";
	
	//error
	private static final String ERR_DIV_ZERO = "Divided_by_zero";
	private static final String ERR_TOO_MANY_ARGS = "Too_many_arguments";
	private static final String ERR_TOO_FEW_ARGS = "Too_few_arguments";
	private static final String ERR_INVALID = "INVALID";
	
	//생성자
	public ClientHandler(Socket socket)
	{
		this.socket = socket;
	}
	
	//Thread 실행 메서드
	//Client로부터 요청을 받아 처리 -> 응답 전송
	@Override
	public void run()
	{
		String threadName = Thread.currentThread().getName();
		
		BufferedReader in = null;
		BufferedWriter out = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			while(true)
			{
				String inputMessage = in.readLine();
				if(inputMessage == null) {
			        System.out.println("Client disconnected unexpectedly");
			        break;
			    }
				
				if(inputMessage.trim().equalsIgnoreCase(CMD_QUIT)) 
				{
					out.write("BYE\n");					
					out.flush();
					System.out.println("Client quit");
					break;
				}
				String response = processRequest(inputMessage);
				
				out.write(response+"\n");
				out.flush();
			}
		} catch(IOException e)
		{
			System.out.println("error" + e.getMessage());
		} finally {
			try {
				if(in != null)
					in.close();
				if(out != null)
					out.close();
				if(socket != null)
					socket.close();
				System.out.println("Client disconnected");
			} catch (IOException e)
			{
				System.out.println("Error" + e.getMessage());
			}
		}
	}
	
	//Client 요청 파싱하고 검증
	private String processRequest(String request)
	{
		//공백으로 토큰 분리
		StringTokenizer st = new StringTokenizer(request.trim(), " ");
		
		int tokenCount = st.countTokens();
        
        if (tokenCount < 3) {
            return RES_ERROR + " " + ERR_TOO_FEW_ARGS;
        }
        if (tokenCount > 3) {
            return RES_ERROR + " " + ERR_TOO_MANY_ARGS;
        }
        
        String opcode = st.nextToken();
        String op1 = st.nextToken();
		String op2 = st.nextToken();
		
		return calculate(opcode, op1, op2);
	}
	
	//실제 계산 수행
	private String calculate(String command, String op1str, String op2str)
	{
		try {
			double op1 = Double.parseDouble(op1str);
            double op2 = Double.parseDouble(op2str);
            double result = 0;
            
            switch(command) {
            	case CMD_ADD:
            		result = op1 + op2;
            		break;
            	case CMD_SUB:
            		result = op1 - op2;
            		break;
            	case CMD_MUL:
            		result = op1 * op2;
            		break;
            	case CMD_DIV:
            		if(op2 == 0)
            		{
            			return RES_ERROR + " " + ERR_DIV_ZERO;
            		}
            		result = op1 / op2;
            		break;
            	default:
            		return RES_ERROR + " " + ERR_INVALID;
            }
            //결과가 정수면 .0없이 즉 정수로, 소수면 소수점 포함해서 출력
            if(result == (int)result) {
                return RES_SUCCESS + " " + (int)result; 
            } else {
                return RES_SUCCESS + " " + result;       
            }
            
		} catch (NumberFormatException e)
		{
			 return RES_ERROR + " " + ERR_INVALID;
		}
	}
	
}
