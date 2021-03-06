package com.comsysto.shop.ui.panel;

import com.comsysto.shop.service.order.model.OrderInfo;
import com.comsysto.shop.ui.panel.base.AbstractPizzaShopBasePanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @author zutherb
 */
public class DeliveryAdressInfoPanel extends AbstractPizzaShopBasePanel {

    private static final long serialVersionUID = -1278005494199018828L;

    private IModel<OrderInfo> orderModel;

    public DeliveryAdressInfoPanel(String id, IModel<OrderInfo> orderModel) {
        super(id);
        this.orderModel = orderModel;
        add(new Label("firstname", new PropertyModel<String>(orderModel, "deliveryAddress.firstname")));
        add(new Label("lastname",   new PropertyModel<String>(orderModel, "deliveryAddress.lastname")));
        add(new Label("street",     new PropertyModel<String>(orderModel, "deliveryAddress.street")));
        add(new Label("zip",        new PropertyModel<String>(orderModel, "deliveryAddress.zip")));
        add(new Label("city",       new PropertyModel<String>(orderModel, "deliveryAddress.city")));
    }

    @Override
    protected void onBeforeRender() {
        setVisible(isAuthorized() );
        super.onBeforeRender();
    }

    public boolean isReadOnly() {
        return false;
    }
}
