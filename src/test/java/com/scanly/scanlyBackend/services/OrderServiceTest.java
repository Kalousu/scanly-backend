package com.scanly.scanlyBackend.services;

import com.scanly.scanlyBackend.dtos.AddOrderItemRequest;
import com.scanly.scanlyBackend.dtos.UpdateItemQuantityRequest;
import com.scanly.scanlyBackend.exceptions.OrderNotFoundException;
import com.scanly.scanlyBackend.mappers.OrderMapper;
import com.scanly.scanlyBackend.models.Coupon;
import com.scanly.scanlyBackend.models.Order;
import com.scanly.scanlyBackend.models.OrderItem;
import com.scanly.scanlyBackend.models.Product;
import com.scanly.scanlyBackend.models.enums.OrderStatus;
import com.scanly.scanlyBackend.repository.CouponRepository;
import com.scanly.scanlyBackend.repository.OrderRepository;
import com.scanly.scanlyBackend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;
    @Mock
    private ProductRepository productRepo;
    @Mock
    private CouponRepository couponRepo;
    @Mock
    private CouponService couponService;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        Order savedOrder = new Order(OrderStatus.OPEN);
        savedOrder.setOrderId(1L);
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        Long orderId = orderService.createOrder();

        assertEquals(1L, orderId);
        verify(orderRepo, times(1)).save(any(Order.class));
    }

    @Test
    void testAddItemNew() {
        Long orderId = 1L;
        AddOrderItemRequest request = new AddOrderItemRequest("barcode1", new BigDecimal("1.0"));
        Order order = new Order(OrderStatus.OPEN);
        order.setOrderId(orderId);
        order.setItems(new ArrayList<>());
        
        Product product = new Product();
        product.setCode("barcode1");
        product.setPricePerUnit(new BigDecimal("10.00"));
        product.setTaxRate(new BigDecimal("0.19"));

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepo.findByCode("barcode1")).thenReturn(Optional.of(product));
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        orderService.addItem(orderId, request);

        assertEquals(1, order.getItems().size());
        assertEquals(0, new BigDecimal("11.9000").compareTo(order.getTotalPrice()));
        verify(orderRepo, times(1)).save(order);
    }

    @Test
    void testUpdateItemQuantity() {
        Long orderId = 1L;
        Long itemId = 10L;
        UpdateItemQuantityRequest request = new UpdateItemQuantityRequest(new BigDecimal("1.0"));
        
        Order order = new Order(OrderStatus.OPEN);
        order.setOrderId(orderId);
        
        OrderItem item = new OrderItem();
        item.setId(itemId);
        item.setAmount(new BigDecimal("1.0"));
        item.setUnitPrice(new BigDecimal("10.0"));
        item.setTaxRate(new BigDecimal("0.19"));
        item.setOrder(order);
        
        List<OrderItem> items = new ArrayList<>();
        items.add(item);
        order.setItems(items);

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        orderService.updateItemQuantity(orderId, itemId, request);

        assertEquals(0, new BigDecimal("2.0").compareTo(item.getAmount()));
        verify(orderRepo, times(1)).save(order);
    }

    @Test
    void testGetByIdNotFound() {
        when(orderRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> {
            orderService.getById(1L);
        });
    }

    @Test
    void testAddItemExisting() {
        Long orderId = 1L;
        AddOrderItemRequest request = new AddOrderItemRequest("barcode1", new BigDecimal("1.0"));
        Order order = new Order(OrderStatus.OPEN);
        order.setOrderId(orderId);
        
        Product product = new Product();
        product.setCode("barcode1");
        
        OrderItem existingItem = new OrderItem();
        existingItem.setId(10L);
        existingItem.setProduct(product);
        existingItem.setAmount(new BigDecimal("1.0"));
        existingItem.setUnitPrice(new BigDecimal("10.0"));
        existingItem.setTaxRate(new BigDecimal("0.19"));
        
        order.setItems(new ArrayList<>(List.of(existingItem)));

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepo.findByCode("barcode1")).thenReturn(Optional.of(product));

        orderService.addItem(orderId, request);

        assertEquals(0, new BigDecimal("2.0").compareTo(existingItem.getAmount()));
        verify(orderRepo, times(1)).save(order);
    }

    @Test
    void testApplyInvalidCoupon() {
        Long orderId = 1L;
        String couponCode = "INVALID";
        Order order = new Order(OrderStatus.OPEN);
        
        Coupon coupon = new Coupon();
        coupon.setActive(false); // Inactive

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(couponRepo.findByCodeIgnoreCase(couponCode)).thenReturn(Optional.of(coupon));

        assertThrows(com.scanly.scanlyBackend.exceptions.InvalidCouponException.class, () -> {
            orderService.applyCoupon(orderId, couponCode);
        });
    }

    @Test
    void testApplyCouponBelowMinOrderValue() {
        Long orderId = 1L;
        String couponCode = "MIN50";
        Order order = new Order(OrderStatus.OPEN);
        order.setItems(new ArrayList<>());
        order.setTotalPrice(new BigDecimal("30.00"));
        
        OrderItem item = new OrderItem();
        item.setTotalPrice(new BigDecimal("30.00"));
        order.getItems().add(item);

        Coupon coupon = new Coupon();
        coupon.setActive(true);
        coupon.setMinOrderValue(new BigDecimal("50.00"));

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(couponRepo.findByCodeIgnoreCase(couponCode)).thenReturn(Optional.of(coupon));

        assertThrows(com.scanly.scanlyBackend.exceptions.InvalidCouponException.class, () -> {
            orderService.applyCoupon(orderId, couponCode);
        });
    }

    @Test
    void testApplyCoupon() {
        Long orderId = 1L;
        String couponCode = "DISCOUNT10";
        Order order = new Order(OrderStatus.OPEN);
        order.setOrderId(orderId);
        order.setItems(new ArrayList<>());
        order.setTotalPrice(new BigDecimal("100.00"));
        
        OrderItem item = new OrderItem();
        item.setTotalPrice(new BigDecimal("100.00"));
        order.getItems().add(item);

        Coupon coupon = new Coupon();
        coupon.setCode(couponCode);
        coupon.setActive(true);
        coupon.setMinOrderValue(new BigDecimal("50.00"));
        coupon.setValidFrom(Instant.now().minus(1, ChronoUnit.DAYS));
        coupon.setValidUntil(Instant.now().plus(1, ChronoUnit.DAYS));
        coupon.setMaxUsages(100);
        coupon.setCurrentUsages(0);

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));
        when(couponRepo.findByCodeIgnoreCase(couponCode)).thenReturn(Optional.of(coupon));
        when(couponService.calculateDiscount(any(), any())).thenReturn(new BigDecimal("10.00"));

        orderService.applyCoupon(orderId, couponCode);

        assertEquals(coupon, order.getAppliedCoupon());
        assertEquals(0, new BigDecimal("10.00").compareTo(order.getDiscountAmount()));
        assertEquals(0, new BigDecimal("90.00").compareTo(order.getTotalPrice()));
        verify(couponRepo, times(1)).save(coupon);
        verify(orderRepo, times(1)).save(order);
    }
}
