package de.bstreit.java.oscr.products;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.bstreit.java.oscr.products.tax.TaxInfo;


/**
 * Here we test for some instances that the contract of equals/hashcode is
 * implemented correctly.
 * 
 * @author streit
 */
public class AbstractSalesItemTest {


	private final Date date1 = new Date(1);
	private final Date date2 = new Date(2);
	private final Date date3 = new Date(3);
	private final Date date4 = new Date(4);

	private final ContainerSize containerSize1 = new ContainerSize("s1", date1, date2);
	private final ContainerSize containerSize2 = new ContainerSize("s2", date3, date4);

	private final TaxInfo taxInfo1 = new TaxInfo("t1", date1, date2);
	private final TaxInfo taxInfo2 = new TaxInfo("t2", date3, date4);


	@Test
	public void sameProductsAreEqualAndHaveEqualHashcode() {
		final Product p1 = new Product("product", date1, date2);
		final Product p2 = new Product("product", date3, date4);

		p1.setPackageSize(containerSize1);
		p2.setPackageSize(containerSize2);

		p1.setTaxInfo(taxInfo1);
		p2.setTaxInfo(taxInfo2);

		Assert.assertEquals(p1, p2);
		Assert.assertEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	public void datesAreNotEqual() {
		Assert.assertNotEquals(date1, date2);
		Assert.assertNotEquals(date1, date3);
		Assert.assertNotEquals(date1, date4);

		Assert.assertNotEquals(date2, date3);
		Assert.assertNotEquals(date2, date4);

		Assert.assertNotEquals(date3, date4);
	}

	@Test
	public void containerSizesAreNotEqual() {
		Assert.assertNotEquals(containerSize1, containerSize2);
	}

	@Test
	public void taxInfosAreNotEqual() {
		Assert.assertNotEquals(taxInfo1, taxInfo2);
	}

}
