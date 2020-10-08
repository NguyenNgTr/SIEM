import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SSHFailedLogMessage {
    String COMM;
    String SYSLOG_TIMESTAMP;
    String MESSAGE;
    public SSHFailedLogMessage(String MESSAGE, String SYSLOG_TIMESTAMP) {
            this.SYSLOG_TIMESTAMP = SYSLOG_TIMESTAMP;
            this.MESSAGE = MESSAGE;
    }

    public String getSYSLOG_TIMESTAMP(){
        return SYSLOG_TIMESTAMP;
    }

    public String getMESSAGE(){
        return MESSAGE;
    }

}
