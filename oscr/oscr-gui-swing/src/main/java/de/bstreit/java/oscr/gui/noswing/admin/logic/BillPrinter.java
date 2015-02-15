package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.text.formatting.BillTotalForADayFormatter;

@Named
public class BillPrinter implements IAdminBean {

	@Inject
	private BillService billService;

	@Inject
	private BillTotalForADayFormatter billTotalForADayFormatter;

	private final DateFormat dateFormat = DateFormat
			.getDateInstance(DateFormat.SHORT);

	private Scanner scanner;

	@Override
	@Transactional
	public void performTask() {
		System.out.println("Which day? (blank for yesterday): ");
		final String dateAsStr = scanner.nextLine().trim();

		final Date day;

		if (StringUtils.isBlank(dateAsStr)) {
			day = getYesterday();
		} else {

			try {
				day = dateFormat.parse(dateAsStr);
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}

		}

		System.out.println(billTotalForADayFormatter.getBillTotalAsString(
				dateFormat.format(day),
				billService.getBillsForDayWithoutStaff(day)));

	}

	private Date getYesterday() {
		Calendar calendar = Calendar.getInstance();
		calendar.roll(Calendar.DAY_OF_MONTH, false);
		return calendar.getTime();
	}

	@Override
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public String toString() {
		return "Print bills";
	}

}
