import javax.net.ssl.*;
import javax.swing.*;
import java.io.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Scanner;


public class VotingClient {
    static final String KEYSTORE = "src/ElectionKey.jks";
    static final String TRUSTSTORE = "src/TrustStore";
    static final String STORE_PSWD= "clairvoyant";
    static final String ALIAS_PSWD = "clairvoyant";
    static ArrayList<String> CANDIDATES = new ArrayList<>();

    public static void main(String [] args) {
        String serverAddress = "localhost";

        try {
            Scanner candidateScanner = new Scanner(new FileReader("src/Candidates"));
            while (candidateScanner.hasNext()) {
                String name = candidateScanner.nextLine();
                CANDIDATES.add(name);
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }

        boolean gotVal = false;
        Voter me = GUI();
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

//            SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket)sslSocketFactory.createSocket(serverAddress,9760);
            String[] suites = sslSocket.getSupportedCipherSuites();
            sslSocket.setEnabledCipherSuites(suites);

            sslSocket.startHandshake();

            PrintWriter out = new PrintWriter(sslSocket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

            out.println("Requesting Validation Number");
            out.println(me.getName());
            out.println(me.getStudentID());
            out.println(me.getDateOfBirth());
            out.println("Getting Validation Number");

            boolean sendingVal = false;
            String fromCLA;

            while((fromCLA = in.readLine()) != null) {
                System.out.println("From CLA: " + fromCLA);

                if (gotVal=fromCLA.equals("Bye.")) {
                    System.out.println("Client Bye.");
                    break;
                }
                if (fromCLA.equals("denied.")) {
                    System.out.println("Denied.");
                    break;
                }
                if (sendingVal) {
                    System.out.println(me.setValNumber(fromCLA));
                    gotVal = true;
                }
                if (fromCLA.equals("Allocating Validation Number")) {
                    sendingVal = true;
                }
            }

            out.close();
            sslSocket.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

        if (gotVal) {
            Vote2CTF v2ctf = new Vote2CTF(me);
        }
    }

    public static Voter GUI() {
        int comboSize = CANDIDATES.size();
        String[] candidates = new String[comboSize];
        for(int i = 0; i < comboSize; i++) {
            candidates[i] = CANDIDATES.get(i);
        }


        JTextField nameField = new JTextField(5);
        JTextField IDField = new JTextField(5);
//        JTextField voteField = new JTextField(5);
        JComboBox<String> CandidateList = new JComboBox<>(candidates);
        JTextField dayField = new JTextField(5);
        JTextField monthField = new JTextField(5);
        JTextField yearField = new JTextField(5);

        JPanel myPanel = new JPanel();

//        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));

        myPanel.add(new JLabel("Name:"));
        myPanel.add(nameField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Student ID: "));
        myPanel.add(IDField);
        myPanel.add(new JLabel("Date Of Birth: "));
        myPanel.add(yearField);
        myPanel.add(new JLabel("Year"));
        myPanel.add(monthField);
        myPanel.add(new JLabel("Month"));
        myPanel.add(dayField);
        myPanel.add(new JLabel("Day  Vote: "));
        myPanel.add(CandidateList);


        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Enter your name, student ID, Vote and date of birth", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            //System.out.println("name: " + nameField.getText());
            //System.out.println("address: " + addressField.getText());

            String name = nameField.getText();
            if (name.equals("")) {
                System.out.println("Please input your name next time.");
                System.exit(1);
            }
//            String vote = voteField.getText();
//            if (vote.equals("")) {
//                System.out.println("Please input your vote next time.");
//                System.exit(1);
//            }

            String vote = (String) CandidateList.getSelectedItem();

            int ID = Integer.parseInt(IDField.getText());
            String day = dayField.getText();
            String month = monthField.getText();
            String year = yearField.getText();

            if (Integer.parseInt(day) < 0 || Integer.parseInt(day) > 31) {
                System.out.println("Invalid Date of Birth.");
                System.exit(1);
            }

            if (Integer.parseInt(month) < 0 || Integer.parseInt(month) > 12) {
                System.out.println("Invalid Date of Birth.");
                System.exit(1);
            }

            if (Integer.parseInt(year) < 1800) {
                System.out.println("Invalid Date of Birth.");
                System.exit(1);
            }

            if (day.length() == 1) {
                day = "0" + day;
            }

            if (month.length() == 1) {
                month = "0" + month;
            }


            String date = year + "-" + month  + "-" + day;

            Voter me = new Voter(name, vote, ID, date);
            return me;

        } else {
            System.out.println("Cancelled.");
            System.exit(1);
        }
        return null;
    }

}