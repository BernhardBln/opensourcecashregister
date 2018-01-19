package de.bstreit.java.oscr.business.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TxService {

  @Transactional
  public void doInTx(Runnable runnable) {
    runnable.run();
  }


  @Transactional(readOnly = true)
  public void doInReadOnlyTx(Runnable runnable) {
    runnable.run();
  }


}
