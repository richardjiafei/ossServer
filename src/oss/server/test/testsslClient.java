package oss.server.test;


//package bgw.protocols.authnet.icbc.ivr.ssl;  

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;  
import java.net.Socket;  
import java.security.KeyStore;
import javax.net.ssl.*;
import java.io.*;
import java.security.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;  

import oss.server.common.OssServeType;

import java.io.DataInputStream;

public class testsslClient implements OssServeType {  
	  static  final String DEFAULT_TRUSTSTORE="SSLKey";
	  static final String DEFAULT_TRUSTSTORE_PASSWORD="password";
	
    static String trustStore=DEFAULT_TRUSTSTORE;
    static String trustStorePassword=DEFAULT_TRUSTSTORE_PASSWORD;
	static int OPHPort = 8266; 
	static int OCCPort = 8266;
	static final int m_oCCOscId = 0x1234;
	static final int m_oPHOscId = 0x5678;	
	static DataInputStream m_OPHdataInputStream;
	static DataOutputStream m_OPHdataOutputStream;
	static DataInputStream m_OCCdataInputStream;
	static DataOutputStream m_OCCdataOutputStream;
	static int m_tempTuunelId;
	static int m_oCCtunnelId;
	static int m_oPHtunnelId;
	
	public static void SendOCCRatchReq(DataOutputStream dataOutputStream){
		try{
			//send message to OCC.
			char oss_type = OSS_TYPE_ATTACH_REQ;
							 
			char messageLen = 10;
			int occTunnelId =135;
			int ocsid = m_oCCOscId;
			Byte nodeType = NODE_TYPE_SERVER;
			
			dataOutputStream.writeChar(oss_type);
			dataOutputStream.writeChar(messageLen);
			dataOutputStream.writeInt(occTunnelId);
			dataOutputStream.writeInt(ocsid);
			dataOutputStream.writeByte(nodeType);
			dataOutputStream.flush();
		}
		catch(Exception e){
			 System.out.println(e);
		}

	}
	
	public static void SendOPHRatchReq(DataOutputStream dataOutputStream){
		try{
			//send message to OCC.
			char oss_type = OSS_TYPE_ATTACH_REQ;
							 
			char messageLen = 10;
			int occTunnelId =136;
			int ocsid = m_oCCOscId;
			Byte nodeType = NODE_TYPE_CLIENT;
			
			dataOutputStream.writeChar(oss_type);
			dataOutputStream.writeChar(messageLen);
			dataOutputStream.writeInt(occTunnelId);
			dataOutputStream.writeInt(ocsid);
			dataOutputStream.writeByte(nodeType);
			dataOutputStream.flush();
		}
		catch(Exception e){
			 System.out.println(e);
		}

	}
	
	public static void GetResp(DataInputStream dataInputStream){
		try {
//			while(true){
				//get MessageHeader
				int oss_type = dataInputStream.readChar();
				int len = dataInputStream.readChar();
				int tunnelId = dataInputStream.readInt();
				m_tempTuunelId = tunnelId;
				System.out.println("testsslClient GetResp Reading l of the length"+len);
				
				//read the message data
				byte[] data = new byte[len - 6];
				if(len >0){
					dataInputStream.readFully(data);
				}
				else{
					System.out.println("##*112 testsslClient GetResp  error");
				}
				
				switch(oss_type){
				case OSS_TYPE_ATTACH_RESP:{
					// process the attach for OCC and OPH
					//int ocsid = dataInputStream.readInt();
					System.out.print(" testsslClient GetResp  receive  OSS_TYPE_ATTACH_REQ! ocsid=%d "+11+"\n");
					
					break;
				}
				
				case OSS_TYPE_DATA: {
					// transfer the data to OCC!
					System.out.println(" testsslClient GetResp  receive the data!");
					break;
				}
				
				default:{
					System.out.println("##*521 testsslClient GetResp  error!");
					break;
				}
				}				
//			}


		} catch (Exception e) {
			System.out.println(e);
		}

	}	
	public static void SendDataToOCC(DataOutputStream dataOutputStream){
		try{
			//send message to OCC.
			char oss_type = OSS_TYPE_DATA;
			byte datalen = 10;				 
			char messageLen = (char)(datalen+6);
			int occTunnelId = m_oCCtunnelId;
			int ocsid = m_oCCOscId;
			Byte nodeType = NODE_TYPE_CLIENT;
			byte data[] = new byte[datalen];
			for(int i=0; i < datalen; i++){
				data[i]  = 0x13;
			}
			
			dataOutputStream.writeChar(oss_type);
			dataOutputStream.writeChar(messageLen);
			dataOutputStream.writeInt(occTunnelId);
			dataOutputStream.write(data,0,datalen);	

			dataOutputStream.flush();
		}
		catch(Exception e){
			 System.out.println(e);
		}

	}
	
