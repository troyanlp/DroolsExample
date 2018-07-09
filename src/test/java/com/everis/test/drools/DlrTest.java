package com.everis.test.drools;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.event.DefaultAgendaEventListener;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.everis.drools.entity.Customer;
import com.everis.drools.entity.Order;
import com.everis.drools.entity.Product;

public class DlrTest {

	private KieSession session;

	@Before
	public void setUp() {
		// KieServices is the factory for all KIE services
		KieServices ks = KieServices.Factory.get();

		// From the kie services, a container is created from the classpath
		KieContainer kc = ks.getKieClasspathContainer();

		session = kc.newKieSession("point-rulesKS");
	}

	@Test
	public void shouldCalculateTotalPrice() {
		Customer customerA = new Customer(Customer.DEFAULT_CUSTOMER, "Customer A");
		Product productA = new Product(1, "Product 1", 350);
		Product productB = new Product(2, "Product 2", 60);
		Order order = new Order(customerA, false);
		order.addProduct(productA);
		order.addProduct(productB);

		session.insert(order);

		session.addEventListener(new DefaultAgendaEventListener() {
			public void afterMatchFired(AfterMatchFiredEvent event) {
				super.afterMatchFired(event);
				Order o = (Order) event.getMatch().getObjects().get(0);
				if (event.getMatch().getRule().getName().compareTo("Initial rule") == 0) {
					assertTrue(order.getTotalPrice() == 410);
				}

			}
		});

		session.fireAllRules();
	}

	@Test
	public void shouldDiscountByStatus() {
		Customer customerA = new Customer(Customer.SILVER_CUSTOMER, "Customer A");
		Customer customerB = new Customer(Customer.GOLD_CUSTOMER, "Customer B");
		Product productA = new Product(1, "Product 1", 350);
		Product productB = new Product(2, "Product 2", 60);
		Order order1 = new Order(customerA, false);
		order1.addProduct(productA);
		order1.addProduct(productB);
		Order order2 = new Order(customerB, false);
		order2.addProduct(productA);
		order2.addProduct(productB);

		session.insert(order1);
		session.insert(order2);

		session.fireAllRules();

		assertTrue(order1.getTotalPrice() == 389.5);
		assertTrue(order2.getTotalPrice() == 369);
	}

	@Test
	public void shouldDiscountByNumberOfProducts() {
		Customer customerA = new Customer(Customer.DEFAULT_CUSTOMER, "Customer A");
		Customer customerB = new Customer(Customer.SILVER_CUSTOMER, "Customer B");
		Customer customerC = new Customer(Customer.GOLD_CUSTOMER, "Customer C");
		Product productA = new Product(1, "Product 1", 350);
		Product productB = new Product(2, "Product 2", 60);
		List<Order> orderList = new ArrayList<Order>();
		{
			Order order = new Order(customerA, false);
			order.addProduct(productA);
			order.addProduct(productB);
			order.addProduct(new Product(3, "Product 3", 60));
			order.addProduct(new Product(4, "Product 4", 60));
			order.addProduct(new Product(5, "Product 5", 60));
			order.addProduct(new Product(6, "Product 6", 15));
			order.addProduct(new Product(7, "Product 7", 5));
			order.addProduct(new Product(8, "Product 8", 35));
			order.addProduct(new Product(9, "Product 9", 10));
			order.addProduct(new Product(10, "Product 10", 40));
			orderList.add(order);
		}
		{
			Order order = new Order(customerB, false);
			order.addProduct(productA);
			order.addProduct(productB);
			order.addProduct(new Product(3, "Product 3", 60));
			order.addProduct(new Product(4, "Product 4", 60));
			order.addProduct(new Product(5, "Product 5", 60));
			order.addProduct(new Product(6, "Product 6", 15));
			order.addProduct(new Product(7, "Product 7", 5));
			order.addProduct(new Product(8, "Product 8", 35));
			order.addProduct(new Product(9, "Product 9", 10));
			order.addProduct(new Product(10, "Product 10", 40));
			orderList.add(order);
		}
		{
			Order order = new Order(customerC, false);
			order.addProduct(productA);
			order.addProduct(productB);
			order.addProduct(new Product(3, "Product 3", 60));
			order.addProduct(new Product(4, "Product 4", 60));
			order.addProduct(new Product(5, "Product 5", 60));
			order.addProduct(new Product(6, "Product 6", 15));
			order.addProduct(new Product(7, "Product 7", 5));
			order.addProduct(new Product(8, "Product 8", 35));
			order.addProduct(new Product(9, "Product 9", 10));
			order.addProduct(new Product(10, "Product 10", 40));
			orderList.add(order);
		}

		orderList.forEach(k -> session.insert(k));

		session.addEventListener(new DefaultAgendaEventListener() {
			public void afterMatchFired(AfterMatchFiredEvent event) {
				super.afterMatchFired(event);
				Order o = (Order) event.getMatch().getObjects().get(0);
				String name = event.getMatch().getRule().getName();
				// System.out.println("Estoy en el event listener de la regla: " +
				// event.getMatch().getRule().getName() + " y el customer es: " +
				// o.getCustomer().getName());
				if (event.getMatch().getRule().getName().compareTo("Initial rule") == 0) {
					System.out.println("Regla: " + name + " Cliente: " + o.getCustomer().getName());
					System.out.println(o.getTotalPrice());
					assertTrue(o.getTotalPrice() == 695);
				} else if (name.compareTo("SILVER customer rule") == 0) {
					System.out.println("Regla: " + name + " Cliente: " + o.getCustomer().getName());
					System.out.println(o.getTotalPrice());
					assertTrue(o.getTotalPrice() == 660.25);
				} else if (name.compareTo("GOLD customer rule") == 0) {
					System.out.println("Regla: " + name + " Cliente: " + o.getCustomer().getName());
					System.out.println(o.getTotalPrice());
					assertTrue(o.getTotalPrice() == 625.5);
				} else if (name.compareTo("Number of products rule") == 0) {
					if (Order.isComparedToDate("01/07/2018", true) && Order.isComparedToDate("01/10/2018", false)) {
						System.out.println("Regla: " + name + " Cliente: " + o.getCustomer().getName());
						System.out.println(o.getTotalPrice());
						switch (o.getCustomer().getStatus()) {
						case Customer.DEFAULT_CUSTOMER:
							assertTrue(o.getTotalPrice() == 590.75);
							break;
						case Customer.SILVER_CUSTOMER:
							assertTrue(o.getTotalPrice() == 561.2125);
							break;
						case Customer.GOLD_CUSTOMER:
							assertTrue(o.getTotalPrice() == 531.675);
							break;
						}
					} else {
						System.out.println("Regla: " + name + " Cliente: " + o.getCustomer().getName());
						System.out.println(o.getTotalPrice());
						switch (o.getCustomer().getStatus()) {
						case Customer.DEFAULT_CUSTOMER:
							assertTrue(o.getTotalPrice() == 695);
							break;
						case Customer.SILVER_CUSTOMER:
							assertTrue(o.getTotalPrice() == 660.25);
							break;
						case Customer.GOLD_CUSTOMER:
							assertTrue(o.getTotalPrice() == 625.5);
							break;
						}
					}
				}
			}
		});

		session.fireAllRules();

	}
}
