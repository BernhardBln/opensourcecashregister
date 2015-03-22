package de.bstreit.java.oscr.business.bill.calculator;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Maps;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.bill.IBillCalculatorFactory;
import de.bstreit.java.oscr.business.bill.IMultipleBillsCalculator;

@Named
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class MultipleBillsCalculator implements IMultipleBillsCalculator {

  @Inject
  private IBillCalculatorFactory billCalculatorFactory;

  private IBillCalculator currentBillCalculator;

	private boolean filled = false;

	private Money totalGross;
	private final Map<VATClass, Money> totalNetByVatClass = Maps.newHashMap();
	private final Map<VATClass, Money> totalGrossByVatClass = Maps.newHashMap();
	private final Map<VATClass, Money> totalVatByVatClass = Maps.newHashMap();

	MultipleBillsCalculator() {
	}

	@Override
	public Money getTotalGross() {
		return totalGross;
	}


  @Override
  public Money getTotalGross() {
    return totalGross;
  }

  @Override
  public Money getTotalNetFor(VATClass vatClass) {
    return totalNetByVatClass.get(vatClass);
  }

	@Override
	public Set<VATClass> getAllVatClasses() {
		return totalVatByVatClass.keySet();
	}

  @Override
  public Money getTotalVATFor(VATClass vatClass) {
    return totalVatByVatClass.get(vatClass);
  }

		filled = !bills.isEmpty();

		for (final Bill bill : bills) {
			currentBillCalculator = billCalculatorFactory.create(bill);

  void analyse(Collection<Bill> bills) {

    filled = !bills.isEmpty();

    for (final Bill bill : bills) {
      currentBillCalculator = billCalculatorFactory.create(bill);

      try {
        addTotalGross();
        addTotalNetByVAT();
        addTotalGrossByVatClass();
        addTotalVatByVatClass();

      } finally {
        currentBillCalculator.close();
      }
    }

  }

  private void addTotalNetByVAT() {
    for (final char abbreviation : currentBillCalculator
        .allFoundVATClassesAbbreviated()) {

      final VATClass vatClass = currentBillCalculator
          .getVATClassForAbbreviation(abbreviation);

      if (totalNetByVatClass.containsKey(vatClass)) {
        final Money newTotalNet = totalNetByVatClass.get(vatClass).add(
            currentBillCalculator.getTotalNetFor(vatClass));

        totalNetByVatClass.put(vatClass, newTotalNet);
      } else {
        totalNetByVatClass.put(vatClass,
            currentBillCalculator.getTotalNetFor(vatClass));
      }

    }
  }

  private void addTotalGrossByVatClass() {

    for (final char abbreviation : currentBillCalculator
        .allFoundVATClassesAbbreviated()) {

      final VATClass vatClass = currentBillCalculator
          .getVATClassForAbbreviation(abbreviation);

      if (totalGrossByVatClass.containsKey(vatClass)) {
        final Money newTotalGross = totalGrossByVatClass.get(vatClass)
            .add(currentBillCalculator.getTotalGrossFor(vatClass));

        totalGrossByVatClass.put(vatClass, newTotalGross);
      } else {
        totalGrossByVatClass.put(vatClass,
            currentBillCalculator.getTotalGrossFor(vatClass));
      }

    }
  }

  private void addTotalVatByVatClass() {

    for (final char abbreviation : currentBillCalculator
        .allFoundVATClassesAbbreviated()) {

      final VATClass vatClass = currentBillCalculator
          .getVATClassForAbbreviation(abbreviation);

	private void addTotalGross() {
		if (totalGross == null) {
			totalGross = currentBillCalculator.getTotalGross();
		} else {
			totalGross = totalGross.add(currentBillCalculator.getTotalGross());
		}
	}

	@Override
	public boolean isFilled() {
		return filled;
	}
}
