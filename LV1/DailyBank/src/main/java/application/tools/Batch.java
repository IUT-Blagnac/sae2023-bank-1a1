package application.tools;

import application.DailyBankState;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

public class Batch implements Runnable{

	private DailyBankState dailyBankState;
	private Stage primaryStage;

	private Client client;
	private CompteCourant compteCourant;

	public Batch(DailyBankState _dailyBankState , Stage _primaryStage,Client client, CompteCourant compteCourant ) {
		this.dailyBankState = _dailyBankState;
		this.primaryStage = _primaryStage;
		this.client = client;
		this.compteCourant = compteCourant;
	}
	
	@Override
	/**
	 * Méthode permettant de lancer le programme batch 
	 */
	public void run() {
		this.creationReleves();
	}
	
	/**
	 * Méthode permettant de génrer les relevés de tous les comptes
	 * 
	 */
	private void creationReleves() {
	}

}
