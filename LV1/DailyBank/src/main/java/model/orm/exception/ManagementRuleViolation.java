package model.orm.exception;

/**
 * Erreur applicative de violation d'une règle de gestion.
 */

@SuppressWarnings("serial")
public class ManagementRuleViolation extends ApplicationException {

	public ManagementRuleViolation(Table tablename, Order order, String message, Throwable cause) {
		super(tablename, order, message, cause);
	}
}
