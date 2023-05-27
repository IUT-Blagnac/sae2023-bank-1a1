package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PrelevementsManagementPane {
	private Stage primaryStage;
	private PrelevementsManagementController pmc;
	private DailyBankState dailyBankStage;
	
	public PrelevementsManagementPane(Stage _parentStage, DailyBankState _dbstage) {
		this.dailyBankStage = _dbstage;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("prelevementsmanagement.fxml"));
			BorderPane root = loader.load();
			
			Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélèvements");
			this.primaryStage.setResizable(false);
			
			this.pmc = loader.getController();
			pmc.initContext(_parentStage, _dbstage);
			pmc.displayDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
