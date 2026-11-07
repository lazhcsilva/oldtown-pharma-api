package br.com.oldtown.pharma.order.service.impl;

import br.com.oldtown.pharma.order.dto.*;
import br.com.oldtown.pharma.order.entity.Order;
import br.com.oldtown.pharma.order.entity.OrderItem;
import br.com.oldtown.pharma.order.entity.OrderStatus;
import br.com.oldtown.pharma.order.mapper.OrderMapper;
import br.com.oldtown.pharma.order.repository.OrderRepository;
import br.com.oldtown.pharma.order.service.OrderService;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.repository.ProductRepository;
import br.com.oldtown.pharma.shared.exception.BadRequestException;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final BigDecimal MAX_TOTAL = new BigDecimal("99999999.99");
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository,
                            UserRepository userRepository, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return mapper.toResponse(getOrder(id));
    }

    @Override
    public OrderResponse insert(CreateOrderRequest request) {
        var customer = userRepository.findById(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found."));
        if (!customer.isActive()) throw new ConflictException("Inactive customer.");
        Order order = new Order();
        order.setCustomer(customer);
        replaceItems(order, request.items());
        return mapper.toResponse(orderRepository.saveAndFlush(order));
    }

    @Override
    public OrderResponse update(Long id, UpdateOrderRequest request) {
        Order order = getOrder(id);
        requireCreated(order);
        replaceItems(order, request.items());
        return mapper.toResponse(orderRepository.saveAndFlush(order));
    }

    @Override
    public void delete(Long id) {
        Order order = getOrder(id);
        if (order.getStatus() == OrderStatus.CANCELED) return;
        requireCreated(order);
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.saveAndFlush(order);
    }

    private Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found."));
    }

    private void requireCreated(Order order) {
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new ConflictException("Only orders in CREATED status can be edited or canceled.");
        }
    }

    private void replaceItems(Order order, List<OrderItemRequest> requests) {
        if (requests == null || requests.isEmpty() || requests.size() > 100) {
            throw new BadRequestException("Order must contain between 1 and 100 items.");
        }
        Set<Long> productIds = new HashSet<>();
        for (OrderItemRequest request : requests) {
            if (request == null || request.productId() == null || request.productId() <= 0
                    || request.quantity() == null || request.quantity() <= 0 || request.quantity() > 1000000) {
                throw new BadRequestException("Order items require a product and a positive quantity up to 1000000.");
            }
            if (!productIds.add(request.productId())) {
                throw new BadRequestException("A product must appear only once per order.");
            }
        }
        Map<Long, Product> products = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
        Map<Long, BigDecimal> previousPrices = order.getItems().stream()
                .collect(Collectors.toMap(item -> item.getProduct().getId(), OrderItem::getUnitPrice));
        LocalDateTime now = LocalDateTime.now();
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest request : requests) {
            Product product = products.get(request.productId());
            if (product == null) throw new NotFoundException("Product not found with id: " + request.productId());
            if (!product.isActive()) throw new ConflictException("Inactive product.");
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(request.quantity());
            item.setUnitPrice(previousPrices.getOrDefault(product.getId(), product.getCurrentPrice(now)));
            if (item.getUnitPrice() == null || item.getUnitPrice().signum() <= 0) {
                throw new ConflictException("Product has no valid selling price.");
            }
            total = total.add(item.getSubtotal());
            items.add(item);
        }
        if (total.compareTo(MAX_TOTAL) > 0) throw new BadRequestException("Order total exceeds 99999999.99.");
        order.getItems().clear();
        order.getItems().addAll(items);
        order.setTotalAmount(total);
    }
}
