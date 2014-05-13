package de.bstreit.java.oscr.gui.swing.admin.util;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class ChoiceHelper<T> {

	private final List<T> listOfChoices;
	private final String title;
	private final Scanner scanner;
	private final boolean hasCancelOption;
	private final ToString<T> toString;

	private ChoiceHelper(List<T> listOfChoices, String title, Scanner scanner,
			boolean hasCancelOption, ToString<T> toString) {
		this.listOfChoices = listOfChoices;
		this.title = title;
		this.scanner = scanner;
		this.hasCancelOption = hasCancelOption;
		this.toString = toString;
	}

	public T makeChoice() {
		while (true) {
			printHeader();

			if (hasCancelOption) {
				System.out.println("\n0 Cancel\n");
			}

			final int choice = Integer.valueOf(scanner.nextLine().trim());

			if (hasCancelOption && choice == 0) {
				return null;
			}

			if (choice <= 0 || choice > listOfChoices.size()) {
				continue;
			}

			return listOfChoices.get(choice - 1);
		}
	}

	public List<T> makeMultipleChoice() {

		while (true) {
			printHeader();

			if (hasCancelOption) {
				System.out.println("\n0 Cancel\n");
			}

			final String[] choicesIdx = scanner.nextLine().trim()
					.split("[^\\d]");

			final List<T> choices = Lists.newArrayList();

			for (final String idx : choicesIdx) {
				final int choice = Integer.valueOf(idx);
				choices.add(listOfChoices.get(choice - 1));
			}

			return choices;
		}

	}

	private void printHeader() {
		System.out.println(title);
		System.out.println(StringUtils.repeat("=", title.length()) + "\n");

		for (int i = 0; i < listOfChoices.size(); i++) {
			System.out.println((i + 1) + " " + asString(i));
		}
	}

	private String asString(int i) {
		if (toString == null) {
			return listOfChoices.get(i).toString();
		}

		return toString.toString(listOfChoices.get(i));
	}

	public static <TYPE> ChoiceHelper<TYPE> withCancelOption(
			List<TYPE> activeProducts, String title, Scanner scanner) {

		return new ChoiceHelper<TYPE>(activeProducts, title, scanner, true,
				null);
	}

	public static <TYPE> ChoiceHelper<TYPE> withoutCancelOption(
			List<TYPE> activeProducts, String title, Scanner scanner) {

		return new ChoiceHelper<TYPE>(activeProducts, title, scanner, false,
				null);
	}

	public static <TYPE> ChoiceHelper<TYPE> withoutCancelOption(
			List<TYPE> activeProducts, String title, Scanner scanner,
			ToString<TYPE> toString) {

		return new ChoiceHelper<TYPE>(activeProducts, title, scanner, false,
				toString);

	}

}
