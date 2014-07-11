package oss.server.main;

//package bgw.protocols.authnet.icbc.ivr.ssl;  

//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.security.KeyStore;

//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
import java.lang.Thread.State;

import javax.net.ssl.SSLServerSocket;




//import oss.server.test.SslThread;
import oss.server.thread.*;

//import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import oss.server.ssl.SSLManager;
import oss.server.common.*;
import oss.server.database.DbManager;
import oss.server.thread.*;

public class OSSMain implements OssServeType {
	static int port = 8266;
	static SSLServerSocket server;
	static ClientInfo[] m_clientInfoArr;
	static int m_clientInfoIndex;
    private  static DbManager m_dbManager;
//	static int[] temp;
//	static SSLServerSocket [] tempsocket; 

	public OSSMain() {
//		socketIndex=0;
	};


	private static void run() {
//		SSLSocket sslSocketArr[] = new SSLSocket[100];
		
//		int socketIndex;
		
		SSLManager sslManager = new SSLManager();
		m_clientInfoIndex = 1;
//		temp = new int[100];
		m_clientInfoArr = new ClientInfo[MAX_CLIENT_NUM];
//		ClientInfo[] m_tempClientInfo = new ClientInfo[MAX_CLIENT_NUM]; 
//		for(int i=0; i < MAX_CLIENT_NUM; i++)
//			m_clientInfoArr[i] = new ClientInfo();
//		tempsocket = new SSLServerSocket[100];
//		}
		
		try {
	        // Start ServerDispatcher thread

	        ServerDispatcher serverDispatcher = new ServerDispatcher();

	        serverDispatcher.start();
	        
	        m_dbManager = DbManager.getInstance();
	        m_dbManager.ConnectDatabase();
			
			server = sslManager.getServerSocket(port);
			System.out.println("OSSMain run on +port + Port wait connecting...");

			while (true) {
//				sslSocketArr[socketIndex] = (SSLSocket) server.accept();

				SSLSocket sslSocket = (SSLSocket) server.accept();
				ClientInfo clientInfo = new ClientInfo();
                m_clientInfoArr[m_clientInfoIndex] = clientInfo;
				m_clientInfoArr[m_clientInfoIndex].mSocket = sslSocket;


               ClientSender clientSender = new ClientSender(m_clientInfoArr[m_clientInfoIndex], serverDispatcher);
               clientSender.start();
               Thread.sleep(10);
               State tempstate11 = clientSender.getState();

               ClientListener clientListener = new ClientListener(m_clientInfoArr,m_clientInfoIndex, serverDispatcher);
               m_clientInfoArr[m_clientInfoIndex].mClientListener = clientListener;
               State tempstate21 = clientListener.getState();
               m_clientInfoArr[m_clientInfoIndex].mClientSender = clientSender;
               State tempstate22 = clientListener.getState();
               clientListener.start();

    

               serverDispatcher.addClient(m_clientInfoArr[m_clientInfoIndex]);				
			
				
//				new SslThread(sslSoc=ketArr,socketIndex);
				m_clientInfoIndex++;
	            Thread.sleep(1);
			}
		} catch (Exception e) {
			System.out.println("OSSMain run function err 80:" + e);
		}
	};

	public static void main(String args[]) {
		run();
	}
}

