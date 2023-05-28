package model.data;

public class PrelevementAutomatique {
	public int idPrelevement;
	public double montant;
	public int dateRecurrente;
	public String beneficiaire;
	public int idNumCompte;
	
	public PrelevementAutomatique() {
		this.idPrelevement = -1000;
		this.montant = 0;
		this.dateRecurrente = 1;
		this.beneficiaire = "";
		this.idNumCompte = -1000;
	}
	
	public PrelevementAutomatique(int idPrelevement, double montant, int dateRecurrente, String beneficiaire, int idNumCompte) {
		this.idPrelevement = idPrelevement;
		this.montant = montant;
		this.dateRecurrente = dateRecurrente;
		this.beneficiaire = beneficiaire;
		this.idNumCompte = idNumCompte;
	}
	
	public String toString() {
		return "[" + this.idPrelevement + "]" + this.montant + " - " + this.beneficiaire;
	}
}
