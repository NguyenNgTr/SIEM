public class SSHFailedLogMessage {
    String SYSLOG_TIMESTAMP;
    String MESSAGE;
    String IP;

    public SSHFailedLogMessage(String MESSAGE, String SYSLOG_TIMESTAMP){
        this.MESSAGE = MESSAGE;
        this.SYSLOG_TIMESTAMP = SYSLOG_TIMESTAMP;
        String[] a = this.MESSAGE.split(" ");
        for (int i = 0; i < a.length; i++){
            if (a[i].equals("from")){
                this.IP = a[i+1];
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
