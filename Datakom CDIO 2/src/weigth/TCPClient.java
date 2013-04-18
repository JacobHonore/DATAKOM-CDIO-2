package weigth;

import java.io.*; 
import java.net.*; 
import java.util.Calendar;
class TCPClient { 

	private String weightMsg, oprID, serverInput, itemName, userInput;
	private int itemNoInput, itemNoStore;
	private String[] splittedInput = new String[10];
	private double tara, netto, bruttoCheck;


	public static void main(String[] args) throws Exception { 

		TCPClient t = new TCPClient();

		Socket clientSocket = new Socket("localhost", 4567); 

		DataOutputStream outToServer = 
				new DataOutputStream(clientSocket.getOutputStream()); 

		BufferedReader inFromServer = 
				new BufferedReader(new
						InputStreamReader(clientSocket.getInputStream())); 
		BufferedReader inFromLocal = new BufferedReader(new FileReader(new File("store.txt")));

		t.sekvens1(inFromLocal, inFromServer, outToServer);

		clientSocket.close();
	}



	public void sekvens1(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		weightMsg = "Indtast ID";
		outToServer.writeBytes("RM20 8 \"" + weightMsg + "\" \" \" \"&3\"\r\n");
		this.sekvens2(inFromLocal, inFromServer, outToServer);
	}
	public void sekvens2(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		serverInput = inFromServer.readLine();
		//		serverInput = inFromServer.readLine();
		//		serverInput = inFromServer.readLine();
		if(serverInput.equals("RM20 B")){
			serverInput = inFromServer.readLine();
			splittedInput = serverInput.split(" ");
			oprID = splittedInput[2];		
			this.sekvens3(inFromLocal, inFromServer, outToServer);
		}
	}


	public void sekvens3(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		//Sekvens 3 kunne kombineres med sekvens 4.
		weightMsg = "Indtast varenummer";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");
		this.sekvens4(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens4(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		serverInput = inFromServer.readLine();
		splittedInput = serverInput.split(" ");
		if(serverInput.equals("RM20 B")){
			serverInput = inFromServer.readLine();
			splittedInput = serverInput.split(" ");
			itemNoInput = Integer.parseInt(splittedInput[2]);
			this.sekvens5(inFromLocal, inFromServer, outToServer);
		}
	}

	public void sekvens5(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		boolean notFound = true;
		while(notFound){
			splittedInput = inFromLocal.readLine().split(",");
			itemNoStore = Integer.parseInt(splittedInput[0]);

			if(itemNoStore == itemNoInput){
				notFound = false;
				itemName = splittedInput[1];
				weightMsg = "Vare: " + itemName + "\nTast 1 for OK eller 0 for Annuller."; 
				outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

				serverInput = inFromServer.readLine();
				//		serverInput = inFromServer.readLine();
				//		serverInput = inFromServer.readLine();
				splittedInput = serverInput.split(" ");
				if(serverInput.equals("RM20 B"))
				{
					serverInput = inFromServer.readLine();
					splittedInput = serverInput.split(" ");
					userInput = splittedInput[2];		
					if(userInput.equals("1"))
					{
						this.sekvens7(inFromLocal, inFromServer, outToServer);
					}
					//Fejl: Kan annullere, men kan derefter ikke vælge samme vare igen.
					else if(userInput.equals("0"))
					{
						this.sekvens3(inFromLocal, inFromServer, outToServer);
					}
				}
			}
		}
	}

	public void sekvens7(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		weightMsg = "Anbring evt. skål og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();

		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();
		else
			this.sekvens7(inFromLocal, inFromServer, outToServer);

		if(serverInput.startsWith("RM20 A"))
			this.sekvens8(inFromLocal, inFromServer, outToServer);

	}

	public void sekvens8(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		outToServer.writeBytes("T\r\n");

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("T S")){
			splittedInput = serverInput.split(" +");
			tara = Double.parseDouble(splittedInput[2]);
			this.sekvens9(inFromLocal, inFromServer, outToServer);
		}
		else this.sekvens7(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens9(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		weightMsg = "Påfyld vare og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();

		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();
		else
			this.sekvens9(inFromLocal, inFromServer, outToServer);

		if(serverInput.startsWith("RM20 A"))
			this.sekvens10(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens10(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		outToServer.writeBytes("S\r\n");

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("S S")){
			splittedInput = serverInput.split(" +");
			netto = Double.parseDouble(splittedInput[2])-tara;
			this.sekvens11(inFromLocal, inFromServer, outToServer);
		}
		else this.sekvens9(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens11(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		weightMsg = "Fjern enheder fra vægt og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();


		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();
		
		else
			this.sekvens11(inFromLocal, inFromServer, outToServer);
		

		if(serverInput.startsWith("RM20 A"))
			this.sekvens12(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens12(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
	
		weightMsg = "Vægt er tareret";
		outToServer.writeBytes("D \""+weightMsg+"\"\r\n");
		outToServer.writeBytes("T\r\n");
		this.sekvens13(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens13(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("T S")){
			splittedInput = serverInput.split(" +");
			bruttoCheck = Double.parseDouble(splittedInput[2]);
			this.sekvens13(inFromLocal, inFromServer, outToServer);
		}
		else this.sekvens14(inFromLocal, inFromServer, outToServer);
	}

	public void sekvens14(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		if(bruttoCheck >= 2 || bruttoCheck <= -2){

			weightMsg = "Afvejning afvist. Tast enter for at veje igen.";
			outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

			serverInput = inFromServer.readLine();

			if(serverInput.equals("RM20 B"))
				serverInput = inFromServer.readLine();
			else
				this.sekvens14(inFromLocal, inFromServer, outToServer);

			if(serverInput.startsWith("RM20 A"))
				this.sekvens7(inFromLocal, inFromServer, outToServer);
		}
		else{

			weightMsg = "Afvejning godkendt. Tast enter for at starte forfra.";
			outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

			serverInput = inFromServer.readLine();

			if(serverInput.equals("RM20 B"))
				serverInput = inFromServer.readLine();
			else
				this.sekvens14(inFromLocal, inFromServer, outToServer);

			if(serverInput.startsWith("RM20 A"))
				this.sekvens15(inFromLocal, inFromServer, outToServer);
		}
	}

	public void sekvens15(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		try{
			log();
		}
		catch(FileNotFoundException e){
			weightMsg = "Logning kunne ikke foretages. Tast enter for at starte forfra";
			outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

			serverInput = inFromServer.readLine();

			if(serverInput.equals("RM20 B"))
				serverInput = inFromServer.readLine();
			else
				this.sekvens15(inFromLocal, inFromServer, outToServer);

			if(serverInput.startsWith("RM20 A"))
				this.sekvens1(inFromLocal, inFromServer, outToServer);
		}


		this.sekvens1(inFromLocal, inFromServer, outToServer);
	}

	public void log() throws IOException{

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Log.txt")));
		Calendar d = Calendar.getInstance();
		String timeStamp = "" + d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-" + d.get(Calendar.DATE) + "   " + d.get(Calendar.HOUR_OF_DAY) + ":" + d.get(Calendar.MINUTE) + ":" + d.get(Calendar.SECOND);

		bw.write(timeStamp + ", " + oprID + ", " + itemNoInput + ", " + itemName + ", " + netto + " kg.");
		bw.flush();
		bw.close();
	}


}




