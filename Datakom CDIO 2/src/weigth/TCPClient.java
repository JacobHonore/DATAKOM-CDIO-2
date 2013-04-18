package weigth;

import java.io.*; 
import java.net.*; 
class TCPClient { 

	private String sentence, modifiedSentence, txt, input;
	private int itemNo, tempInt;
	private String[] temp = new String[10];



	public static void main(String[] args) throws Exception { 

		TCPClient t = new TCPClient();
		BufferedReader inFromUser = 
				new BufferedReader(new InputStreamReader(System.in)); 

		Socket clientSocket = new Socket("localhost", 4567); 

		DataOutputStream outToServer = 
				new DataOutputStream(clientSocket.getOutputStream()); 

		BufferedReader inFromServer = 
				new BufferedReader(new
						InputStreamReader(clientSocket.getInputStream())); 
		BufferedReader inFromLocal = new BufferedReader(new FileReader(new File("store.txt")));

		t.sekvens1(inFromLocal, inFromServer, outToServer);
	}



	public void sekvens1(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		txt = "Indtast ID";
			outToServer.writeBytes("RM20 8 \"" + txt + "\" \" \" \"&3\"\r\n");
			this.sekvens2(inFromLocal, inFromServer, outToServer);
	}
	public void sekvens2(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		String temp2 = inFromServer.readLine();
		//		temp2 = inFromServer.readLine();
		//		temp2 = inFromServer.readLine();
		System.out.println("" + temp2);
		if(temp2.equals("RM20 B")){
			System.out.println("" + temp2);
			temp2 = inFromServer.readLine();
			temp = temp2.split(" ");
			input = temp[2];		
			System.out.println("" + input);
			this.sekvens3(inFromLocal, inFromServer, outToServer);
		}
	}


	public void sekvens3(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		//Sekvens 3 kunne kombineres med sekvens 4.
		txt = "Indtast varenummer";
		outToServer.writeBytes("RM20 4 \"" + txt + "\" \" \" \"&3\"\r\n");
		this.sekvens4(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens4(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		String temp2 = inFromServer.readLine();
		temp = temp2.split(" ");
		System.out.println("" + temp2);
		if(temp2.equals("RM20 B")){
			temp2 = inFromServer.readLine();
			temp = temp2.split(" ");
			itemNo = Integer.parseInt(temp[2]);
			System.out.println("" + itemNo);
			this.sekvens5(inFromLocal, inFromServer, outToServer);
		}
	}

	public void sekvens5(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		boolean notFound = true;
		while(notFound){
			temp = inFromLocal.readLine().split(",");
			tempInt = Integer.parseInt(temp[0]);

			if(tempInt == itemNo){
				notFound = false;
				input = temp[1];
				txt = "Vare: " + input + "\nTast 1 for OK eller 0 for Annuller"; 
				outToServer.writeBytes("RM20 4 \"" + txt + "\" \" \" \"&3\"\r\n");

				String temp2 = inFromServer.readLine();
				//		temp2 = inFromServer.readLine();
				//		temp2 = inFromServer.readLine();
				temp = temp2.split(" ");
				System.out.println("" + temp2);
				if(temp2.equals("RM20 B"))
				{
					temp2 = inFromServer.readLine();
					temp = temp2.split(" ");
					input = temp[2];		
					System.out.println("" + input);
					if(input.equals("1"))
					{
						this.sekvens6(inFromLocal, inFromServer, outToServer);
					}
					//Fejl: Kan annullere, men kan derefter ikke vælge samme vare igen.
					else if(input.equals("0"))
					{
						this.sekvens3(inFromLocal, inFromServer, outToServer);
					}
				}
			}
		}
	}

	public void sekvens6(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		txt = "Anbring eventuelt en skaal og tarer.";
		outToServer.writeBytes("RM20 4 \"" + txt + "\" \" \" \"&3\"\r\n");
		this.sekvens7(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens7(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		txt = "Vaegten tareres";
		outToServer.writeBytes("RM46 1 0 \"" + txt + "\" \" \" \"&3\"\r\n");
	}
}


	//		}
	//		while();

	//		try{
	//			do{
	//				System.out.println("Available commands: " +
	//						"\nPress 1 for 'Read Scale'" +
	//						"\nPress 2 for 'Tare Scale' " +
	//						"\nPress 3 for 'Zero Scale' " +
	//						"\nPress 4 for 'Display Text on Scale'" +
	//						"\nPress 5 for 'Display Weight on Scale'" +
	//						"\nPress 6 for 'Display Text on Scale, and recieve answer'" +
	//						"\nPress 7 for 'Quit'");
	//				sentence = inFromUser.readLine();
	//
	//				switch (sentence) {
	//
	//				case "1":
	//					outToServer.writeBytes("S\r\n");
	//					break;
	//
	//				case "2":
	//					outToServer.writeBytes("T\r\n");
	//					break;
	//
	//				case "3":
	//					outToServer.writeBytes("Z\r\n");
	//					break;
	//
	//				case "4":
	//					outToServer.writeBytes("D hi \r\n");
	//					break;
	//
	//				case "5":
	//					outToServer.writeBytes("DW\r\n");
	//					break;
	//					
	//				case "6":
	//					System.out.println("Indtast forespørgsel: ");
	//					String txt = inFromUser.readLine();
	//					outToServer.writeBytes("RM20 8 \"" + txt + "\" \"\" \"&3\"\r\n");
	//					break;
	//
	//				case "7":
	//					System.out.println("Programmet lukkes");
	//					outToServer.writeBytes("Q\r\n");
	//					break;
	//				}
	//
	//				modifiedSentence = inFromServer.readLine(); 
	//
	//				System.out.println("FROM SERVER: " + modifiedSentence); 
	//			}
	//			while(!sentence.equals("7"));	
	//		}
	//		catch(SocketException e){
	//			System.out.println("Connection lost!");
	//		}



