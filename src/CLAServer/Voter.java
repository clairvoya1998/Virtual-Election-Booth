package CLAServer;

public class Voter {
    private String name;
    private int studentID;
    private String dateOfBirth;
    private String valNumber = null;

    public Voter(String name, int studentID, String dateOfBirth){
        this.name=name;
        this.studentID=studentID;
        this.dateOfBirth = dateOfBirth;
    }
    public String toString(){
        return "name: " + name +" studentID: "+studentID+" valNumber: "+valNumber;
    }

    public String getName(){
        return name;
    }

    public int getStudentID() {
        return studentID;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getValNumber(){
        return valNumber;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setvalNumber(String valNumber){
        this.valNumber=valNumber;
    }

    public boolean equals(Object o){
        if(o instanceof Voter){
            Voter voter = (Voter) o;
            if(voter.name==this.name&&voter.studentID==voter.studentID&&voter.valNumber==this.valNumber)
                return true;
            return false;
        }
        else
            return false;
    }
}
