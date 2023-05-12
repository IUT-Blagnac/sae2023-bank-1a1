package application;

import application.control.ExceptionDialog;
import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;
import model.orm.Access_BD_AgenceBancaire;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

/**
 * Classe décrivant l'état courant de l'application : - Employé connecté -
 * Agence bancaire de l'employé connecté - L'employé connecté est il chef
 * d'agence ou pas
 */

public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;

	/**
	 * Employé connecté.
	 *
	 * @return l'employé connecté (ou null si personne connecté)
	 */
	public Employe getEmployeActuel() {
		return this.empAct;
	}

	/**
	 * Agence bancaire où travaille l'employé connecté.
	 *
	 * @return l'agence bancaire (ou null si personne connecté)
	 */
	public AgenceBancaire getAgenceActuelle() {
		return this.agAct;
	}

	/**
	 * Staut de l'employé connecté
	 *
	 * @return true si chef d'agence, false sinon (ou personne connecté)
	 */
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}

	/**
	 * Permet de modifier l'employé connecté.
	 *
	 * @param employeActif nouvel employé (null si personne connecté)
	 */
	public void setEmployeActuel(Employe employeActif) {

		Access_BD_AgenceBancaire aab;
		AgenceBancaire ag;

		if (employeActif == null) {
			this.empAct = null;
			this.agAct = null;
			this.isChefDAgence = false;
		} else {
			try {
				aab = new Access_BD_AgenceBancaire();
				ag = aab.getAgenceBancaire(employeActif.idAg);
				this.empAct = employeActif;
				this.agAct = ag;
				this.isChefDAgence = this.definirChefDAgence(this.empAct.droitsAccess);

			} catch (DataAccessException | DatabaseConnexionException | RowNotFoundOrTooManyRowsException e) {
				this.empAct = null;
				this.agAct = null;
				this.isChefDAgence = false;
				ExceptionDialog ed = new ExceptionDialog(null, null, e);
				ed.doExceptionDialog();
			}
		}
	}

	/**
	 * Permet de modifier le statut de l'employé connecté.
	 *
	 * @param droitsAccess ConstantesIHM.AGENCE_CHEF ou
	 * ConstantesIHM.AGENCE_GUICHETIER
	 */
	private boolean definirChefDAgence(String droitsAccess) {
		boolean ca;

		ca = false;
		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			ca = true;
		}
		return ca;
	}
}
