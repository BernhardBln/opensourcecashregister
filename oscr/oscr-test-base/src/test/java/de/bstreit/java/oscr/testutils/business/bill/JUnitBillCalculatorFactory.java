package de.bstreit.java.oscr.testutils.business.bill;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.bill.IBillCalculatorFactory;


public class JUnitBillCalculatorFactory implements IBillCalculatorFactory {

  private Map<Character, VATClass> abbreviationsToVATClass = Maps.newHashMap();

  private Map<VATClass, Money> vatClassToTotalNet = Maps.newHashMap();
  private Map<VATClass, Money> vatClassToTotalGross = Maps.newHashMap();

  private Map<BillItem, Character> billItemsToAbbreviations = Maps.newHashMap();
  private Map<BillItem, Money> billItemsToNetPrices = Maps.newHashMap();

  private Money totalGross;


  public void reset() {
    abbreviationsToVATClass = Maps.newHashMap();

    vatClassToTotalNet = Maps.newHashMap();
    vatClassToTotalGross = Maps.newHashMap();

    billItemsToAbbreviations = Maps.newHashMap();
    billItemsToNetPrices = Maps.newHashMap();

    totalGross = null;
  }


  @Override
  public IBillCalculator create(Bill bill) {
    final IBillCalculator billCalculator = mock(IBillCalculator.class);

    when(billCalculator.allFoundVATClassesAbbreviated()).thenReturn(
        Sets.newTreeSet(abbreviationsToVATClass.keySet()));

    for (BillItem billItem : billItemsToNetPrices.keySet()) {
      when(billCalculator.getNetFor(billItem)).thenReturn(billItemsToNetPrices.get(billItem));
    }

    when(billCalculator.getTotalGross()).thenReturn(totalGross);

    for (VATClass vatClass : vatClassToTotalGross.keySet()) {
      when(billCalculator.getTotalGrossFor(vatClass)).thenReturn(vatClassToTotalGross.get(vatClass));
    }

    for (VATClass vatClass : vatClassToTotalNet.keySet()) {
      when(billCalculator.getTotalNetFor(vatClass)).thenReturn(vatClassToTotalNet.get(vatClass));
    }

    for (VATClass vatClass : vatClassToTotalNet.keySet()) {
      when(billCalculator.getTotalVATFor(vatClass)).thenReturn(
          vatClassToTotalGross.get(vatClass).subtract(vatClassToTotalNet.get(vatClass)));
    }

    for (BillItem billItem : billItemsToAbbreviations.keySet()) {
      when(billCalculator.getVATClassAbbreviationFor(billItem)).thenReturn(
          billItemsToAbbreviations.get(billItem).toString());
    }

    for (Character character : abbreviationsToVATClass.keySet()) {

      when(billCalculator.getVATClassForAbbreviation(character))//
          .thenReturn(abbreviationsToVATClass.get(character));

    }


    return billCalculator;
  }

  public void addVATClassAndTotalNetAndTotalGross(char abbreviation, String string, int i, String totalNet,
      String totalGross) {

    final VATClass vatClass = new VATClass(string, new BigDecimal(i), null, null);

    abbreviationsToVATClass.put(abbreviation, vatClass);

    vatClassToTotalNet.put(vatClass, makeMoney(totalNet));
    vatClassToTotalGross.put(vatClass, makeMoney(totalGross));
  }

  public void setVATClassAndNetPriceFor(BillItem billItem, char c, String netPrice) {
    billItemsToAbbreviations.put(billItem, c);
    billItemsToNetPrices.put(billItem, makeMoney(netPrice));
  }


  public void setTotalGross(String totalGross) {
    this.totalGross = makeMoney(totalGross);
  }

  private Money makeMoney(String netPrice) {
    return new Money(netPrice, "EUR");
  }

}
