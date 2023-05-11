package model.data;

import java.sql.Date;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 */

public class Operation {

	public int idOperation;
	public double montant;
	public Date dateOp;
	public Date dateValeur;
	public int idNumCompte;
	public String idTypeOp;

	public Operation(int idOperation, double montant, Date dateOp, Date dateValeur, int idNumCompte, String idTypeOp) {
		super();
		this.idOperation = idOperation;
		this.montant = montant;
		this.dateOp = dateOp;
		this.dateValeur = dateValeur;
		this.idNumCompte = idNumCompte;
		this.idTypeOp = idTypeOp;
	}

	public Operation(Operation o) {
		this(o.idOperation, o.montant, o.dateOp, o.dateValeur, o.idNumCompte, o.idTypeOp);
	}

	public Operation() {
		this(-1000, 0, null, null, -1000, null);
	}

	@Override
	public String toString() {
		return this.dateOp + " : " + String.format("%25s", this.idTypeOp) + " "
				+ String.format("%10.02f", this.montant);

//		return "Operation [idOperation=" + idOperation + ", montant=" + montant + ", dateOp=" + dateOp + ", dateValeur="
//				+ dateValeur + ", idNumCompte=" + idNumCompte + ", idTypeOp=" + idTypeOp + "]";
	}
}
