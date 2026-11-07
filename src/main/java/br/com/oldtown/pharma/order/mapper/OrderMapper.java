package br.com.oldtown.pharma.order.mapper;

import br.com.oldtown.pharma.order.dto.OrderResponse;
import br.com.oldtown.pharma.order.dto.OrderItemResponse;
import br.com.oldtown.pharma.order.entity.Order;
import br.com.oldtown.pharma.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "customerId", source = "customer.id")
    OrderResponse toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toResponse(OrderItem item);
}
