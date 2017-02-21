package socketpackage;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DbConn.DataConnection;
import DbConn.EvidenceData;


public class MyReceiver {
	public static final int PORT = 9999;//监听的端口号    
  
    public void init() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(PORT);  
            while (true) {  
                // 一旦有堵塞, 则表示服务器与客户端获得了连接  
                Socket client = serverSocket.accept();  
                // 处理这次连接  
                new HandlerThread(client);  
            }  
        } catch (Exception e) {  
            System.out.println("服务器异常: " + e.getMessage());  
        }  
    }  
  
    private class HandlerThread implements Runnable {  
        private Socket socket;  
        public HandlerThread(Socket client) {  
            socket = client;  
            //多线程处理
            new Thread(this).start();  
        }  
  
        public void run() {  
        	 
            try {  
                // 读取客户端数据  
            	DataInputStream inputStream = null;
            	DataOutputStream outputStream = null;
            	String strInputstream ="";         
            	inputStream =new DataInputStream(socket.getInputStream());                 
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] by = new byte[2048];
            	int n;
                while((n=inputStream.read(by))!=-1){
                	baos.write(by,0,n);     	
                }
                strInputstream = new String(baos.toByteArray());
                socket.shutdownInput();
                baos.close();                               
                // 处理客户端数据  
                //将socket接受到的数据还原为JSONObject
                JSONObject json = new JSONObject(strInputstream);   
                //处理传过来的数据并且写本地数据库
                EvidenceData data=new EvidenceData();
                data.eventtimeTimestamp =Timestamp.valueOf((String)json.get("event_time"));   
                data.idString=CommonAttribute.getHostidString();
                data.contentString=(String)json.get("content");
                data.simhaString=(String)json.get("simhash");
                data.oper_type=(String)json.get("oper_type");
                data.owner_node=(String)json.get("owner_node");
                data.remark=(String)json.get("remark");
                String isSuccess="1";		                
		        Map<String, String> map = new HashMap<String, String>();
		        map.put("isSuccess", isSuccess);
		        json = new JSONObject(map);
			        String jsonString = json.toString();		              
			        outputStream = new DataOutputStream(new BufferedOutputStream (socket.getOutputStream()));   
			        outputStream.writeUTF(jsonString);
			        outputStream.flush();
			        outputStream.close(); 
			//响应之后写数据库
			    DataConnection connection=new DataConnection();
			    connection.insertData(data);
            } catch (Exception e) {  
                System.out.println("服务器 run 异常: " + e.getMessage());  
            } finally {  
                if (socket != null) {  
                    try {  
                        socket.close();  
                    } catch (Exception e) {  
                        socket = null;  
                        System.out.println("服务端 finally 异常:" + e.getMessage());  
                    }  
                }  
            } 
        }  
    }  
}