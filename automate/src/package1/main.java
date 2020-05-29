package package1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;



public class main {

	public static String chooseAuto(int choice) {
		String automate = new File("src/files/L3NEW-MpI-5-"+choice+".txt").getAbsolutePath();
    	return automate;
	}
	
	public static Automate lireFichier() throws IOException {
		Scanner scanner = new Scanner(System.in);
    	int numGraph =0;
        	
        	do {
        	System.out.println("Sur quel automate voulez vous exécuter le programme ? (0 à 44, -1 pour arrêter )");
        		numGraph = scanner.nextInt();
        		if (numGraph == -1)
        			System.exit(2);
        	}while (numGraph<0 || numGraph>44);
		
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
				ligne.add(i+" ");
			}
			
			
			for(int j=0; j<auto.nbWords();j++) {
				String temp = new String();
				boolean first = false;
				int max =0;
				for(Transition tr : transi) {
					if(tr.getDepart() == i && tr.getMot().contentEquals(words.get(j))) {
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
	
	public static boolean isDetermined(Automate auto) {
		ArrayList<Integer> entries = auto.getEntries();
		ArrayList<Integer> exits = auto.getExits();
		ArrayList<String> mots = auto.getMots();
		ArrayList<Integer> states = auto.getStates();
		ArrayList<Transition> transitions = auto.getTransitions();
		boolean isDetermined = true;
		
		for(Integer st : states) {
			for(String mot : mots) {
				int count =0;
				for (Transition tr : transitions) {
					if(tr.getDepart()== st && tr.getMot().contentEquals(mot)) {
						count++;
					}
				}
				if(count>1)
					isDetermined=false;
			}	
		}
		if(entries.size()>1)
			isDetermined = false;
		return isDetermined;
	}
	
	public static Automate determiniser(Automate auto) {
		if(auto.asynchrone()) {
			auto = synchronisation(auto);
		}
		
		Automate AD = new Automate();
		// Creation de l'etat 1
		ArrayList<Integer> entries = auto.getEntries();
		ArrayList<Integer> exits = auto.getExits();
		AD.addEntry(0);
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
					AD.addTransition( new Transition(0, tr.getArrivee(), mot));
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
						AD.addTransition( new Transition(k+1, tr.getArrivee(), mot));
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
					AD.addExit(ind);
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
		int etatPoub = auto.nbStates() ; 
		
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
						if(tr.getDepart()== i && tr.getMot().contentEquals(mot)) {
							flag = true;
						}
					}
					
					if(!flag) {
						auto.addTransition(new Transition(i, etatPoub, mot));
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
	
	public static ArrayList<Integer> stateArrivOfAnTransition(Automate auto, int s, String mot){
		ArrayList<Integer> entries = auto.getEntries();
		ArrayList<Integer> exits = auto.getExits();
		ArrayList<String> mots = auto.getMots();
		ArrayList<Transition> transitions = auto.getTransitions();
		if(auto.isAutoDeter()) {
			ArrayList<ArrayList<Integer>> states = auto.getEtatsDetermin();
			Integer size = states.size();
		}
		else {
			ArrayList<Integer> states = auto.getStates();
			Integer size = states.size();
		}
		
		ArrayList<Integer> stateArriv = new ArrayList<Integer>();
		
		
		for(Transition tr : transitions) {
			if(tr.getDepart()== s && tr.getMot().equals(mot)) {
				stateArriv.add(tr.getArrivee());
			}
		}
		Collections.sort(stateArriv);
		return stateArriv;
	}
	
	public static ArrayList<Integer> getArrivAllWords(Automate auto, int state){//tool function for divisegroup()
		ArrayList<Transition> transitions = auto.getTransitions();
		ArrayList<String> mots = auto.getMots();
		ArrayList<Integer> allArriv = new ArrayList<Integer>();
		for(String mot : mots) {
			for(Transition tr : transitions) {
				if(tr.getDepart() == state && tr.getMot().equals(mot)) {
					allArriv.add(tr.getArrivee());
				}
			}
		}
		return allArriv;
	} 
	
	public static ArrayList<ArrayList<Integer>> divideGroup(Automate auto, ArrayList<Integer> groupe){ //tool function for minimasation
		ArrayList<ArrayList<Integer>> newListOfGroups = new ArrayList<ArrayList<Integer>>();
		for(Integer state : groupe) {
			ArrayList<Integer> newGroup = new ArrayList<Integer>();
			ArrayList<Integer> allArriv = getArrivAllWords(auto, state);
			for(Integer s : groupe) {
				if(allArriv.equals(getArrivAllWords(auto, s))) {
					newGroup.add(s);
				}
			}
			if(!newListOfGroups.contains(newGroup)) {
				newListOfGroups.add(newGroup);
			}
		}
		
		
		return newListOfGroups;
	}
	
	public static Automate minimiser(Automate auto) {
		if(auto.isAutoDeter()) {
			ArrayList<Integer> entries = auto.getEntries();
			ArrayList<Integer> exits = auto.getExits();
			ArrayList<Transition> transitions = auto.getTransitions();
			ArrayList<ArrayList<Integer>> states = auto.getEtatsDetermin();
			ArrayList<String> mots = auto.getMots();
			Integer size = states.size();
			
			Automate etape = new Automate();
			// Initialisation
			// Pour chaque état de l'ADFC, on regarde si c'est une sortie, ou non, et on créer un nouvelle automate en fonction 
			ArrayList<Integer> etatGroup1 = new ArrayList<Integer>();
			ArrayList<Integer> etatGroup2 = new ArrayList<Integer>();
			for(int i=0; i<size; i++) {
				if(exits.contains(i)) {
					etatGroup1.add(i);
				}
				else {
					etatGroup2.add(i);
				}
			}

			for(String mot :mots) {
				for(int i=0; i<size; i++) {
					ArrayList<Integer> arriv = stateArrivOfAnTransition(auto, i, mot);
					boolean find = false;
					int j =0;
					while(!find) {
						while(j<etatGroup1.size() && !find) {
							if( arriv.equals(states.get(etatGroup1.get(j)))) { 
								etape.addTransition(new Transition(i, 1, mot));
								find=true;
							}
							
							j++;
						}
						j=0;
						while(j<etatGroup2.size() && !find) {
							if( arriv.equals(states.get(etatGroup2.get(j)))) { 
								etape.addTransition(new Transition(i, 2, mot));
								find=true;
							}
							j++;
						}
					}
				}
			}
			
			afficher(etape);
			// suite
			ArrayList<ArrayList<Integer>> listOfGroups = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> listOfGroupsMemo = new ArrayList<ArrayList<Integer>>(listOfGroups);
			ArrayList<ArrayList<Integer>> listOfGroupsMemo2 = new ArrayList<ArrayList<Integer>>(listOfGroups);
			listOfGroups.add(etatGroup1);
			listOfGroups.add(etatGroup2);
			
			boolean isMinimized =false;
			
			while(!isMinimized) {
				boolean divided =false;
				while(!divided) { // On divise en sous groupes
					for(int group=0; group<listOfGroups.size(); group++) { // pour chaque groupe
						ArrayList<ArrayList<Integer>> newListOfGroups = divideGroup(etape, listOfGroups.get(group));
						listOfGroups.remove(listOfGroups.get(group));
						for(ArrayList<Integer> newGroup : newListOfGroups) {
							listOfGroups.add(newGroup);
						}
					}
					// condition d'arrêt de la division
					if(listOfGroups.equals(listOfGroupsMemo)) {
						divided =true;
					}
					else {
						listOfGroupsMemo.clear();
						listOfGroupsMemo = listOfGroups;
					}	
					
				}
				// condition d'arrêt de la minimisation
				if(listOfGroups.equals(listOfGroupsMemo)) {
					isMinimized =true;
				}
				else {
					listOfGroupsMemo.clear();
					listOfGroupsMemo = listOfGroups;
				}	
				
				// On refait l'automate avec les nouveaux groupes
				System.out.println(listOfGroups);
				Automate newEtape = new Automate();
			
				for(int i=0; i<size; i++) {
					for(String mot : mots) {
						for(int group=0; group<listOfGroups.size(); group++) { // chaque groupe
							for(Integer st :  listOfGroups.get(group)) // chaque etat de chaque groupe
								for(Transition tr : transitions) {
									if(tr.getDepart()== i && tr.getMot().equals(mot)) {
										ArrayList<Integer> arriv = stateArrivOfAnTransition(auto, i, mot);
										if(arriv.equals(states.get(st))) {
											newEtape.addTransition(new Transition(i, group, mot));
										}
									}
							}
						}
					}
				}
				
				// on reccommence 
				etape = newEtape;
				afficher(etape);
				//isMinimized = true;
			}
			// creation de l'automate finale minimisé
			Automate AM = new Automate();
			ArrayList<Transition> transiEtape = etape.getTransitions();
			for(int group=0; group<listOfGroups.size(); group++) {
				for(String mot : mots) {
					for(Transition tr : transiEtape) {
						if(tr.getMot().equals(mot) && tr.getDepart() == listOfGroups.get(group).get(0) ){
							AM.addTransition(new Transition(group, tr.getArrivee(), mot));
						}
					}
				}
			}
			
			// ajout des entrees et sorties
			for(int group=0; group<listOfGroups.size(); group++) {
				for(Integer st : listOfGroups.get(group)) {
					if(entries.contains(st))
						AM.addEntry(group);
					
					if(exits.contains(st)) 
						AM.addExit(group);
				}
			}
			
			return AM;
		}
		System.out.println("La minimisation est implenté uniquement pour les automates qui subissent la déterminisation pendant la durée du programme.");
		return auto;
	} 
	
	public static void reconnaissanceMot(Automate auto, String argMot) {
		if(auto.isAutoDeter()) {
			ArrayList<String> mots = auto.getMots();
			ArrayList<Integer> entries = auto.getEntries();
			ArrayList<Integer> exits = auto.getExits();
			ArrayList<Transition> transitions = auto.getTransitions();
			ArrayList<ArrayList<Integer>> states = auto.getEtatsDetermin();
			
			int size = states.size();
			
			boolean reconnu = false;
			boolean blocked = false;
			boolean toShort = false;
			String blockedCar="";
			int c = 0;
			int currentState=entries.get(0);
			while(!reconnu && !blocked && !toShort ) {
				ArrayList<Integer> arriv = new ArrayList<Integer>();
				String car = String.valueOf(argMot.charAt(c));
				if(!car.equals("*")) {
					for(Transition tr : transitions)
					{
						if(tr.getDepart()==currentState && tr.getMot().equals(car)) {
							arriv.add(tr.getArrivee());				
						}
					}
					if(arriv.isEmpty()) {
						blocked =true;
						blockedCar = car;
					}
					else {
						for(int i=0; i<size; i++) {
							if(states.get(i).equals(arriv)) {
								currentState=i;
							}
						}
					}	
				}
				if(exits.contains(currentState) && c==argMot.length()-1 && (mots.contains(car) || car.equals("*")))  {
					reconnu = true;
				
				}else {
					c++;
					if(c>argMot.length()-1)
						toShort =true;
				}
			}
			if(blocked == true) {
				System.out.println("Bloqué !");
				System.out.println("Caractère : [" + blockedCar +"] non reconnu.");
			}
			if(reconnu == true) 
				System.out.println("Bravo !");
			if(toShort == true) 
				System.out.println("Le mot n'a pas atteint la sortie (mot trop court, ou boucle sur un seul état jusqu'a la fin du mot");
		}
		else {
			ArrayList<String> mots = auto.getMots();
			ArrayList<Integer> entries = auto.getEntries();
			ArrayList<Integer> exits = auto.getExits();
			ArrayList<Transition> transitions = auto.getTransitions();
			ArrayList<Integer> states = auto.getStates();
			
			int size = states.size();
			
			boolean reconnu = false;
			boolean blocked = false;
			boolean toShort = false;
			String blockedCar="";
			int c = 0;
			int currentState=entries.get(0);
			while(!reconnu && !blocked && !toShort) {
				
				boolean currentStateChange = false;
				ArrayList<Integer> arriv = new ArrayList<Integer>();
				String car = String.valueOf(argMot.charAt(c));
				if(!car.equals("*")) {
					for(Transition tr : transitions)
					{
						if(tr.getDepart()==currentState && tr.getMot().equals(car)) {
							currentState = tr.getArrivee();
							currentStateChange = true;
						}
					}
					if(!currentStateChange) {
						blocked =true;
						blockedCar = car;
					}
				}
				if(exits.contains(currentState) &&  c==argMot.length()-1 &&  (mots.contains(car) || car.equals("*")) ){
						reconnu = true;
				}
				else {	
					c++;
					if(c>argMot.length()-1)
						toShort =true;
				}
			}
			if(blocked == true) { 
				System.out.println("Bloqué !");
				System.out.println("Caractère : [" + blockedCar +"] non reconnu.");
			}
			if(reconnu == true) 
				System.out.println("Bravo !");
			
			if(toShort == true) 
				System.out.println("Le mot est trop court, il n'est donc pas reconnue");
			
		}
		
	}
	
	public static void lancementReconnaissanceMot(Automate auto) {
		Scanner scanner = new Scanner(System.in);
		String mot;
        do {
        	System.out.println("Saisissez un mot à tester : (-1 pour arréter)");
        	mot = scanner.nextLine();
        	if(mot.equals("-1"))
        		break;
        	reconnaissanceMot(auto, mot);
        	
        }while (true);
	}
	
	public static boolean isStandard(Automate auto) {
		ArrayList<Integer> entries = auto.getEntries();
		ArrayList<Integer> exits = auto.getExits();
		ArrayList<String> mots = auto.getMots();
		ArrayList<Integer> states = auto.getStates();
		ArrayList<Transition> transitions = auto.getTransitions();
		boolean isStandard= true;
		
		if(entries.size()>1) {
			isStandard = false;
			return isStandard;
		}
		else {
			int entry = entries.get(0);
			for(Transition tr : transitions) {
				if(tr.getArrivee() == entry) {
					isStandard = false;
				}
			}
		}
		
		return isStandard;
	}
	
	public static Automate standardiser(Automate auto) {
		if(!auto.isAutoDeter()) {
			ArrayList<Integer> entries = auto.getEntries();
			ArrayList<Integer> exits = auto.getExits();
			ArrayList<Transition> transitions = auto.getTransitions();
			ArrayList<Integer> states = auto.getStates();
			int size = states.size();
			ArrayList<String> mots = auto.getMots();
			
			int newEtatEntry = size+1;
			for(Integer e : entries) {
				for(String mot : mots) {
					for(int tr=0; tr<transitions.size(); tr++) {
						if(transitions.get(tr).getDepart() == e && transitions.get(tr).getMot().equals(mot)){
							auto.addTransition(new Transition(newEtatEntry, transitions.get(tr).getArrivee(), mot));
						}
					}
				}
			}
			
			auto.getEntries().clear();
			auto.addEntry(newEtatEntry);
			
			return auto;
		}
		else {
			ArrayList<ArrayList<Integer>> states = auto.getEtatsDetermin();
			ArrayList<Integer> entries = auto.getEntries();
			ArrayList<Integer> exits = auto.getExits();
			ArrayList<Transition> transitions = auto.getTransitions();
			int size = states.size();
			ArrayList<String> mots = auto.getMots();
			
			ArrayList<Integer> newEtatEntry = new ArrayList<Integer>();
			newEtatEntry.add(size);
			auto.addEtatDetermin(newEtatEntry);
			
			for(Integer e : entries) {
				for(String mot : mots) {
					for(int tr=0; tr<transitions.size(); tr++) {
						if(transitions.get(tr).getDepart() == e && transitions.get(tr).getMot().equals(mot)){
							auto.addTransition(new Transition(size, transitions.get(tr).getArrivee(), mot));
						}
					}
				}
			}
			
			auto.getEntries().clear();
			auto.addEntry(size);
			return auto;
		}
			
	}

	public static Automate complementaire(Automate auto) {
		if(auto.isAutoDeter()) {
			ArrayList<Integer> exits = auto.getExits();
			ArrayList<ArrayList<Integer>> states = auto.getEtatsDetermin();
			
			System.out.println(exits);
			
			for(int i=0; i<states.size(); i++) {
				if(exits.contains(i)) {
					Integer j = i; // Obligé de passé un objet en param de remove, sinon c un remove par indice 
					exits.remove(j);
				}
				else if(!exits.contains(i)){
					exits.add(i);
				}
			}
		}
		else {
			ArrayList<Integer> exits = auto.getExits();
			ArrayList<Integer> states = auto.getStates();
			
			for(Integer state : states) {
				if(exits.contains(state)) {
					Integer j = state; 
					exits.remove(j);
				}
				else {
					exits.add(state);
				}
			}
		}
		
		
		return auto;
		
	}
	
	public static void main(String[] args) {
			try {
				Automate auto = lireFichier();
				auto.reportAsync();
				if(auto.asynchrone()) {
					System.out.println("Automate asynchrone\n");
					afficher(auto);		
					System.out.println("Passage en automate synchrone :");
					afficher(synchronisation(auto));
				}
				else {
					System.out.println("Automate synchrone\n");
					afficher(auto);	
				}
				
				Automate AFD;
				Automate AFDC;
				
				if(isDetermined(auto)) {
					System.out.println("L'automate est déja déterminé (AFD) :");
					System.out.println("Automate déterminisé complet AFDC :");
					AFD = new Automate(auto);
					AFDC = new Automate(completer(AFD));
					afficher(AFDC);
				}
				else {
					System.out.println("L'automate n'est pas déterminé (AFD) :");
					System.out.println("Automate déterminisé complet AFDC :");
					AFD = new Automate(determiniser(auto));
					AFDC = new Automate(completer(AFD));
					afficher(AFDC);
				}
				
				
				System.out.println("Reconnaissance de mot sur l'AFDC :");
				lancementReconnaissanceMot(AFDC);
				
				Scanner scan = new Scanner(System.in);
				String rep="";
				do {
					System.out.println("Voulez vous tentez la minimisation ? y/n ");
					rep = scan.next();
				}
				while(!rep.equals("y") && !rep.equals("n"));
				
				if(rep.equals("y")) {
					System.out.println("Minimisation de l'AFDC :");
					Automate temp = new Automate(determiniser(auto));
					Automate AFDCM = new Automate(minimiser(temp));
					System.out.println("Automate déterminisé complet minimisé AFDCM :");
					afficher(AFDCM);
					System.out.println("Reconnaissance de mot sur l'AFDCM :");
					lancementReconnaissanceMot(AFDCM);
				}
				
				
				System.out.println("Automate complémentaire de l'AFDC :");
				Automate AFDCcomp = new Automate(complementaire(AFDC));
				afficher(AFDCcomp);
				
				System.out.println("Reconnaissance de mot sur l'AFDC complémentaire :");
				lancementReconnaissanceMot(AFDCcomp);
				
				if(isStandard(AFDCcomp)) {
					System.out.println("L'automate complémentaire de l'AFDC est déjà standardisé :");
					afficher(AFDCcomp);
				}
				else {
					System.out.println("L'automate complémentaire de l'AFDC n'est pas standard :");
					System.out.println("Automate complémentaire de l'AFDC standardisé :");
					Automate AFDCcompStd = new Automate(standardiser(AFDCcomp));
					afficher(AFDCcompStd);
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
}
