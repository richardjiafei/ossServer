package oss.server.thread;

/**

 * Nakov Chat Server - (c) Svetlin Nakov, 2002

 *

 * Sends messages to the client. Messages are stored in a message queue. When

 * the queue is empty, ClientSender falls in sleep until a new message is

 * arrived in the queue. When the queue is not empty, ClientSender sends the

 * messages from the queue to the client socket.

 */

 

import java.io.*;
import java.net.*;
import java.util.*;
//import oss.server.thread.*;
import java.io.DataOutputStream;

import oss.server.common.*;
/*extends Thread*/
public class ClientSender extends Thread implements OssServeType
{

    private Vector mMessageQueue = new Vector();

 

    private ServerDispatcher mServerDispatcher;

    private ClientInfo mClientInfo;

    private PrintWriter mOut;
    private DataOutputStream m_dataOutputStream;

 

    public ClientSender(ClientInfo aClientInfo, ServerDispatcher aServerDispatcher)

    throws IOException

    {

        mClientInfo = aClientInfo;

        mServerDispatcher = aServerDispatcher;

        Socket socket = aClientInfo.mSocket;

//        mOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        m_dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

 

    /**

     * Adds given message to the message queue and notifies this thread

     * (actually getNextMessageFromQueue method) that a message is arrived.

     * sendMessage is called by other threads (ServeDispatcher).

     */

    public synchronized void sendMessage(String aMessage)

    {

        mMessageQueue.add(aMessage);

        notify();

    }

    public synchronized void sendMessage(MsgContent aMessage)

    {

        mMessageQueue.add(aMessage);

        notify();

    } 

    /**

     * @return and deletes the next message from the message queue. If the queue

     * is empty, falls in sleep until notified for message arrival by sendMessage

     * method.

     */

    private synchronized MsgContent getNextMessageFromQueue() throws InterruptedException

    {

        while (mMessageQueue.size()==0)

           wait();

        MsgContent message = (MsgContent) mMessageQueue.get(0);

        mMessageQueue.removeElementAt(0);

        return message;

    }

 

    /**

     * Sends given message to the client's socket.

     */

    private void sendMessageToClient(MsgContent aMessage)

    {
    	try {
        	if(OSS_TYPE_ATTACH_RESP == aMessage.oss_type){
        		m_dataOutputStream.writeChar(aMessage.oss_type);
        		m_dataOutputStream.writeChar(aMessage.messageLen);
        		m_dataOutputStream.writeInt(aMessage.TunnelId);
        		m_dataOutputStream.writeInt(aMessage.TunnelId);
        	}
        	else if(OSS_TYPE_DATA == aMessage.oss_type){
        		m_dataOutputStream.writeChar(aMessage.oss_type);
        		m_dataOutputStream.writeChar(aMessage.messageLen);
        		m_dataOutputStream.writeInt(aMessage.TunnelId);
        		if(aMessage.messageLen > 6){
        			m_dataOutputStream.write(aMessage.data, 0, (int)(aMessage.messageLen - 6));	
        		}
        	}
        	else{
        		System.out.print("##*511sendMessageToClient oss_type=%d, invalid para\n"+aMessage.oss_type);
        	}
        		

            m_dataOutputStream.flush();
    	}
    	catch(Exception e){
    		System.out.print("sendMessageToClient Exception\n");;
    	}
    }

 

    /**

     * Until interrupted, reads messages from the message queue

     * and sends them to the client's socket.

     */

    public void run()

    {
    	System.out.print("ClientSender run start \n");
        try {
        	Thread.sleep(1);
           while (!isInterrupted()) {
//        	while(true){

        	   MsgContent message = getNextMessageFromQueue();

               sendMessageToClient(message);

           }

        } catch (Exception e) {

           // Commuication problem

        }

 

        // Communication is broken. Interrupt both listener and sender threads

        mClientInfo.mClientListener.interrupt();

        mServerDispatcher.deleteClient(mClientInfo);

    }

 

}