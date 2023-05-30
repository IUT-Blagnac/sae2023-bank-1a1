package model.data;

public class LigneTableauAssurance {
	
	public int periode;
	public double montantEmprunt;
	public double tauxAssurance;
	public double mensualite;
	public double totalAssurance;
	
	
	/**
	 * 
	 * @param periode: nombre de période de l'emprunt à cette ligne
	 * @param montantEmprunt :Capital restant dû en début de période
	 * @param tauxAssurance : le taux de l'assurance
	 * @param mensualite : montant de l'assurance mensuel
	 * @param totalAssurance : total de l'assurance
	 * @author bilon kwadjani
	 */
	public LigneTableauAssurance(int periode, double montantEmprunt, double tauxAssurance, double mensualite,
			double totalAssurance) {

		this.periode = periode;
		this.montantEmprunt = montantEmprunt;
		this.tauxAssurance = tauxAssurance;
		this.mensualite = mensualite;
		this.totalAssurance = totalAssurance;
	}
	
	
	public int getPeriode() {
		return periode;
	}

	public double getMontantEmprunt() {
		return Math.round(this.montantEmprunt * 100.0)/ 100.0;
	}

	public double getTauxAssurance() {
		return this.tauxAssurance;
	}

	public double getMensualite() {
		return Math.round(this.mensualite * 100.0)/ 100.0;
	}

	public double getTotalAssurance() {
		return Math.round(this.totalAssurance * 100.0)/ 100.0;
	}

	@Override
	public String toString() {
			return + this.periode + "     |  " + Math.round(this.montantEmprunt * 100.0) / 100.0
					+ "  |  " +this.tauxAssurance  + "  |  " + Math.round(this.mensualite * 100.0)/ 100.0 + "  |  "
					+ Math.round(this.totalAssurance * 100.0)/ 100.0 + "  |  ";
	}
}
