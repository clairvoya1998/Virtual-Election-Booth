import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class Voter {
    private String ID; // a random number
    private String Name;
    private String Vote; // the name of person voted
    private int studentID;
    private String dateOfBirth;
    private String valNumber;

    private String randomID() {
        Random rdm = new SecureRandom();
        byte bytes[] = new byte[16];
        rdm.nextBytes(bytes);
        return Arrays.toString(bytes);
    }

    public Voter(String voterName, String voterVote, int studentID, String dateOfBirth) {
        this.ID = randomID();
        this.Name = voterName;
        this.Vote = voterVote;
        this.studentID = studentID;
        this.dateOfBirth = dateOfBirth;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getVote() {
        return Vote;
    }

    public int getStudentID() {
        return studentID;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getValNumber() {
        return valNumber;
    }

    public String setValNumber(String valNumber) {
        return this.valNumber = valNumber;
    }
}
