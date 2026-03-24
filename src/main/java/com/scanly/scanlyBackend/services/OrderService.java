package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.AddOrderItemRequest;
import com.scanly.scanlyBackend.dtos.OrderItemResponse;
import com.scanly.scanlyBackend.dtos.OrderResponse;
import com.scanly.scanlyBackend.dtos.UpdateItemQuantityRequest;
import com.scanly.scanlyBackend.exceptions.OrderNotFoundException;
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
    @Autowired
    private ProductService productService;

    public List<OrderResponse> getAll(){
        return orderRepo.findAll().stream()
                .map(order -> {
                    List<OrderItemResponse> itemResponses = order.getItems().stream()
                            .map(item -> new OrderItemResponse(
                                    item.getId(),
                                    item.getAmount(),
                                    item.getProduct().getName(),
                                    item.getUnitPrice(),
                                    item.getTaxRate(),
                                    item.getTotalPrice()
                            )).toList();

                    BigDecimal totalGross = itemResponses.stream()
                            .map(OrderItemResponse::totalPriceGross)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new OrderResponse(
                            order.getOrderId(),
                            order.getCreationDate(),
                            itemResponses,
                            totalGross,
                            order.getStatus()
                    );
                }).toList();
    }

    public OrderResponse getById(Long orderId){
        return orderRepo.findById(orderId)
                .map(order -> {
                    List<OrderItemResponse> itemResponses = order.getItems().stream()
                            .map(item -> new OrderItemResponse(
                                    item.getId(),
                                    item.getAmount(),
                                    item.getProduct().getName(),
                                    item.getUnitPrice(),
                                    item.getTaxRate(),
                                    item.getTotalPrice()
                            )).toList();

                    BigDecimal totalGross = itemResponses.stream()
                            .map(OrderItemResponse::totalPriceGross)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new OrderResponse(
                            order.getOrderId(),
                            order.getCreationDate(),
                            itemResponses,
                            totalGross,
                            order.getStatus()
                    );
                }).orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
    }

    public Long createOrder(){
        Order savedOrder = orderRepo.save(new Order(OrderStatus.OPEN));
        return savedOrder.getOrderId();
    }

    @Transactional
    public void addItem(Long orderId, AddOrderItemRequest item){
        Order order = orderRepo.findById(orderId).get();
        Product product = productRepo.findByCode(item.code()).get();
        OrderItem orderItem = new OrderItem(
                order,
                product,
                new BigDecimal(item.amount()),
                product.getPricePerUnit(),
                product.getTaxRate()
        );
        orderItem.setTotalPrice(orderItem.calculateTotalPrice(orderItem.getAmount(), orderItem.getTaxRate(), orderItem.getUnitPrice()));
        order.addItem(orderItem);
        BigDecimal orderTotal = order.getItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(orderTotal);
        orderRepo.save(order);
    }

    public void updateItemQuantity(Long orderId, Long itemId, UpdateItemQuantityRequest request){
        Order order = orderRepo.findById(orderId).get();
        OrderItem item = order.getItems().stream()
                .filter(item1 -> item1.getId().equals(itemId))
                .findFirst()
                .get();

        BigDecimal newQuantity = item.getAmount().add(BigDecimal.valueOf(request.delta()));
        item.setAmount(newQuantity);
        orderRepo.save(order);
    }
}
