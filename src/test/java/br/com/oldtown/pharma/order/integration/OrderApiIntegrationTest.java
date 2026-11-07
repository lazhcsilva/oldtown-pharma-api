package br.com.oldtown.pharma.order.integration;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.order.entity.OrderStatus;
import br.com.oldtown.pharma.order.repository.OrderRepository;
import br.com.oldtown.pharma.product.entity.Product;
import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.product.repository.ProductRepository;
import br.com.oldtown.pharma.user.entity.Role;
import br.com.oldtown.pharma.user.entity.User;
import br.com.oldtown.pharma.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderApiIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private UserRepository users;
    @Autowired private CategoryRepository categories;
    @Autowired private ProductRepository products;
    @Autowired private OrderRepository orders;
    @Autowired private ObjectMapper json;
    private User customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = new User("Test", "Customer", "order.integration@example.com");
        customer.setPasswordHash("SYNTHETIC-TEST-HASH");
        customer.setRole(Role.CUSTOMER);
        customer = users.saveAndFlush(customer);
        Category category = categories.saveAndFlush(new Category("Order test category", "Test"));
        product = new Product();
        product.setName("Order product");
        product.setDescription("Test");
        product.setManufacturer("Test");
        product.setCostPrice(new BigDecimal("10"));
        product.setOriginalPrice(new BigDecimal("30"));
        product.setBarcode("TEST-ORDER");
        product.setSku("TEST-ORDER-SKU");
        product.setProductType(ProductType.COMMON);
        product.setCategory(category);
        product = products.saveAndFlush(product);
    }

    @Test
    void shouldPersistOrderAndCalculatePricesWithoutExposingCredentials() throws Exception {
        long id = createOrder();
        var saved = orders.findById(id).orElseThrow();
        assertThat(saved.getTotalAmount()).isEqualByComparingTo("60");
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().get(0).getOrder()).isSameAs(saved);
        assertThat(saved.getItems().get(0).getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.CREATED);

        mvc.perform(get("/api/v1/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("customerId").value(customer.getId()))
                .andExpect(jsonPath("customer").doesNotExist())
                .andExpect(jsonPath("passwordHash").doesNotExist())
                .andExpect(jsonPath("items[0].unitPrice").value(30));
        assertThat(json.writeValueAsString(customer)).doesNotContain("SYNTHETIC-TEST-HASH", "passwordHash");
    }

    @Test
    void shouldPreserveRecordedPriceWhenUpdatingQuantitiesAfterCatalogChange() throws Exception {
        long id = createOrder();
        product.setOriginalPrice(new BigDecimal("50"));
        products.saveAndFlush(product);

        mvc.perform(put("/api/v1/orders/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productId\":" + product.getId() + ",\"quantity\":3}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("totalAmount").value(90))
                .andExpect(jsonPath("items[0].unitPrice").value(30));
        assertThat(orders.findById(id).orElseThrow().getItems()).hasSize(1);
    }

    @Test
    void shouldCancelWithoutDeletingHistoryAndAllowRepeatedCancellation() throws Exception {
        long id = createOrder();
        mvc.perform(delete("/api/v1/orders/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/orders/{id}", id)).andExpect(status().isNoContent());

        var order = orders.findById(id).orElseThrow();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("60");
        mvc.perform(put("/api/v1/orders/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productId\":" + product.getId() + ",\"quantity\":3}]}"))
                .andExpect(status().isConflict());
    }

    @ParameterizedTest
    @ValueSource(strings = {"PAID", "FINISHED"})
    void shouldRejectCancellationOfProcessedOrders(String status) throws Exception {
        long id = createOrder();
        var order = orders.findById(id).orElseThrow();
        order.setStatus(OrderStatus.valueOf(status));
        orders.saveAndFlush(order);

        mvc.perform(delete("/api/v1/orders/{id}", id)).andExpect(status().isConflict());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.valueOf(status));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "null", "1000001"})
    void shouldRejectInvalidQuantities(String quantity) throws Exception {
        long count = orders.count();
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(request("{\"productId\":" + product.getId() + ",\"quantity\":" + quantity + "}")))
                .andExpect(status().isBadRequest());
        assertThat(orders.count()).isEqualTo(count);
    }

    @Test
    void shouldRejectDuplicateProducts() throws Exception {
        String item = "{\"productId\":" + product.getId() + ",\"quantity\":1}";
        long count = orders.count();
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(request(item + "," + item)))
                .andExpect(status().isBadRequest());
        assertThat(orders.count()).isEqualTo(count);
    }

    @Test
    void shouldNotSavePartialOrderWhenProductIsMissing() throws Exception {
        long count = orders.count();
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(request("{\"productId\":" + product.getId()
                                + ",\"quantity\":1},{\"productId\":999999,\"quantity\":1}")))
                .andExpect(status().isNotFound());
        assertThat(orders.count()).isEqualTo(count);
    }

    @Test
    void shouldRejectInactiveProduct() throws Exception {
        product.setActive(false);
        products.saveAndFlush(product);
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(request("{\"productId\":" + product.getId() + ",\"quantity\":1}")))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnEmptyPageAndKeepLegacyRouteAvailable() throws Exception {
        mvc.perform(get("/orders").param("page", "100000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").isEmpty());
    }

    @Test
    void shouldRejectEmptyOrder() throws Exception {
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON).content(request("")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForUnknownOrder() throws Exception {
        mvc.perform(get("/api/v1/orders/999999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/v1/orders/999999")).andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUnknownCustomer() throws Exception {
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":999999,\"items\":[{\"productId\":"
                                + product.getId() + ",\"quantity\":1}]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInactiveCustomer() throws Exception {
        customer.setActive(false);
        users.saveAndFlush(customer);
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(request("{\"productId\":" + product.getId() + ",\"quantity\":1}")))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectTotalExceedingDatabasePrecision() throws Exception {
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(request("{\"productId\":" + product.getId() + ",\"quantity\":1000000}")))
                .andExpect(status().isCreated());
        product.setOriginalPrice(new BigDecimal("200"));
        products.saveAndFlush(product);
        long count = orders.count();
        mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(request("{\"productId\":" + product.getId() + ",\"quantity\":1000000}")))
                .andExpect(status().isBadRequest());
        assertThat(orders.count()).isEqualTo(count);
    }

    @Test
    void shouldRejectInvalidOrderSorting() throws Exception {
        mvc.perform(get("/api/v1/orders").param("sort", "[\"string\"]"))
                .andExpect(status().isBadRequest());
    }

    private long createOrder() throws Exception {
        String response = mvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(request("{\"productId\":" + product.getId()
                                + ",\"quantity\":2,\"unitPrice\":0.01}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("totalAmount").value(60))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(response).path("id").asLong();
    }

    private String request(String items) {
        return "{\"customerId\":" + customer.getId() + ",\"totalAmount\":0.01,\"items\":[" + items + "]}";
    }
}
