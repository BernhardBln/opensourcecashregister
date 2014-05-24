package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.transaction.PlatformTransactionManager;

import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.offers.VariationOffer;
import de.bstreit.java.oscr.business.offers.dao.IVariationOfferRepository;

@Named
public class DbUpdate implements IAdminBean {

	@Inject
	private DataSource dataSource;

	@Inject
	private IBillRepository billRepository;

	@Inject
	private IVariationOfferRepository variationOfferRepository;

	@Inject
	private PlatformTransactionManager ptm;

	@Override
	public void performTask() {
		try {
			performDbUpdateV1_1();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	@Transactional
	private void performDbUpdateV1_1() throws SQLException {

		try (final Connection connection = dataSource.getConnection()) {

			try (final Statement statement = connection.createStatement()) {

				modifyBillItems(statement);
				dropColumn(statement);

			}
		}

	}

	private void dropColumn(final Statement statement) throws SQLException {
		final String update = "ALTER TABLE BILLITEM DROP COLUMN variationoffer_id";
		// statement.executeUpdate(update);
	}

	private void modifyBillItems(final Statement statement) throws SQLException {
		final String query = "SELECT bb.bill_id as billid, b.id as billitemid, b.variationoffer_id as offerid FROM BILLITEM B, BILL_BILLITEM BB"
				+ " WHERE B.id = BB.biLLITEMS_ID AND B.variationoffer_id IS NOT NULL";

		try (final ResultSet result = statement.executeQuery(query)) {

			while (result.next()) {

				final String billId = result.getString(1);
				final Long billItemId = result.getLong(2);
				final Long variationOfferId = result.getLong(3);

				final Bill bill = billRepository.getOne(billId);

				for (final BillItem bi : bill.getBillItems()) {
					if (billItemId.equals(bi.getId())) {

						final VariationOffer variationOffer = variationOfferRepository
								.getOne(variationOfferId);
						bi.toggleVariationOffer(variationOffer);

						billRepository.save(bill);

						break;
					}
				}

				// First, select all not yet converted bill items
				// second, update them
				// third, remove column "variationoffer_id"
			}
		}
	}

	@Override
	public void setScanner(Scanner scanner) {
	}

	@Override
	public String toString() {
		return "DB update";
	}

}
