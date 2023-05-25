package model.data;


/**
 * Classe permettant de stocker des informations d'une simulation d'emprunt à une période donnée
 * 
 * Elle permet de véhiculer de l'information
 * 
 * Les getter permettent de récupérer l'information par les objets TableColumn instanciés dans la classe SimulationReadController 
 * et leur propriété PropertyValueFactory qui a besoin de getter afin de récupérer les valeur d'un attribut de LigneTableauEmprunt
 * 
 * @author illan
 *
 */
public class LigneTableauEmprunt {

	
	
	public int getPeriode() {
		return periode;
	}

	public double getCapitalRestantDebut() {
		return Math.round(this.capitalRestantDebut * 100.0)/ 100.0;
	}

	public double getInterets() {
		return Math.round(this.interets * 100.0) / 100.0;
	}

	public double getPrincipal() {
		return Math.round(this.principal * 100.0)/ 100.0;
	}

	public double getMontantARembourser() {
		return Math.round(this.montantARembourser * 100.0)/ 100.0;
	}

	public double getCapitalRestantFin() {
		return Math.round(this.capitalRestantFin * 100.0)/ 100.0;
	}

	public int periode;
	public double capitalRestantDebut;
	public double interets ;
	public double principal;
	public double montantARembourser;
	public double capitalRestantFin;

	/**
	 * 
	 * Constructeur paramétré de LigneTableauEmprunt permettant d'initialiser tous les attributs de l'objet LigneTableauEmprunt
	 * 
	 * @param periode : Nuémro de période de l'emprunt à cette ligne
	 * @param capitalRestantDebut : Capital restant dû en début de période
	 * @param interets : Montant des intérêts
	 * @param principal : Montant du principal (Partie du capital remboursé à cettre période)
	 * @param montantARembourser : Montant de l'annuité/la mensualité à cette période
	 * @param capitalRestantFin Capital restant dû en fin de période
	 */
	public LigneTableauEmprunt(int periode, double capitalRestantDebut, double interets, double principal,
			double montantARembourser, double capitalRestantFin) {

		this.periode = periode;
		this.capitalRestantDebut = capitalRestantDebut;
		this.interets = interets;
		this.principal = principal;
		this.montantARembourser = montantARembourser;
		this.capitalRestantFin = capitalRestantFin;
	}
	
	@Override
	public String toString() {
		return + this.periode + "     |  " + Math.round(this.capitalRestantDebut * 100.0) / 100.0
				+ "  |  " + Math.round(this.interets * 100.0) / 100.0  + "  |  " + Math.round(this.principal * 100.0)/ 100.0 + "  |  "
				+ Math.round(this.montantARembourser * 100.0)/ 100.0 + "  |  " + Math.round(this.capitalRestantFin * 100.0)/ 100.0;
	}
}
