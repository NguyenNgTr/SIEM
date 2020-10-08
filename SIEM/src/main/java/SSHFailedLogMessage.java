public class SSHFailedLogMessage {
    String SYSLOG_TIMESTAMP;
    String MESSAGE;
    String IP;

    public SSHFailedLogMessage(String MESSAGE, String SYSLOG_TIMESTAMP){
        this.MESSAGE = MESSAGE;
        this.SYSLOG_TIMESTAMP = SYSLOG_TIMESTAMP;
        String[] splitMess = this.MESSAGE.split(" ");
        for (int i = 0; i < splitMess.length; i++){
            if (splitMess[i].equals("from")){
                this.IP = splitMess[i+1];
                break;
            }
        }
    }

    public String getSYSLOG_TIMESTAMP(){
        return SYSLOG_TIMESTAMP;
    }

    public String getMESSAGE(){
        return MESSAGE;
    }

    public String getIP(){
        return IP;
    }
}
