package weight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class Sequences {

	private String weightMsg, oprID, serverInput, itemName, userInput;
	private int itemNoInput, itemNoStore;
	private String[] splittedInput = new String[10];
	private double tara, netto, bruttoCheck;

	public void sequence1(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		weightMsg = "Indtast ID";
		outToServer.writeBytes("RM20 8 \"" + weightMsg + "\" \" \" \"&3\"\r\n");
		this.sequence2(inFromLocal, inFromServer, outToServer);
	}
	public void sequence2(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		serverInput = inFromServer.readLine();
		//		serverInput = inFromServer.readLine();
		//		serverInput = inFromServer.readLine();
		if(serverInput.equals("RM20 B")){
			serverInput = inFromServer.readLine();
			splittedInput = serverInput.split(" ");
			oprID = splittedInput[2];		
			this.sequence3(inFromLocal, inFromServer, outToServer);
		}
	}


	public void sequence3(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		//Sekvens 3 kunne kombineres med sekvens 4.
		weightMsg = "Indtast varenummer";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");
		this.sequence4(inFromLocal, inFromServer, outToServer);
	}

	public void sequence4(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		serverInput = inFromServer.readLine();
		splittedInput = serverInput.split(" ");
		if(serverInput.equals("RM20 B")){
			serverInput = inFromServer.readLine();
			splittedInput = serverInput.split(" ");
			itemNoInput = Integer.parseInt(splittedInput[2]);
			this.sequence5(inFromLocal, inFromServer, outToServer);
		}
	}

	public void sequence5(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
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
						this.sequence6_7(inFromLocal, inFromServer, outToServer);
					}
					//Fejl: Kan annullere, men kan derefter ikke v�lge samme vare igen.
					else if(userInput.equals("0"))
					{
						this.sequence3(inFromLocal, inFromServer, outToServer);
					}
				}
			}
		}
	}

	public void sequence6_7(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		weightMsg = "Anbring evt. sk�l og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();

		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();
		else
			this.sequence6_7(inFromLocal, inFromServer, outToServer);

		if(serverInput.startsWith("RM20 A"))
			this.sequence8(inFromLocal, inFromServer, outToServer);

	}

	public void sequence8(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		outToServer.writeBytes("T\r\n");

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("T S")){
			splittedInput = serverInput.split(" +");
			tara = Double.parseDouble(splittedInput[2]);
			this.sequence9(inFromLocal, inFromServer, outToServer);
		}
		else this.sequence6_7(inFromLocal, inFromServer, outToServer);
	}

	public void sequence9(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		weightMsg = "P�fyld vare og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();

		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();
		else
			this.sequence9(inFromLocal, inFromServer, outToServer);

		if(serverInput.startsWith("RM20 A"))
			this.sequence10(inFromLocal, inFromServer, outToServer);
	}

	public void sequence10(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		outToServer.writeBytes("S\r\n");

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("S S")){
			splittedInput = serverInput.split(" +");
			netto = Double.parseDouble(splittedInput[2])-tara;
			this.sequence11(inFromLocal, inFromServer, outToServer);
		}
		else this.sequence9(inFromLocal, inFromServer, outToServer);
	}

	public void sequence11(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		weightMsg = "Fjern enheder fra v�gt og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();


		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();

		else
			this.sequence11(inFromLocal, inFromServer, outToServer);


		if(serverInput.startsWith("RM20 A"))
			this.sequence12(inFromLocal, inFromServer, outToServer);
	}

	public void sequence12(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		weightMsg = "V�gt er tareret";
		outToServer.writeBytes("D \""+weightMsg+"\"\r\n");
		outToServer.writeBytes("T\r\n");
		this.sequence13(inFromLocal, inFromServer, outToServer);
	}

	public void sequence13(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("T S")){
			splittedInput = serverInput.split(" +");
			bruttoCheck = Double.parseDouble(splittedInput[2]);
			this.sequence13(inFromLocal, inFromServer, outToServer);
		}
		else this.sequence14(inFromLocal, inFromServer, outToServer);
	}

	public void sequence14(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		if(bruttoCheck >= 2 || bruttoCheck <= -2){

			weightMsg = "Afvejning afvist. Tast enter for at veje igen.";
			outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

			serverInput = inFromServer.readLine();

			if(serverInput.equals("RM20 B"))
				serverInput = inFromServer.readLine();
			else
				this.sequence14(inFromLocal, inFromServer, outToServer);

			if(serverInput.startsWith("RM20 A"))
				this.sequence6_7(inFromLocal, inFromServer, outToServer);
		}
		else{

			weightMsg = "Afvejning godkendt. Tast enter for at starte forfra.";
			outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

			serverInput = inFromServer.readLine();

			if(serverInput.equals("RM20 B"))
				serverInput = inFromServer.readLine();
			else
				this.sequence14(inFromLocal, inFromServer, outToServer);

			if(serverInput.startsWith("RM20 A"))
				this.sequence15(inFromLocal, inFromServer, outToServer);
		}
	}

	public void sequence15(BufferedReader inFromLocal, BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

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
				this.sequence15(inFromLocal, inFromServer, outToServer);

			if(serverInput.startsWith("RM20 A"))
				this.sequence1(inFromLocal, inFromServer, outToServer);
		}


		this.sequence1(inFromLocal, inFromServer, outToServer);
	}

	public void log() throws IOException{

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Log.txt")));
		Calendar d = Calendar.getInstance();
		String timeStamp = "" + d.get(Calendar.YEAR) + "-" + (d.get(Calendar.MONTH) + 1) + "-" + d.get(Calendar.DATE) + "   " + d.get(Calendar.HOUR_OF_DAY) + ":" + d.get(Calendar.MINUTE) + ":" + d.get(Calendar.SECOND);

		bw.newLine();
		bw.write(timeStamp + ", " + oprID + ", " + itemNoInput + ", " + itemName + ", " + netto + " kg.");
		bw.flush();
		bw.close();
	}


}