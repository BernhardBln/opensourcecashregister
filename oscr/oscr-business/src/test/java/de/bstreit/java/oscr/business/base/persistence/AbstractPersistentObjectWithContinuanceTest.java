package de.bstreit.java.oscr.business.base.persistence;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.offers.ProductOffer;
import de.bstreit.java.oscr.business.products.AbstractSalesItem;
import de.bstreit.java.oscr.business.products.ContainerSize;
import de.bstreit.java.oscr.business.products.Extra;
import de.bstreit.java.oscr.business.products.Product;
import de.bstreit.java.oscr.business.products.Variation;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.taxation.TaxUsage;

/**
 * <p>
 * Here we test for some instances that the contract of equals/hashcode is
 * implemented correctly.
 * </p>
 *
 * <p>
 * The hierarchy is:
 * </p>
 *
 * <pre>
 *  {@link AbstractPersistentObject} (has field "id" which is mutable)
 *     |
 *  {@link AbstractPersistentObjectWithContinuance} (has fields "validFrom" (immutable) and "validTo" (mutable))
 *     |
 *  Concrete subclass
 * </pre>
 *
 * <p>
 * {@link AbstractPersistentObjectWithContinuance} implements
 * {@link #equals(Object)} and {@link #hashCode()} in the following way:
 * </p>
 *
 * <ul>
 * <li>For equality, check that the other object is not null and of the same
 * type (for example, an {@link Extra} and a {@link Variation} will not be
 * considered equal, even if they have the same values in all their fields, as
 * they are distinct classes)</li>
 * <li>From {@link AbstractPersistentObject} and
 * {@link AbstractPersistentObjectWithContinuance} use the (only) immutable
 * "validFrom" field in {@link #equals(Object)} and {@link #hashCode()}.</li>
 * <li>Subclasses implement the
 * {@link AbstractPersistentObjectWithContinuance#additionalEqualsForSubclasses(Object)}
 * and
 * {@link AbstractPersistentObjectWithContinuance#additionalHashcodeForSubclasses()}
 * methods which allow them to include their own fields in the comparison and
 * hashcode calculations. In case they do not want to include any fields they
 * can simply return <code>true</code> and <code>0</code>.</li>
 * </ul>
 *
 * In this test we check that in {@link AbstractPersistentObjectWithContinuance}
 * , the
 * {@link AbstractPersistentObjectWithContinuance#additionalEqualsForSubclasses(Object)}
 * and
 * {@link AbstractPersistentObjectWithContinuance#additionalHashcodeForSubclasses()}
 * methods are used correctly for {@link #equals(Object)} and
 * {@link #hashCode()}.
 *
 * @author streit
 */

public class AbstractPersistentObjectWithContinuanceTest {

	//
	// Lots of objects which should all be unequal to each other
	//

	private final Date date1 = new Date(1);
	private final Date date2 = new Date(2);
	private final Date date3 = new Date(3);
	private final Date date4 = new Date(4);

	private final ContainerSize containerSize1 = new ContainerSize("s1", date1,
			date2);
	private final ContainerSize containerSize2 = new ContainerSize("s2", date3,
			date4);

	private final TaxInfo taxInfo1 = new TaxInfo("t1", new VATClass("v1",
			BigDecimal.valueOf(0.1), date1, date2),
			TaxUsage.GLOBAL_REDUCED_VAT, date1, date2);
	private final TaxInfo taxInfo2 = new TaxInfo("t2", new VATClass("v2",
			BigDecimal.valueOf(0.2), date1, date2),
			TaxUsage.GLOBAL_STANDARD_VAT, date3, date4);

	private final Product product1 = new Product("p1", date1, date2);
	private final Product product2 = new Product("p2", date3, date4);

	//
	// Tests to check the correct implementation of equals and hashcode by
	// using the information provided by subclasses correctly
	//

	/**
	 * Check if two products are considered equal if they have the same name and
	 * the same fromDate (the other properties of the product may vary).
	 */
	@Test
	public void sameProductsAreEqualAndHaveEqualHashcode() {
		final Product p1 = new Product("product", date1, date2);
		final Product p2 = new Product("product", date1, date4);

		p1.setPackageSize(containerSize1);
		p2.setPackageSize(containerSize2);

		p1.setOverridingTaxInfo(taxInfo1);
		p2.setOverridingTaxInfo(taxInfo2);

		Assert.assertEquals(p1, p2);
		Assert.assertEquals(p1.hashCode(), p2.hashCode());
	}

