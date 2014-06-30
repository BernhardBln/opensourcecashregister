package de.bstreit.java.oscr.business.bill;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.staff.dao.IUserRepository;
import de.bstreit.java.oscr.business.taxation.TaxInfo;

@Named
public class BillTestFactory {

	Logger logger = LoggerFactory.getLogger(BillTestFactory.class);

	@Inject
	private IUserRepository userRepository;

	public Bill create(TaxInfo defaultGlobalTaxInfo, Date billOpeningDate,
			Date billClosingDate) {
		final Bill bill = new Bill(defaultGlobalTaxInfo, billOpeningDate);

		if (billClosingDate != null) {
			bill.closeBill(getUser(), billClosingDate);
			logger.info("# of users: " + userRepository.count());
		}

		return bill;
	}

	private User getUser() {
		final User user = userRepository.findByLoginname("test");
		if (user != null) {
			return null;
		}

		return userRepository.save(new User("test", "test"));
	}
}
