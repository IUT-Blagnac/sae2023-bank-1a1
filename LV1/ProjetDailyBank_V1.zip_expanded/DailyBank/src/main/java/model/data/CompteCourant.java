package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 */

public class CompteCourant {

	public int idNumCompte;
	public int debitAutorise;
	public double solde;
	public String estCloture; // "O" ou "N"

	public int idNumCli;

	public CompteCourant(int idNumCompte, int debitAutorise, double solde, String estCloture, int idNumCli) {
		super();
		this.idNumCompte = idNumCompte;
		this.debitAutorise = debitAutorise;
		this.solde = solde;
		this.estCloture = estCloture;
		this.idNumCli = idNumCli;
	}

	public CompteCourant(CompteCourant cc) {
		this(cc.idNumCompte, cc.debitAutorise, cc.solde, cc.estCloture, cc.idNumCli);
	}

	public CompteCourant() {
		this(0, 0, 0, "N", -1000);
	}

	@Override
	public String toString() {
		String s = "" + String.format("%05d", this.idNumCompte) + " : Solde=" + String.format("%12.02f", this.solde)
				+ "  ,  Découvert Autorise=" + String.format("%8d", this.debitAutorise);
		if (this.estCloture == null) {
			s = s + " (Cloture)";
		} else {
			s = s + (this.estCloture.equals("N") ? " (Ouvert)" : " (Cloture)");
		}
		return s;
	}

}
