package DbConn;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import socketpackage.CommonAttribute;

public class DataConnection {
	
	private Connection _conn=null;
	private Map<String,Connection> _connMap=new HashMap();
	private ArrayList<String> ipaddrsArrayList=new ArrayList<String>();
	 // MySQL��JDBC URL��д��ʽ��jdbc:mysql://�������ƣ����Ӷ˿�/���ݿ������?����=ֵ
    // ������������Ҫָ��useUnicode��characterEncoding
    // ִ�����ݿ����֮ǰҪ�����ݿ����ϵͳ�ϴ���һ�����ݿ⣬�����Լ�����
    // �������֮ǰ��Ҫ�ȴ���javademo���ݿ�
    String conn1 = "jdbc:mysql://";
    String conn2=":3306/datastore?"
            + "user=datauser&password=123456&useUnicode=true&characterEncoding=UTF8";
    public DataConnection() throws ClassNotFoundException
    {
    	initConnection();
    }
    private void initConnection() throws ClassNotFoundException
    {
    	loadProperty();
    	try { 
	         Class.forName("com.mysql.jdbc.Driver");// ��̬����mysql����
	         // or:
	         // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
	         // or��
	         // new com.mysql.jdbc.Driver();

	         System.out.println("successfully load driver");
	         // һ��Connection����һ�����ݿ�����
	         for (String addr : ipaddrsArrayList) {
				_conn = DriverManager.getConnection((conn1+addr+conn2));
	            _connMap.put(addr, _conn);//����map�У��´��ҵ�ֱ��ʹ��
	        	 
			}
	         System.out.println("Connection initial successfully.");
	         
	     } catch (SQLException e) {
	         System.out.println("error during sql operations");
	         e.printStackTrace();
	     }
    }
    //������
	public Connection GetConnection(String addr) throws SQLException, ClassNotFoundException
	{
		loadProperty();
		_conn=_connMap.get(addr);
		if(_conn==null){
		 try { 
	         Class.forName("com.mysql.jdbc.Driver");// ��̬����mysql����
	         // or:
	         // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
	         // or��
	         // new com.mysql.jdbc.Driver();

	         System.out.println("successfully load driver");
	         // һ��Connection����һ�����ݿ�����
	         _conn = DriverManager.getConnection((conn1+"addr"+conn2));
	         _connMap.put(addr, _conn);//����map�У��´��ҵ�ֱ��ʹ��
	     } catch (SQLException e) {
	         System.out.println("error during sql operations");
	         e.printStackTrace();
	     }
	     }

		return _conn;
		
	}
    
	
	public void closeConnection() throws SQLException
	{
		for (Connection conn : _connMap.values()) {
			conn.close();
		}
		System.out.println("connection released successfully.");
	}
    
  public boolean insertData(EvidenceData data)
  {
	  //��filterd�ĵ�ַ�б��зֱ��������
	  try{
		  ArrayList<Connection> targetAddrArrayList=filterAddr(CommonAttribute.hostipString);
		  for (Connection connection : targetAddrArrayList) {
			String sql="insert into EvidenceTable values("+"\'"+
		  data.idString+"\'"+","+"\'"+
		  data.eventtimeTimestamp+"\'"+","+"\'"+
					data.owner_node+"\'"+","+"\'"+
		  data.oper_type+"\'"+","+"\'"+
					data.simhaString+"\'"+","+"\'"+
		  data.contentString+"\'"+","+"\'"+data.remark+"\'"+")";
			Statement statement=connection.createStatement();
			statement.execute(sql);
		}
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  System.out.println("error when write data.");
		  return false;
	  }
	  return true;
  }
    
  public String queryData(String condition)
  {
	  //�������еģ�Ȼ��Ա�
	  //��filterd�ĵ�ַ�б��зֱ��������
	 int totalcount=0;
	  HashMap<String, Integer> results=new HashMap<String, Integer>();
	  try{
		  
		  for (Connection connection : _connMap.values()) {
			String sql="select * from EvidenceTable where myid="+"\'"+condition+"\'";
			Statement statement=connection.createStatement();
			ResultSet rs=statement.executeQuery(sql);
			while (rs.next()){
				totalcount++;
                  //�鿴���ݣ��ҳ�Ʊ������һ��
                String content=rs.getString("content");
            	if(results.containsKey(content))
            	{
            		Integer preInteger=results.get(content)+1;
            		results.replace(content, preInteger);
            	}
            	else {
					results.put(content, 1);
				}
               // simhash�Ĵ��������    rs.getString("Code");


            }
		}
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  System.out.println("error when query data.");
		  return null;
	  }
	  //�ҳ�results��Ʊ�������Ǹ����
	  String re="";
	  int count=0;
	  Boolean exist2=false;
	  for (String contenString : results.keySet()) {
		 //�������滻
		  if(results.get(contenString)>=count)
		  { 
			  if((results.get(contenString))==count)
			  {
				  
				  exist2=true;
			  }
			  else {
				  exist2=false;
			}
			  re=contenString;
			  count=results.get(contenString);
			 
		  }
	}
	  //���ȷ������ֻ��һ��������������������ӡ��Ϣ
	  String voteString="";
	  if(totalcount>0) voteString=count+"/"+totalcount;
	  if(!exist2)
	  return re+" "+voteString;
	  else {
		  System.out.println("exist two types of contents that has the same votes.");
	      return re+" "+voteString;
	  }
	  
  }
  private ArrayList<Connection> filterAddr(String curAddr)
  {
	  ArrayList<Connection> filteredResult=new ArrayList<Connection>();
	  //Ŀǰ��ѡ�������еģ�֮����ܻ����㷨��ѡ���Եز�������
	  for (String addr:_connMap.keySet())
	  {
		  filteredResult.add(_connMap.get(addr));
	  }
	  return filteredResult;
  }
	private void loadProperty()
	{
	    Properties prop = new Properties();     
        try{
            //��ȡ�����ļ�a.properties
            InputStream in = new FileInputStream("addrs.properties");
            prop.load(in);     ///���������б�
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
              String valueString=prop.getProperty(key);
              ipaddrsArrayList.add(valueString);
              System.out.println(valueString);
            }
            in.close();

        }
        catch(Exception e){
        	e.printStackTrace();
            System.out.println(e);
        }
    } 
	}

