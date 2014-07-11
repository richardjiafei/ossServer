/**

 * Nakov Chat Server

 * (c) Svetlin Nakov, 2002

 * http://www.nakov.com

 *

 * Nakov Chat Server is multithreaded chat server. It accepts multiple clients

 * simultaneously and serves them. Clients can send messages to the server.

 * When some client send a message to the server, this message is dispatched

 * to  the client connected to the server.

 *

 * The server consists of two components - "server core" and "client handlers".

 *

 * The "server core" consists of two threads:

 *   - NakovChatServer - accepts client connections, creates client threads to

 * handle them and starts these threads

 *   - ServerDispatcher - waits for a messages and sends arrived messages to

 * all the clients connected to the server

 *

 * The "client handlers" consist of two threads:

 *   - ClientListener - listens for message arrivals from the socket and

 * forwards them to the ServerDispatcher thread

 *   - ClientSender - sends messages to the client

 *

 * For each accepted client, a ClientListener and ClientSender threads are

 * created and started. A ClientInfo object is also created to contain the

 * information about the client. Also the ClientInfo object is added to the

 * ServerDispatcher's clients list. When some client is disconnected, is it

 * removed from the clients list and both its ClientListener and ClientSender

 * threads are interrupted.

 *

 *

 * NakovChatServer class is entry point for the program. It opens a server

 * socket, starts the dispatcher thread and infinitely accepts client connections,

 * creates threads for handling them and starts these threads.

 */
package oss.server.thread;


import java.net.*;

import java.util.*;
import oss.server.thread.*;

 

public class ServerDispatcher extends Thread

{

    private Vector mMessageQueue = new Vector();

    private Vector mClients = new Vector();

 

    /**

     * Adds given client to the server's client list.

     */

    public synchronized void addClient(ClientInfo aClientInfo)

    {

        mClients.add(aClientInfo);

    }

 

    /**

     * Deletes given client from the server's client list

     * if the client is in the list.

     */

    public synchronized void deleteClient(ClientInfo aClientInfo)

    {

        int clientIndex = mClients.indexOf(aClientInfo);

        if (clientIndex != -1)

           mClients.removeElementAt(clientIndex);

    }

 

    /**

     * Adds given message to the dispatcher's message queue and notifies this

     * thread to wake up the message queue reader (getNextMessageFromQueue method).

     * dispatchMessage method is called by other threads (ClientListener) when

     * a message is arrived.

     */

    public synchronized void dispatchMessage(ClientInfo aClientInfo, MsgContent aMessage)

    {

//        Socket socket = aClientInfo.mSocket;

//        String senderIP = socket.getInetAddress().getHostAddress();

//        String senderPort = "" + socket.getPort();

//        aMessage = senderIP + ":" + senderPort + " : " + aMessage;

        mMessageQueue.add(aMessage);

        notify();

    }

 

    /**

     * @return and deletes the next message from the message queue. If there is no

     * messages in the queue, falls in sleep until notified by dispatchMessage method.

     */

    private synchronized MsgContent getNextMessageFromQueue()

    throws InterruptedException

    {

        while (mMessageQueue.size()==0)

           wait();

        MsgContent message = (MsgContent) mMessageQueue.get(0);

        mMessageQueue.removeElementAt(0);

        return message;

    }

 

    /**

     * Sends given message to all clients in the client list. Actually the

     * message is added to the client sender thread's message queue and this

     * client sender thread is notified.

     */

    private synchronized void sendMessageToAllClients(String aMessage)

    {

        for (int i=0; i<mClients.size(); i++) {

           ClientInfo clientInfo = (ClientInfo) mClients.get(i);

           clientInfo.mClientSender.sendMessage(aMessage);

        }

    }

    private synchronized void sendMessageToClient(MsgContent aMessage)

    {

           aMessage.clientInfo.mClientSender.sendMessage(aMessage);

    }
    

    /**

     * Infinitely reads messages from the queue and dispatch them

     * to all clients connected to the server.

     */

    public void run()

    {

        try {

           while (true) {

               MsgContent message = getNextMessageFromQueue();

               sendMessageToClient(message);

           }

        } catch (InterruptedException ie) {

           // Thread interrupted. Stop its execution

        }

    }

 

}

 
