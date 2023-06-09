package model.data;

import java.sql.Date;

/**
 * Classe héritant de Operation afin de stocker le numéro de compte du destinataire.
 *
 * @author illan
 *
 */
public class OperationTransfert extends Operation {

	public int idNumCompteDestinataire;

	public OperationTransfert(int idOperation, double montant, Date dateOp, Date dateValeur, int idNumCompte,
			String idTypeOp, int idNumCompteDestinataire) {
		super(idOperation, montant, dateOp, dateValeur, idNumCompte, idTypeOp);

		this.idNumCompteDestinataire = idNumCompteDestinataire;
	}

	public OperationTransfert() {
		super();
		this.idNumCompteDestinataire = -1;
	}

}
