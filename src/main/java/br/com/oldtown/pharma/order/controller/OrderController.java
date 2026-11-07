package br.com.oldtown.pharma.order.controller;

import br.com.oldtown.pharma.order.dto.*;
import br.com.oldtown.pharma.order.service.OrderService;
import br.com.oldtown.pharma.shared.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@Tag(name = "Orders", description = "Order creation, item updates and cancellation")
@RestController
@RequestMapping({"/orders", "/api/v1/orders"})
@Validated
public class OrderController {
    private static final Set<String> SORT_PROPERTIES = Set.of("id", "createdAt", "status", "totalAmount");
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get orders (paginated)")
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "size", in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "20")),
            @Parameter(name = "sort", in = ParameterIn.QUERY,
                    description = "Allowed properties: id, createdAt, status, totalAmount. Repeat for multiple orders.",
                    array = @ArraySchema(schema = @Schema(type = "string", example = "id,asc")),
                    example = "[\"id,asc\"]")
    })
    @GetMapping
    public ResponseEntity<PagedModel<OrderResponse>> findAll(@Parameter(hidden = true) Pageable pageable) {
        pageable.getSort().forEach(order -> {
            if (!SORT_PROPERTIES.contains(order.getProperty())) {
                throw new BadRequestException("Invalid order sort property. Use id, createdAt, status or totalAmount.");
            }
        });
        return ResponseEntity.ok(new PagedModel<>(orderService.findAll(pageable)));
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @Operation(summary = "Create order", description = "Prices and totals are calculated by the server.")
    @PostMapping
    public ResponseEntity<OrderResponse> insert(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.insert(request));
    }

    @Operation(summary = "Replace items of a CREATED order", description = "Existing products retain their recorded unit prices.")
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable @Positive Long id,
                                               @Valid @RequestBody UpdateOrderRequest request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @Operation(summary = "Cancel a CREATED order", description = "Preserves history. Repeated cancellation has no additional effect.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
