package CLAServer;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class CLA {
    static final String KEYSTORE = "src/ElectionKey.jks";
    static final String TRUSTSTORE = "src/TrustStore";
    static final String STORE_PSWD= "clairvoyant";
    static final String ALIAS_PSWD = "clairvoyant";


    public static void main(String [] args) {

        //ArrayList<Voter> voters = putAllVoters();
        Voter last = null;

        try {
            ArrayList<Voter> voters = putAllVoters();
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

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

//            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(9760);

            while(true) {
                SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();
                String[] suites = sslSocket.getSupportedCipherSuites();
                sslSocket.setEnabledCipherSuites(suites);

                PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

                Validation validation = new Validation(voters);
                String response = validation.validVoter(in.readLine());
                System.out.println(response);
                out.println(response);

                String inputLine;
                String outputLine;
                boolean issuedNum = false;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    outputLine = validation.validVoter(inputLine);
                    out.println(outputLine);
                    if (outputLine.equals("Already issued Validation Number")) {
                        issuedNum = true;
                        break;
                    }
                    if (validation.getState() == 10) {
                        last = validation.getLast();

                        out.println("Bye.");
                        System.out.println("CLA Bye.");
                        break;
                    }
                }


                sslSocket.close();
                printResults(voters);
                CLA2CTF c2c = new CLA2CTF();
                if (last != null)
                    c2c.sendCTF(last);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<Voter> putAllVoters() throws FileNotFoundException {
        Scanner validVoters = new Scanner(new FileReader("src/Voters"));
        ArrayList<Voter> voters = new ArrayList<>();
        while (validVoters.hasNext()) {
            String[] voter = validVoters.nextLine().split(",");
//            System.out.println(voter[0]);
//            System.out.println(voter[1]);
//            System.out.println(voter[2]);

            voters.add(new Voter(voter[0], Integer.parseInt(voter[1]), voter[2]));
        }
        return voters;
    }

    private static void printResults(ArrayList<Voter> list) {
        System.out.println("\nRESULTS");
        ListIterator<Voter> iterator = list.listIterator();
        while(iterator.hasNext()){
            Voter aVoter = iterator.next();
            System.out.println("Voter Name: " +aVoter.getName() + " Val#: " + aVoter.getValNumber());
        }
    }
}
