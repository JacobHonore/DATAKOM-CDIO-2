package weigth;

import java.io.*; 
import java.net.*; 
class TCPClient { 

	public static void main(String[] args) throws Exception { 

		String sentence, modifiedSentence, txt, oprID, item;
		int itemNo, temp1;
		String[] temp = new String[10];

		BufferedReader inFromUser = 
				new BufferedReader(new InputStreamReader(System.in)); 

		Socket clientSocket = new Socket("localhost", 4567); 

		DataOutputStream outToServer = 
				new DataOutputStream(clientSocket.getOutputStream()); 

		BufferedReader inFromServer = 
				new BufferedReader(new
						InputStreamReader(clientSocket.getInputStream())); 
		BufferedReader inFromLocal = new BufferedReader(new FileReader(new File("store.txt")));

		//		do{
		//sekvens 1 
		txt = "Indtast ID";
		outToServer.writeBytes("RM20 8 \"" + txt + "\" \" \" \"&3\"\r\n");

		//sekvens 2
		String temp2 = inFromServer.readLine();
		//		temp2 = inFromServer.readLine();
		//		temp2 = inFromServer.readLine();
		System.out.println("" + temp2);
		if(temp2.equals("RM20 B")){
			System.out.println("" + temp2);
			temp2 = inFromServer.readLine();
			temp = temp2.split(" ");
			oprID = temp[2];		
			System.out.println("" + oprID);
			

			//sekvens 3
			txt = "Indtast varenummer";
			outToServer.writeBytes("RM20 4 \"" + txt + "\" \" \" \"&3\"\r\n");

			//sekvens 4
			temp2 = inFromServer.readLine();
			temp = temp2.split(" ");
			System.out.println("" + temp2);
			if(temp2.equals("RM20 B")){
				temp2 = inFromServer.readLine();
				temp = temp2.split(" ");
				itemNo = Integer.parseInt(temp[2]);

				//sekvens 5
				boolean notFound = true;
				while(notFound){
					temp = inFromLocal.readLine().split(",");
					temp1 = Integer.parseInt(temp[0]);

					if(temp1 == itemNo){
						notFound = false;
						item = temp[1];
						txt = "Vare: " + item + "\nTast 1 for OK eller 0 for Annuller"; 
						outToServer.writeBytes("RM20 4 \"" + txt + "\" \" \" \"&3\"\r\n");
					}
				}
			}
		}



		inFromLocal.readLine();


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

		clientSocket.close(); 
		inFromUser.close();
		inFromLocal.close();
	} 
} 



