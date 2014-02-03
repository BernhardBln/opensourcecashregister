package de.bstreit.java.oscr.gui.formatting;

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


  /**
   * Create new date provider that will return the given dates in the given
   * order.
   * 
   * @param dates
   */
  public FixDateProvider(Date... dates) {
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
    return dates[idx++];
  }

}
