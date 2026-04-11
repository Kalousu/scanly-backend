package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.AddOrderItemRequest;
import com.scanly.scanlyBackend.dtos.OrderResponse;
import com.scanly.scanlyBackend.dtos.UpdateItemQuantityRequest;
import com.scanly.scanlyBackend.exceptions.OrderNotFoundException;
import com.scanly.scanlyBackend.exceptions.ProductNotFoundException;
import com.scanly.scanlyBackend.mappers.OrderMapper;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.OrderItem;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.repository.CouponRepository;
import com.scanly.scanlyBackend.repository.OrderRepository;
import com.scanly.scanlyBackend.repository.ProductRepository;
import com.scanly.scanlyBackend.exceptions.CouponNotFoundException;
import com.scanly.scanlyBackend.exceptions.InvalidCouponException;
import com.scanly.scanlyBackend.models.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    CouponRepository couponRepo;
    @Autowired
    private CouponService couponService;
    @Autowired
    private OrderMapper orderMapper;

    public List<OrderResponse> getAll(){
        return orderRepo.findAll().stream()
                .map(orderMapper::toOrderResponse).toList();
    }

    public OrderResponse getById(Long orderId){
        return orderRepo.findById(orderId)
                .map(orderMapper::toOrderResponse).orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
    }

    public Long createOrder(){
        Order savedOrder = orderRepo.save(new Order(OrderStatus.OPEN));
        return savedOrder.getOrderId();
    }

    @Transactional
    public void addItem(Long orderId, AddOrderItemRequest item){
        Order order = orderRepo.findById(orderId).get();
        Product product = productRepo.findByCode(item.code()).get();
        Optional<OrderItem> existingItem = order.getItems().stream()
                .filter(item1 -> item1.getProduct().getCode().equals(product.getCode())).findFirst();
        if(existingItem.isPresent()){
            updateItemQuantity(orderId, existingItem.get().getId(), new UpdateItemQuantityRequest(item.amount()));
        } else {
            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    item.amount(),
                    product.getPricePerUnit(),
                    product.getTaxRate()
            );
            orderItem.setTotalPrice(orderItem.calculateTotalPrice(orderItem.getAmount(), orderItem.getTaxRate(), orderItem.getUnitPrice()));
            order.addItem(orderItem);
            BigDecimal orderTotal = order.getItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalPrice(orderTotal);
            orderRepo.save(order);
        }
    }

    public void updateItemQuantity(Long orderId, Long itemId, UpdateItemQuantityRequest request){
        Order order = orderRepo.findById(orderId).get();
        OrderItem item = order.getItems().stream()
                .filter(item1 -> item1.getId().equals(itemId))
                .findFirst()
                .get();

        BigDecimal newQuantity = item.getAmount().add(request.delta());
        if(newQuantity.compareTo(BigDecimal.ZERO) == 0){
            deleteItem(orderId, itemId);
        } else {
            item.setAmount(newQuantity);
            item.setTotalPrice(item.calculateTotalPrice(newQuantity, item.getTaxRate(), item.getUnitPrice()));
        }
        orderRepo.save(order);
    }

    public void deleteItem(Long orderId, Long itemId) {
        Order order = orderRepo.findById(orderId).get();
        List<OrderItem> items = order.getItems();
        if(!items.removeIf(item -> item.getId().equals(itemId))){
            throw new ProductNotFoundException("Product with id " + itemId + " not found");
        };
        BigDecimal orderTotal = order.getItems().stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(orderTotal);
        orderRepo.save(order);
    }

    public void deleteOrder(Long orderId){
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));
        orderRepo.delete(order);
    }

    @Transactional
    public void applyCoupon(Long orderId, String couponCode) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));

        Coupon coupon = couponRepo.findByCodeIgnoreCase(couponCode.trim())
                .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + couponCode + " not found"));

        if (!coupon.isValid()) {
            throw new InvalidCouponException("Coupon is not valid");
        }

        BigDecimal subtotal = order.getItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new InvalidCouponException(
                    String.format("Minimum order value for this coupon: %.2f EUR", coupon.getMinOrderValue())
            );
        }

        BigDecimal discount = couponService.calculateDiscount(coupon, subtotal);

        order.setAppliedCoupon(coupon);
        order.setDiscountAmount(discount);
        order.setTotalPrice(subtotal.subtract(discount).max(BigDecimal.ZERO));

        coupon.incrementUsage();
        couponRepo.save(coupon);
        orderRepo.save(order);
    }

    @Transactional
    public void removeCoupon(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + orderId + " not found"));

        order.setAppliedCoupon(null);
        order.setDiscountAmount(BigDecimal.ZERO);

        BigDecimal subtotal = order.getItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(subtotal);
        orderRepo.save(order);
    }
}
