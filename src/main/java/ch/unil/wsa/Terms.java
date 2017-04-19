package ch.unil.wsa;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.common.collect.Lists;

public class Terms {

	public static ArrayList<String> all() throws IOException {
		// Stop words
		ArrayList<String> list = Lists.newArrayList();
		
		// Open the file
		FileInputStream fstream = new FileInputStream("res/words.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		//Read File Line By Line
		while ((strLine = br.readLine()) != null) {
		  list.add(strLine);
		}

		//Close the input stream
		br.close();
		
		
		// Candidate names
		list.add("@PhilippePoutou");
		list.add("@jeanlassalle");
		list.add("@MLP_officiel");
		list.add("@EmmanuelMacron");
		list.add("@JLMelenchon‏");
		list.add("@benoithamon");
		list.add("@n_arthaud");
		list.add("@JCheminade");
		list.add("@FrancoisFillon");
		list.add("@dupontaignan");
		list.add("@UPR_Asselineau");
		
		// Hashtags
		list.add("#Presidentielle2017");
		list.add("#Présidentielle2017");
		list.add("#Election2017");
		list.add("#election2017");
		
		return list;
	}
}
