package weight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class Sequences {

	private String weightMsg, oprID, serverInput, itemName, userInput;
	private int itemNoInput, itemNoStore;
	private String[] splittedInput = new String[10];
	private double tara, netto, bruttoCheck;

	//-----------------------------------------------------------------
	// (1)	V�gtdisplay sp�rger om oprID og afventer input
	//-----------------------------------------------------------------
	public void sequence1(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		weightMsg = "Indtast ID";
		outToServer.writeBytes("RM20 8 \"" + weightMsg + "\" \" \" \"&3\"\r\n");
		outToServer.flush();
		this.sequence2(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (2)	oprID indl�ses og gemmes i lokal variabel
	//-----------------------------------------------------------------
	public void sequence2(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		System.out.println("Inde i seq2");
		serverInput = inFromServer.readLine();
		System.out.println("SERVERINPUT I SEQ2: " + serverInput);
		//				serverInput = inFromServer.readLine();
		//		serverInput = inFromServer.readLine();
		if(serverInput.equals("RM20 B")){
			serverInput = inFromServer.readLine();
			if (!aborted()) {
				splittedInput = serverInput.split(" ");
				oprID = splittedInput[2];
				this.sequence3(inFromServer, outToServer);
			}
			else
				this.sequence1(inFromServer, outToServer);
		}
		System.out.println("sekvens3 kaldt");
	}

	//-----------------------------------------------------------------
	// (3)	V�gtdisplay sp�rger om varenummer og afventer input
	//-----------------------------------------------------------------
	public void sequence3(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		//Sekvens 3 kunne kombineres med sekvens 4.
		weightMsg = "Indtast varenummer";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");
		this.sequence4(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (4)	Varenummer indl�ses og gemmes i lokal variabel
	//-----------------------------------------------------------------
	public void sequence4(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		serverInput = inFromServer.readLine();
		splittedInput = serverInput.split(" ");
		//Herunder modtager vi RM20 B ordren som indeholder et varenummer.
		//Kommandoen opdeles ved hjælp af split og vi vælger plads [2], således at vi ender med et varenummer. 
		if(serverInput.equals("RM20 B")){ 
			serverInput = inFromServer.readLine();
			if(!aborted()){
				splittedInput = serverInput.split(" ");
				itemNoInput = Integer.parseInt(splittedInput[2]);
				this.sequence5_6(inFromServer, outToServer);
			}
			else
				this.sequence1(inFromServer, outToServer);
		}
	}

	//-----------------------------------------------------------------
	// (5)	Program sammenligner userinput med varenumre i store.txt
	// (6)	N�r identisk varenummer er fundet sp�rger program bruger
	//		om det er korrekt varenavn. Hvis ja sendes videre til n�ste
	//		sekvens. Hvis nej k�res sequence3()
	//-----------------------------------------------------------------
	public void sequence5_6(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{

		BufferedReader inFromLocal = new BufferedReader(new FileReader(new File("store.txt")));

		boolean notFound = true;

		while(notFound){
			try{
				splittedInput = inFromLocal.readLine().split(",");
			}
			catch(NullPointerException e){
				notFound = true;
			}
			itemNoStore = Integer.parseInt(splittedInput[0]);
			if(itemNoStore == itemNoInput){ 
				inFromLocal.close();
				//Så snart at det indtastede nummer er lig et nummer i "databasen", 
				// sættes notFound = false og nedenstående kode eksekveres. 
				notFound = false;
				itemName = splittedInput[1];
				weightMsg = "Vare: " + itemName + "\nTast 1 for OK eller 0 for Annuller."; 
				outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

				serverInput = inFromServer.readLine();
				//	serverInput = inFromServer.readLine();
				//	serverInput = inFromServer.readLine();
				splittedInput = serverInput.split(" ");
				if(serverInput.equals("RM20 B"))
				{
					serverInput = inFromServer.readLine();
					if(!aborted()){
						splittedInput = serverInput.split(" ");
						userInput = splittedInput[2];		

						//Hvis varen er korrekt fortsættes der til sekvens 7 ellers starter man forfra i sekvens 3.
						if(userInput.equals("1"))
						{
							this.sequence7(inFromServer, outToServer);
						}
						//Fejl: Kan annullere, men kan derefter ikke v�lge samme vare igen.
						else if(userInput.equals("0"))
						{
							this.sequence3(inFromServer, outToServer);
						}
					}
					else
						this.sequence1(inFromServer, outToServer);
				}
			}
		}
		weightMsg = "Varenummer ikke fundet. Tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");
		inFromServer.readLine();
		this.sequence3(inFromServer, outToServer);

	}

	//-----------------------------------------------------------------
	// (7)	V�gtdisplay beder om evt. tara og bekr�fte 	
	//-----------------------------------------------------------------
	public void sequence7(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		weightMsg = "Anbring evt. sk�l og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();

		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();
		else
			this.sequence7(inFromServer, outToServer);

		if(!aborted())
			this.sequence8(inFromServer, outToServer);
		else
			this.sequence1(inFromServer, outToServer);

	}

	//-----------------------------------------------------------------
	// (8) V�gt tareres og tara gemmes i lokal variabel
	//-----------------------------------------------------------------
	public void sequence8(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		outToServer.writeBytes("T\r\n");

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("T S")){
			splittedInput = serverInput.split(" +");
			tara = Double.parseDouble(splittedInput[2]);
			this.sequence9(inFromServer, outToServer);
		}
		else this.sequence7(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (9) V�gtdisplay sp�rger om oprID og afventer input
	//-----------------------------------------------------------------
	public void sequence9(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		weightMsg = "P�fyld vare og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();

		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();
		else
			this.sequence9(inFromServer, outToServer);

		if(!aborted())
			this.sequence10(inFromServer, outToServer);
		else
			this.sequence1(inFromServer, outToServer);
	}

	public void sequence10(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		outToServer.writeBytes("S\r\n");

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("S S")){
			splittedInput = serverInput.split(" +");
			netto = Double.parseDouble(splittedInput[2])-tara;
			this.sequence11(inFromServer, outToServer);
		}
		else this.sequence9(inFromServer, outToServer);
	}

	public void sequence11(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		weightMsg = "Fjern enheder fra v�gt og tast enter.";
		outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

		serverInput = inFromServer.readLine();


		if(serverInput.equals("RM20 B"))
			serverInput = inFromServer.readLine();

		else
			this.sequence11(inFromServer, outToServer);


		if(!aborted())
			this.sequence12(inFromServer, outToServer);
		else
			this.sequence1(inFromServer, outToServer);
	}

	public void sequence12(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		weightMsg = "V�gt er tareret";
		outToServer.writeBytes("D \""+weightMsg+"\"\r\n");
		outToServer.writeBytes("T\r\n");
		this.sequence13(inFromServer, outToServer);
	}

	public void sequence13(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		serverInput = inFromServer.readLine();
		if(serverInput.startsWith("T S")){
			splittedInput = serverInput.split(" +");
			bruttoCheck = Double.parseDouble(splittedInput[2]);
			this.sequence13(inFromServer, outToServer);
		}
		else this.sequence14(inFromServer, outToServer);
	}

	public void sequence14(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		if(bruttoCheck >= 2 || bruttoCheck <= -2){

			weightMsg = "Afvejning afvist. Tast enter for at veje igen.";
			outToServer.writeBytes("RM20 4 \"" + weightMsg + "\" \" \" \"&3\"\r\n");

			serverInput = inFromServer.readLine();

			if(serverInput.equals("RM20 B"))
				serverInput = inFromServer.readLine();
			else
				this.sequence14(inFromServer, outToServer);

			if(serverInput.startsWith("RM20 A"))
				this.sequence7(inFromServer, outToServer);
		}
		else{

			weightMsg = "Afvejning godkendt!";

			outToServer.writeBytes("D \""+ weightMsg +"\"\r\n");
			inFromServer.readLine();
			inFromServer.readLine();
			this.sequence15(inFromServer, outToServer);
		}
	}

	public void sequence15(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

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
				this.sequence15(inFromServer, outToServer);

			if(serverInput.startsWith("RM20 A"))
				this.sequence1(inFromServer, outToServer);
		}


		this.sequence1(inFromServer, outToServer);
	}

	public void log() throws IOException{

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Log.txt"), true));
		Calendar d = Calendar.getInstance();
		DecimalFormat df = new DecimalFormat("00");
		String timeStamp = "" + d.get(Calendar.YEAR) + "-" + df.format(d.get(Calendar.MONTH) + 1) + "-" + df.format(d.get(Calendar.DATE)) + "-" + df.format(d.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(d.get(Calendar.MINUTE)) + ":" + df.format(d.get(Calendar.SECOND));

		bw.write(timeStamp + ", " + oprID + ", " + itemNoInput + ", " + itemName + ", " + netto + " kg.");
		bw.newLine();
		bw.flush();
		bw.close();
	}

	boolean aborted(){
		return !serverInput.startsWith("RM20 A");
	}


}
