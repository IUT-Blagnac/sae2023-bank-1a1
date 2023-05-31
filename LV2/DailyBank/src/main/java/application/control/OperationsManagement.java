package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.OperationTransfert;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class OperationsManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private Client clientDuCompte;
	private OperationsManagementController omcViewController;
	private CompteCourant compteConcerne;



//	/**
//	 * methode qui prend en parametre un agence Bancaire et un Employe .
//	 * @param unAg
//	 * @param unEmploye
//	 * @return true si l'employer est chef d'agence sinon false
//	 * @author Kwadjani Bilon
//	 */
//	private boolean estChefAg(AgenceBancaire unAg ,Employe unEmploye) {
//		boolean estChef =false;
//
//		if((unAg.idEmployeChefAg==unEmploye.idEmploye )&&(unAg.idAg==unEmploye.idAg)) {
//			estChef=true;
//		}
//		return estChef;
//	}


	public OperationsManagement(Stage _parentStage, DailyBankState _dbstate, Client client, CompteCourant compte) {

		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("operationsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 900 + 20, 350 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des opérations");
			this.primaryStage.setResizable(false);

			this.omcViewController = loader.getController();
			this.omcViewController.initContext(this.primaryStage, this, _dbstate, client, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doOperationsManagementDialog() {
		this.omcViewController.displayDialog();
	}

	/**
	 * Permet de faire saisir à l'utilisateur une nouvelle opération de débit
	 *
	 *
	 * @return L'operation de débit créé
	 */
	public Operation enregistrerDebit() {

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.DEBIT);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				double nSolde =this.compteConcerne.solde-op.montant;

				System.out.println(""+this.compteConcerne.solde);
				System.out.println(""+op.montant);
				System.out.println(""+nSolde);
				System.out.println(""+this.compteConcerne.debitAutorise);

				if(this.dailyBankState.isChefDAgence() && (this.compteConcerne.debitAutorise>nSolde)) {
					    ao.insertDebitExceptionnel(this.compteConcerne.idNumCompte,op.montant, op.idTypeOp);
				}else {
					ao.insertDebit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);
				}

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}

	/**
	 * Permet de faire saisir à l'utilisateur une nouvelle opération de crédit
	 *
	 * @author illan
	 *
	 * @return L'operation de crédit créé
	 */
	public Operation enregistrerCredit() {

		System.out.println("Création d'une page de dialog de saisie de crédit");

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		Operation op = oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.CREDIT);
		System.out.println(op);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				ao.insertCredit(this.compteConcerne.idNumCompte, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				System.err.println(e);
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				System.err.println(ae);
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		System.out.println(op);
		return op;
	}

	/**
	 * Permet de faire saisir à l'utilisateur une nouvelle opération de transfert
	 *
	 * @author illan
	 *
	 * @return L'operation de transfert qui est créé
	 */
	public OperationTransfert enregistrerTransfert() {

		System.out.println("Création d'une page de dialog de saisie de transfert");

		OperationEditorPane oep = new OperationEditorPane(this.primaryStage, this.dailyBankState);
		OperationTransfert op = (OperationTransfert) oep.doOperationEditorDialog(this.compteConcerne, CategorieOperation.TRANSFERT);
		System.out.println(op);
		if (op != null) {
			try {
				Access_BD_Operation ao = new Access_BD_Operation();

				ao.insertTransfert(this.compteConcerne.idNumCompte, op.idNumCompteDestinataire, op.montant, op.idTypeOp);

			} catch (DatabaseConnexionException e) {
				System.err.println(e);
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				System.err.println(ae);
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		System.out.println(op);
		return op;
	}

	/**

	 * Cette méthode permet de récupérer les opérations et le solde d'un compte courant spécifié.
	 *
	 * Elle va récupérer la liste des opérations effectuées sur le compte à partir de la base de données, et va
	 * également récupérer le solde du compte à partir de la base de données. Ces informations seront ensuite
	 * renvoyées sous forme d'une paire de valeurs contenant le compte courant et la liste des opérations.
	 *
	 * En cas d'erreur de connexion à la base de données ou d'exception de l'application, un message d'erreur
	 * sera affiché à l'utilisateur sous forme de dialogue et une liste vide sera renvoyée.
	 *
	 *@return PairesOfValue<CompteCourant, ArrayList<Operation>> la paire de valeurs contenant le compte courant et la liste des opérations.
	 */
	public PairsOfValue<CompteCourant, ArrayList<Operation>> operationsEtSoldeDunCompte() {
		ArrayList<Operation> listeOP = new ArrayList<>();

		try {
			// Relecture BD du solde du compte
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			this.compteConcerne = acc.getCompteCourant(this.compteConcerne.idNumCompte);

			// lecture BD de la liste des opérations du compte de l'utilisateur
			Access_BD_Operation ao = new Access_BD_Operation();
			listeOP = ao.getOperations(this.compteConcerne.idNumCompte);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeOP = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeOP = new ArrayList<>();
		}
		System.out.println(this.compteConcerne.solde);
		return new PairsOfValue<>(this.compteConcerne, listeOP);
	}
}
