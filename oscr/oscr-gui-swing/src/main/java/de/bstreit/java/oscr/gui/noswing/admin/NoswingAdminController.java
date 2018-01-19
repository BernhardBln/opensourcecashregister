package de.bstreit.java.oscr.gui.noswing.admin;

import com.google.common.collect.Lists;
import de.bstreit.java.oscr.gui.noswing.admin.logic.AbortedException;
import de.bstreit.java.oscr.gui.noswing.admin.logic.IAdminBean;
import de.bstreit.java.oscr.gui.noswing.admin.util.ChoiceHelper;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

@Named
public class NoswingAdminController {

  @Inject
  private ConfigurableApplicationContext context;

  private final List<IAdminBean> adminBeans = Lists.newArrayList();

  public void launchApplication() {
    // Later: GUI

    try (Scanner scanner = new Scanner(System.in)) {
      initAdminBeans(scanner);

      while (true) {
        final ChoiceHelper<IAdminBean> choiceHelper = ChoiceHelper
          .withCancelOption(adminBeans, "Choose Task", scanner);

        final IAdminBean adminBean = choiceHelper.makeChoice();

        if (adminBean == null) {
          return;
        }

        try {
          adminBean.performTask();
        } catch (final AbortedException e) {

        } catch (Exception e) {
          e.printStackTrace();
          System.out.println("\n\n");
          e.getMessage();
        }
      }
    }
  }

  private void initAdminBeans(Scanner scanner) {
    final Map<String, IAdminBean> adminBeansByName =
      context.getBeansOfType(IAdminBean.class);

    // Order by toString of beans
    Map<String, IAdminBean> orderedBeans = new TreeMap<>();

    adminBeansByName
      .values()
      .forEach(b -> orderedBeans.put(b.toString(), b));

    // now retrieve ordered beans
    for (final IAdminBean adminBean : orderedBeans.values()) {
      adminBean.setScanner(scanner);
      adminBeans.add(adminBean);
    }


  }

}
