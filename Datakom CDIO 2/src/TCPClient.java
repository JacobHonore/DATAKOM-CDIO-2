import java.io.*; 
import java.net.*; 
class TCPClient { 

	public static void main(String[] args) throws Exception { 

		String sentence; 
		String modifiedSentence; 

		BufferedReader inFromUser = 
				new BufferedReader(new InputStreamReader(System.in)); 

		Socket clientSocket = new Socket("localhost", 8000); 

		DataOutputStream outToServer = 
				new DataOutputStream(clientSocket.getOutputStream()); 

		BufferedReader inFromServer = 
				new BufferedReader(new
						InputStreamReader(clientSocket.getInputStream())); 

		try{
			do{
				System.out.println("Available commands: " +
						"\nPress 1 for 'Read Scale'" +
						"\nPress 2 for 'Tare Scale' " +
						"\nPress 3 for 'Zero Scale' " +
						"\nPress 4 for 'Display Text on Scale'" +
						"\nPress 5 for 'Display Weight on Scale'" +
						"\nPress 6 for 'Display Text on Scale, and recieve answer'" +
						"\nPress 7 for 'Quit'");
				sentence = inFromUser.readLine();

				switch (sentence) {

				case "1":
					outToServer.writeBytes("S\r\n");
					break;

				case "2":
					outToServer.writeBytes("T\r\n");
					break;

				case "3":
					outToServer.writeBytes("Z\r\n");
					break;

				case "4":
					outToServer.writeBytes("D hi \r\n");
					break;

				case "5":
					outToServer.writeBytes("DW\r\n");
					break;
					
				case "6":
					System.out.println("Indtast foresp��rgsel: ");
					String txt = inFromUser.readLine();
					outToServer.writeBytes("RM20 8 \"" + txt + "\" \"\" \"&3\"\r\n");
					break;

				case "7":
					System.out.println("Programmet lukkes");
					outToServer.writeBytes("Q\r\n");
					break;
				}

				modifiedSentence = inFromServer.readLine(); 

				System.out.println("FROM SERVER: " + modifiedSentence); 
			}
			while(!sentence.equals("7"));	
		}
		catch(SocketException e){
			System.out.println("Connection lost!");
		}

		clientSocket.close(); 
		inFromUser.close();
	} 
} 



