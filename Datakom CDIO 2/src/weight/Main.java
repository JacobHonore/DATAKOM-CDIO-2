package weight;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {




	public static void main(String[] args) throws Exception { 

		Sequences s = new Sequences();

		Socket clientSocket = new Socket("localhost", 4567); 

		DataOutputStream outToServer = 
				new DataOutputStream(clientSocket.getOutputStream()); 

		BufferedReader inFromServer = 
				new BufferedReader(new
						InputStreamReader(clientSocket.getInputStream())); 
		BufferedReader inFromLocal = new BufferedReader(new FileReader(new File("store.txt")));

		s.sequence1(inFromLocal, inFromServer, outToServer);

		clientSocket.close();
	}

}
