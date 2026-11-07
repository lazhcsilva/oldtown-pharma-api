package br.com.oldtown.pharma.order.service;

import br.com.oldtown.pharma.order.dto.CreateOrderRequest;
import br.com.oldtown.pharma.order.dto.UpdateOrderRequest;
import br.com.oldtown.pharma.order.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderResponse> findAll(Pageable pageable);
    OrderResponse findById(Long id);
    OrderResponse insert(CreateOrderRequest request);
    OrderResponse update(Long id, UpdateOrderRequest request);
    void delete(Long id);
}
