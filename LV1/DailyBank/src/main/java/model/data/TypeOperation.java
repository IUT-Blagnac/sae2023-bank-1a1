package model.data;

/*
 * Attributs mis en public car cette classe ne fait que "véhiculer" des données.
 */

public class TypeOperation {

	public String idTypeOp;

	public TypeOperation(String idTypeOp) {
		super();
		this.idTypeOp = idTypeOp;
	}

	public TypeOperation(TypeOperation to) {
		this(to.idTypeOp);
	}

	public TypeOperation() {
		this((String) null);
	}

	@Override
	public String toString() {
		return "TypeOperation [idTypeOp=" + this.idTypeOp + "]";
	}

}
