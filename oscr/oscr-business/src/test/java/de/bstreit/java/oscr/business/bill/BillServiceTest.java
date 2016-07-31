package de.bstreit.java.oscr.business.bill;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.bstreit.java.oscr.business.base.date.ICurrentDateProvider;
import de.bstreit.java.oscr.business.bill.dao.IBillRepository;
import de.bstreit.java.oscr.business.eventbroadcasting.EventBroadcaster;
import de.bstreit.java.oscr.business.export.IService;
import de.bstreit.java.oscr.business.staff.IUserService;
import de.bstreit.java.oscr.business.taxation.TaxInfo;
import de.bstreit.java.oscr.business.util.DateFactory;


@RunWith(MockitoJUnitRunner.class)
public class BillServiceTest {

  @Mock
  private IBillRepository billRepository;
  @Mock
  private ICurrentDateProvider currentDateProvider;
  @Mock
  private TaxInfo defaultTaxInfoForNewBills;
  @Mock
  private EventBroadcaster eventBroadcaster;
  @Mock
  private IMultipleBillsCalculatorFactory multipleBillsCalculatorFactory;
  @Mock
  private Set<IService> services;
  @Mock
  private IUserService userProvider;

  @InjectMocks
  private BillService billService;


  @Test
  public void testGetBillsForMonthOf() throws Exception {

    Date someDayInJuly = DateFactory.getDateWithTimeMidnight(2016, 7, 30);
    billService.getBillsForMonthOf(someDayInJuly);

    // assert
    final ArgumentCaptor<Date> fromCaptor = ArgumentCaptor.forClass(Date.class);
    final ArgumentCaptor<Date> toCaptor = ArgumentCaptor.forClass(Date.class);

    verify(billRepository)
        .getBillsForDayWithoutStaff(fromCaptor.capture(), toCaptor.capture());

    Date from = fromCaptor.getValue();
    Date to = toCaptor.getValue();

    Date expectedFrom = DateFactory.getDateWithTimeMidnight(2016, 7, 1);
    Date expectedTo = DateFactory.getDateWithTimeMidnight(2016, 8, 1);

    assertEquals(expectedFrom, from);
    assertEquals(expectedTo, to);
  }

  @Test
  public void testGetBillsForMonthOf_lastMontOfYear() throws Exception {

    Date someDayInJuly = DateFactory.getDateWithTimeMidnight(2016, 12, 5);
    billService.getBillsForMonthOf(someDayInJuly);

    // assert
    final ArgumentCaptor<Date> fromCaptor = ArgumentCaptor.forClass(Date.class);
    final ArgumentCaptor<Date> toCaptor = ArgumentCaptor.forClass(Date.class);

    verify(billRepository)
        .getBillsForDayWithoutStaff(fromCaptor.capture(), toCaptor.capture());

    Date from = fromCaptor.getValue();
    Date to = toCaptor.getValue();

    Date expectedFrom = DateFactory.getDateWithTimeMidnight(2016, 12, 1);
    Date expectedTo = DateFactory.getDateWithTimeMidnight(2017, 1, 1);

    assertEquals(expectedFrom, from);
    assertEquals(expectedTo, to);
  }

  @Test
  public void testGetBillsForAllDay() throws Exception {

    Date someDayInJuly = DateFactory.getDateWithTimeMidnight(2016, 7, 30);

    billService.getBillsForAllDay(someDayInJuly);

    // assert
    final ArgumentCaptor<Date> fromCaptor = ArgumentCaptor.forClass(Date.class);
    final ArgumentCaptor<Date> toCaptor = ArgumentCaptor.forClass(Date.class);

    verify(billRepository)
        .getBillsForDayWithoutStaff(fromCaptor.capture(), toCaptor.capture());

    Date from = fromCaptor.getValue();
    Date to = toCaptor.getValue();

    Date expectedFrom = DateFactory.getDateWithTimeMidnight(2016, 7, 30);
    Date expectedTo = DateFactory.getDateWithTimeMidnight(2016, 7, 31);

    assertEquals(expectedFrom, from);
    assertEquals(expectedTo, to);
  }

  @Test
  public void testGetBillsForAllDay_lastDayOfMonth() throws Exception {

    Date someDayInJuly = DateFactory.getDateWithTimeMidnight(2016, 7, 31);

    billService.getBillsForAllDay(someDayInJuly);

    // assert
    final ArgumentCaptor<Date> fromCaptor = ArgumentCaptor.forClass(Date.class);
    final ArgumentCaptor<Date> toCaptor = ArgumentCaptor.forClass(Date.class);

    verify(billRepository)
        .getBillsForDayWithoutStaff(fromCaptor.capture(), toCaptor.capture());

    Date from = fromCaptor.getValue();
    Date to = toCaptor.getValue();

    Date expectedFrom = DateFactory.getDateWithTimeMidnight(2016, 7, 31);
    Date expectedTo = DateFactory.getDateWithTimeMidnight(2016, 8, 1);

    assertEquals(expectedFrom, from);
    assertEquals(expectedTo, to);
  }

  @Test
  public void testGetBillsForAllDay_lastDayOfYear() throws Exception {

    Date someDayInJuly = DateFactory.getDateWithTimeMidnight(2016, 12, 31);

    billService.getBillsForAllDay(someDayInJuly);

    // assert
    final ArgumentCaptor<Date> fromCaptor = ArgumentCaptor.forClass(Date.class);
    final ArgumentCaptor<Date> toCaptor = ArgumentCaptor.forClass(Date.class);

    verify(billRepository)
        .getBillsForDayWithoutStaff(fromCaptor.capture(), toCaptor.capture());

    Date from = fromCaptor.getValue();
    Date to = toCaptor.getValue();

    Date expectedFrom = DateFactory.getDateWithTimeMidnight(2016, 12, 31);
    Date expectedTo = DateFactory.getDateWithTimeMidnight(2017, 1, 1);

    assertEquals(expectedFrom, from);
    assertEquals(expectedTo, to);
  }
}
