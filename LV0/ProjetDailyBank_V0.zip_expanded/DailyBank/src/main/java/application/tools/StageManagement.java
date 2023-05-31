package application.tools;

import javafx.stage.Stage;

/**
 * Classe utilitaire pour centrer automatiquement une fenêtre sur une autre (2
 * stage en fait). <BR />
 *
 * Se fait en fait en calculant à l'ouverture la position de la fenêtre en
 * fonction de la position et de la taille de la fenêtre sur laquelle se
 * centrer.
 *
 */
public class StageManagement {

	/**
	 * Static method for centering primary Stage over its parent Stage.
	 *
	 * @param parent  Stage on which primary is centered on
	 * @param primary Stage to be centered (with regards to parent)
	 */
	public static void manageCenteringStage(Stage parent, Stage primary) {

		// Calculate the center position of the parent Stage
		double centerXPosition = parent.getX() + parent.getWidth() / 2d;
		double centerYPosition = parent.getY() + parent.getHeight() / 2d;

		// Hide the pop-up stage before it is shown and becomes relocated
		primary.setOnShowing(ev -> primary.hide());

		// Relocate the pop-up Stage
		primary.setOnShown(ev -> {
			primary.setX(centerXPosition - primary.getWidth() / 2d);
			primary.setY(centerYPosition - primary.getHeight() / 2d);
			primary.show();
		});
	}
}
