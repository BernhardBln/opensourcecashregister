package de.bstreit.java.oscr.business.export.consumption;

import java.util.Date;

import de.bstreit.java.oscr.business.bill.Bill;

public class ConsumptionCounter {

	private final int lunchBegin;
	private final int dinnerBegin;

	private int breakfast = 0;
	private int lunch = 0;
	private int dinner = 0;

	/**
	 * Create a new consumption counter. Hours in 24h format.
	 * 
	 * @param lunchBegin
	 *            everything before this hour is counted as breakfast (e.g. 11
	 *            -> 10:59:59.999 is still breakfast)
	 * @param dinnerBegin
	 *            everything before this hour is counted as lunch (&lt;),
	 *            everything after as dinner (&ge;)
	 */
	public ConsumptionCounter(int lunchBegin, int dinnerBegin) {
		this.lunchBegin = lunchBegin;
		this.dinnerBegin = dinnerBegin;
	}

	public void countConsumption(Bill bill) {
		final Date date = bill.getBillOpened();
		final int hour = date.getHours();

		if (hour < lunchBegin) {
			breakfast++;
			return;
		}

		if (hour < dinnerBegin) {
			lunch++;
			return;
		}

		dinner++;
	}

	public int getBreakfast() {
		return breakfast;
	}

	public int getLunch() {
		return lunch;
	}

	public int getDinner() {
		return dinner;
	}

}
