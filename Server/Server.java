import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public
class Server {
    public
            static
    void
            main(String[] arstring) {
        try {
            SSLServerSocketFactory sslServersocketFactory =
                    (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket =
                    (SSLServerSocket) sslServersocketFactory.createServerSocket(4443);
            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
            
            if (sslSocket.isConnected())
            	System.out.println("Connected to a client");
            
            InputStream inputStream = sslSocket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String command = "";
            String response = "";
            
            while ((command = bufferedReader.readLine()) != null) {
            	
					System.out.println("Command Recieved: "+command);

					if (command.toLowerCase().compareTo("hello")==0)
					    response = "Bingo !! You got it write!\n";
					else
					    response = "Type Hello to see a response\n";
					
					
					OutputStream outputStream = sslSocket.getOutputStream();
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
					outputStreamWriter.write(response, 0, response.length());
					outputStreamWriter.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}