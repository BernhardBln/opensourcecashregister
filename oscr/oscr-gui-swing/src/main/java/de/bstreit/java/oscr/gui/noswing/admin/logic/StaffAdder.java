package de.bstreit.java.oscr.gui.noswing.admin.logic;

import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import de.bstreit.java.oscr.business.staff.User;
import de.bstreit.java.oscr.business.staff.dao.IUserRepository;

@Named
public class StaffAdder implements IAdminBean {

	@Inject
	private IUserRepository userRepository;

	private Scanner scanner;

	@Override
	public void performTask() {
		System.out.println("Existing members: ");
		final List<User> allUser = userRepository.findAll();
		for (final User staffMember : allUser) {
			System.out.println(" * " + staffMember);
		}

		System.out.println("Login name (leave empty to abort): ");
		final String loginName = scanner.nextLine().trim();

		if (StringUtils.isBlank(loginName)) {
			return;
		}

		System.out.print("Full name: ");
		final String fullName = scanner.nextLine().trim();

		final User user = new User(loginName, fullName);

		userRepository.save(user);
	}

	@Override
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public String toString() {
		return "Add staff member";
	}

}
