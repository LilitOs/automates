package package1;

import java.util.ArrayList;
import java.util.Collections;

public class Automate {
	public static String motVide="*";
	private ArrayList<Integer> entries;
	private ArrayList<Integer> exits;
	private ArrayList<Transition> transitions;
	
	// une fois que l'automate est déterminisé
	private ArrayList<ArrayList<Integer>> etatsDetermin; 
	private boolean AutoDeter;
	
	// si completion
	private boolean etatPoubelle;
	
	public Automate() {
		entries = new ArrayList<Integer>();
		exits = new ArrayList<Integer>();
		transitions = new ArrayList<Transition>();
		AutoDeter = false;
		etatPoubelle = false;
	}
	
	public Automate(Automate auto) {
		this.entries = auto.getEntries();	
		this.exits = auto.getExits();	
		this.transitions = auto.getTransitions();	
		this.AutoDeter = auto.isAutoDeter();
		this.etatPoubelle = auto.isEtatPoubelle();
		this.etatsDetermin = auto.getEtatsDetermin();
	}
	
	public void resetAutomate() {
		entries.clear();
		exits.clear();
		transitions.clear();
		AutoDeter = false;
		etatPoubelle = false;
	}
	
	public void addEntry(int entry) {
		if(!entries.contains(entry))
			entries.add(entry);
	}
	
	public void addExit(int exit) {
		if(!exits.contains(exit))
			exits.add(exit);
	}
	
	public boolean isAutoDeter() {
		return AutoDeter;
	}

	public ArrayList<ArrayList<Integer>> getEtatsDetermin() {
		return etatsDetermin;
	}

	public void setEtatsDetermin(ArrayList<ArrayList<Integer>> etatsDetermin) {
		this.etatsDetermin = etatsDetermin;
	}
	
	public void addEtatDetermin(ArrayList<Integer> etat) {
		etatsDetermin.add(etat);
	}

	public void setAutoDeter(boolean autoDeter) {
		AutoDeter = autoDeter;
	}
	
	public boolean isEtatPoubelle() {
		return etatPoubelle;
	}

	public void setEtatPoubelle(boolean etatPoubelle) {
		this.etatPoubelle = etatPoubelle;
	}

	public void addTransition(Transition t) {
		if(!transitions.contains(t))
			transitions.add(t);
	}
	
	public void removeTransition(Transition t) {
		transitions.remove(t);
	}
	
	
	public ArrayList<Integer> getEntries() {
		return entries;
	}

	public ArrayList<Integer> getExits() {
		return exits;
	}

	public ArrayList<Transition> getTransitions() {
		return transitions;
	}
	
	public ArrayList<String> getMots(){
		ArrayList<String> words = new ArrayList<String>();
		
		for(Transition transi : transitions) {
			if(!words.contains(transi.getMot())) {
				words.add(transi.getMot());
			}
		}
		return words;
	}

	
	public ArrayList<Integer> getStates() {
		ArrayList<Integer> states = new ArrayList<Integer>();
		
		for(Transition transi : transitions) {
			if(!states.contains(transi.getDepart())) {
				states.add(transi.getDepart());
			}
			else if(!states.contains(transi.getArrivee())){
				states.add(transi.getArrivee());
			}
		}
		Collections.sort(states);
		return states;
	}

	@Override
	public String toString() {
		return "Automate [entries=" + entries + ", exits=" + exits + ", transitions=" + transitions + "]";
	}
	
	public int nbStates() {
		int count =0;
		ArrayList<Integer> states = new ArrayList<Integer>();
		
		for(Transition transi : transitions) {
			if(!states.contains(transi.getDepart())) {
				count++;
				states.add(transi.getDepart());
			}
			else if(!states.contains(transi.getArrivee())){
				count++;
				states.add(transi.getArrivee());
			}
		}
	
		return count;
	}
	
	public int nbWords() {
		int count =0;
		ArrayList<String> words = new ArrayList<String>();
		
		for(Transition transi : transitions) {
			if(!words.contains(transi.getMot())) {
				count++;
				words.add(transi.getMot());
			}
		}
		return count;
	}
	
	// true si l'automode est asynchrone (un état reconnait le mot vide)
	public boolean asynchrone() {
		return this.getMots().contains(motVide);
	}
	
	public void reportAsync() {
		for(Transition transi : transitions) {
			if(transi.getMot().equals(motVide)) {
				System.out.println(transi + " -> mot vide recconu\n");
			}
			else {
				System.out.println(transi + " -> mot vide non recconu\n");
			}

		}
	}
}
