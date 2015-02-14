package de.bstreit.java.oscr.business.bill.calculator;

/**
 * Describe which elements of a bill should be counted.
 * <p>
 * In case this is set to total, ignore all promo offers when counting.
 * <p>
 * In case this is set to payment, substract promo offers when counting (and set
 * to 0 in case the whole bill is marked as promo)
 * <p>
 * In case this is set to promo-total, count the totals of all bills that are
 * marked as promo, and additionally add the absolute amounts of all promo
 * offers.
 * <p>
 * For example, if we have two bills:
 *
 * <pre>
 * Bill 1 - PROMO
 * - item1    3,00 EUR
 * - item2    5,00 EUR
 *
 * Bill 2
 * - item1    2,00 EUR - 1,00 EUR promo offer = 1,00 EUR
 * - item2    1,50 EUR
 * </pre>
 *
 * <ul>
 * <li>total: 11,50
 * <li>payment: 2,50 (1 + 1,50 from second bill)
 * <li>promo-total: 9 EUR
 * </ul>
 */
public enum WhatToCount {
	/** what the customer actually pays */
	PAYMENT,

	/** the total of all products (not withdrawing reductions) */
	TOTAL,

	/** the (absolute) total of all promo savings (basically, TOTAL - PAYMENT) */
	PROMO_TOTAL
}
