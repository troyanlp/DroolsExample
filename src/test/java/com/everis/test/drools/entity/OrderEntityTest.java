package com.everis.test.drools.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import com.everis.drools.entity.Order;

public class OrderEntityTest {

	@Test
	public void dateComparisonTest() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate localdate = LocalDate.parse("09/07/2010", formatter);
		Order order = new Order();
		order.setDate(localdate);

		assertTrue(order.isComparedToDate("01/07/2010", true));
		assertTrue(order.isComparedToDate("11/07/2010", false));
		assertFalse(order.isComparedToDate("11/07/2010", true));
		assertFalse(order.isComparedToDate("01/07/2010", false));

	}
}
