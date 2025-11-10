//ClientEx.java
//Author: kimyushin
//Date: 2025.11.10.

package myfirstnetworkapp;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ClientEx {
	
	private static final String FILE_NAME = "server_info.dat";
	
	//Protocol define
	//command
	private static final String CMD_QUIT = "QUIT";
    
	//response
	private static final String RES_SUCCESS = "200";
	private static final String RES_ERROR = "400";
	
	//error
	private static final String ERR_DIV_ZERO = "Divided_by_zero";
	private static final String ERR_TOO_MANY_ARGS = "Too_many_arguments";
	private static final String ERR_TOO_FEW_ARGS = "Too_few_arguments";
	private static final String ERR_INVALID = "INVALID";
	
	
	//Client 메인 메서드
	//server 연결 후 사용자 입력받아서 server로 전송
	public static void main(String[] args)
	{
		//설정파일에서 server info 읽기
		ServerConfig config = new ServerConfig(FILE_NAME);
		String host = config.getHost();
        int port = config.getPort();
        
        System.out.println("=================================");
        System.out.println("Calculator Client");
        System.out.println("Server: " + host + ":" + port);
        System.out.println("=================================\n");
        
        Socket socket = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        Scanner scanner = new Scanner(System.in);
        
        try {
        	socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            System.out.println("Connected to Server!");
            System.out.println("Commands: ADD, SUB, MUL, DIV, QUIT");
            System.out.println("Example: ADD 10 20\n");
            
            while(true)
            {
                System.out.print(">>");
                String userInput = scanner.nextLine();
                
                //빈 입력은 무시해줌
                if (userInput.trim().isEmpty()) {
                    continue;
                }
           
                out.write(userInput + "\n");
                out.flush();
                
                //QUIT 처리
                if (userInput.trim().equalsIgnoreCase(CMD_QUIT)) {
                    String response = in.readLine();
                    System.out.println("Server: " + response);
                    break;
                }
                
                String response = in.readLine();
                
                if (response == null) {
                    System.out.println("Server disconnected");
                    break;
                }
                
                displayResponse(response);
            }
        }catch (IOException e)
        {
        	System.out.println("Client error: " + e.getMessage());
        } finally {
        	try {
        		//다 쓰면 종료
                if (scanner != null) scanner.close();
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
                System.out.println("Connection closed");
            } catch (IOException e) {
                System.out.println("Error closing connection");
            }
        }
	}
	
	private static void displayResponse(String response) {
		//응답을 Response Code, Data로 분리
        String[] parts = response.split(" ", 2);
        
        if (parts.length < 1) {
            System.out.println("Invalid response");
            return;
        }
        
        String responseCode = parts[0];
        
        //Response Code에 따른 처리
        if (responseCode.equals(RES_SUCCESS)) {

            if (parts.length == 2) {
                System.out.println("Result: " + parts[1]);
            }
        } else if (responseCode.equals(RES_ERROR)) {

            if (parts.length == 2) {
                String errorType = parts[1];
                System.out.println("Error: " + getErrorMessage(errorType));
            }
        } else {
            System.out.println("Unknown response: " + response);
        }
    }
	
	//error code를 문자열로
	private static String getErrorMessage(String errorCode) {
        switch (errorCode) {
            case ERR_DIV_ZERO:
                return "Cannot divide by zero";
            case ERR_TOO_MANY_ARGS:
                return "Too many arguments";
            case ERR_TOO_FEW_ARGS:
                return "Too few arguments";
            case ERR_INVALID:
                return "Invalid command";
            default:
                return errorCode;
        }
    }
}


//server 설정 읽는 class
class ServerConfig {
    private String host;
    private int port;
    
    //기본값 파일이 없거나 여는데 실패하면 사용됨
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9999;
    
    //생성자
    public ServerConfig(String configFile) {
        loadConfig(configFile);
    }
    
    //실질적으로 설정파일 읽는 메서드
    private void loadConfig(String configFile) {
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new FileReader(configFile));
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                //key=value로 파싱
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    if (key.equalsIgnoreCase("host") || key.equalsIgnoreCase("ip")) {
                        this.host = value;
                    } 
                    else if (key.equalsIgnoreCase("port")) {
                        this.port = Integer.parseInt(value);
                    }
                }
            }
            
            System.out.println("Config file loaded: " + configFile);
            
        } catch (FileNotFoundException e) {
            //파일이 없으면 기본값 사용
            System.out.println("Config file not found. Using default: " 
                + DEFAULT_HOST + ":" + DEFAULT_PORT);
            this.host = DEFAULT_HOST;
            this.port = DEFAULT_PORT;
        } catch (IOException | NumberFormatException e) {
            //파일 읽지못해도 기본값 사용
            System.out.println("Failed to read config file. Using default: " 
                + DEFAULT_HOST + ":" + DEFAULT_PORT);
            this.host = DEFAULT_HOST;
            this.port = DEFAULT_PORT;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // 무시
            }
        }
    }
    
    //host 정보 반환
    public String getHost() {
        return host;
    }
    
    //port 정보 반환
    public int getPort() {
        return port;
    }
}
