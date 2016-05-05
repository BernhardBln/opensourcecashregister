package de.bstreit.java.oscr.business.export;

/**
 * A service running in the background, exporting stats every then and now
 * 
 * @author Bernhard Streit
 */
public interface IService {

  /**
   * Starts the service in a separate thread
   */
  void runInBackground();

  /**
   * Stops the service at once when idle; otherwise stop after export has
   * finished.
   */
  void stopService();

}
