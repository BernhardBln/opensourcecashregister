package de.bstreit.java.oscr.gui.swing.admin;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.collect.Lists;

import de.bstreit.java.oscr.gui.swing.admin.logic.IAdminBean;
import de.bstreit.java.oscr.gui.swing.admin.util.ChoiceHelper;

@Named
public class SwingAdminController {

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

				adminBean.performTask();
			}
		}
	}

	private void initAdminBeans(Scanner scanner) {
		final Map<String, IAdminBean> adminBeansByName = context
				.getBeansOfType(IAdminBean.class);

		for (final String beanName : adminBeansByName.keySet()) {
			final IAdminBean adminBean = context.getBean(beanName,
					IAdminBean.class);
			adminBean.setScanner(scanner);
			adminBeans.add(adminBean);
		}

	}

}
