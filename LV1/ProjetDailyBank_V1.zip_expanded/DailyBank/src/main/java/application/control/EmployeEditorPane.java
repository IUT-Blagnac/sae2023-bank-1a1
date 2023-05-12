package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmployeEditorPaneController;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;

public class EmployeEditorPane {
	
	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private EmployeEditorPaneController emcViewPaneController;
	
	public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate)
	{
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("employeeditorpane.fxml"));
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

			this.emcViewPaneController = loader.getController();
			this.emcViewPaneController.initContext(this.primaryStage, null, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doEmployeManagementDialog(Employe employe)
	{
		emcViewPaneController.displayDialog(employe);
	}
}
