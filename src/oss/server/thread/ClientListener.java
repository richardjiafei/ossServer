package oss.server.thread;


/**

 * Nakov Chat Server - (c) Svetlin Nakov, 2002

 *

 * ClientListener class is purposed to listen for client messages and

 * to forward them to ServerDispatcher.

 */

 

import java.io.*;
import java.lang.Thread.State;
import java.net.*;

import oss.server.common.OssServeType;
import oss.server.database.DbManager;
import oss.server.database.RecordData;
import oss.server.thread.*;

import java.io.DataInputStream;
 

public class ClientListener extends Thread  implements OssServeType

{

    private ServerDispatcher mServerDispatcher;

    private ClientInfo mClientInfo;

    private BufferedReader mIn;
    private ClientInfo m_clientInfoArr[];
    private int m_clientIndex;
    private int m_nodeType;

 /*old*/
    private DataInputStream m_dataInputStream;
//	DataOutputStream m_dataOutputStream;
	//	static BufferedReader m_bufferReader;
//	static PrintWriter m_printWrite;
//	static OutputStream m_outputStream;
    private Socket m_socket;
//    private Socket m_socketArr[];
    private  DbManager m_dbManager;
    private long m_occTunnelId;
//    private int m_socketIndex;
	

	
	public void ProcessAttachReq(MsgContent messageIn){

		RecordData recordData = new RecordData();

		try{
			
			if(NODE_TYPE_SERVER == messageIn.nodetype){
				//write mysql and alloc index 
//				m_dbManager.ConnectDatabase();
				recordData.ocsId = messageIn.ocsId;
				recordData.nodetype = messageIn.nodetype;
				recordData.ip = m_socket.getInetAddress().toString();
				recordData.port = m_socket.getPort();
				recordData.clientIndex = m_clientIndex;
				
				m_dbManager.InsertOCCUser(recordData);
				
				//send message to OCC.
				MsgContent messageOut = new MsgContent();
				messageOut.oss_type = OSS_TYPE_ATTACH_RESP;
				messageOut.messageLen = 10;
				messageOut.TunnelId = m_dbManager.GetOCCtunnelIdByOcsId(recordData.ocsId);
//				message.data = new Byte[10];
				messageOut.clientInfo = mClientInfo;
				m_nodeType = messageIn.nodetype;
				
				
				mServerDispatcher.dispatchMessage(mClientInfo, messageOut);

			}// CLIENT OPH
			else if(NODE_TYPE_CLIENT == messageIn.nodetype){
				// write mysql and alloc index and
//				m_dbManager.ConnectDatabase();
				if (m_dbManager.IsOCCConnect(messageIn.ocsId)) {
					
					recordData.ocsId = messageIn.ocsId;
					recordData.port = m_socket.getPort();
					recordData.nodetype = messageIn.nodetype;
					recordData.ip = m_socket.getInetAddress().toString();
					recordData.clientIndex = m_clientIndex;
					//todo
					recordData.occTunnelId = m_dbManager.GetOCCtunnelIdByOcsId(messageIn.ocsId);
//					recordData.socketIndex = m_socketIndex;
					m_dbManager.InsertOPHUser(recordData);
					
					m_occTunnelId = recordData.occTunnelId;
					
					//send message to OPH
					MsgContent messageOut = new MsgContent();
					messageOut.TunnelId = m_dbManager.GetOPHtunnelIdByOcsId(messageIn.ocsId);
					messageOut.oss_type = OSS_TYPE_ATTACH_RESP;
					messageOut.messageLen = 10;
//					messageOut.data = new byte[10];
					messageOut.clientInfo = mClientInfo;
					m_nodeType = messageIn.nodetype;
					
					mServerDispatcher.dispatchMessage(mClientInfo, messageOut);
					
				}// reject oph access 
				else{
					
				}
					
				
				//if OCC is ok;
				
			}
			else{
				System.out.printf("##*221 error!");
			}			
		}
		catch(Exception e) {
			System.out.println(e);
		}
		// SERVER OCC

			
	}
	
