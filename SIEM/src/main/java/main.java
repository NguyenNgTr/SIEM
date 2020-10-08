import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;

import com.espertech.esper.runtime.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class main {
    public static EPRuntime initSSHLogMessageRuntime() {
        // Create new compiler
        EPCompiler epCompiler = EPCompilerProvider.getCompiler();
        //Create new configuration
        Configuration configuration = new Configuration();
        // Add event into configuration
        configuration.getCommon().addEventType(SSHFailedLogMessage.class);
        configuration.getCommon().addEventType(SSHLogMessage.class);
        // Passing configuration to runtime
        EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        // Create EPL statement
        CompilerArguments args = new CompilerArguments(configuration);
        EPCompiled epCompiled;
        try {
            epCompiled = epCompiler.compile("@name('SSHLogMessage') SELECT * FROM SSHLogMessage WHERE MESSAGE LIKE \'%Failed password%\'", args);
        }
        catch (EPCompileException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }
        // Deploy EPL Compiled Module
        EPDeployment deployment;
        try {
            deployment = runtime.getDeploymentService().deploy(epCompiled);
        }
        catch (EPDeployException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }
        // Attach callback to listener
        EPStatement statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "SSHLogMessage");
        statement.addListener( (newData, oldData, statement1, runtime1) -> {
            String MESSAGE = (String) newData[0].get("MESSAGE");
            String SYSLOG_TIMESTAMP = (String) newData[0].get("SYSLOG_TIMESTAMP");
            System.out.println(String.format(MESSAGE + " at " + SYSLOG_TIMESTAMP));
            //Passing next object to runtime
            runtime.getEventService().sendEventBean(new SSHFailedLogMessage(MESSAGE, SYSLOG_TIMESTAMP), "SSHFailedLogMessage");
        });
        /*--------------------------------------------------------------------------------------------------------------------------------------*/

        EPCompiled epCompiled2;
        try {
            epCompiled2 = epCompiler.compile("@name('SSHFailedLogMessage') SELECT * FROM SSHFailedLogMessage " +
                    " match_recognize (" +
                    "   MEASURES A as A, B as B, C as C" +
                    "   PATTERN (A B C)" +
                    "   DEFINE " +
                    "     A as A.MESSAGE LIKE \'%Failed password%\'," +
                    "     B as B.MESSAGE LIKE \'%Failed password%\' and B.IP LIKE A.IP," +
                    "     C as C.MESSAGE LIKE \'%Failed password%\' and C.IP LIKE A.IP)", args);
        }
        catch (EPCompileException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }

        EPDeployment deployment2;
        try {
            deployment2 = runtime.getDeploymentService().deploy(epCompiled2);
        }
        catch (EPDeployException ex) {
            // handle exception here
            throw new RuntimeException(ex);
        }

        EPStatement statement2 = runtime.getDeploymentService().getStatement(deployment2.getDeploymentId(), "SSHFailedLogMessage");
        statement2.addListener( (newData, oldData, statement1, runtime1) -> {
            String IP = (String) newData[0].get("C.IP");
            String SYSLOG_TIMESTAMP = (String) newData[0].get("C.SYSLOG_TIMESTAMP");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println(String.format("Three failed login attempts from " + IP + " at " + SYSLOG_TIMESTAMP));
            System.out.println("\n");
        });
        return runtime;
    }

    public static void main(String[] args) throws IOException{
        EPRuntime SSHLogMessageRuntime = initSSHLogMessageRuntime();
        while (true) {
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", "journalctl -u ssh.service -o json");
            Process process = builder.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ArrayList<String> json = readFromBuffer(reader);

            String JSON;
            for (int i = 0; i < json.size(); i++) {
                JSON = json.get(i);
                SSHLogMessage sshLogMessage = new SSHLogMessage(JSON);
                SSHLogMessageRuntime.getEventService().sendEventBean(sshLogMessage, "SSHLogMessage");
            }
            break;
        }
    }

    public static ArrayList<String> readFromBuffer(BufferedReader bufferedReader) throws IOException {
        String line;
        ArrayList<String> result = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null)
            try{
                result.add(line);
            } catch (Exception e){
                System.out.println(e);
            }
        return result;
    }
}
