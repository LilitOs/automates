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
		String automate = new File("src/files/automate"+choice+".txt").getAbsolutePath();
    	return automate;
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
    				Transition tr = new Transition(Integer.parseInt(ligne[0]), Integer.parseInt(ligne[2]), ligne[1] );
    				if(!auto.getTransitions().contains(tr))
    				auto.addTransition(tr);
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
		
		// Si AD le nombre d'états n'est pas calculé pareil
		int nbStates;
		if(auto.isAutoDeter()) {
			nbStates = auto.getEtatsDetermin().size();
		}
		else
		{
			nbStates = auto.nbStates();
		}
		
			
		int nbWords = auto.nbWords();
		int maxPerLine =2;
		
		// Remplissage matrice
		ligne1.add("  ");
		ligne1.add("  ");
		for(int i=0; i<nbWords;i++) {
			ligne1.add(words.get(i) + " ");
		}
		matrice.add(ligne1);
	
		for(int i=0; i<nbStates; i++) {
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
			// si auto = AD (donc les états ont plusieurs valeurs)
			if(auto.isAutoDeter()) {
				String etat ="";
				for(Integer e : auto.getEtatsDetermin().get(i)) {
					if(etat.equals("")) {
						etat += e;
					}
					else {
						etat += "," + e;
					}
				}
				ligne.add(etat);
			}
			else {
				ligne.add(i+1+" ");
			}
			
			
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
		// Si un état poubelle existe
		/*
		if(auto.isEtatPoubelle()) {
			ArrayList<String> lignePoubelle = new ArrayList<String>();
			String temp ="";
			for(String mot : auto.getMots()) {
				temp = Integer.toString(-1);
				lignePoubelle.add(temp);
			}
			matrice.add(lignePoubelle);
		}
		*/
			
		
		//Affichage
		String format = "|%" + Integer.toString((maxPerLine*2)-1) +"." + Integer.toString((maxPerLine*2)-1) + "s";
		String line = new String(new char[(((maxPerLine+2)*2)-1)*nbStates+1]).replace('\0', '-');
		System.out.println(line);
		for(ArrayList<String> ligne : matrice) {
			for(String e : ligne) {
				System.out.print(String.format(format, e));
			}
			System.out.println("|");
		}
		System.out.println(line);
	}
	
	public static ArrayList<Transition> allTransiOfOneStateDepart(Automate auto, Integer state){
		ArrayList<Transition> listOfTransi = new ArrayList<Transition>();
		for(Transition transi : auto.getTransitions()){
			if(transi.getDepart()==state) {
				listOfTransi.add(transi);
			}
		}
		
		return listOfTransi;
	}
	
	public static Automate synchronisation(Automate auto) {
		ArrayList<Transition> transiMotsVides = new ArrayList<Transition>();
		ArrayList<Integer> entries = auto.getEntries();
		ArrayList<Integer> exits = auto.getExits();
		//Recup les transi avc mot vides
		for(Transition transi : auto.getTransitions()) {
			if(transi.getMot().equals(Automate.motVide)) {
				transiMotsVides.add(transi);
				System.out.println(transi);
			}
		}
		
		// Ajouter les transis équivalents
	
		for(Transition trMotVide : transiMotsVides){
			//Retirer les transis mot vides	( important de le mettre dans une boucle séparé de la suite, évite bcp de problèmes)
			auto.removeTransition(trMotVide);
		}
		
		// Premier passage
		for(Transition trMotVide : transiMotsVides){
			ArrayList<Transition> transiInfo = allTransiOfOneStateDepart(auto, trMotVide.getArrivee());
			if(entries.contains(trMotVide.getArrivee()))
				auto.addEntry(trMotVide.getDepart());
			if(exits.contains(trMotVide.getArrivee()))
				auto.addExit(trMotVide.getDepart());
			for(Transition trInf : transiInfo) {
				Transition trToAdd = new Transition(trMotVide.getDepart(), trInf.getArrivee(),trInf.getMot());
				if(!auto.getTransitions().contains(trToAdd))
					auto.addTransition(trToAdd);
			}
		}
		
		// 2eme passage, on recommence pour bien ajouté les transitions qui ont pu être oublié à cause de l'ordre de leurs dispositions dans le fichier (c'est compliqué, meilleur méthode que j'ai trouvé)
	
		for(Transition trMotVide : transiMotsVides){
			ArrayList<Transition> transiInfo = allTransiOfOneStateDepart(auto, trMotVide.getArrivee());
			for(Transition trInf : transiInfo) {
				Transition trToAdd = new Transition(trMotVide.getDepart(), trInf.getArrivee(),trInf.getMot());
				if(!auto.getTransitions().contains(trToAdd))
					auto.addTransition(trToAdd);	
			}
		}
		return auto;
	}
	
	public static Automate determiniser(Automate auto) {
		if(auto.asynchrone()) {
			auto = synchronisation(auto);
		}
		
		Automate AD = new Automate();
		// Creation de l'etat 1
		ArrayList<Integer> entries = auto.getEntries();
		ArrayList<Integer> exits = auto.getExits();
		AD.addEntry(1);
		ArrayList<ArrayList<Integer>> states = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> memoStates = new ArrayList<ArrayList<Integer>>();
		ArrayList<String> mots = auto.getMots();
		ArrayList<Integer> state0 = new ArrayList<Integer>();
		
		for(Integer e : entries) {
			state0.add(e);
		}
		states.add(state0);
		memoStates.add(state0);
		 // etape 1
		for(String mot : mots) {
			ArrayList<Integer> newState = new ArrayList<Integer>();
			for(Integer e : entries) {
				for(Transition tr : allTransiOfOneStateDepart(auto, e)) {
					if(tr.getMot().equals(mot)){
						if(!newState.contains(tr.getArrivee()))
							newState.add(tr.getArrivee());
					AD.addTransition( new Transition(1, tr.getArrivee(), mot));
					}
					
				}
			}
			if(!memoStates.contains(newState) && !newState.isEmpty()) { // a voir si ça marche avc des listes de listes
				states.add(newState);
				memoStates.add(newState);
			}
		}
		states.remove(state0);
		
		//test
		for(ArrayList<Integer> state : states) {
			for(Integer e : state) {
				System.out.print(e);
			}
			System.out.println();
		}
		
		// le reste
		int i = 0;
		int k = 0;
		while(!states.isEmpty()) {
			for(String mot : mots) {
				ArrayList<Integer> newState = new ArrayList<Integer>();
				for(Integer e : states.get(i)) {
					for(Transition tr : allTransiOfOneStateDepart(auto, e)) {
						if(tr.getMot().equals(mot)){
							if(!newState.contains(tr.getArrivee()))
								newState.add(tr.getArrivee());
						AD.addTransition( new Transition(k+2, tr.getArrivee(), mot));
						}
						
					}
				} 
				if(!memoStates.contains(newState) && !newState.isEmpty()) { // a voir si ça marche avc des listes de listes
					states.add(newState);
					memoStates.add(newState);
				}
				
			}
			states.remove(states.get(i));
			k++;
		}
		
		// on place les sorties
		for(int ind = 0; ind<memoStates.size(); ind++) {
			for(Integer e : exits) {
				if(memoStates.get(ind).contains(e)) {
					AD.addExit(ind+1);
				}
			}
		}
		
		AD.setAutoDeter(true);
		AD.setEtatsDetermin(memoStates);
		
		return AD;
	}
	
	public static Automate completer(Automate auto) {
		ArrayList<String> mots = auto.getMots();
		ArrayList<Transition> transitions = auto.getTransitions();	
		int etatPoub = auto.nbStates() +1; 
		
		if(auto.isAutoDeter()) {
			
			ArrayList<ArrayList<Integer>> listOfStates = auto.getEtatsDetermin(); 
			Integer size = listOfStates.size();
			
			boolean poubCreer = false;
			boolean flag;
			
			// Ajout des transitions vers l'état poubelle si besoin
			for(String mot : mots) {
				for(int i=0; i<size; i++) {
					flag = false;
					for(Transition tr : transitions) {
						if(tr.getDepart()== i+1 && tr.getMot().contentEquals(mot)) {
							flag = true;
						}
					}
					
					if(!flag) {
						auto.addTransition(new Transition(i+1, etatPoub, mot));
						poubCreer = true;
					}
				}
			}
			
			// Creation des transitions de l'états poubelles reconnaissant tous les mots vers elle même si poubelle créer
			if(poubCreer) {
				ArrayList<Integer> poubelle = new ArrayList<Integer>();
				poubelle.add(etatPoub);
				for(String m : mots) {
					auto.addTransition(new Transition(etatPoub, etatPoub, m));
				}
				auto.addEtatDetermin(poubelle);
			}
		}
		else {
			
			ArrayList<Integer> states = auto.getStates();

			boolean poubCreer = false;
			boolean flag;
			
			// Ajout des transitions vers l'état poubelle si besoin
			for(String mot : mots) {
				for(Integer state : states) {
					flag = false;
					for(Transition tr : transitions) {
						if(tr.getDepart()== state && tr.getMot().contentEquals(mot)) {
							flag = true;
						}
					}
					
					if(!flag) {
						auto.addTransition(new Transition(state, etatPoub, mot));
						poubCreer = true;
					}
				}
			}
			
			// Creation des transitions de l'états poubelles reconnaissant tous les mots vers elle même si poubelle créer
			if(poubCreer) {
				for(String m : mots) {
					auto.addTransition(new Transition(etatPoub, etatPoub, m));
				}
			}
		}
		auto.setEtatPoubelle(true);
		return auto;
		
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
				auto.reportAsync();
				if(auto.asynchrone())
					System.out.println("Automate asynchrone\n");
				else
					System.out.println("Automate synchrone\n");

				afficher(auto);				
				afficher(synchronisation(auto));
				Automate AD = determiniser(auto);
				afficher(AD);
				afficher(completer(AD));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
}
