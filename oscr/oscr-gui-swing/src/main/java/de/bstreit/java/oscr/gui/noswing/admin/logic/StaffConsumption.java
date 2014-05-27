package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Maps;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.util.DateFactory;

@Named
public class StaffConsumption implements IAdminBean {

	@Inject
	private IBillRepository billRepository;

	@Override
	public void performTask() {
		final Calendar calendar = Calendar.getInstance();

		final Collection<Bill> billsForStaff = getBillsForLastMonth(calendar);

		System.out.println("Bills for staff - " + getMonth(calendar));
		System.out.println("===============");
		System.out.println("");
		System.out.println("Breakfast: until 11");
		System.out.println("Lunch: 11 - 4");
		System.out.println("Dinner: from 4");
		System.out.println("");

		final Map<User, ConsumptionCounter> consumption = Maps.newHashMap();
		for (final Bill bill : billsForStaff) {
			if (!bill.isConsumedByStaff()) {
				continue;
			}

			final User staffMember = bill.getStaffConsumer();
			if (!consumption.containsKey(staffMember)) {
				consumption.put(staffMember, new ConsumptionCounter(11, 4));
			}

			consumption.get(staffMember).countConsumption(bill);
		}

		for (final User staffMember : consumption.keySet()) {
			System.out.println(staffMember.getFullname());

			final ConsumptionCounter consumptionCounter = consumption
					.get(staffMember);
			System.out.println(" Breakfast: "
					+ consumptionCounter.getBreakfast());
			System.out.println(" Lunch: " + consumptionCounter.getLunch());
			System.out.println(" Dinner: " + consumptionCounter.getDinner());
			System.out.println("");
		}

	}

	private Collection<Bill> getBillsForLastMonth(final Calendar calendar) {
		final Date to = getDate(calendar);

		calendar.roll(Calendar.MONTH, false);
		final Date from = getDate(calendar);

		final Collection<Bill> billsForStaff = billRepository.getBillsForStaff(
				from, to);
		return billsForStaff;
	}

	private String getMonth(Calendar calendar) {
		final String monthName = calendar.getDisplayName(Calendar.MONTH,
				Calendar.LONG, Locale.getDefault());
		final int year = calendar.get(Calendar.YEAR);

		return monthName + " " + year;
	}

	private Date getDate(final Calendar calendar) {
		final int month = calendar.get(Calendar.MONTH) + 1;
		final int year = calendar.get(Calendar.YEAR);

		return DateFactory.getDateWithTimeMidnight(year, month, 1);
	}

	@Override
	public void setScanner(Scanner scanner) {

	}

	@Override
	public String toString() {
		return "Staff Consumption for previous month";
	}
}
