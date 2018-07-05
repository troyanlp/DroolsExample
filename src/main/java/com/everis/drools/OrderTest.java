package com.everis.drools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.everis.drools.entity.Customer;
import com.everis.drools.entity.Order;
import com.everis.drools.entity.Product;
import com.everis.drools.entity.User;

public class OrderTest 
{
    public static void main( String[] args ) {
    	 // KieServices is the factory for all KIE services
        KieServices ks = KieServices.Factory.get();

        // From the kie services, a container is created from the classpath
        KieContainer kc = ks.getKieClasspathContainer();

        try {
			execute( kc );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void execute( KieContainer kc ) throws Exception{
        // From the container, a session is created based on
        // its definition and configuration in the META-INF/kmodule.xml file
        KieSession ksession = kc.newKieSession("point-rulesKS");
        
        List<Order> orderList = getInitData();

        for (int i = 0; i < orderList.size(); i++) {
            Order o = orderList.get(i);
            ksession.insert(o);
            ksession.fireAllRules();
            
        }
        
        showResults(orderList);

        ksession.dispose();

    }
    
    private static List<Order> getInitData() throws Exception {  
        Customer customerA = new Customer(Customer.DEFAULT_CUSTOMER, "Customer A");
        Customer customerB = new Customer(Customer.SILVER_CUSTOMER, "Customer B");
        Customer customerC = new Customer(Customer.GOLD_CUSTOMER, "Customer C");
        Customer customerD = new Customer(Customer.DEFAULT_CUSTOMER, "Customer D");
        Customer customerE = new Customer(Customer.SILVER_CUSTOMER, "Customer E");
        
        Product productA = new Product(1, "Product 1", 350);
        Product productB = new Product(2, "Product 2", 60);
        
    	List<Order> orderList = new ArrayList<Order>();
    	{
             Order order = new Order(customerA, false);  
             order.addProduct(productA);
             order.addProduct(productB);
             orderList.add(order);  
        }
    	{
            Order order = new Order(customerB, false);  
            order.addProduct(productA);
            order.addProduct(productB);
            orderList.add(order);  
        }
        {
            Order order = new Order(customerC, false);  
            order.addProduct(productA);
            order.addProduct(productB);
            orderList.add(order);  
        }
        {
            Order order = new Order(customerD, false);  
            order.addProduct(productA);
            order.addProduct(productB);
            order.addProduct(new Product(3,"Product 3", 60));
            order.addProduct(new Product(4,"Product 4", 60));
            order.addProduct(new Product(5,"Product 5", 60));
            order.addProduct(new Product(6,"Product 6", 15));
            order.addProduct(new Product(7,"Product 7", 5));
            order.addProduct(new Product(8,"Product 8", 35));
            order.addProduct(new Product(9,"Product 9", 10));
            order.addProduct(new Product(10,"Product 10", 40));
            orderList.add(order);  
        }
        {
            Order order = new Order(customerE, false);  
            order.addProduct(productA);
            order.addProduct(productB);
            order.addProduct(new Product(3,"Product 3", 60));
            order.addProduct(new Product(4,"Product 4", 60));
            order.addProduct(new Product(5,"Product 5", 60));
            order.addProduct(new Product(6,"Product 6", 15));
            order.addProduct(new Product(7,"Product 7", 5));
            order.addProduct(new Product(8,"Product 8", 35));
            order.addProduct(new Product(9,"Product 9", 10));
            order.addProduct(new Product(10,"Product 10", 40));
            orderList.add(order);  
        }
        return orderList;  
    }
 
	private static void showResults(List<Order> orders) {
		for (Order order : orders) {
			System.out.println("Cliente " + order.getCustomer().getName() + " productos: " + order.getProducts().size()
					+ " Precio total: " + order.getTotalPrice());
		}
	}
    
}
