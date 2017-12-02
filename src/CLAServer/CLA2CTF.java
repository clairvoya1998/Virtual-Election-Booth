package CLAServer;

import javax.net.ssl.*;
import java.io.*;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;

public class CLA2CTF {
    static final String KEYSTORE = "src/ElectionKey.jks";
    static final String TRUSTSTORE = "src/TrustStore";
    static final String STORE_PSWD= "clairvoyant";
    static final String ALIAS_PSWD = "clairvoyant";
    public String serverAddress="localhost";

    public void sendCTF(Voter v) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(KEYSTORE), STORE_PSWD.toCharArray());

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(TRUSTSTORE), STORE_PSWD.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, ALIAS_PSWD.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

//            SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, 9761);
            String[] suites = sslSocket.getSupportedCipherSuites();
            sslSocket.setEnabledCipherSuites(suites);
            sslSocket.startHandshake();

            PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

            String fromServer;
            String fromUser;

//            out.println(v.getName());
//            out.println(v.getStudentID());
//            out.println(v.getDateOfBirth());
            out.println(v.getValNumber());

            boolean sendingVal=false;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("From CTF Server: " + fromServer);
                if (fromServer.equals("Bye.")||fromServer.equals("denied.")) {
                    System.out.println("Client Bye.");
                    break;
                }
            }
			/*
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }*/
        } catch (Exception e) {
            System.err.println(e.getCause());
            System.exit(1);
        }
    }
}
