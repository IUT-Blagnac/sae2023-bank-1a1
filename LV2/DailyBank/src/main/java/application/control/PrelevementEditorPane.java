package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import application.view.PrelevementEditorController;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;

public class PrelevementEditorPane {
	private Stage primaryStage;
	private PrelevementEditorController pmc;
	private DailyBankState dailyBankStage;
	
	public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstage, CompteCourant _compte,Operation _operation,String modifier) {
		this.dailyBankStage = _dbstage;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("prelevementseditor.fxml"));
			BorderPane root = loader.load();
			
			Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Nouveau prélèvement");
			this.primaryStage.setResizable(false);
			
			this.pmc = loader.getController();
			pmc.initContext(primaryStage, dailyBankStage,_compte,_operation, modifier);
			pmc.displayDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
