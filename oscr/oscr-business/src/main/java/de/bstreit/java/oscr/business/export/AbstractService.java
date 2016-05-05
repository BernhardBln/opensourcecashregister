package de.bstreit.java.oscr.business.export;


public abstract class AbstractService implements IService, Runnable {

  private final long intervalInMinutes;

  private Thread currentThread;
  private Boolean stopService = false;


  /**
   * Create a new service that is executed every intervalInMinutes minutes.
   * 
   * @param intervalInMinutes
   */
  public AbstractService(long intervalInMinutes) {
    this.intervalInMinutes = intervalInMinutes;
  }

  @Override
  public void runInBackground() {
    if (stopService) {
      throw new IllegalStateException(
          "Service did run already, do not re-use!");
    }

    initService();

    currentThread = new Thread(this);
    currentThread.start();
  }

  @Override
  public void run() {

    if (!canRun())
      return;

    while (!isStopRequest()) {
      sleep();
      execute();
    }

  }

  protected abstract void initService();

  protected abstract boolean canRun();

  protected abstract void execute();


  private boolean isStopRequest() {
    synchronized (stopService) {
      return stopService;
    }
  }

  private void sleep() {
    try {
      Thread.sleep(intervalInMinutes);
    } catch (final InterruptedException e) {
    }
  }

  @Override
  public void stopService() {

    synchronized (stopService) {
      stopService = true;
    }

    // Just in case we're sleeping...
    currentThread.interrupt();
  }

}