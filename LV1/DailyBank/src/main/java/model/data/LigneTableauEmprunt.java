package model.data;

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
