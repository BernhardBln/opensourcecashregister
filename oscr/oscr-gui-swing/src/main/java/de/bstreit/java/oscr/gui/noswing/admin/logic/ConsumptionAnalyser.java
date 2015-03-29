package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculatorFactory;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.staff.User;

@Named
public class ConsumptionAnalyser {

	@Inject
	private IBillRepository billRepository;

	@Inject
	private Currency currency;

	@Inject
	private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;

	@Value("${staffConsumption.breakfast.price}")
	private String breakfastPriceStr;

	private Money breakfastPrice;

	@Value("${staffConsumption.lunch.price}")
	private String lunchPriceStr;

	private Money lunchPrice;

	@Value("${staffConsumption.dinner.price}")
	private String dinnerPriceStr;

	@Value("${staffConsumption.managementUser}")
	private String managementUser;

	private Money dinnerPrice;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofLocalizedDateTime(FormatStyle.SHORT);

	private transient Map<User, ConsumptionCounter> consumptionCounters = Maps
			.newHashMap();

	private transient List<Bill> managementConsumption = Lists.newArrayList();
	private transient Map<User, List<Bill>> realStaffConsumption = Maps
			.newHashMap();

	@PostConstruct
	public void init() {
		breakfastPrice = new Money(breakfastPriceStr, currency);
		lunchPrice = new Money(lunchPriceStr, currency);
		dinnerPrice = new Money(dinnerPriceStr, currency);
	}

	@Transactional
	public void analyse(YearMonth month,
			Consumer<ConsumptionAnalyser> resultConsumer) {

		try {
			final Collection<Bill> billsForStaff = getBillsOf(month);

			countStaffBills(billsForStaff);
			printResults();

			printManagementConsumption();

			resultConsumer.accept(this);

		} finally {
			consumptionCounters.clear();
			managementConsumption.clear();
			realStaffConsumption.clear();
		}
	}

	private void printManagementConsumption() {
		List<Bill> billsToConsider = managementConsumption;
		String consumer = "Management";

		printConsumption(billsToConsider, consumer);
	}

	private void printConsumption(List<Bill> billsToConsider, String consumer) {
		final IMultipleBillsCalculator multipleBillsCalculator = multipleBillsCalculatorFactory
				.create(billsToConsider, WhatToCount.TOTAL);

		Money totalNet = new Money("0", currency);

		for (final VATClass vatClass : multipleBillsCalculator
				.getAllVatClasses()) {
			totalNet = totalNet.add(multipleBillsCalculator
					.getTotalNetFor(vatClass));
		}

		System.out.println(MessageFormat.format("\n" + consumer
				+ " consumption: GROS {0} - NET {1}",
				multipleBillsCalculator.getTotalGross(), totalNet));

		for (final VATClass vatClass : multipleBillsCalculator
				.getAllVatClasses()) {
			System.out.println(" -> " + vatClass + " of "
					+ multipleBillsCalculator.getTotalGrossFor(vatClass)
					+ " (gros) -> "
					+ multipleBillsCalculator.getTotalVATFor(vatClass));
		}

		System.out.println("\n");
	}

	public Set<User> getStaff() {
		return consumptionCounters.keySet();
	}

	private void printResults() {
		for (final User staffMember : getStaff()) {
			System.out.println(staffMember.getFullname());

			List<Consumption> consumptions = getConsumptionFor(staffMember);
			for (Consumption consumption : consumptions) {
				printoutMeal(consumption);
			}

			System.out.println("TOTAL: " + getConsumptionForTax(staffMember));
			System.out.println("\nReal consumption:");
			printConsumption(getRealConsumption(staffMember),
					staffMember.getFullname());
			System.out.println("");
		}
	}

	private List<Bill> getRealConsumption(final User staffMember) {
		return realStaffConsumption.get(staffMember);
	}

	public Money getConsumptionForTax(final User staffMember) {

		final ConsumptionCounter consumptionCounter = consumptionCounters
				.get(staffMember);

		Money total = new Money("0", currency);
		total = total.add(breakfastPrice.multiply(consumptionCounter
				.getBreakfast()));
		total = total.add(lunchPrice.multiply(consumptionCounter.getLunch()));
		total = total.add(dinnerPrice.multiply(consumptionCounter.getDinner()));
		return total;

	}

	private void printoutMeal(Consumption consumption) {
		System.out
		.println(" " + consumption.getMeal() + ": "
				+ consumption.getAmount() + " ("
				+ consumption.getTotal() + ")");
	}

	private List<Consumption> getConsumptionFor(User staffMember) {
		List<Consumption> consumptions = Lists.newArrayList();

		final ConsumptionCounter consumptionCounter = consumptionCounters
				.get(staffMember);

		consumptions.add(new Consumption("Breakfast", consumptionCounter
				.getBreakfast(), breakfastPrice));
		consumptions.add(new Consumption("Lunch",
				consumptionCounter.getLunch(), lunchPrice));
		consumptions.add(new Consumption("Dinner", consumptionCounter
				.getDinner(), dinnerPrice));

		return consumptions;
	}

	public static class Consumption {
		private String meal;
		private int amount;
		private Money mealPrice;

		public Consumption(String meal, int amount, Money mealPrice) {
			super();
			this.meal = meal;
			this.amount = amount;
			this.mealPrice = mealPrice;
		}

		public String getMeal() {
			return meal;
		}

		public int getAmount() {
			return amount;
		}

		public Money getTotal() {
			return mealPrice.multiply(amount);
		}
	}

	private void countStaffBills(final Collection<Bill> billsForStaff) {
		for (final Bill bill : billsForStaff) {
			if (!bill.isConsumedByStaff()) {
				continue;
			}

			final User staffMember = bill.getStaffConsumer();

			if (staffMember.getName().equals(managementUser)) {
				managementConsumption.add(bill);
			} else {

				if (!consumptionCounters.containsKey(staffMember)) {
					consumptionCounters.put(staffMember,
							new ConsumptionCounter(11, 4));
				}

				if (!realStaffConsumption.containsKey(staffMember)) {
					realStaffConsumption.put(staffMember, Lists.newArrayList());
				}

				consumptionCounters.get(staffMember).countConsumption(bill);
				getRealConsumption(staffMember).add(bill);
			}
		}
	}

	private Collection<Bill> getBillsOf(final YearMonth month) {

		LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = month.atEndOfMonth().atTime(LocalTime.MAX);

		System.out.println("From\n " + dateTimeFormatter.format(startOfMonth)
				+ "\nto\n " + dateTimeFormatter.format(endOfMonth) + "\n\n");

		final Collection<Bill> billsForStaff = billRepository
				.getBillsForStaffRanged(toDate(startOfMonth),
						toDate(endOfMonth));

		return billsForStaff;
	}

	private static Date toDate(LocalDateTime atTime) {
		return Date.from(atTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public String toString() {
		return "Staff Consumption for previous month";
	}

}
