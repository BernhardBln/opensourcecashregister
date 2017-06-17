package de.bstreit.java.oscr.business.taxation;

import de.bstreit.java.oscr.testutils.TestConstants;

public class MockedVATFinder extends SimpleVATFinderDoNotUseInProduction {


  public MockedVATFinder() {
    this.setFoodSellVATClassTaxInfo(TestConstants.FOOD_SALE);
    this.setReducedVATClassTaxInfo( TestConstants.TO_GO);

    this.setReducedVATClass(TestConstants.REDUCED_VAT_CLASS);
    this.setNormalVATClass(TestConstants.NORMAL_VAT_CLASS);
  }

}
