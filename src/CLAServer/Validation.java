package CLAServer;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;

public class Validation{
    private static final int START = 0;
    private static int NAME=1;
    private static int STUDENTID=2;
    private static int DOB=3;
    private static int EXIST = 4;
    private static int FINISHED=10;
    private int state = START;

    ArrayList<Voter> voters = new ArrayList<>();
    Voter last;
    String currentName;
    int currentStudentID;
    String currentDOB;

    public Validation(ArrayList<Voter> voters) {
        this.voters = voters;
    }

    public ArrayList<Voter> getVoters() {
        return voters;
    }

    public Voter getLast() {
        return last;
    }

    public int getState() {
        return state;
    }

    public String validVoter(String input) {
        String output = null;

        if (state != FINISHED) {
            if (input.equals("Requesting Validation Number")) {
                output = "Requeting Voter's Name";
                state = NAME;

            } else if (state == NAME) {
                Iterator<Voter> iterator = voters.iterator();
                while (iterator.hasNext()) {
                    Voter voter = iterator.next();
                    if (voter.getName().equalsIgnoreCase(input)) {
                        output = "Requesting Voter's Student ID";
                        System.out.println(output);
                        currentName = input;
                        state = STUDENTID;
                        return output;
                    }
                }
                output = "denied.";

            } else if (state == STUDENTID) {
                Iterator<Voter> iterator = voters.iterator();
                while (iterator.hasNext()) {
                    Voter voter = iterator.next();
                    if (voter.getName().equalsIgnoreCase(currentName)) {
                        if (voter.getStudentID() == Integer.parseInt(input)) {
                            output = "Requesting Voter's Date Of Birth";
                            System.out.println(output);
                            currentStudentID = Integer.parseInt(input);
                            state = DOB;
                            return output;
                        }
                    }
                }
                output = "denied.";

            } else if (state == DOB) {
                Iterator<Voter> iterator = voters.iterator();
                while (iterator.hasNext()) {
                    Voter voter = iterator.next();
                    if (voter.getName().equalsIgnoreCase(currentName)) {
                        if (voter.getStudentID() == currentStudentID) {
                            if (voter.getDateOfBirth().equalsIgnoreCase(input)) {
                                output = "Allocating Validation Number";
                                System.out.println(output);
                                currentDOB = input;
                                state = EXIST;
                                return output;
                            }
                        }
                    }
                }
                output = "denied.";

            } else if (state == EXIST) {
                System.out.println("testing");
                ListIterator<Voter> iterator = voters.listIterator();
                while (iterator.hasNext()) {
                    Voter voter = iterator.next();
                    if (voter.getName().equalsIgnoreCase(currentName)) {
                        if (voter.getStudentID() == currentStudentID) {
                            if (voter.getDateOfBirth().equalsIgnoreCase(currentDOB)) {
                                if (voter.getValNumber() == null) {
                                    String valNum = randomValNum();
                                    output = valNum;
                                    voter.setvalNumber(valNum);
                                    iterator.set(voter);
                                    last = voter;
                                    //System.out.println(out+aVoter.toString());
                                    state = FINISHED;
                                    return output;
                                }
                            }
                        }
                    }
                }
                output = "Already issued Validation Number";
            }
        }
        return output;
    }

    public String randomValNum() {
        Random rdm = new SecureRandom();
        byte bytes[] = new byte[16];
        rdm.nextBytes(bytes);
        return Arrays.toString(bytes);
    }
}
