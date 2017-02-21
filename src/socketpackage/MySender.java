package socketpackage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import DbConn.EvidenceData;

import java.io.BufferedInputStream;

public class MySender {
	private static final String IP_ADDR = null;//本地地址，此地址在程序启动的时候由运行参数传入
	public static final int PORT = 9999;//服务器端口号 ，receiver的为9999
	private static ArrayList<String> _destinyAddrsArrayList=null;//这个列表每次从memberservice询问
	
	public static int send(byte[] jsonByte){
		int isRegSuccess = 0;
		 while (true) {  
	        	Socket socket = null;
	        	try {
	        		for (String IP_ADDR : _destinyAddrsArrayList) {
						socket = new Socket(IP_ADDR, PORT);  
					DataOutputStream outputStream = null;
		        	outputStream = new DataOutputStream(socket.getOutputStream());

                    System.out.println("发的数据长度为:"+jsonByte.length);
		        	outputStream.write(jsonByte);
		        	outputStream.flush();
		            System.out.println("传输数据完毕");
		            socket.shutdownOutput();
		            
		            //读取服务器端数据  
		            DataInputStream inputStream = null;
		            String strInputstream ="";
		            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
		            strInputstream=inputStream.readUTF();
	               // System.out.println("输入信息为："+strInputstream);
	                JSONObject js = new JSONObject(strInputstream);
		            System.out.println(js.get("isSuccess"));
		            isRegSuccess=Integer.parseInt((String) js.get("isSuccess")); 
		            // 如接收到 "OK" 则断开连接  
		            if (js != null) {  
		               // System.out.println("客户端将关闭连接");  
		                Thread.sleep(500);  
		                break;  
		            }  }
	        		if (socket != null) {
	        			try {
							socket.close();
						} catch (IOException e) {
							socket = null; 
							System.out.println("客户端 finally 异常:" + e.getMessage()); 
						}
	        		}
		            
	        	} catch (Exception e) {
	        		System.out.println("客户端异常:" + e.getMessage()); 
	        		break;
	        	} finally {
	        		if (socket != null) {
	        			try {
							socket.close();
						} catch (IOException e) {
							socket = null; 
							System.out.println("客户端 finally 异常:" + e.getMessage()); 
						}
	        		}
	        	}
	        }
		 return isRegSuccess;	
	}
	
    public static byte[] GenerateEvidenceData(EvidenceData data)
    {
    	Map<String, String> map = new HashMap<String, String>();
    	//map.put("myidentity",CommonAttribute.getHostidString());myidentity的内容为接收方的hostid
    	map.put("event_time",data.eventtimeTimestamp+"");
    	map.put("owner_node", CommonAttribute.getHostidString());
    	map.put("oper_type",data.oper_type);
    	map.put("simhash", data.simhaString);
    	map.put("content",data.contentString);
    	map.put("remark", data.remark);
    	//将json转化为String类型  
    	JSONObject json = new JSONObject(map);
    	String jsonString = "";
        jsonString = json.toString();
    	//将String转化为byte[]
    	//byte[] jsonByte = new byte[jsonString.length()+1];
    	byte[] jsonByte = jsonString.getBytes();
    	//返回封装json对象的byte[]对象
		return jsonByte;
    	
    }
}