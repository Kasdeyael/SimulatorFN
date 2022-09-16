package main;

import parser.ParseLabel;
import parser.Parser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class Main {


	public static void main(String[] args) {

		Options options = new Options();	
		//add options
		options.addOption("t","time", true, "Specifies the time to parse a file, in minutes. Only applies to unlabeled datasets.");
		options.addOption("f","file", true, "Sets the dataset file");
		options.addOption("l","label", false, "Parses the file if it's labeled");
		options.addOption("h","help", false, "Shows the possible options.");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);

			if(cmd.hasOption("help")) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("help", options);
			}
			if(cmd.hasOption("label") && cmd.hasOption("file")) {
				new ParseLabel().parse(cmd.getOptionValue("file"));
			}else if(cmd.hasOption("time") && cmd.hasOption("file")) {//1440 for pope, 60 for election
				new Parser().parse(cmd.getOptionValue("file"),Integer.parseInt(cmd.getOptionValue("time")));
			}

		} catch ( IllegalArgumentException e) {
			System.out.println("Error parsing the file : " +e.getMessage());
		} catch(ParseException ex) {
			System.out.println("Error parsing the command arguments");
		}


		//PARA FUTURAS REFERENCIAS, LA DE S&I YA ESTA PREPARADA Y NO HAY MAS. 
		//LA DEL VATICANO SE HA COMPROBADO Y UTILIZA LOS USUARIOS CORRECTOS
		//LA DE 04-11 TAMBIEN, AUNQUE USE changeDateFormatSpec ES PORQUE SE HA RECORTADO EL ARCHIVO ORIGINAL.

		//EN AMBOS CASOS FUNCIONA CORRECTO


		//S&I ---- (debe estar quitando " y , por ;. Ordenar y [1,13], eliminar unrelated.
		//new ParseLabel().parse("palinAnonimized.csv");


		//VATICAN
		//ParseJson pjSon = new ParseJson();
		//pjSon.parse("FakeNewsVaticanConfirms496.jsonl", 1440, false);

		//par.parse("fakePope.jsonl",1440);
		//pjSon.changeDateFormat("FakeNewsVaticanConfirms496.jsonl");
		//pjSon.orderByDate("FakeNewsVaticanConfirms496.jsonl_format1.csv");
		//new ParseJson().parseJsonFile("FakeNewsVaticanConfirms496.jsonl_format1.csv",1440);


		//04-11 (igual)
		//ParseJson pjSon2 =new ParseJson();
		//pjSon2.parse("tweets04-11.jsonl", 60, false);


		//JSON

		//new Parser().parse("fakePope.jsonl",1440);
	}

}

