package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.time.YearMonth;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.staff.User;

@Named
public class StaffConsumption implements IAdminBean {

	private final ConsumptionAnalyser consumptionAnalyser;

	@Inject
	public StaffConsumption(ConsumptionAnalyser consumptionAnalyser) {
		super();
		this.consumptionAnalyser = consumptionAnalyser;
	}

	@Override
	public void performTask() {
		final YearMonth lastMonth = getLastMonth();

		consumptionAnalyser.analyse(lastMonth,
				analyser -> printResult(analyser));
	}

	private YearMonth getLastMonth() {
		return YearMonth.now().minusMonths(1);
	}

	public void printResult(ConsumptionAnalyser consumptionAnalyser) {

		User user = consumptionAnalyser.getStaff().iterator().next();
		Money consumptionForTax = consumptionAnalyser
				.getConsumptionForTax(user);

		System.out.println(consumptionForTax);

		// final Collection<Bill> billsForStaff =
		// getBillsForLastMonth(calendar);
		//
		// printHeader(lastMonth);

		// countStaffBills(billsForStaff);
		// printResults();
		//
		// printManagementConsumption();

	}

	//
	// private void printManagementConsumption() {
	// List<Bill> billsToConsider = managementConsumption;
	// String consumer = "Management";
	//
	// printConsumption(billsToConsider, consumer);
	// }
	//
	// private void printConsumption(List<Bill> billsToConsider, String
	// consumer) {
	// final IMultipleBillsCalculator multipleBillsCalculator =
	// multipleBillsCalculatorFactory
	// .create(billsToConsider, WhatToCount.TOTAL);
	//
	// Money totalNet = new Money("0", currency);
	//
	// for (final VATClass vatClass : multipleBillsCalculator
	// .getAllVatClasses()) {
	// totalNet = totalNet.add(multipleBillsCalculator
	// .getTotalNetFor(vatClass));
	// }
	//
	// System.out.println(MessageFormat.format("\n" + consumer
	// + " consumption: GROS {0} - NET {1}",
	// multipleBillsCalculator.getTotalGross(), totalNet));
	//
	// for (final VATClass vatClass : multipleBillsCalculator
	// .getAllVatClasses()) {
	// System.out.println(" -> " + vatClass + " of "
	// + multipleBillsCalculator.getTotalGrossFor(vatClass)
	// + " (gros) -> "
	// + multipleBillsCalculator.getTotalVATFor(vatClass));
	// }
	//
	// System.out.println("\n");
	// }
	//
	// private void printResults() {
	// for (final User staffMember : consumption.keySet()) {
	// System.out.println(staffMember.getFullname());
	//
	// final ConsumptionCounter consumptionCounter = consumption
	// .get(staffMember);
	//
	// printoutMeal(breakfastPrice, consumptionCounter.getBreakfast(),
	// "Breakfast");
	// printoutMeal(lunchPrice, consumptionCounter.getLunch(), "Lunch");
	// printoutMeal(dinnerPrice, consumptionCounter.getDinner(), "Dinner");
	//
	// Money total = new Money("0", currency);
	// total = total.add(breakfastPrice.multiply(consumptionCounter
	// .getBreakfast()));
	// total = total
	// .add(lunchPrice.multiply(consumptionCounter.getLunch()));
	// total = total.add(dinnerPrice.multiply(consumptionCounter
	// .getDinner()));
	//
	// System.out.println("TOTAL: " + total);
	// System.out.println("\nReal consumption:");
	// printConsumption(realStaffConsumption.get(staffMember),
	// staffMember.getFullname());
	// System.out.println("");
	// }
	// }
	//
	// private void countStaffBills(final Collection<Bill> billsForStaff) {
	// for (final Bill bill : billsForStaff) {
	// if (!bill.isConsumedByStaff()) {
	// continue;
	// }
	//
	// final User staffMember = bill.getStaffConsumer();
	//
	// if (staffMember.getName().equals(managementUser)) {
	// managementConsumption.add(bill);
	// } else {
	//
	// if (!consumption.containsKey(staffMember)) {
	// consumption.put(staffMember, new ConsumptionCounter(11, 4));
	// }
	//
	// if (!realStaffConsumption.containsKey(staffMember)) {
	// realStaffConsumption.put(staffMember, Lists.newArrayList());
	// }
	//
	// consumption.get(staffMember).countConsumption(bill);
	// realStaffConsumption.get(staffMember).add(bill);
	// }
	// }
	// }
	//
	// private void printHeader(final Calendar calendar) {
	// System.out.println("Bills for staff - " + getMonth(calendar));
	// System.out.println("===============");
	// System.out.println("");
	// System.out.println("Breakfast: until 11 (costs: " + breakfastPrice
	// + ")");
	// System.out.println("Lunch: 11 - 4 (costs: " + lunchPrice + ")");
	// System.out.println("Dinner: from 4 (costs: " + dinnerPrice + ")");
	// System.out.println("");
	// }
	//
	// private void printoutMeal(Money mealPrice, int amount, String label) {
	// final Money total = mealPrice.multiply(amount);
	// System.out.println(" " + label + ": " + amount + " (" + total + ")");
	// }
	//
	// private Collection<Bill> getBillsForLastMonth(final Calendar calendar) {
	// final Date to = getDate(calendar);
	//
	// final Date from = getDate(calendar);
	//
	// System.out.println("From " + from + " to " + to);
	//
	// final Collection<Bill> billsForStaff = billRepository.getBillsForStaff(
	// from, to);
	// return billsForStaff;
	// }
	//
	// private String getMonth(Calendar calendar) {
	// final String monthName = calendar.getDisplayName(Calendar.MONTH,
	// Calendar.LONG, Locale.getDefault());
	// final int year = calendar.get(Calendar.YEAR);
	//
	// return monthName + " " + year;
	// }
	//
	// private Date getDate(final Calendar calendar) {
	// final int month = calendar.get(Calendar.MONTH) + 1;
	// final int year = calendar.get(Calendar.YEAR);
	//
	// return DateFactory.getDateWithTimeMidnight(year, month, 1);
	// }

	@Override
	public void setScanner(Scanner scanner) {

	}

	@Override
	public String toString() {
		return "Staff Consumption for previous month";
	}
}