	public static void SendDataToOPH(DataOutputStream dataOutputStream){
		try{
			//send message to OCC.
			char oss_type = OSS_TYPE_DATA;
			byte datalen = 10;				 
			char messageLen = (char)(datalen+6);
			int occTunnelId = m_oPHtunnelId;
			int ocsid = m_oPHOscId;
			Byte nodeType = NODE_TYPE_CLIENT;
			byte data[] = new byte[datalen];
			for(int i=0; i < datalen; i++){
				data[i]  = 0x78;
			}
			
			dataOutputStream.writeChar(oss_type);
			dataOutputStream.writeChar(messageLen);
			dataOutputStream.writeInt(occTunnelId);
			dataOutputStream.write(data,0,datalen);	

			dataOutputStream.flush();
		}
		catch(Exception e){
			 System.out.println(e);
		}

	}	

	  public static TrustManager[] getTrustManagers()
			    throws IOException, GeneralSecurityException
			  {
			    // 首先获得缺省的TrustManagerFactory对象.
			    String alg=TrustManagerFactory.getDefaultAlgorithm();
			    TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);
			    
			    // 配置KeyManagerFactory对象使用的TrustStoree.我们通过一个文件加载
			    // TrustStore.
			    FileInputStream fis=new FileInputStream(trustStore);
			    KeyStore ks=KeyStore.getInstance("jks");
			    ks.load(fis, trustStorePassword.toCharArray());
			    fis.close();

			    // 使用获得的KeyStore初始化TrustManagerFactory对象
			    tmFact.init(ks);

			    // 获得TrustManagers对象
			    TrustManager[] tms=tmFact.getTrustManagers();
			    return tms;
			  }
	
	  public static SSLSocketFactory getSSLSocketFactory()
			    throws IOException, GeneralSecurityException
			  {
			    // 调用getTrustManagers方法获得trust managers
			    TrustManager[] tms=getTrustManagers();
			    
			    // 调用getKeyManagers方法获得key manager
			    //KeyManager[] kms=getKeyManagers();

			    // 利用KeyManagers创建一个SSLContext对象.用获得的KeyStore和
			    // TrustStore初始化该SSLContext对象.我们使用缺省的SecureRandom.
			    SSLContext context=SSLContext.getInstance("SSL");
			    context.init(null, tms, null);

			    // 最后获得了SocketFactory对象.
			    SSLSocketFactory ssf=context.getSocketFactory();
			    return ssf;
			  }
	
 public static void main(String args[]) {  
     try {  

    	 //way 2

//		String key = "/mnt/xpshare/opensourcecode/key/SSLKey2";
//
//		char keyStorePass[] = "password".toCharArray();
//
//		char keyPassword[] = "password".toCharArray();
//
//		KeyStore ks = KeyStore.getInstance("JKS");
//		FileInputStream pfIS = new FileInputStream(key);
//
//		ks.load(pfIS, keyStorePass);
//
//		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//
//		kmf.init(ks, keyPassword);
//
//		SSLContext sslContext = SSLContext.getInstance("SSLv3");
//
//		sslContext.init(kmf.getKeyManagers(), null, null);
//
//		SSLSocketFactory factory = sslContext.getSocketFactory();
//
//		Socket socket = factory.createSocket("127.0.0.1",OCCPort);

    	 
		//way3
		SSLSocketFactory factory = getSSLSocketFactory();

		Socket socket = factory.createSocket("127.0.0.1",OCCPort);

		
    	 //way 2
//         SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory  
//                 .getDefault();  
//
//         Socket socket = factory.createSocket("127.0.0.1", OCCPort);  
//         System.out.print("test tpj!");
         
 		m_OCCdataInputStream =new DataInputStream(socket.getInputStream());
		m_OCCdataOutputStream = new DataOutputStream(socket.getOutputStream());


		
		//access OCC 
		SendOCCRatchReq(m_OCCdataOutputStream);
		GetResp(m_OCCdataInputStream);
		m_oCCtunnelId = m_tempTuunelId;
		Socket socket1 = factory.createSocket("127.0.0.1", OPHPort);  
        System.out.print("test tpj!");
        
		m_OPHdataInputStream =new DataInputStream(socket1.getInputStream());
		m_OPHdataOutputStream = new DataOutputStream(socket1.getOutputStream());
		
		//access OPH
		SendOPHRatchReq(m_OPHdataOutputStream);
		GetResp(m_OPHdataInputStream);
		m_oPHtunnelId = m_tempTuunelId;
		//send data from OCC to OPH
		SendDataToOCC(m_OPHdataOutputStream);
		GetResp(m_OCCdataInputStream);
		//send data from OPH to OCC
		//SendDataToOPH(m_OPHdataOutputStream);
		
		
		 m_OCCdataOutputStream.close();
		 m_OPHdataOutputStream.close();	
		 m_OCCdataInputStream.close();
		 m_OPHdataInputStream.close();			 
         socket.close();
         socket1.close();
     } catch (Exception e) {  
         System.out.println(e);  
     }  
 }  
}