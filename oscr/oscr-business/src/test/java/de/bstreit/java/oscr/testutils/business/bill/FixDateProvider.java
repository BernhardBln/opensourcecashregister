package de.bstreit.java.oscr.testutils.business.bill;

import java.util.Date;

import de.bstreit.java.oscr.business.base.date.ICurrentDateProvider;

/**
 * Usage:
 * 
 * <pre>
 * FixDateProvider p = new FixDateProvider(date1, date2);
 * p.getCurrentDate(); // return date1
 * p.getCurrentDate(); // return date2
 * </pre>
 * 
 * <p>
 * A third invocation of {@link #getCurrentDate()} would throw an
 * {@link ArrayIndexOutOfBoundsException}.
 * </p>
 * 
 * @author Bernhard Streit
 */
public class FixDateProvider implements ICurrentDateProvider {

  private Date[] dates;

  private int idx = 0;

  /** If true, start over with idx = 0 after we used the last date */
  private boolean repeat = false;


  /**
   * Create new date provider that will return the given dates in the given
   * order.
   * 
   * @param dates
   */
  private FixDateProvider(Date... dates) {
    this.dates = dates;
  }

  /**
   * 
   * @return the next date
   * @throws ArrayIndexOutOfBoundsException
   *           in case this method is invoked more than there were dates
   *           provided in the constructor.
   */
  @Override
  public Date getCurrentDate() {
    startOverIfDesiredAndAtEnd();

    return dates[idx++];
  }

  /**
   * Reset the index to 0 if repeat is set to true and we have reached the end
   * of the array.
   */
  private void startOverIfDesiredAndAtEnd() {
    if (repeat && idx == dates.length) {
      idx = 0;
    }
  }

  /**
   * 
   * @param dates
   * @return a {@link FixDateProvider} that starts over with the first date
   *         after the last date was returned.
   */
  public static ICurrentDateProvider repeat(Date... dates) {

    final FixDateProvider repeatingProvider = new FixDateProvider(dates);

    repeatingProvider.repeat = true;

    return repeatingProvider;

  }

}
