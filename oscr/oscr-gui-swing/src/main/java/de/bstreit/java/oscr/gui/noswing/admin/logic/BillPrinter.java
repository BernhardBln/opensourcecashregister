package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.bill.IBillProcessor;
import de.bstreit.java.oscr.text.formatting.BillFormatter;

@Named
public class BillPrinter implements IAdminBean {

	@Inject
	private BillService billService;

	@Inject
	private BillFormatter billFormatter;

	private Scanner scanner;

	@Override
	public void performTask() {
		System.out.println("Which day? (blank for yesterday): ");
		final String dateAsStr = scanner.nextLine().trim();

		final Date day;

		if (StringUtils.isBlank(dateAsStr)) {
			day = getYesterday();
		} else {

			try {
				day = DateFormat.getInstance().parse(dateAsStr);
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}

		}

		billService.processBillsAt(new IBillProcessor() {

			@Override
			public void processBill(Bill bill) {
				System.out.println(billFormatter.formatBill(bill));
				System.out.println("\n\n");
			}

		}, day);
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
