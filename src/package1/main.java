package package1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;



public class main {

	public static String chooseAuto(int choice) {
		String automate0 = new File("src/files/automate0.txt").getAbsolutePath();
    	
    	switch(choice) {
		  case 0:	  
		    return automate0; 
		default:
		    return null;
    	}
	}
	
	public static Automate lireFichier() throws IOException {
		Scanner scanner = new Scanner(System.in);
    	int numGraph =0;
        	
        	do {
        	System.out.println("Sur quel automate voulez vous exécuter le programme ? (0 à 10, -1 pour arrêter )");
        		numGraph = scanner.nextInt();
        		if (numGraph == -1)
        			System.exit(2);
        	}while (numGraph<0 || numGraph>10);
		
        	String pathname = chooseAuto(numGraph);
        	
        	Automate auto = new Automate();
        	BufferedReader in = new BufferedReader( new FileReader(pathname));
    		String line;
    		
    		while ((line = in.readLine()) != null) {
    			
    			String[] ligne = line.split(" ");
    			
    			
    			if(ligne[0].equals("E")) {
    				for(int i = 1; i<ligne.length; i++) {
    					auto.addEntry(Integer.parseInt(ligne[i]));
    				}
    			}
    			else if (ligne[0].equals("S")) {
    				for(int i = 1; i<ligne.length; i++) {
    					auto.addExit(Integer.parseInt(ligne[i]));
    				}
    			}
    			else {
    				auto.addTransition(new Transition(Integer.parseInt(ligne[0]), Integer.parseInt(ligne[2]), ligne[1] ));
    			}
    		}	
    		in.close();
        	return auto;
	}
	
	public static void afficher(Automate auto) {
		ArrayList<Transition> transi = auto.getTransitions();
		ArrayList<Integer> states = auto.getStates();
		ArrayList<Integer> entries = auto.getEntries();
		ArrayList<Integer> exits = auto.getExits();
		ArrayList<String> words = auto.getMots();
		ArrayList<String> ligne1 = new ArrayList<String>();
		ArrayList<ArrayList<String>> matrice = new ArrayList<ArrayList<String>>();

		int nbStates = auto.nbStates();
		int nbWords = auto.nbWords();
		int maxPerLine =2;
		
		// Remplissage matrice
		ligne1.add("  ");
		ligne1.add("  ");
		for(int i=0; i<nbWords;i++) {
			ligne1.add(words.get(i) + " ");
		}
		matrice.add(ligne1);
	
		for(int i=0; i<auto.nbStates(); i++) {
			ArrayList<String> ligne = new ArrayList<String>();
			
			if(entries.contains(states.get(i)) && !exits.contains(states.get(i))){
				ligne.add("E ");
			}
			else if(!entries.contains(states.get(i)) && exits.contains(states.get(i))) {
				ligne.add("S ");

			}
			else if (entries.contains(states.get(i)) && exits.contains(states.get(i))) {
				ligne.add("ES ");

			}
			else {
				ligne.add(" ");
			}
			ligne.add(i+1+" ");
			
			for(int j=0; j<auto.nbWords();j++) {
				String temp = new String();
				boolean first = false;
				int max =0;
				for(Transition tr : transi) {
					if(tr.getDepart() == i+1 && tr.getMot().contentEquals(words.get(j))) {
						if(!first) {
							temp = Integer.toString(tr.getArrivee());
							first = true;
							max++;
						}else {
							temp+= "," + Integer.toString(tr.getArrivee());
							max++;
						}
					}
				}
				ligne.add(temp);
				if(max > maxPerLine) {
					maxPerLine = max;
				}
			}
			matrice.add(ligne);
		}
		
		//Affichage
		String format = "|%" + Integer.toString((maxPerLine*2)-1) +"." + Integer.toString((maxPerLine*2)-1) + "s";
		String line = new String(new char[(((maxPerLine+1)*2)-1)*nbStates+1]).replace('\0', '-');
		System.out.println(line);
		for(ArrayList<String> ligne : matrice) {
			for(String e : ligne) {
				System.out.print(String.format(format, e));
			}
			System.out.println("|");
		}
		System.out.println(line);
	}
	
	public static void main(String[] args) {
			try {
				Automate auto = lireFichier();
				System.out.println(auto);
				System.out.println(auto.nbStates());
				System.out.println(auto.nbWords());
				System.out.println(auto.getStates());
				System.out.println(auto.getMots());
				System.out.println("entries : " + auto.getEntries());
				System.out.println("exits : " + auto.getExits());
				afficher(auto);				


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
}
