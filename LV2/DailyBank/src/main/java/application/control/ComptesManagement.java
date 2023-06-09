package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.Access_BD_CompteCourant;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;

	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.cmcViewController = loader.getController();
			this.cmcViewController.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doComptesManagementDialog() {
		this.cmcViewController.displayDialog();
	}

	public void gererOperationsDUnCompte(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dailyBankState,
				this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	/**
	 * Cette methode permet de crée un nouveau compte pour un client et l'insert dans la Base de Donnée
	 * Par Bilon KWADJANI
	 */
	public CompteCourant creerNouveauCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		if (compte != null) {
			try {
				/* Temporaire jusqu'à implémentation
				compte = null;
				AlertUtilities.showAlert(this.primaryStage, "En cours de développement", "Non implémenté",
						"Enregistrement réel en BDD du compe non effectué\nEn cours de développement", AlertType.ERROR);
				 */
				Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
				ac.insertCompteC(compte);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
		return compte;
	}


	public CompteCourant modifierCompte(CompteCourant cptAModifier) {
		CompteCourant compteModifie;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		compteModifie = cep.doCompteEditorDialog(this.clientDesComptes, cptAModifier , EditionMode.MODIFICATION);
		if (compteModifie != null) {
			try {

				Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
				ac.updateCompteCourant(compteModifie);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
		return compteModifie;
	}

	/**
	 *	Cette fonction permet de récupérer la liste des comptes courants associés à un client.
	 *
	 *	@return une liste d'objets CompteCourant contenant les informations sur chaque compte courant associé au client.
	 *	Si aucun compte n'est associé au client, une liste vide est retournée.
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}

	/**
	 *
	 *Clôture ou rouvre le compte courant spécifié en entrée en fonction de son état actuel.
	 *
	 *@param cc Le compte courant à clôturer ou rouvrir.
	 *
	 *@author Bilon
	 *
	 **/
	public void cloture(CompteCourant cc) {
		try {
			Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
//			if("O".equals(cc.estCloture)){
//				ac.reOuvrirCompte(cc.idNumCompte);
//			}else {
				if(cc.solde!=0){
					System.out.println("1");
					Alert soldepresent = new Alert(AlertType.INFORMATION);
					soldepresent.setHeaderText("Information clôturer compte");
					soldepresent.setContentText("Pour clôturer le compte votre solde doit être de 0 . ");
					soldepresent.show();
				}else {
					System.out.println("2");
					ac.cloturerCompte(cc.idNumCompte);
				}
//			}
		} catch (RowNotFoundOrTooManyRowsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 *
	 *Supprime un compte courant clôturé
	 *
	 *@param cc Le compte courant à clôturer ou rouvrir.
	 *
	 *@author Illan GABARRA
	 *
	 **/
	public void suppressionCompte(CompteCourant cSup) {
		Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
		if("O".equals(cSup.estCloture)){
			try {
				acc.supprimerCompte(cSup);
			} catch (DataAccessException | RowNotFoundOrTooManyRowsException | DatabaseConnexionException e) {
			}
		}

	}
}
