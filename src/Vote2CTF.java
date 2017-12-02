import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

public class Vote2CTF {
    static final String KEYSTORE = "src/ElectionKey.jks";
    static final String TRUSTSTORE = "src/TrustStore";
    static final String STORE_PSWD= "clairvoyant";
    static final String ALIAS_PSWD = "clairvoyant";

    public Vote2CTF(Voter you) {
        System.out.println("Vote to CTF:  vote,ID,Val#  " + you.getVote()+you.getID()+you.getValNumber() );
        String serverAddress="localhost";

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
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, 9762);
            String[] suites = sslSocket.getSupportedCipherSuites();
            sslSocket.setEnabledCipherSuites(suites);
            sslSocket.startHandshake();

            PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

            out.println(you.getValNumber());
            out.println(you.getID());
            out.println(you.getVote());

            //See if it was accepted
            String fromCTF=in.readLine();
            System.out.println("From CTF. Your vote was: " + fromCTF);

            sslSocket.close();

        } catch (Exception e) {
            System.err.println(e.getCause());
            System.exit(1);
        }
    }
}
