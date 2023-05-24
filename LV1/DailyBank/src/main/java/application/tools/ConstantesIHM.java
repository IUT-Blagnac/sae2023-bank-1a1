package application.tools;

import model.data.Client;
import model.data.Employe;

/**
 * Ensemble de constantes utilisées dans tout DailyBank.
 *
 */
public class ConstantesIHM {

	// Clients

	/**
	 * Code client inactif.
	 */
	public static final String CLIENT_INACTIF = "O";
	/**
	 * Code client actif.
	 */
	public static final String CLIENT_ACTIF = "N";

	// Employés

	/**
	 * Libellé (en BD) d'un chef de banque.
	 */
	public static final String AGENCE_CHEF = "chefAgence";
	/**
	 * Libellé (en BD) d'un guichetier.
	 */
	public static final String AGENCE_GUICHETIER = "guichetier";

	// Types opérations

	/*
	 * Libellés (en BD) des différents types d'opérations existants.
	 */
	public static final String TYPE_OP_1 = "Dépôt Espèces";
	public static final String TYPE_OP_2 = "Retrait Espèces";
	public static final String TYPE_OP_3 = "Dépôt Chèque";
	public static final String TYPE_OP_4 = "Paiement Chèque";
	public static final String TYPE_OP_5 = "Retrait Carte Bleue";
	public static final String TYPE_OP_6 = "Paiement Carte Bleue";
	public static final String TYPE_OP_7 = "Virement Compte à Compte";
	public static final String TYPE_OP_8 = "Prélèvement automatique";
	public static final String TYPE_OP_9 = "Prélèvement agios";

	// Listes d'opérations

	/**
	 * Liste des opérations de débit possible en agence
	 */
	public static final String[] OPERATIONS_DEBIT_GUICHET = { ConstantesIHM.TYPE_OP_2, ConstantesIHM.TYPE_OP_5 };
	/**
	 * Liste des opérations de crédit possible en agence
	 */
	public static final String[] OPERATIONS_CREDIT_GUICHET = { ConstantesIHM.TYPE_OP_1, ConstantesIHM.TYPE_OP_3 };

	/**
	 * Liste des opérations de transfert possible en agence
	 */
	public static final String[] OPERATIONS_TRANSFERT_GUICHET = { ConstantesIHM.TYPE_OP_7 };
	
	
	/*
	 * Libellés des différents types de simulations.
	 */
	public static final String TYPE_SIMUL_1 = "Emprunt Taux fixe";
	public static final String TYPE_SIMUL_2 = "Assurance Taux fixe";
	
	/**
	 * Liste des simulations possible
	 */
	public static final String[] SIMULATIONS = { ConstantesIHM.TYPE_SIMUL_1, ConstantesIHM.TYPE_SIMUL_2 };
	
	
	/*
	 * Libellés des différents types de périodes
	 */
	public static final String TYPE_PERIODE_1 = "Mensualités";
	public static final String TYPE_PERIODE_2 = "Annuités";
	
	/**
	 * Liste des périodes possible
	 */
	public static final String[] PERIODES_SIMULATIONS = { ConstantesIHM.TYPE_PERIODE_1, ConstantesIHM.TYPE_PERIODE_2 };
	
	
	// Méthodes utilitaires

	/**
	 * Teste si un droit d'accès correspond à un Admin, soit un chef d'agence pour
	 * le moment.
	 *
	 * @param droitAccess Libellé du droit d'accès
	 * @return true si admin, false sinon
	 */
	public static boolean isAdmin(String droitAccess) {
		return droitAccess.equals(ConstantesIHM.AGENCE_CHEF);
	}

	/**
	 * Teste si un droit d'accès correspond à un Admin, soit un chef d'agence pour
	 * le moment.
	 *
	 * @param employe Employé à tester
	 * @return true si admin, false sinon
	 */
	public static boolean isAdmin(Employe employe) {
		return employe.droitsAccess.equals(ConstantesIHM.AGENCE_CHEF);
	}

	/**
	 * Teste si un client est actif.
	 *
	 * @param c Client à tester
	 * @return true si le client est actif, false sinon
	 */
	public static boolean estActif(Client c) {
		return c.estInactif.equals(ConstantesIHM.CLIENT_ACTIF);
	}

	/**
	 * Teste si un client est inactif.
	 *
	 * @param c Client à tester
	 * @return true si le client est inactif, false sinon
	 */
	public static boolean estInactif(Client c) {
		return c.estInactif.equals(ConstantesIHM.CLIENT_INACTIF);
	}

}
