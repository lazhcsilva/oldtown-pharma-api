package br.com.oldtown.pharma.order.service;

import br.com.oldtown.pharma.order.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAll();
    Order findById(Long id);
    void insert(Order order);
    void update(Long id, Order order);
    void delete(Long id);
}
