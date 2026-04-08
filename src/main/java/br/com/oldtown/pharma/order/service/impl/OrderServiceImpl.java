package br.com.oldtown.pharma.order.service.impl;

import br.com.oldtown.pharma.order.entity.Order;
import br.com.oldtown.pharma.order.repository.OrderRepository;
import br.com.oldtown.pharma.order.service.OrderService;
import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NotFoundException("Orders not found.");
        }
        return orders;
    }

    @Override
    public Order findById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElseThrow(() -> new NotFoundException("Order not found."));
    }

    @Override
    public void insert(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BadRequestException("Order without items.");
        } else {
            System.out.println("OK");
        }
    }

    @Override
    public void update(Long id, Order order) {

    }

    @Override
    public void delete(Long id) {

    }


}
