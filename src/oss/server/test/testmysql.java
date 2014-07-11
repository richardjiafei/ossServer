package oss.server.test;



//import javax.servlet.*;
//import javax.servlet.http.*;
import java.sql.*;
import java.sql.Connection.*; 

public class testmysql
{ 

	public static void main(String args[]) 
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