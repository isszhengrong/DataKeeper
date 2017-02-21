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
	public static final int PORT = 9999;//�����Ķ˿ں�    
  
    public void init() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(PORT);  
            while (true) {  
                // һ���ж���, ���ʾ��������ͻ��˻��������  
                Socket client = serverSocket.accept();  
                // �����������  
                new HandlerThread(client);  
            }  
        } catch (Exception e) {  
            System.out.println("�������쳣: " + e.getMessage());  
        }  
    }  
  
    private class HandlerThread implements Runnable {  
        private Socket socket;  
        public HandlerThread(Socket client) {  
            socket = client;  
            //���̴߳���
            new Thread(this).start();  
        }  
  
        public void run() {  
        	 
            try {  
                // ��ȡ�ͻ�������  
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
                // ����ͻ�������  
                //��socket���ܵ������ݻ�ԭΪJSONObject
                JSONObject json = new JSONObject(strInputstream);   
                //�������������ݲ���д�������ݿ�
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
			//��Ӧ֮��д���ݿ�
			    DataConnection connection=new DataConnection();
			    connection.insertData(data);
            } catch (Exception e) {  
                System.out.println("������ run �쳣: " + e.getMessage());  
            } finally {  
                if (socket != null) {  
                    try {  
                        socket.close();  
                    } catch (Exception e) {  
                        socket = null;  
                        System.out.println("����� finally �쳣:" + e.getMessage());  
                    }  
                }  
            } 
        }  
    }  
}