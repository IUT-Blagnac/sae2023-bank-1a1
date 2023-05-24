package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.OperationsManagementController;
import application.view.SimulationManagementController;
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

public class SimulationManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private SimulationManagementController smcViewController;

	public SimulationManagement(Stage _parentStage, DailyBankState _dbstate) {

		this.dailyBankState = _dbstate;
		
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationsManagementController.class.getResource("simulationmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 550 + 20, 390 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Simulations d'emprunts/assurances");
			this.primaryStage.setResizable(false);

			this.smcViewController = loader.getController();
			this.smcViewController.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doSimulationManagementDialog() {
		this.smcViewController.displayDialog();
	}
}
