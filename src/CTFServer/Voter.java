package CTFServer;

public class Voter {
//    private String name;
//    private String dateOfbirth;
//    private int studentID;
    private String valNumber;

    private Boolean hasVoted;
    private String vote;
    private String selfID;


//    public Voter(String name, int studentID, String dateOfbirth, String valNumber) {
//        this.name = name;
//        this.dateOfbirth = dateOfbirth;
//        this.studentID = studentID;
//        this.valNumber = valNumber;
//        hasVoted = false;
//    }

    public Voter(String valNumber) {
        this.valNumber = valNumber;
        this.hasVoted = false;
    }

//    public String toString(){
//        return "From CLA: name: " +name+" studentID: "+ studentID + "DateOfBirth: " + dateOfbirth +" valNumber: "+valNumber+"\n";
//    }

    public String toString(){
        return "From CLA: valNumber: "+valNumber+"\n";
    }

//    public String getName(){
//        return name;
//    }

//    public String getDateOfbirth() {
//        return dateOfbirth;
//    }

//    public void setDateOfbirth(String dateOfbirth) {
//        this.dateOfbirth = dateOfbirth;
//    }

//    public int getStudentID() {
//        return studentID;
//    }

//    public void setStudentID(int studentID) {
//        this.studentID = studentID;
//    }

    public String getValNumber(){
        return valNumber;
    }

//    public void setName(String name){
//        this.name=name;
//    }
    public void setvalNumber(String valNumber){
        this.valNumber=valNumber;
    }

//    public boolean equals(Object o){
//        if(o instanceof Voter){
//            Voter voter = (Voter) o;
//            if(voter.name.equals(this.name)&&voter.studentID==this.studentID&&voter.dateOfbirth.equals(this.dateOfbirth)&&voter.valNumber.equals(this.valNumber))
//                return true;
//            return false;
//        }
//        else
//            return false;
//    }
    public String getSelfID() {
        return selfID;
    }
    public void setSelfID(String selfID) {
        this.selfID = selfID;
    }
    public String getVote() {
        return vote;
    }
    public void setVote(String vote) {
        this.vote = vote;
    }
    public Boolean getHasVoted() {
        return hasVoted;
    }
    public void setHasVoted(Boolean hasVoted) {
        this.hasVoted = hasVoted;
    }
}
