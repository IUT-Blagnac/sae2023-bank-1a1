package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 */

public class AgenceBancaire {

	public int idAg;

	public String nomAg;
	public String adressePostaleAg;
	public int idEmployeChefAg;

	public AgenceBancaire(int idAg, String nomAg, String adressePostaleAg, int idEmployeChefAg) {
		super();
		this.idAg = idAg;
		this.nomAg = nomAg;
		this.adressePostaleAg = adressePostaleAg;
		this.idEmployeChefAg = idEmployeChefAg;
	}

	public AgenceBancaire(AgenceBancaire ag) {
		this(ag.idAg, ag.nomAg, ag.adressePostaleAg, ag.idEmployeChefAg);
	}

	public AgenceBancaire() {
		this(-1000, null, null, -1000);
	}

	@Override
	public String toString() {
		return "AgenceBancaire [idAg=" + this.idAg + ", nomAg=" + this.nomAg + ", adressePostaleAg="
				+ this.adressePostaleAg + ", idEmployeChefAg=" + this.idEmployeChefAg + "]";
	}
}
