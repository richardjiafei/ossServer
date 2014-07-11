package oss.server.database;

//import javax.servlet.*;
//import javax.servlet.http.*;
import java.sql.*;
import java.sql.Connection.*; 

public class DbManager
{ 
    private static DbManager uniqueInstance = null;

    

    private DbManager() {

       // Exists only to defeat instantiation.

    }

 

    public static DbManager getInstance() {

       if (uniqueInstance == null) {

           uniqueInstance = new DbManager();

       }

       return uniqueInstance;

    }
	
	public Statement m_statement;
	public void ConnectDatabase(){
		try	{ 
		     Class.forName("com.mysql.jdbc.Driver");     
		
		     //加载MYSQL JDBC驱动程序
		     //Class.forName("org.gjt.mm.mysql.Driver"); 
		     System.out.println("Success loading Mysql Driver!"); 
		
		} 
		catch(Exception e) 
		{ 
		     System.out.print("Error loading Mysql Driver!"); 
		     e.printStackTrace(); 
		} 
			   
		
		try{ 
		
		     Connection connect = DriverManager.getConnection( 
		         "jdbc:mysql://localhost:3306/school","root","Richard1008"); 
		
		     System.out.println("Success connect Mysql server!"); 
		
		     m_statement = connect.createStatement(); 
		     m_statement.execute("use ossserver");
		
		     ResultSet rs = m_statement.executeQuery("select * from occ"); 

		    //user 为 你表的名称
			while(rs.next()) 
			{ 
			
			       System.out.println(rs.getString("ip")); 
			
			} 
		
		} 
		catch(Exception e) 
		{ 
		
		     System.out.print("get data error!"); 
		
		     e.printStackTrace(); 
		
		} 
		
	}

	public int GetOCCUserBytunnelId(int tunnelId){
		int clientIndex = 0xffff;
		try{
			String sql = "select clientIndex from occ where tunnelId1="+tunnelId;
			ResultSet rs = m_statement.executeQuery(sql);
			rs.next();
//			rs.previous();
			clientIndex = rs.getInt(1);
		}
		catch(Exception e){
		     System.out.print("get data error!"); 
				
		     e.printStackTrace(); 			
		}

		return clientIndex;
	}

	
	public int GetOCCtunnelIdByOcsId(int ocsId){
		int tunnelId = 0;
		try{
			String sql = "select * from occ where ocsId="+ocsId;
			ResultSet rs = m_statement.executeQuery(sql);
			rs.next();
			tunnelId = rs.getInt(1); 
			//获取字段名 
//			clientIndex = rsmd.getColumnName(7); 
			
		}
		catch(Exception e){
		     System.out.print("get data error!"); 
				
		     e.printStackTrace(); 			
		}

		return tunnelId;
	}
	
	public int GetOPHtunnelIdByOcsId(int ocsId){
		int tunnelId = 0;
		try{
			String sql = "select * from oph where ocsId="+ocsId;
			ResultSet rs = m_statement.executeQuery(sql);
			rs.next();
			tunnelId = rs.getInt(1); 
			//获取字段名 
//			clientIndex = rsmd.getColumnName(7); 
			
		}
		catch(Exception e){
		     System.out.print("get data error!"); 
				
		     e.printStackTrace(); 			
		}

		return tunnelId;
	}		
	
	public int GetOPHUserBytunnelId(int tunnelId){
		int clientIndex = 0xffff;
		try{
			String sql = "select clientIndex from oph where ocsId where tunnelId1="+tunnelId;
			ResultSet rs = m_statement.executeQuery(sql);
			rs.next();
			clientIndex = rs.getInt(1); 
			//获取字段名 
//			clientIndex = rsmd.getColumnName(7); 
			
		}
		catch(Exception e){
		     System.out.print("get data error!"); 
				
		     e.printStackTrace(); 			
		}

		return clientIndex;
	}	
	
	
	public void InsertOCCUser(RecordData recordData){
		try{
//			String sql = "insert into occ(ip,port,ocsId,modetype,clientIndex) values("+recordData.ip+","+recordData.port+","+recordData.ocsId+","+recordData.nodetype+","+recordData.clientIndex +");";
			String sql = "insert into occ(ocsId,modetype,clientIndex) values("+recordData.ocsId+","+recordData.nodetype+","+recordData.clientIndex +");";
			m_statement.execute(sql);
		}
		catch(Exception e){
		     System.out.print("get data error!"); 
				
		     e.printStackTrace(); 			
		}

		return;
	}
	
	public void InsertOPHUser(RecordData recordData){
		try{
//			String sql = "insert into oph(ip,port,ocsId,modetype) values("+recordData.ip+","+recordData.port+","+recordData.ocsId+","+recordData.nodetype+","+recordData.clientIndex+");";
			String sql = "insert into oph(tunnelId1,ocsId,modetype,clientIndex) values("+recordData.occTunnelId+","+recordData.ocsId+","+recordData.nodetype+","+recordData.clientIndex+");";
			m_statement.execute(sql);
		}
		catch(Exception e){
		     System.out.print("get data error!"); 
				
		     e.printStackTrace(); 			
		}

		return;
	}	
	
	public boolean IsOCCConnect(int ocsId){
		boolean result= false;
		
		try{
			result = true;
			String sql ="select * from occ where ocsId="+ocsId;
			ResultSet rs = m_statement.executeQuery(sql);
			if(rs.next()){
				result = true;
			}
		
		}
		catch(Exception e){
			
		}
		
		return result;
	}
	

	public static void test(String args[]) 
	{ 
		
		try
		
		{ 
		     Class.forName("com.mysql.jdbc.Driver");     
		
		     //加载MYSQL JDBC驱动程序
		     //Class.forName("org.gjt.mm.mysql.Driver"); 
		     System.out.println("Success loading Mysql Driver!"); 
		
		} 
		catch(Exception e) 
		{ 
		     System.out.print("Error loading Mysql Driver!"); 
		     e.printStackTrace(); 
		} 
		
		   
		
		try
		
		{ 
		
		     Connection connect = DriverManager.getConnection( 
		         "jdbc:mysql://localhost:3306/school","root","Richard1008"); 
		
		     System.out.println("Success connect Mysql server!"); 
		
		     Statement stmt = connect.createStatement(); 
		
		     ResultSet rs = stmt.executeQuery("select * from teacher"); 
                                                         
		
		    //user 为 你表的名称
		
			while(rs.next()) 
			{ 
			
			       System.out.println(rs.getString("name")); 
			
			} 
		
		} 
		catch(Exception e) 
		{ 
		
		     System.out.print("get data error!"); 
		
		     e.printStackTrace(); 
		
		} 
		
	} 

} 

