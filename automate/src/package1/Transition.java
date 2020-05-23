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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arrivee;
		result = prime * result + depart;
		result = prime * result + ((mot == null) ? 0 : mot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transition other = (Transition) obj;
		if (arrivee != other.arrivee)
			return false;
		if (depart != other.depart)
			return false;
		if (mot == null) {
			if (other.mot != null)
				return false;
		} else if (!mot.equals(other.mot))
			return false;
		return true;
	}
	
	
}
