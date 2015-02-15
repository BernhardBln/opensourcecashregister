package de.bstreit.java.oscr.business.bill;

import java.util.SortedSet;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;

public interface IBillCalculator extends AutoCloseable {

	/** Has to be invoked prior using the bill calculator */
	public void analyse(Bill bill);

	public abstract Money getTotalGross();

	public abstract Money getTotalNetFor(VATClass vatClass);

	public abstract Money getTotalGrossFor(VATClass vatClass);

	public abstract Money getTotalVATFor(VATClass vatClass);

	public abstract Money getNetFor(BillItem billItem);

	public abstract String getVATClassAbbreviationFor(BillItem billItem);

	public abstract VATClass getVATClassForAbbreviation(Character abbreviation);

	public abstract SortedSet<Character> allFoundVATClassesAbbreviated();

	@Override
	public abstract void close();

}