package com.example.warehousemanagement.service;

import com.example.warehousemanagement.entity.Order;
import com.example.warehousemanagement.entity.OrderItem;
import com.example.warehousemanagement.entity.Product;
import com.example.warehousemanagement.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * 创建订单项
     * @param orderItem 订单项信息
     * @return 创建的订单项
     */
    public OrderItem createOrderItem(OrderItem orderItem) {
        validateOrderItem(orderItem);
        return orderItemRepository.save(orderItem);
    }

    /**
     * 批量创建订单项
     * @param orderItems 订单项列表
     * @return 创建的订单项列表
     */
    public List<OrderItem> createOrderItems(List<OrderItem> orderItems) {
        orderItems.forEach(this::validateOrderItem);
        return orderItemRepository.saveAll(orderItems);
    }

    /**
     * 更新订单项数量
     * @param orderItemId 订单项ID
     * @param quantity 新数量
     * @return 更新后的订单项
     */
    public OrderItem updateQuantity(Long orderItemId, Integer quantity) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("订单项不存在: " + orderItemId));

        if (quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        orderItem.setQuantity(quantity);
        return orderItemRepository.save(orderItem);
    }

    /**
     * 更新订单项单价
     * @param orderItemId 订单项ID
     * @param unitPrice 新单价
     * @return 更新后的订单项
     */
    public OrderItem updateUnitPrice(Long orderItemId, BigDecimal unitPrice) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("订单项不存在: " + orderItemId));

        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("单价必须大于0");
        }

        orderItem.setUnitPrice(unitPrice);
        return orderItemRepository.save(orderItem);
    }

    /**
     * 根据订单查询订单项
     * @param order 订单
     * @return 订单项列表
     */
    @Transactional(readOnly = true)
    public List<OrderItem> findByOrder(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    /**
     * 根据商品查询订单项
     * @param product 商品
     * @return 订单项列表
     */
    @Transactional(readOnly = true)
    public List<OrderItem> findByProduct(Product product) {
        return orderItemRepository.findByProduct(product);
    }

    /**
     * 根据订单和商品查询订单项
     * @param order 订单
     * @param product 商品
     * @return 订单项
     */
    @Transactional(readOnly = true)
    public Optional<OrderItem> findByOrderAndProduct(Order order, Product product) {
        return orderItemRepository.findByOrderAndProduct(order, product);
    }

    /**
     * 根据批次号查询订单项
     * @param batchNo 批次号
     * @return 订单项列表
     */
    @Transactional(readOnly = true)
    public List<OrderItem> findByBatchNo(String batchNo) {
        return orderItemRepository.findByBatchNo(batchNo);
    }

    /**
     * 根据仓库ID查询订单项
     * @param warehouseId 仓库ID
     * @return 订单项列表
     */
    @Transactional(readOnly = true)
    public List<OrderItem> findByWarehouseId(Long warehouseId) {
        return orderItemRepository.findByWarehouseId(warehouseId);
    }

    /**
     * 删除订单项
     * @param orderItemId 订单项ID
     */
    public void deleteOrderItem(Long orderItemId) {
        if (!orderItemRepository.existsById(orderItemId)) {
            throw new IllegalArgumentException("订单项不存在: " + orderItemId);
        }
        orderItemRepository.deleteById(orderItemId);
    }

    /**
     * 验证订单项
     */
    private void validateOrderItem(OrderItem orderItem) {
        if (orderItem.getOrder() == null) {
            throw new IllegalArgumentException("订单不能为空");
        }
        if (orderItem.getProduct() == null) {
            throw new IllegalArgumentException("商品不能为空");
        }
        if (orderItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        if (orderItem.getUnitPrice() == null || orderItem.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("单价必须大于0");
        }
    }
}