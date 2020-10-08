import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SSHLogMessage {
    String COMM;
    String SYSLOG_TIMESTAMP;
    String MESSAGE;
    public SSHLogMessage(String JSON) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(JSON);
        this.COMM = jsonNode.get("_COMM").asText();
        if (COMM.contains("sshd")) {
            this.SYSLOG_TIMESTAMP = jsonNode.get("SYSLOG_TIMESTAMP").asText();
            this.MESSAGE = jsonNode.get("MESSAGE").asText();
        }
    }

    public String getSYSLOG_TIMESTAMP(){
        return SYSLOG_TIMESTAMP;
    }

    public String getMESSAGE(){
        return MESSAGE;
    }

}
