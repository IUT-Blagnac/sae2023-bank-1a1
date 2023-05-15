package model.orm.exception;

/**
 * Erreur de connexion à la bd. Impossible d'établir une connexion sur la bd.
 */

@SuppressWarnings("serial")
public class DatabaseConnexionException extends ApplicationException {

	public DatabaseConnexionException(String message, Throwable e) {
		super(Table.NONE, Order.OTHER, message, e);
	}
}