package de.bstreit.java.oscr.testutils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.bstreit.java.oscr.business.base.date.ICurrentDateProvider;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.BillService;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.TaxUsage;
import de.bstreit.java.oscr.testutils.business.bill.FixDateProvider;

/**
 * This configuration provides a bill service instance and its mocked
 * dependencies in order to make it usable in junit tests.
 *
 * <p>
 * You might want to provider your own {@link ICurrentDateProvider} bean; in
 * that case, create a bean method and mark it with @{@link Primary}. The same
 * might be true for the locale and the default currency.
 *
 * @author Bernhard Streit
 */
@Configuration
public class MakeBillServiceUsableInJUnitTest {

	@Bean
	public BillService billService() {
		return new BillService();
	}

	// @Bean
	// @Primary
	// public IBillRepository mockedBillRepository() {
	//
	// final IBillRepository mock = mock(IBillRepository.class);
	//
	// // simply return argument when invoking save
	// when(mock.save(Mockito.any(Bill.class))).then(new Answer<Bill>() {
	//
	// @Override
	// public Bill answer(InvocationOnMock invocation) throws Throwable {
	// return (Bill) invocation.getArguments()[0];
	// }
	//
	// });
	//
	// return mock;
	// }

	// @Bean
	// @Primary
	// public IUserService mockedUserProvider() {
	// return mock(IUserService.class);
	// }

	@Bean
	public TaxInfo defaultTaxInfoForNewBills() {
		return new TaxInfo("In-house", new VATClass("Normaler Steuersatz",
				BigDecimal.valueOf(0.19), null, null),
				TaxUsage.GLOBAL_REDUCED_VAT, null, null);
	}

	@Bean
	@Primary
	public ICurrentDateProvider repeatingCurrentDateProvider()
			throws ParseException {
		final DateFormat df = DateFormat.getDateTimeInstance(
				DateFormat.DEFAULT, DateFormat.DEFAULT, locale());

		System.out.println(df.format(new Date()));

		final Date date1 = df.parse("31.01.2014 12:01:00");
		final Date date2 = df.parse("31.01.2014 12:05:00");

		return FixDateProvider.repeat(date1, date2);
	}

	@Bean
	public Locale locale() {
		return Locale.GERMANY;
	}

	@Bean
	public Currency getDefaultCurrency() {
		return Currency.getInstance(locale());
	}

}
