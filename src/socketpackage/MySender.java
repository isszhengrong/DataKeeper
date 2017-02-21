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
	private static final String IP_ADDR = null;//���ص�ַ���˵�ַ�ڳ���������ʱ�������в�������
	public static final int PORT = 9999;//�������˿ں� ��receiver��Ϊ9999
	private static ArrayList<String> _destinyAddrsArrayList=null;//����б�ÿ�δ�memberserviceѯ��
	
	public static int send(byte[] jsonByte){
		int isRegSuccess = 0;
		 while (true) {  
	        	Socket socket = null;
	        	try {
	        		for (String IP_ADDR : _destinyAddrsArrayList) {
						socket = new Socket(IP_ADDR, PORT);  
					DataOutputStream outputStream = null;
		        	outputStream = new DataOutputStream(socket.getOutputStream());

                    System.out.println("�������ݳ���Ϊ:"+jsonByte.length);
		        	outputStream.write(jsonByte);
		        	outputStream.flush();
		            System.out.println("�����������");
		            socket.shutdownOutput();
		            
		            //��ȡ������������  
		            DataInputStream inputStream = null;
		            String strInputstream ="";
		            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
		            strInputstream=inputStream.readUTF();
	               // System.out.println("������ϢΪ��"+strInputstream);
	                JSONObject js = new JSONObject(strInputstream);
		            System.out.println(js.get("isSuccess"));
		            isRegSuccess=Integer.parseInt((String) js.get("isSuccess")); 
		            // ����յ� "OK" ��Ͽ�����  
		            if (js != null) {  
		               // System.out.println("�ͻ��˽��ر�����");  
		                Thread.sleep(500);  
		                break;  
		            }  }
	        		if (socket != null) {
	        			try {
							socket.close();
						} catch (IOException e) {
							socket = null; 
							System.out.println("�ͻ��� finally �쳣:" + e.getMessage()); 
						}
	        		}
		            
	        	} catch (Exception e) {
	        		System.out.println("�ͻ����쳣:" + e.getMessage()); 
	        		break;
	        	} finally {
	        		if (socket != null) {
	        			try {
							socket.close();
						} catch (IOException e) {
							socket = null; 
							System.out.println("�ͻ��� finally �쳣:" + e.getMessage()); 
						}
	        		}
	        	}
	        }
		 return isRegSuccess;	
	}
	
    public static byte[] GenerateEvidenceData(EvidenceData data)
    {
    	Map<String, String> map = new HashMap<String, String>();
    	//map.put("myidentity",CommonAttribute.getHostidString());myidentity������Ϊ���շ���hostid
    	map.put("event_time",data.eventtimeTimestamp+"");
    	map.put("owner_node", CommonAttribute.getHostidString());
    	map.put("oper_type",data.oper_type);
    	map.put("simhash", data.simhaString);
    	map.put("content",data.contentString);
    	map.put("remark", data.remark);
    	//��jsonת��ΪString����  
    	JSONObject json = new JSONObject(map);
    	String jsonString = "";
        jsonString = json.toString();
    	//��Stringת��Ϊbyte[]
    	//byte[] jsonByte = new byte[jsonString.length()+1];
    	byte[] jsonByte = jsonString.getBytes();
    	//���ط�װjson�����byte[]����
		return jsonByte;
    	
    }
}