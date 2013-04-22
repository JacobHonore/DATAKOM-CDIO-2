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
	
	Data d = new Data();

	//-----------------------------------------------------------------
	// (1)	Vægtdisplay spørger om oprID og afventer input
	//-----------------------------------------------------------------
	public void sequence1(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		d.setWeightMsg("Indtast ID");
		outToServer.writeBytes("RM20 8 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");
		outToServer.flush();
		this.sequence2(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (2)	oprID indlæses og gemmes i lokal variabel
	//-----------------------------------------------------------------
	public void sequence2(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		d.setServerInput(inFromServer.readLine());
//		d.setServerInput(inFromServer.readLine());
		d.setServerInput(inFromServer.readLine());
		if(d.getServerInput().equals("RM20 B")){
			d.setServerInput(inFromServer.readLine());
			if (!aborted()) {
				d.setSplittedInput(d.getServerInput().split(" "));
				d.setOprID(d.getSplittedInput()[2]); // MÅSKE FEJL!!!!
				this.sequence3(inFromServer, outToServer);
			}
			else
				this.sequence1(inFromServer, outToServer);
		}
	}

	//-----------------------------------------------------------------
	// (3)	Vægtdisplay spørger om varenummer og afventer input
	//-----------------------------------------------------------------
	public void sequence3(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		//Sekvens 3 kunne kombineres med sekvens 4.
		d.setWeightMsg("Indtast varenummer");
		outToServer.writeBytes("RM20 4 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");
		this.sequence4(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (4)	Varenummer indlæses og gemmes i lokal variabel
	//-----------------------------------------------------------------
	public void sequence4(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		d.setServerInput(inFromServer.readLine());
		d.setSplittedInput(d.getServerInput().split(" "));
		//Herunder modtager vi RM20 B ordren som indeholder et varenummer.
		//Kommandoen opdeles ved hjælp af split og vi vælger plads [2], således at vi ender med et varenummer. 
		if(d.getServerInput().equals("RM20 B")){ 
			d.setServerInput(inFromServer.readLine());
			if(!aborted()){
				d.setSplittedInput(d.getServerInput().split(" "));
				
				System.out.println("" + d.getServerInput());
				System.out.println("" + d.getSplittedInput()[2]);
				
				String temp;
				temp = d.getSplittedInput()[2].replaceAll("\"","");
				System.out.println(temp);
				
				d.setItemNoInput(Integer.parseInt(temp));
				this.sequence5_6(inFromServer, outToServer);
			}
			else
				this.sequence1(inFromServer, outToServer);
		}
	}

	//-----------------------------------------------------------------
	// (5)	Program sammenligner userinput med varenumre i store.txt
	// (6)	Når identisk varenummer er fundet spørger program bruger
	//		om det er korrekt varenavn. Hvis ja sendes videre til n�ste
	//		sekvens. Hvis nej køres sequence3()
	//-----------------------------------------------------------------
	public void sequence5_6(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{

		BufferedReader inFromLocal = new BufferedReader(new FileReader(new File("store.txt")));

		boolean notFound = true;

		while(notFound){
			try{
				d.setSplittedInput(inFromLocal.readLine().split(","));
			}
			catch(NullPointerException e){
				notFound = true;
			}
			d.setItemNoStore(Integer.parseInt(d.getSplittedInput()[0]));
			System.out.println("store: " + d.getItemNoStore());
			System.out.println("input: " + d.getItemNoInput() + "\n");
			
			if(d.getItemNoStore() == d.getItemNoInput()){ 
				inFromLocal.close();
				//Så snart at det indtastede nummer er lig et nummer i "databasen", 
				// sættes notFound = false og nedenstående kode eksekveres. 
				notFound = false;
				d.setItemName(d.getSplittedInput()[1]);
				
				d.setWeightMsg("Vare: " + d.getItemName()); 
				
				outToServer.writeBytes("RM20 4 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");

				d.setServerInput(inFromServer.readLine());
//				d.setServerInput(inFromServer.readLine());
//				d.setServerInput(inFromServer.readLine());
				
				d.setSplittedInput(d.getServerInput().split(" "));
				if(d.getServerInput().equals("RM20 B"))
				{
					d.setServerInput(inFromServer.readLine());
					if(!aborted()){
						d.setSplittedInput(d.getServerInput().split(" "));
						d.setUserInput(d.getSplittedInput()[2]);		

						//Hvis varen er korrekt fortsættes der til sekvens 7 ellers starter man forfra i sekvens 3.						
						if(d.getUserInput().equals("\"1\""))
						{
							this.sequence7(inFromServer, outToServer);
						}
						//Fejl: Kan annullere, men kan derefter ikke v�lge samme vare igen.
						else if(d.getUserInput().equals("\"0\""))
						{
							this.sequence3(inFromServer, outToServer);
						}
					}
					else
						this.sequence1(inFromServer, outToServer);
				}
			}
		}
		d.setWeightMsg("Varenummer ikke fundet");
		outToServer.writeBytes("RM20 4 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");
		inFromServer.readLine();
		this.sequence3(inFromServer, outToServer);

	}

	//-----------------------------------------------------------------
	// (7)	Vægtdisplay beder om evt. tara og at brugeren bekræfter. 	
	//-----------------------------------------------------------------
	public void sequence7(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		d.setWeightMsg("Anbring skål");
		outToServer.writeBytes("RM20 4 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");

		d.setServerInput(inFromServer.readLine());

		if(d.getServerInput().equals("RM20 B"))
			d.setServerInput(inFromServer.readLine());
		else
			this.sequence7(inFromServer, outToServer);

		if(!aborted())
			this.sequence8(inFromServer, outToServer);
		else
			this.sequence1(inFromServer, outToServer);

	}

	//-----------------------------------------------------------------
	// (8) Vægt tareres og tara gemmes i lokal variabel
	//-----------------------------------------------------------------
	public void sequence8(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException
	{
		outToServer.writeBytes("T\r\n");

		d.setServerInput(inFromServer.readLine());
		if(d.getServerInput().startsWith("T S")){
			d.setSplittedInput(d.getServerInput().split(" +"));
			d.setTara(Double.parseDouble(d.getSplittedInput()[2]));
			this.sequence9(inFromServer, outToServer);
		}
		else this.sequence7(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (9) Operatøren instrueres til at påfylde vare og derefter trykke enter.
	//-----------------------------------------------------------------
	public void sequence9(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		d.setWeightMsg("Påfyld vare");
		outToServer.writeBytes("RM20 4 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");

		d.setServerInput(inFromServer.readLine());

		if(d.getServerInput().equals("RM20 B"))
			d.setServerInput(inFromServer.readLine());
		else
			this.sequence9(inFromServer, outToServer);

		if(!aborted())
			this.sequence10(inFromServer, outToServer);
		else
			this.sequence1(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (10) Netto registreres.
	//-----------------------------------------------------------------
	public void sequence10(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		outToServer.writeBytes("S\r\n");

		d.setServerInput(inFromServer.readLine());
		if(d.getServerInput().startsWith("S S")){
			d.setSplittedInput(d.getServerInput().split(" +"));
			d.setNetto(Double.parseDouble(d.getSplittedInput()[2])-d.getTara());
			this.sequence11(inFromServer, outToServer);
		}
		else this.sequence9(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (11) Operatøren instrueres til at fjerne netto og tara.
	//-----------------------------------------------------------------
	public void sequence11(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		d.setWeightMsg("Fjern enheder");
		outToServer.writeBytes("RM20 4 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");

		d.setServerInput(inFromServer.readLine());


		if(d.getServerInput().equals("RM20 B"))
			d.setServerInput(inFromServer.readLine());

		else
			this.sequence11(inFromServer, outToServer);


		if(!aborted())
			this.sequence12(inFromServer, outToServer);
		else
			this.sequence1(inFromServer, outToServer);
	}
	//-----------------------------------------------------------------
	// (12) Vægt tareres, så den er klar til en ny omgang
	//-----------------------------------------------------------------	
	public void sequence12(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		outToServer.writeBytes("T\r\n");
		this.sequence13(inFromServer, outToServer);
	}

	//-----------------------------------------------------------------
	// (13) Minus brutto registreres.
	//-----------------------------------------------------------------
	public void sequence13(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		d.setServerInput(inFromServer.readLine());
		if(d.getServerInput().startsWith("T S")){
			d.setSplittedInput(d.getServerInput().split(" +"));
			d.setBruttoCheck(Double.parseDouble(d.getSplittedInput()[2]));
			this.sequence13(inFromServer, outToServer);
		}
		else this.sequence14(inFromServer, outToServer);
	}
	
	//-----------------------------------------------------------------
	// (14) Bruttokontrol OK, hvis det er tilfældet.
	//-----------------------------------------------------------------
	public void sequence14(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{
		if(d.getBruttoCheck() >= 2 || d.getBruttoCheck() <= -2){

			d.setWeightMsg("Afvejning afvist");
			outToServer.writeBytes("RM20 4 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");

			d.setServerInput(inFromServer.readLine());

			if(d.getServerInput().equals("RM20 B"))
				d.setServerInput(inFromServer.readLine());
			else
				this.sequence14(inFromServer, outToServer);

			if(d.getServerInput().startsWith("RM20 A"))
				this.sequence7(inFromServer, outToServer);
		}
		else{

			d.setWeightMsg("Afvejning godkendt!");

			outToServer.writeBytes("RM20 4 \""+ d.getWeightMsg() +"\"\r\n");
			inFromServer.readLine();
			inFromServer.readLine();
			this.sequence15(inFromServer, outToServer);
		}
	}

	//-----------------------------------------------------------------
	// (15) Mængde på lager afskrives og historikken opdateres.
	//-----------------------------------------------------------------
	public void sequence15(BufferedReader inFromServer, DataOutputStream outToServer) throws IOException{

		try{
			log();
		}
		catch(FileNotFoundException e){
			d.setWeightMsg("Logfejl. Afvist!");
			outToServer.writeBytes("RM20 4 \"" + d.getWeightMsg() + "\" \" \" \"&3\"\r\n");

			d.setServerInput(inFromServer.readLine());

			if(d.getServerInput().equals("RM20 B"))
				d.setServerInput(inFromServer.readLine());
			else
				this.sequence15(inFromServer, outToServer);

			if(d.getServerInput().startsWith("RM20 A"))
				this.sequence1(inFromServer, outToServer);
		}


		this.sequence1(inFromServer, outToServer);
	}

	public void log() throws IOException{

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Log.txt"), true));
		Calendar c = Calendar.getInstance();
		DecimalFormat df = new DecimalFormat("00");
		String timeStamp = "" + c.get(Calendar.YEAR) + "-" + df.format(c.get(Calendar.MONTH) + 1) + "-" + df.format(c.get(Calendar.DATE)) + "-" + df.format(c.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(c.get(Calendar.MINUTE)) + ":" + df.format(c.get(Calendar.SECOND));

		bw.write(timeStamp + ", " + d.getOprID() + ", " + d.getItemNoInput() + ", " + d.getItemName() + ", " + d.getNetto() + " kg.");
		bw.newLine();
		bw.flush();
		bw.close();
	}

	boolean aborted(){
		return !d.getServerInput().startsWith("RM20 A");
	}


}
