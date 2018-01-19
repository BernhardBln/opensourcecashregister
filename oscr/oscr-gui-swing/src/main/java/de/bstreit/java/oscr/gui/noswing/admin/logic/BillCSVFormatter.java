package de.bstreit.java.oscr.gui.noswing.admin.logic;

import de.bstreit.java.oscr.business.base.finance.money.Money;
import de.bstreit.java.oscr.business.base.finance.tax.VATClass;
import de.bstreit.java.oscr.business.base.finance.tax.dao.IVATClassRepository;
import de.bstreit.java.oscr.business.bill.Bill;
import de.bstreit.java.oscr.business.bill.BillItem;
import de.bstreit.java.oscr.business.bill.IBillCalculator;
import de.bstreit.java.oscr.business.bill.IBillCalculatorFactory;
import de.bstreit.java.oscr.business.bill.calculator.WhatToCount;
import de.bstreit.java.oscr.business.offers.PromoOffer;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.text.formatting.BillItemWrapper;
import de.bstreit.java.oscr.text.formatting.MoneyFormatter;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

/**
 * Format a bill for textual representation.
 * <p>
 * Currently optimised for Germany
 *
 * @author Bernhard Streit
 */
@Named
public class BillCSVFormatter {

  private static final String HEADER = "Datum Eroeffnung\t" +
    "Datum Schliessung\t" +
    "Ausser Haus\t" +
    "Bezahlung\t" +

    "Ermaessigung_typ\t" +


    "Ermaessigung_brutto\t" +

    "Ermaessigung_brutto_7prozent\t" +
    "Ermaessigung_netto_7prozent\t" +
    "Ermaessigung_netto_7prozent_Mwst\t" +

    "Ermaessigung_brutto_19prozent\t" +
    "Ermaessigung_netto_19prozent\t" +
    "Ermaessigung_netto_19prozent_Mwst\t" +


    "brutto_ohne_ermaessigung\t" +

    "brutto_ohne_ermaessigung_7prozent\t" +
    "netto_ohne_ermaessigung_7prozent\t" +
    "netto_ohne_ermaessigung_7prozent_Mwst\t" +

    "brutto_ohne_ermaessigung_19prozent\t" +
    "netto_ohne_ermaessigung_19prozent\t" +
    "netto_ohne_ermaessigung_19prozent_Mwst\t" +


    "brutto_mit_ermaessigung\t" +

    "brutto_mit_ermaessigung_7prozent\t" +
    "netto_mit_ermaessigung_7prozent\t" +
    "netto_mit_ermaessigung_7prozent_Mwst\t" +

    "brutto_mit_ermaessigung_19prozent\t" +
    "netto_mit_ermaessigung_19prozent\t" +
    "netto_mit_ermaessigung_19prozent_Mwst\t" +


    "verkaufte_produkte\n";

  private String NEWLINE = "\n";

  public static final int MAX_LINE_LENGTH = 44;

  public static final int MAX_PRODUCT_COLUMN_LENGTH = 20;


  @Value("${staffConsumption.managementUser}")
  private String managementUser;

  @Value("${taxexport.standardVATDesignation}")
  private String standardVATDesignation;


  @Inject
  private IBillCalculatorFactory billCalculatorFactory;

  @Inject
  private IVATClassRepository vatClassRepository;

  @Inject
  private Locale locale;

  @Inject
  @Named("togoTaxInfo")
  private TaxInfo toGoTaxinfo;

  @Inject
  private Currency currency;

  private BillItemWrapper billItemWrapper;
  private final MoneyFormatter moneyFormatter = new MoneyFormatter();
  private DateFormat dateFormat;
  private NumberFormat numberFormat;

  private transient Bill _bill;
  private transient StrBuilder _builder;
  private transient IBillCalculator paymentCalc;
  private transient IBillCalculator totalCalc;
  private transient IBillCalculator promoCalc;

  private transient VATClass standardVATClass;
  private transient VATClass reducedVATClass;


  @PostConstruct
  public void init() {
    dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
      DateFormat.SHORT, locale);
    billItemWrapper = new BillItemWrapper(MAX_PRODUCT_COLUMN_LENGTH, NEWLINE);


    // TODO: what about old vat classes?
    standardVATClass = vatClassRepository.findByDesignationAndValidToIsNull
      (standardVATDesignation);

    // TODO: workaround, improve!
    reducedVATClass = vatClassRepository
      .findAllByValidToIsNull()
      .stream()
      .filter(v -> !v.equals(standardVATClass))
      .findFirst()
      .get();