	/**
	 * Assert that properties provided by a subclass for equals and hashcode
	 * (here, only the product) and the fromDate from
	 * {@link AbstractPersistentObjectWithContinuance} are sufficient for
	 * equality and equal hashcodes.
	 */
	@Test
	public void equalsAndHashCodeOfSubclassesAreRespected_equalOffers() {
		final ProductOffer offer1 = new ProductOffer(product1, null, null,
				date1, date2);
		final ProductOffer offer2 = new ProductOffer(product1, null, null,
				date1, date3);

		Assert.assertEquals(offer1, offer2);
		Assert.assertEquals(offer1.hashCode(), offer2.hashCode());
	}

	/**
	 * <p>
	 * Assert that in case we have unequal fields in the subclasses, the equals
	 * method returns false.
	 * </p>
	 * <p>
	 * We also check that {@link #hashCode()} returns values which are not equal
	 * - <b>this, however, is not guaranteed and in an unlikely case it might
	 * happen that the test fails here</b>. But the chance is very low, and with
	 * that test, we check that hashcode is used properly in order to gain
	 * performance when using hashed collections.
	 * </p>
	 */
	@Test
	public void equalsAndHashCodeOfSubclassesAreRespected_notEqualOffers_subclassNotEqual() {
		final ProductOffer offer1 = new ProductOffer(product1, null, null,
				date1, date2);
		final ProductOffer offer2 = new ProductOffer(product2, null, null,
				date1, date2);

		Assert.assertNotEquals(offer1, offer2);
		checkHashcodesNotEqualWithWarning(offer1, offer2);
	}

	/**
	 * <p>
	 * Assert that in case we have unequal fields in the superclass, the equals
	 * method returns false.
	 * </p>
	 * <p>
	 * We also check that {@link #hashCode()} returns values which are not equal
	 * - <b>this, however, is not guaranteed and in an unlikely case it might
	 * happen that the test fails here</b>. But the chance is very low, and with
	 * that test, we check that hashcode is used properly in order to gain
	 * performance when using hashed collections.
	 * </p>
	 */
	@Test
	public void equalsAndHashCodeOfSubclassesAreRespected_notEqualOffers_superclassNotEqual() {
		final ProductOffer offer1 = new ProductOffer(product1, null, null,
				date1, date2);
		final ProductOffer offer2 = new ProductOffer(product1, null, null,
				date3, date4);

		Assert.assertNotEquals(offer1, offer2);
		checkHashcodesNotEqualWithWarning(offer1, offer2);
	}

	/**
	 * <p>
	 * Assert that in case we have unequal fields in the subclasses and in the
	 * superclass, the equals method returns false.
	 * </p>
	 * <p>
	 * We also check that {@link #hashCode()} returns values which are not equal
	 * - <b>this, however, is not guaranteed and in an unlikely case it might
	 * happen that the test fails here</b>. But the chance is very low, and with
	 * that test, we check that hashcode is used properly in order to gain
	 * performance when using hashed collections.
	 * </p>
	 */
	@Test
	public void equalsAndHashCodeOfSubclassesAreRespected_notEqualOffers_bothNotEqual() {
		final ProductOffer offer1 = new ProductOffer(product1, null, null,
				date1, date2);
		final ProductOffer offer2 = new ProductOffer(product2, null, null,
				date3, date4);

		Assert.assertNotEquals(offer1, offer2);
		checkHashcodesNotEqualWithWarning(offer1, offer2);
	}

	/**
	 * Assert that the two objects have different hash codes. In case that fails
	 * add a warning that this might not be a bug.
	 *
	 * @param o1
	 * @param o2
	 */
	private void checkHashcodesNotEqualWithWarning(final Object o1,
			final Object o2) {
		Assert.assertNotEquals(
				"Unequal objects SHOULD have unequal hash codes - "
						+ "but in the unlikely case they are equal "
						+ "that is not necessarily a bug! In that "
						+ "case try other values in the fields before debugging.",
				o1.hashCode(), o2.hashCode());
	}

	/**
	 * <p>
	 * Check that different types are never equal, even if all fields considered
	 * in {@link #equals(Object)} are equal.
	 * </p>
	 * <p>
	 * Here we use {@link Extra} and {@link Variation} which both are subclasses
	 * of {@link AbstractSalesItem} and do not introduce their own fields.
	 * </p>
	 */
	@Test
	public void differentTypeAreNeverEqual() {
		final String name = "same name";

		final AbstractSalesItem extra = new Extra(name, date1, date2);
		final AbstractSalesItem variation = new Variation(name, date1, date2);

		Assert.assertNotEquals(extra, variation);
	}

	//
	// Basic tests to check that the objects created are all different
	//

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

	@Test
	public void productsAreNotEqual() {
		Assert.assertNotEquals(product1, product2);
	}

}
