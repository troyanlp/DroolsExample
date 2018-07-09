package com.everis.test.drools;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.event.DefaultAgendaEventListener;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import com.everis.drools.entity.Customer;
import com.everis.drools.entity.Order;
import com.everis.drools.entity.Product;

public class DecisionTableTest {

	private static StatelessKnowledgeSession session;
	KieSession ksession;

	@Before
	public void setUp() throws Exception {
		KieServices ks = KieServices.Factory.get();

		// Load Excel file
		KnowledgeBase knowledgeBase = createKnowledgeBaseFromSpreadsheet();
		session = knowledgeBase.newStatelessKnowledgeSession();

		// From the kie services, a container is created from the classpath
		KieContainer kc = ks.getKieClasspathContainer();

		ksession = kc.newKieSession("point-rulesKS");
	}

	private static KnowledgeBase createKnowledgeBaseFromSpreadsheet() throws Exception {

		DecisionTableConfiguration dtconf = KnowledgeBuilderFactory

				.newDecisionTableConfiguration();

		dtconf.setInputType(DecisionTableInputType.XLS);

		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory

				.newKnowledgeBuilder();

		knowledgeBuilder.add(ResourceFactory

				.newClassPathResource("rules/spreadsheets/rules.xls"),

				ResourceType.DTABLE, dtconf);

		if (knowledgeBuilder.hasErrors()) {

			throw new RuntimeException(knowledgeBuilder.getErrors().toString());

		}

		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

		return knowledgeBase;

	}

	@Test
	public void shouldCalculateTotalPrice() {
		Customer customerA = new Customer(Customer.DEFAULT_CUSTOMER, "Customer A");
		Product productA = new Product(1, "Product 1", 350);
		Product productB = new Product(2, "Product 2", 60);
		Order order = new Order(customerA, true);
		order.addProduct(productA);
		order.addProduct(productB);

		session.addEventListener(new DefaultAgendaEventListener() {
			public void afterMatchFired(AfterMatchFiredEvent event) {
				super.afterMatchFired(event);
				String rule = event.getMatch().getRule().getName();
				Order o = (Order) event.getMatch().getObjects().get(0);
				if (rule.compareTo("Initial Rule") == 0) {
					assertTrue(order.getTotalPrice() == 410);
				}

			}
		});
		session.execute(order);
	}

	@Test
	public void shouldDiscountByStatus() {
		Customer customerA = new Customer(Customer.SILVER_CUSTOMER, "Customer A");
		Customer customerB = new Customer(Customer.GOLD_CUSTOMER, "Customer B");
		Product productA = new Product(1, "Product 1", 350);
		Product productB = new Product(2, "Product 2", 60);
		Order order1 = new Order(customerA, true);
		order1.addProduct(productA);
		order1.addProduct(productB);
		Order order2 = new Order(customerB, true);
		order2.addProduct(productA);
		order2.addProduct(productB);

		session.execute(order1);
		session.execute(order2);

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
			Order order = new Order(customerA, true);
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
			Order order = new Order(customerB, true);
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
			Order order = new Order(customerC, true);
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

		ksession.addEventListener(new DefaultAgendaEventListener() {
			public void afterMatchFired(AfterMatchFiredEvent event) {
				super.afterMatchFired(event);
				Order o = (Order) event.getMatch().getObjects().get(0);
				String name = event.getMatch().getRule().getName();

				if (event.getMatch().getRule().getName().compareTo("Initial rule") == 0) {
					assertTrue(o.getTotalPrice() == 695);
				} else if (name.compareTo("SILVER customer rule") == 0) {
					assertTrue(o.getTotalPrice() == 660.25);
				} else if (name.compareTo("GOLD customer rule") == 0) {
					assertTrue(o.getTotalPrice() == 625.5);
				} else if (name.compareTo("Number of products rule") == 0) {
					if (Order.isComparedToDate("01/07/2018", true) && Order.isComparedToDate("01/10/2018", false)) {
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

		orderList.forEach(k -> session.execute(k));

	}

}
