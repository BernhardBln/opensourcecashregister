package de.bstreit.java.oscr.business.bill.calculator;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.bstreit.java.oscr.business.bill.IBillCalculatorFactory;

@RunWith(MockitoJUnitRunner.class)
public class MultipleBillsCalculatorTest {

	@Mock
	private IBillCalculatorFactory billCalculatorFactory;

	@InjectMocks
	private MultipleBillsCalculator multipleBillsCalculator;

	/** Test case for "1 EUR off" etc. */
	@Ignore
	@Test
	public void testPromo() {
		Assert.fail("Not implemented");
	}

}
