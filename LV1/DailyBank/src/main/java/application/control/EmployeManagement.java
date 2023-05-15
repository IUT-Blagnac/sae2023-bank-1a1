package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.Access_BD_Client;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeManagement {
	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private EmployeManagementController emcViewController;
	
		public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("employesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des employes");
			this.primaryStage.setResizable(false);

			this.emcViewController = loader.getController();
			this.emcViewController.initContext(this.primaryStage, this, _dbstate);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void doEmployeManagementDialog()
	{
		emcViewController.displayDialog();
	}
	
}
		
		
