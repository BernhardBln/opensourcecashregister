/*
 * Open Source Cash Register
 * 
 * Copyright (C) 2013 Bernhard Streit
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
 * See /licenses/gpl-3.txt for a copy of the GNU GPL.
 * See /README.txt for more information about the software and the author(s).
 * 
 */
package de.bstreit.java.oscr.base.finance.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.CurrencyType;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import com.google.common.base.Objects;

public class MoneyType implements CompositeUserType {

	private static final Type[] FIELD_TYPES = { BigDecimalType.INSTANCE, CurrencyType.INSTANCE };
	private static final String[] FIELD_NAMES = { "amount", "currency" };

	@Override
	public Class<?> returnedClass() {
		return Money.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equal(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws SQLException {

		assert names.length == 2;

		// already handles null check:
		BigDecimal amount = (BigDecimal) BigDecimalType.INSTANCE.get(rs, names[0], session);

		// already handles null check:
		Currency currency = (Currency) CurrencyType.INSTANCE.get(rs, names[1], session);

		return amount == null || currency == null ? null : new Money(amount, currency);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		if (value == null) {
			BigDecimalType.INSTANCE.set(st, null, index, session);
			CurrencyType.INSTANCE.set(st, null, index + 1, session);
		} else {
			final Money money = (Money) value;
			BigDecimalType.INSTANCE.set(st, money.getAmount(), index, session);
			CurrencyType.INSTANCE.set(st, money.getCurrency(), index + 1, session);
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null || !(value instanceof Money)) {
			throw new HibernateException("value is not of type Money!");
		}

		Money money = (Money) value;
		return new Money(money.getAmount(), money.getCurrency());
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public String[] getPropertyNames() {
		return FIELD_NAMES;
	}

	@Override
	public Type[] getPropertyTypes() {
		return FIELD_TYPES;

	}

	@Override
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		if (component == null || !(component instanceof Money)) {
			return null;
		}

		if (property == 0) {
			return ((Money) component).getAmount();
		}

		return ((Money) component).getCurrency();
	}

	@Override
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		throw new UnsupportedOperationException("Money is immutable!");
	}

	@Override
	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	@Override
	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
			throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Object replace(Object original, Object target, SessionImplementor session, Object owner)
			throws HibernateException {
		return original;
	}

}