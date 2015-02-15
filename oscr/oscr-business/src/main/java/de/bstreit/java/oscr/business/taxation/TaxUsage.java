package de.bstreit.java.oscr.business.taxation;

/**
 * Changes of existing members here <b>MUST</b> be treated with a database
 * update!
 * <p>
 * Max length for enum names: 30 chars
 */
public enum TaxUsage {

	/**
	 * Can be applied on the whole bill, standard VAT (there can only be one of
	 * this kind)
	 */
	GLOBAL_STANDARD_VAT,

	/**
	 * Can be applied on the whole bill, reduced VAT (there can only be one of
	 * this kind)
	 */
	GLOBAL_REDUCED_VAT,

	/**
	 * Additional markers to be applied on bill item level (e.g. when sitting
	 * down in-house, ordering a cappuccino and buying a pack of beans, the
	 * beans should be charged reduced VAT, although the whole bill is marked as
	 * in-house (and hence standard VAT).
	 */
	BILLITEM_VAT;

	private TaxUsage() {

		if (this.name().length() > 30) {
			// in case you really need more than 30 letters, change all @Column
			// definitions of fields that are of this types
			throw new IllegalStateException(
					"Fields in this enum should not be longer than 30 chars");
		}

	}
}
