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
	 // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
    // 避免中文乱码要指定useUnicode和characterEncoding
    // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
    // 下面语句之前就要先创建javademo数据库
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
	         Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
	         // or:
	         // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
	         // or：
	         // new com.mysql.jdbc.Driver();

	         System.out.println("successfully load driver");
	         // 一个Connection代表一个数据库连接
	         for (String addr : ipaddrsArrayList) {
				_conn = DriverManager.getConnection((conn1+addr+conn2));
	            _connMap.put(addr, _conn);//放入map中，下次找到直接使用
	        	 
			}
	         System.out.println("Connection initial successfully.");
	         
	     } catch (SQLException e) {
	         System.out.println("error during sql operations");
	         e.printStackTrace();
	     }
    }
    //测试用
	public Connection GetConnection(String addr) throws SQLException, ClassNotFoundException
	{
		loadProperty();
		_conn=_connMap.get(addr);
		if(_conn==null){
		 try { 
	         Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
	         // or:
	         // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
	         // or：
	         // new com.mysql.jdbc.Driver();

	         System.out.println("successfully load driver");
	         // 一个Connection代表一个数据库连接
	         _conn = DriverManager.getConnection((conn1+"addr"+conn2));
	         _connMap.put(addr, _conn);//放入map中，下次找到直接使用
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
	  //向filterd的地址列表中分别插入数据
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
	  //查找所有的，然后对比
	  //向filterd的地址列表中分别插入数据
	 int totalcount=0;
	  HashMap<String, Integer> results=new HashMap<String, Integer>();
	  try{
		  
		  for (Connection connection : _connMap.values()) {
			String sql="select * from EvidenceTable where myid="+"\'"+condition+"\'";
			Statement statement=connection.createStatement();
			ResultSet rs=statement.executeQuery(sql);
			while (rs.next()){
				totalcount++;
                  //查看数据，找出票数最多的一个
                String content=rs.getString("content");
            	if(results.containsKey(content))
            	{
            		Integer preInteger=results.get(content)+1;
            		results.replace(content, preInteger);
            	}
            	else {
					results.put(content, 1);
				}
               // simhash的处理后面做    rs.getString("Code");


            }
		}
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
		  System.out.println("error when query data.");
		  return null;
	  }
	  //找出results中票数最多的那个结果
	  String re="";
	  int count=0;
	  Boolean exist2=false;
	  for (String contenString : results.keySet()) {
		 //大于则替换
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
	  //最后确认最大的只有一个，如果存在两个，则打印消息
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
	  //目前的选择是所有的，之后可能会有算法来选择性地插入数据
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
            //读取属性文件a.properties
            InputStream in = new FileInputStream("addrs.properties");
            prop.load(in);     ///加载属性列表
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

