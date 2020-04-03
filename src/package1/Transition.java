package package1;

public class Transition {
	private int depart;
	private int arrivee;
	private String mot;
	
	public Transition(int depart, int arrivee, String mot) {
		this.depart = depart;
		this.arrivee = arrivee;
		this.mot = mot;
	}

	public int getDepart() {
		return depart;
	}

	public void setDepart(int depart) {
		this.depart = depart;
	}

	public int getArrivee() {
		return arrivee;
	}

	public void setArrivee(int arrivee) {
		this.arrivee = arrivee;
	}

	public String getMot() {
		return mot;
	}

	public void setMot(String mot) {
		this.mot = mot;
	}

	@Override
	public String toString() {
		return depart + " " + mot + " " + arrivee;
	}
	
	
}
