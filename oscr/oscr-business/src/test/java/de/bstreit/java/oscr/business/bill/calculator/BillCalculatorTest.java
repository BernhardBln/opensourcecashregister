package de.bstreit.java.oscr.business.bill.calculator;

import java.util.Currency;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.taxation.IVATFinder;
import de.bstreit.java.oscr.business.taxation.dao.ITaxInfoRepository;


@RunWith(MockitoJUnitRunner.class)
public class BillCalculatorTest {

  @Mock
  private Currency defaultCurrency;


  @Mock
  private ITaxInfoRepository taxInfoRepository;


  @Mock
  private IVATClassRepository vatClassRepository;


  @Mock
  private IVATFinder vatFinder;

  @InjectMocks
  private IBillCalculator billCalculator;


  @Test
  public void testGetTotalNetFor() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

}
