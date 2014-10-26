/*
 * Open Source Cash Register
 *
 * Copyright (C) 2013, 2014 Bernhard Streit
 *
 * This file is part of the Open Source Cash Register program.
 *
 * Open Source Cash Register is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Open Source Cash Register is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * --------------------------------------------------------------------------
 *
 * See oscr/licenses/gpl-3.txt for a copy of the GNU GPL.
 * See oscr/README.txt for more information about the software and the author(s).
 *
 */
package de.bstreit.java.oscr.initialdata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p>
 * In this project we want to load some test data into the database.
 * </p>
 * <p>
 * This test data is stored as constant fields in different classes (one for
 * each type), so called data containers. For example, entities of type
 * "ExtraOffer" are stored in the class "ExtraOffers", of type "TaxInfo" in
 * "TaxInfos" etc.
 * </p>
 * <p>
 * Those data containers all extend this abstract class in order to automate the
 * loading in {@link DataLoader}. Remember to mark the subclasses with
 * {@link javax.inject.Named} - otherwise they won't be found by Spring and
 * hence not loaded into the database.
 * </p>
 * <p>
 * The {@link AbstractDataContainerTest} verifies that all classes that extend
 * from this class and lie in this package or below are annotated with the
 * {@link javax.inject.Named} property.
 * </p>
 *
 * @author Bernhard Streit
 */
public abstract class AbstractDataContainer<TYPE> {

	@Inject
	JpaRepository<TYPE, ?> repository;

	/**
	 * This is used to verify that only constants of the correct type are loaded
	 * into the database.
	 * 
	 * @return the class type TYPE
	 */
	public abstract Class<TYPE> getType();

	/**
	 * @return all static constants in the subclass of type {@link #getType()}
	 */
	Collection<TYPE> getEntitiesOfType() {
		final Field[] declaredFields = this.getClass().getDeclaredFields();
		final List<TYPE> entities = new ArrayList<TYPE>();

		for (Field field : declaredFields) {

			if (isConstantAndOfType(field)) {
				addEntityFromFieldTo(entities, field);
			}

		}

		return entities;
	}

	/**
	 * 
	 * @param field
	 * @return true, if the field is a constant (public static final) and of the
	 *         expected type provided by {@link #getType()}
	 */
	private boolean isConstantAndOfType(Field field) {
		final int modifiers = field.getModifiers();

		final boolean isConstant = Modifier.isStatic(modifiers)
				&& Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers);

		final boolean correctType = field.getType().equals(getType());

		return isConstant && correctType;
	}

	/**
	 * Reads out the field, casts its value to {@link #getType()} and stores it
	 * into values.
	 * 
	 * @param values
	 * @param field
	 */
	private void addEntityFromFieldTo(final List<TYPE> values, final Field field) {
		try {
			final Object constantValue = field.get(null);
			final TYPE constantValueCasted = getType().cast(constantValue);

			values.add(constantValueCasted);

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// Not expected
			throw new RuntimeException(
					"Unexpected exception caught while gathering static instances",
					e);
		}
	}

	public abstract String getName();

}
