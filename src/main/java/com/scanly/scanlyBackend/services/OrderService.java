package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.AddOrderItemRequest;
import com.scanly.scanlyBackend.exceptions.ProductNotFoundException;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.OrderItem;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.repository.OrderRepository;
import com.scanly.scanlyBackend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    ProductRepository productRepo;

    public List<Order> getAll(){
        return orderRepo.findAll();
    }

    public Long createOrder(){
        Order savedOrder = orderRepo.save(new Order(OrderStatus.OPEN));
        return savedOrder.getOrderId();
    }
    @Transactional
    public void addItem(Long orderId, AddOrderItemRequest item){
        Order currOrder = orderRepo.findById(orderId).get();
        Product product = productRepo.findByCode(item.code()).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrder(currOrder);
        orderItem.setAmount(BigDecimal.valueOf(item.amount()));
        orderItem.setUnitPrice(product.getPricePerUnit());
        orderItem.setTotalPrice(product.getPricePerUnit().multiply(BigDecimal.valueOf(item.amount())));
        currOrder.addItem(orderItem);
        orderRepo.save(currOrder);
    }
}