	public void ProcessDataTransfer(MsgContent messageIn){
		
//		RecordData recordData = new RecordData();
		int clientIndex = 0;
		if(NODE_TYPE_SERVER == m_nodeType){
			clientIndex = m_dbManager.GetOPHUserBytunnelId(messageIn.TunnelId);
		}
		else if(NODE_TYPE_CLIENT == m_nodeType){
			clientIndex = m_dbManager.GetOCCUserBytunnelId(messageIn.TunnelId);
		}
			
//		int clientIndex = m_dbManager.GetOPHUserBytunnelId(messageIn.TunnelId);
		
		//send message to OCC.
		messageIn.clientInfo = m_clientInfoArr[clientIndex];
 		mServerDispatcher.dispatchMessage(m_clientInfoArr[clientIndex], messageIn);		
		
//		switch(messageIn.nodetype){
//		case NODE_TYPE_SERVER:{
//			//send message to the special OPH
//			//m_dbManager.ConnectDatabase();
//					
//			int clientIndex = m_dbManager.GetOPHUserBytunnelId(messageIn.TunnelId);
//			
//			//send message to OCC.
//     		mServerDispatcher.dispatchMessage(m_clientInfoArr[m_clientIndex], messageIn);
//     		
//			break;
//			
//		}
//		case NODE_TYPE_CLIENT:{
//			
//			// send the message to OCC
////			m_dbManager.ConnectDatabase();
//			
//			int clientIndex = m_dbManager.GetOCCUserBytunnelId(messageIn.TunnelId);
//			
//			//send message to OCC.
//     		mServerDispatcher.dispatchMessage(m_clientInfoArr[m_clientIndex], messageIn);
//     		
//			break;
//		}
//		default:{
//			System.out.println("ProcessDataTransfer invalid nodetype=%d"+messageIn.nodetype);
//		}
//		
//		}
		
	}
    
    
/*old end */
    public ClientListener(ClientInfo aClientInfoArr[],int clientIndex, ServerDispatcher aServerDispatcher)

    throws IOException

    {
		try {
			State tempstate = getState();
	        mClientInfo = aClientInfoArr[clientIndex];
	        m_clientInfoArr = aClientInfoArr;
	        m_clientIndex = clientIndex;
	        mServerDispatcher = aServerDispatcher;

			m_socket = mClientInfo.mSocket;
//			m_bufferReader = new BufferedReader(new InputStreamReader(m_socket.getInputStream(),
//					"gb2312"));
			m_dataInputStream =new DataInputStream(m_socket.getInputStream());
						
	//		m_outputStream = m_socket.getOutputStream();
//			m_dbManager = new DbManager();
			m_dbManager = DbManager.getInstance();
			//start();

		} catch (Exception e) {
			System.out.println(e);
		}
    	
//        mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }

 

    /**

     * Until interrupted, reads messages from the client socket, forwards them

     * to the server dispatcher's queue and notifies the server dispatcher.

     */

    public void run()

    {
    	System.out.print("ClientListener run start \n");
    	
		try {
			Thread.sleep(1);
//			while(true){
			while (!isInterrupted()) {
				//get MessageHeader
				MsgContent messageIn = new MsgContent();
				messageIn.oss_type = m_dataInputStream.readChar();
				messageIn.messageLen = m_dataInputStream.readChar();
				messageIn.TunnelId = m_dataInputStream.readInt();
				
				System.out.println("Reading l of the length"+messageIn.messageLen);
				
				//read the message data
				
				
				switch(messageIn.oss_type){
				case OSS_TYPE_ATTACH_REQ:{
					// process the attach for OCC and OPH
					messageIn.ocsId = m_dataInputStream.readInt();
					messageIn.nodetype = m_dataInputStream.readByte();
					int temp1 = (int)messageIn.nodetype;
					System.out.println("receive  OSS_TYPE_ATTACH_REQ!");
					ProcessAttachReq(messageIn);
					break;
				}
				
				case OSS_TYPE_DATA: {
					// transfer the data to OCC!
					messageIn.data = new byte[messageIn.messageLen - 6];
					if(messageIn.messageLen >0){
						m_dataInputStream.readFully(messageIn.data);
					}
					else{
						System.out.println("##*112 error");
					}
					
					System.out.println("receive the data!");
					ProcessDataTransfer(messageIn);
					break;
				}
				
				default:{
					System.out.println("##*331 error!");
					break;
				}
				}				
			}


		} catch (Exception e) {
			System.out.println(e);
		}
	
    	


        // Communication is broken. Interrupt both listener and sender threads

        mClientInfo.mClientSender.interrupt();

        mServerDispatcher.deleteClient(mClientInfo);

    }

 

}
