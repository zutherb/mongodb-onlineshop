package com.comsysto.shop.repository.order.impl;

import com.comsysto.shop.repository.order.api.OrderRepository;
import com.comsysto.shop.repository.order.model.DeliveryAddress;
import com.comsysto.shop.repository.order.model.Order;
import com.comsysto.shop.repository.order.model.OrderItem;
import com.comsysto.shop.repository.product.api.ProductRepository;
import com.comsysto.shop.repository.product.model.Product;
import com.comsysto.shop.repository.product.model.ProductType;
import com.comsysto.shop.repository.user.api.UserRepository;
import com.comsysto.shop.repository.user.model.Address;
import com.comsysto.shop.repository.user.model.Role;
import com.comsysto.shop.repository.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * User: christian.kroemer@comsysto.com
 * Date: 6/27/13
 * Time: 4:58 PM
 */
@ActiveProfiles("test")
@ContextConfiguration(locations = "classpath:com/comsysto/shop/repository/order/spring-context.xml")
public class OrderDBTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    private static User sampleUser;
    private static Product sampleProduct;

    @Autowired
    private MongoOperations mongoOperations;

    @Before
    public void setUp() {
        mongoOperations.dropCollection(User.class);
        mongoOperations.dropCollection(Product.class);

        userRepository.save(createSampleUser());
        productRepository.save(createSampleProduct());

        sampleUser = userRepository.findAll().get(0);
        sampleProduct = productRepository.findAll().get(0);
    }

    @Test
    public void saveAndRetrieveOrderTest() {
        mongoOperations.dropCollection(Order.class);

        Order sampleOrder = createSampleOrder();
        orderRepository.save(sampleOrder);

        assertEquals(1, orderRepository.countAll());
        assertEquals(sampleOrder.getOrderId(), orderRepository.findAll().get(0).getOrderId());
    }

    private static Order createSampleOrder() {
        return new Order(4242L,
                sampleUser.getId(),
                Collections.singletonList(new OrderItem(sampleProduct)),
                new DeliveryAddress(sampleUser.getAddress()),
                "the-session-id");
    }

    private static User createSampleUser() {
        return new User("johnsmith", "John", "Smith", "securepw", new Address("", "", "", ""), Collections.<Role>emptySet());
    }

    private static Product createSampleProduct() {
        return new Product("4242", ProductType.PIZZA, "Pizza Sampliziosa", 5.95);
    }
}