    numberFormat = NumberFormat.getNumberInstance(locale);
    numberFormat.setMinimumFractionDigits(4);
    numberFormat.setMaximumFractionDigits(4);

  }

  public String formatBill(final Bill bill) {

    if (bill == null) {
      return null;
    }

    this._bill = bill;

    try (IBillCalculator paymentCalc = billCalculatorFactory.create(bill, WhatToCount.PAYMENT);
         IBillCalculator totalCalc = billCalculatorFactory.create(bill, WhatToCount.TOTAL);
         IBillCalculator promoCalc = billCalculatorFactory.create(bill, WhatToCount.PROMO_TOTAL);
    ) {

      this.paymentCalc = paymentCalc;
      this.totalCalc = totalCalc;
      this.promoCalc = promoCalc;

      return getBillAsText();

    } finally {
      this._bill = null;
      this._builder = null;
    }
  }

  public String getHeader() {
    return HEADER;
  }

  private String getBillAsText() {
    _builder = new StrBuilder();

    _builder
      .append(dateFormat.format(_bill.getBillOpened()))
      .append("\t")
      .append(_bill.getBillClosed() != null ? dateFormat.format(_bill.getBillClosed()) : "<offen>")
      .append("\t")
      .append(isToGo(_bill))
      .append("\t")
      .append(payment(_bill))
      .append("\t")
      .append(reductionType(_bill))
      .append("\t")


      .append(numberFormat.format(promoCalc
        .getTotalGross()
        .getAmount()))
      .append("\t")

      .append(numberFormat.format(promoCalc
        .getTotalGrossFor(reducedVATClass)
        .getAmount()))

      .append("\t")
      .append(numberFormat.format(promoCalc
        .getTotalNetFor(reducedVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(promoCalc
        .getTotalVATFor(reducedVATClass)
        .getAmount()))
      .append("\t")

      .append(numberFormat.format(promoCalc
        .getTotalGrossFor(standardVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(promoCalc
        .getTotalNetFor(standardVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(promoCalc
        .getTotalVATFor(standardVATClass)
        .getAmount()))
      .append("\t")


      .append(numberFormat.format(totalCalc
        .getTotalGross()
        .getAmount()))
      .append("\t")

      .append(numberFormat.format(totalCalc
        .getTotalGrossFor(reducedVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(totalCalc
        .getTotalNetFor(reducedVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(totalCalc
        .getTotalVATFor(reducedVATClass)
        .getAmount()))
      .append("\t")

      .append(numberFormat.format(totalCalc
        .getTotalGrossFor(standardVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(totalCalc
        .getTotalNetFor(standardVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(totalCalc
        .getTotalVATFor(standardVATClass)
        .getAmount()))
      .append("\t")


      .append(numberFormat.format(paymentCalc
        .getTotalGross()
        .getAmount()))
      .append("\t")

      .append(numberFormat.format(paymentCalc
        .getTotalGrossFor(reducedVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(paymentCalc
        .getTotalNetFor(reducedVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(paymentCalc
        .getTotalVATFor(reducedVATClass)
        .getAmount()))
      .append("\t")

      .append(numberFormat.format(paymentCalc
        .getTotalGrossFor(standardVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(paymentCalc
        .getTotalNetFor(standardVATClass)
        .getAmount()))
      .append("\t")
      .append(numberFormat.format(paymentCalc
        .getTotalVATFor(standardVATClass)
        .getAmount()))
      .append("\t")


      .append(soldProducts(_bill))
      .append("\n")
    ;


    return _builder.toString();
  }

  private String soldProducts(Bill bill) {
    return bill
      .getBillItems()
      .stream()
      .map(BillItem::getName)
      .collect(joining(","));
  }

  private String isToGo(Bill bill) {
    return bill
      .getGlobalTaxInfo()
      .equals(toGoTaxinfo) ? "ja" : "nein";
  }


  private String reductionType(Bill bill) {

    if (bill.isFreePromotionOffer()) {
      return "100%";
    }

    if (bill.hasReduction()) {
      return bill.getReduction() + "%";
    }

    Optional<Money> totalAbsoluteReduction = bill
      .getOfferedItemsFlat()
      .stream()
      .filter(o -> o instanceof PromoOffer)
      .map(o -> o.getPriceGross())
      .reduce((m1, m2) -> m1.add(m2));

    if (totalAbsoluteReduction.isPresent()) {
      return numberFormat.format(  totalAbsoluteReduction
        .get()
        .getAmount()) + " EUR";
    }

    return "";

  }

  private String payment(Bill bill) {
    if (bill.getStaffConsumer() == null) {
      return "bar";
    }

    if (bill
      .getStaffConsumer()
      .isCustomer()) {

      return "customer/" + bill
        .getStaffConsumer()
        .getFullname();
    }

    // if we are here, can only be team or manager

    if (bill
      .getStaffConsumer()
      .getName()
      .equals(managementUser)) {

      return "Eigenentnahme";
    }

    // team

    return "VerpflegungMitarbeiter/" + bill
      .getStaffConsumer()
      .getFullname();
  }

}
