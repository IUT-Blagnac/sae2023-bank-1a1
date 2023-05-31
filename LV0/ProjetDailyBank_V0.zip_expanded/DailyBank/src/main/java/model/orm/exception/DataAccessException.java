package model.orm.exception;

/**
 * Erreur d'accès à la bd. Une requête a été soumise et aboutit à une erreur
 * (souvent SQLException)
 */

@SuppressWarnings("serial")
public class DataAccessException extends ApplicationException {

	public DataAccessException(Table tablename, Order order, String message, Throwable cause) {
		super(tablename, order, message, cause);
	}
}
