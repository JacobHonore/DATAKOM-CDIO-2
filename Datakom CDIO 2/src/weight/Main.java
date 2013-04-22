package weight;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {




	public static void main(String[] args) throws Exception { 

		Sequences s = new Sequences();

		Socket clientSocket = new Socket("169.254.2.3", 8000); 

		DataOutputStream outToServer = 
				new DataOutputStream(clientSocket.getOutputStream()); 

		BufferedReader inFromServer = 
				new BufferedReader(new
						InputStreamReader(clientSocket.getInputStream())); 
		

		s.sequence1(inFromServer, outToServer);

		clientSocket.close();
	}

}
