package CTFServer;

import javax.net.ssl.*;
import javax.swing.*;
import java.io.*;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;

public class CTF {
    static final String KEYSTORE = "src/ElectionKey.jks";
    static final String TRUSTSTORE = "src/TrustStore";
    static final String STORE_PSWD= "clairvoyant";
    static final String ALIAS_PSWD = "clairvoyant";
    static HashMap<String, Integer> CANDIDATES = new HashMap<>();

    private static void loadCandidates() throws FileNotFoundException{
        Scanner candidateScanner = new Scanner(new FileReader("src/Candidates"));
        while (candidateScanner.hasNext()) {
            String name = candidateScanner.nextLine();
            CANDIDATES.put(name, 0);
        }
    }

    private static int loadValidVoters() throws FileNotFoundException{
        Scanner voterScan = new Scanner(new FileReader("src/Voters"));
        int votersCounter = 0;
        while(voterScan.hasNext()) {
            voterScan.nextLine();
            votersCounter++;
        }
        return votersCounter;
    }

    public static void main(String[] args) {

        boolean BallotClosed = false;
        ArrayList<Voter> voters = new ArrayList<Voter>();

        try {
            loadCandidates();
        } catch (FileNotFoundException e) {
            e.getMessage();
        }

        String GUIinput = GUI();
        if (GUIinput.equals("Invalid Date.") || GUIinput.equals("Cancelled")) {
            System.out.println(GUIinput);
            System.exit(1);
        }

        String[] date = new String[5];
        date = GUIinput.split("-");


        try {

            int votersCounter = loadValidVoters();

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

            SSLServerSocketFactory sslServerSocketfactory = sslContext.getServerSocketFactory();

            SSLServerSocket sslServerSocket = (SSLServerSocket)sslServerSocketfactory.createServerSocket(9761);
            SSLServerSocket sslServerSocket2 = (SSLServerSocket)sslServerSocketfactory.createServerSocket(9762);


            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd 'at' hh:mm");
            String setup = date[0] + "-" + date[1] + "-" + date[2] + " at " + date[3] + ":" + date[4];
            dateFormatter.parse(setup);
            Date myset = dateFormatter.parse(setup);

            while(true){

                Date current = new Date();
                if (current.after(myset)) {
                    BallotClosed = true;
                    break;
                }

                SSLSocket sslSocket = (SSLSocket)sslServerSocket.accept();
                String[] suites = sslSocket.getSupportedCipherSuites();
                sslSocket.setEnabledCipherSuites(suites);
                sslSocket.startHandshake();

                PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

                //Just read in the name, studentID, date of birth, and valNumber
//                Voter newVoter= new Voter(in.readLine(), Integer.parseInt(in.readLine()), in.readLine(), in.readLine());
                Voter newVoter= new Voter(in.readLine());

                System.out.println(newVoter.toString());
                voters.add(newVoter);
                sslSocket.close();

                SSLSocket sslSocket2 = (SSLSocket)sslServerSocket2.accept();
                String[] suites2 = sslSocket2.getSupportedCipherSuites();
                sslSocket2.setEnabledCipherSuites(suites2);

                PrintWriter out2 = new PrintWriter(sslSocket2.getOutputStream(), true);
                BufferedReader in2 = new BufferedReader(new InputStreamReader(sslSocket2.getInputStream()));

                String valID= in2.readLine();
                String selfID= in2.readLine();
                String vote= in2.readLine();
                System.out.println("Read vote: " + vote + " " + valID + " " + selfID);

                //TODO check if val# is in list, save vote, hasVoted, and self ID in Voter, make print results method
                boolean foundVoter =false;

                ListIterator<Voter> iterator = voters.listIterator();
                while(iterator.hasNext()){
                    Voter aVoter = iterator.next();
                    System.out.println(aVoter.getValNumber());

                    if(aVoter.getValNumber().equalsIgnoreCase(valID)&&aVoter.getHasVoted()){
                        out2.println("Already voted");
                        foundVoter=true;
                    }
                    if(aVoter.getValNumber().equalsIgnoreCase(valID)&& !aVoter.getHasVoted()){
                        out2.println("Vote accepted");
                        aVoter.setSelfID(selfID);
                        aVoter.setVote(vote);
                        aVoter.setHasVoted(true);
                        iterator.set(aVoter);
                        foundVoter=true;
                        votersCounter--;
                    }
                }
                if(!foundVoter){
                    out2.println("Validation ID not found");
                }

                if (votersCounter == 0) {
                    System.out.println("Everyone has voted.");
                    BallotClosed = true;
                    break;
                }


                sslSocket2.close();
            }

            if (BallotClosed) {
                System.out.println("The Ballot is closed, thank you for participating!");
                printResults(voters);
                System.exit(1);
            }

        }catch (Exception e) {
            System.err.println("Server Error: " + e.getMessage());
            System.err.println("Localized: " + e.getLocalizedMessage());
            System.err.println("Stack Trace: " + e.getStackTrace());
            System.err.println("To String: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void printResults(List<Voter> list) {
        System.out.println("\nList of IDs and Votes");
        ListIterator<Voter> iterator = list.listIterator();

        while(iterator.hasNext()){
            Voter aVoter = iterator.next();
            String aVote = aVoter.getVote();
            if (CANDIDATES.containsKey(aVote)) {
                int tmpCounter = CANDIDATES.get(aVote);
                tmpCounter++;
                CANDIDATES.put(aVote, tmpCounter);
            }
        }

        String winner = null;
        int voteCount = 0;
        boolean tie = false;
        HashMap<String, Integer> tieCandidates = new HashMap<>();

        for (String key : CANDIDATES.keySet()) {
            if (CANDIDATES.get(key) > voteCount) {
                winner = key;
                tie = false;
                tieCandidates.clear();
                voteCount = CANDIDATES.get(key);
            }
            else if (CANDIDATES.get(key) == voteCount) {
                if (winner == null) {
                    winner = key;
                    tie = false;
                }
                else {
                    tie = true;
                    tieCandidates.put(key, CANDIDATES.get(key));
                }
            }

            System.out.println("Votes for " + key + ": " + CANDIDATES.get(key));
        }

        if (tie) {
            tieCandidates.put(winner, voteCount);
            System.out.println("We have met a tie situdation, the winner will be further considered.");
            System.out.println("They are:");
            for (String key: tieCandidates.keySet()) {
                System.out.println(key);
            }
        }
        else if (winner != null) {
            System.out.println("The winner is " + winner + ", good job!");
        }
        else if (winner == null) {
            System.err.println("Counting System is compromised.");
        }
    }

    public static String GUI() {
        String output = null;

        JTextField dayField = new JTextField(5);
        JTextField monthField = new JTextField(5);
        JTextField yearField = new JTextField(5);
        JTextField hourField = new JTextField(5);
        JTextField minuteField = new JTextField(5);


        JPanel myPanel = new JPanel();

        myPanel.add(new JLabel("Year"));
        myPanel.add(yearField);
        myPanel.add(new JLabel("Month"));
        myPanel.add(monthField);
        myPanel.add(new JLabel("Day"));
        myPanel.add(dayField);
        myPanel.add(new JLabel("Hour"));
        myPanel.add(hourField);
        myPanel.add(new JLabel("Minute"));
        myPanel.add(minuteField);



        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Enter your Ballot Closure time please, in 24 hours form", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            //System.out.println("name: " + nameField.getText());
            //System.out.println("address: " + addressField.getText());


            int day = Integer.parseInt(dayField.getText());
            int month = Integer.parseInt(monthField.getText());
            int year = Integer.parseInt(yearField.getText());
            int hour = Integer.parseInt(hourField.getText());
            int minute = Integer.parseInt(minuteField.getText());

            int currentYear = Calendar.getInstance().get(Calendar.YEAR);


            if (day < 0 || day > 31) {
                output = "Invalid Date.";
            }

            else if (month < 0 || month > 12) {

                output = "Invalid Date.";
            }

            else if (year < currentYear) {

                output = "Invalid Date.";
            }

            else if (hour < 0 || hour > 24) {
                output = "Invalid Date.";
            }

            else if (minute < 0 || minute > 60) {
                output = "Invalid Date.";
            }


            else {
                String date = year + "-" + month + "-" + day + "-" + hour + "-" + minute;

                output = date;
            }

        } else {
            output = "Cancelled";
        }
        return output;
    }

}
