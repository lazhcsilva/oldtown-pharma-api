package br.com.oldtown.pharma.order.dto;

import br.com.oldtown.pharma.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, Long customerId, LocalDateTime createdAt, OrderStatus status,
                            BigDecimal totalAmount, List<OrderItemResponse> items) {}
