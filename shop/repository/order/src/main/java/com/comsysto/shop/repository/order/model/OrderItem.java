package com.comsysto.shop.repository.order.model;

import com.comsysto.shop.repository.product.model.Product;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author zutherb
 */
public class OrderItem implements Serializable {

    private Product product;
    private String uuid;

    public OrderItem() {
        uuid = UUID.randomUUID().toString();
    }

    public OrderItem(Product product) {
        this();
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
