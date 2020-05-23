package package1;

import java.util.ArrayList;
import java.util.Collections;

public class Automate {
	public static String motVide="*";
	private ArrayList<Integer> entries;
	private ArrayList<Integer> exits;
	private ArrayList<Transition> transitions;
	
	// une fois que l'automate est d�terminis�
	private ArrayList<ArrayList<Integer>> etatsDetermin; 
	private boolean AutoDeter;
	
	public Automate() {
		entries = new ArrayList<Integer>();
		exits = new ArrayList<Integer>();
		transitions = new ArrayList<Transition>();
		AutoDeter = false;
	}
	
	public void addEntry(int entry) {
		entries.add(entry);
	}
	
	public void addExit(int exit) {
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

	public void setAutoDeter(boolean autoDeter) {
		AutoDeter = autoDeter;
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
	
	// true si l'automode est asynchrone (un �tat reconnait le mot vide)
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
