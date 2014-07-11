package oss.server.ssl;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
//import sun.net.ftp.FtpClient


public class SSLManager {
	static int port = 8266;
	static SSLServerSocket server;	

	public SSLServerSocket getServerSocket(int thePort) {
		SSLServerSocket s = null;
		try {
			String key = "/mnt/xpshare/opensourcecode/key/SSLKey";

			char keyStorePass[] = "password".toCharArray();

			char keyPassword[] = "password".toCharArray();

			KeyStore ks = KeyStore.getInstance("JKS");
			FileInputStream pfIS = new FileInputStream(key);

			ks.load(pfIS, keyStorePass);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

			kmf.init(ks, keyPassword);

			SSLContext sslContext = SSLContext.getInstance("SSLv3");

			sslContext.init(kmf.getKeyManagers(), null, null);

			SSLServerSocketFactory factory = sslContext
					.getServerSocketFactory();

			s = (SSLServerSocket) factory.createServerSocket(thePort);

		} catch (Exception e) {
			System.out.println(e);
		}
		return (s);
	}

}

