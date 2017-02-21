package DataDealer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import javax.sound.sampled.Line;

import DbConn.DataConnection;
import DbConn.EvidenceData;

public class DataKeeper {
	private static DataConnection connection=null;

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// TODO Auto-generated method stub
	connection=new DataConnection();
    //做成命令行的形式，输入 操作 ip地址 [文件路径]
	while(true)
	{
		try{
    System.out.print("evidenceKeeper>>");
    //判断输入的参数的个数
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String input = null;
    input = br.readLine();
    String[] ops=input.toString().replace("  ", " ").split(" ");
    int argscount=ops.length;
    //如果个数小于1或者大于三都是非法的
    if(argscount>=4||argscount<1)
    {
        System.out.println("wrong arguments,see -help for some helps");
    	//System.out.println("evidenceKeeper>>");
    	continue;
    }
    else if(argscount==1&&ops[0].equals("-help"))
	{
		printUsage();
		continue;
	}
    else if(argscount==1&&ops[0].equals("exit"))
 	{
 		connection.closeConnection();
 		return;
 		//continue;
 	}
    else if(argscount==3&&ops[0].equals("-w"))
	{
		//write 操作
		String addr=ops[1];
		String pathString=ops[2];
		manageWrite(addr,pathString);
		continue;
	}
    else if(argscount==2&&ops[0].equals("-q"))
    {
    	//query操作
    	String condition=ops[1];
    	String resultString=queryData(condition);
    	if(!resultString.equals("")) System.out.println("evidenceKeeper>>"+resultString);
    	continue;
    }
    else {
    	 System.out.println("wrong arguments,see -help for some helps");
     	//System.out.println("evidenceKeeper>>");
     	continue;
	}
    
	}
		catch(Exception ex)
		{
			ex.printStackTrace();
			connection.closeConnection();//释放连接
		}
		
	}
	
	}
	
	 private static String queryData(String condition)
	 {
		String result= connection.queryData(condition);
		return result;
	 }
	
	
	private static void printUsage()
	{
		System.out.println("evidenceKeeper>>Usage:\n-help  help info.\nexit  exit the program.\n-w  [ipAddress]  [path]    write data\n-q  [keyinfo]    query for data");
	    //System.out.println("evidenceKeeper>>");
	}
	
	private static void manageWrite(String addr,String path) throws ClassNotFoundException
	{
		
		//读取文件的内容
		File file = new File(path);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                //针对每一行数据写数据库
               String[] dataStrings=tempString.split(" ");
               if(dataStrings.length==2)
               {
            	   EvidenceData data=new EvidenceData();
            	   data.idString=dataStrings[0];
            	   data.contentString=dataStrings[1];
            	   data=DataDealerTool.getsimHash(data);
           	    if(connection.insertData(data))
           	    {
           	    	//System.out.println("inserted 1 row.");
           	    	line++;
           	    }
               }
            }
            
            reader.close();
            System.out.println("evidenceKeeper>>inserted "+line+" rows");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
     
	    
	}

}
