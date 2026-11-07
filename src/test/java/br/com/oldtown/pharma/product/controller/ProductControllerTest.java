package br.com.oldtown.pharma.product.controller;

import br.com.oldtown.pharma.product.service.ProductService;
import br.com.oldtown.pharma.product.dto.request.CreateProductRequest;
import br.com.oldtown.pharma.product.dto.request.UpdateProductRequest;
import br.com.oldtown.pharma.product.dto.request.UpdatePriceRequest;
import br.com.oldtown.pharma.product.dto.request.CreatePromotionalPriceRequest;
import br.com.oldtown.pharma.product.dto.response.ProductResponse;
import br.com.oldtown.pharma.product.dto.response.PromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.DeletePromotionalPriceResponse;
import br.com.oldtown.pharma.product.dto.response.UpdatePriceResponse;
import br.com.oldtown.pharma.product.entity.enums.ProductType;
import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;
import br.com.oldtown.pharma.shared.exception.NotFoundException;
import br.com.oldtown.pharma.shared.exception.ConflictException;
import org.springframework.http.MediaType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import br.com.oldtown.pharma.shared.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {
    private ProductService productService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"[\"string\"]", "string", "currentPrice", "unknown", "name,invalid", "lower(name)"})
    void shouldRejectInvalidSortBeforeCallingService(String sort) throws Exception {
        mockMvc.perform(get("/api/v1/products/search").param("sort", sort))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(400))
                .andExpect(jsonPath("message").value(org.hamcrest.Matchers.containsString("Invalid sort property")))
                .andExpect(jsonPath("path").value("/api/v1/products/search"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldAcceptMultipleSortOrdersWithPriceFilter() throws Exception {
        when(productService.search(any(), any())).thenAnswer(invocation ->
                Page.empty(invocation.getArgument(1, Pageable.class)));

        mockMvc.perform(get("/api/v1/products/search")
                        .param("maxPrice", "20").param("sort", "name,asc", "originalPrice,desc"))
                .andExpect(status().isOk());

        var pageable = org.mockito.ArgumentCaptor.forClass(Pageable.class);
        var criteria = org.mockito.ArgumentCaptor.forClass(
                br.com.oldtown.pharma.product.specification.ProductSearchCriteria.class);
        verify(productService).search(criteria.capture(), pageable.capture());
        assertThat(criteria.getValue().maxPrice()).isEqualByComparingTo("20");
        assertThat(pageable.getValue().getSort()).containsExactly(
                Sort.Order.asc("name"), Sort.Order.desc("originalPrice"));
    }

    @Test
    void shouldAcceptSearchWithoutSort() throws Exception {
        when(productService.search(any(), any())).thenAnswer(invocation ->
                Page.empty(invocation.getArgument(1, Pageable.class)));

        mockMvc.perform(get("/api/v1/products/search").param("maxPrice", "20"))
                .andExpect(status().isOk());

        var pageable = org.mockito.ArgumentCaptor.forClass(Pageable.class);
        verify(productService).search(any(), pageable.capture());
        assertThat(pageable.getValue().getSort().isUnsorted()).isTrue();
        assertThat(pageable.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    void shouldFindProductById() throws Exception {
        when(productService.findById(1L)).thenReturn(productResponse());

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("Test product"))
                .andExpect(jsonPath("currentPrice").value(30));
    }

    @Test
    void shouldReturnNotFoundForMissingProduct() throws Exception {
        when(productService.findById(99L)).thenThrow(new NotFoundException("Product not found."));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("status").value(404))
                .andExpect(jsonPath("message").value("Product not found."));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        when(productService.create(any())).thenReturn(productResponse());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON).content(createBody("Test product")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1));

        var request = org.mockito.ArgumentCaptor.forClass(CreateProductRequest.class);
        verify(productService).create(request.capture());
        assertThat(request.getValue().name()).isEqualTo("Test product");
        assertThat(request.getValue().categoryId()).isEqualTo(2L);
        assertThat(request.getValue().originalPrice()).isEqualByComparingTo("30");
    }

    @Test
    void shouldRejectBlankProductName() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON).content(createBody("")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("details").isNotEmpty());

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnConflictForDuplicateProduct() throws Exception {
        when(productService.create(any())).thenThrow(new ConflictException("Product already exists."));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON).content(createBody("Test product")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("message").value("Product already exists."));
    }

    @Test
    void shouldUpdateBasicData() throws Exception {
        UpdateProductRequest request = new UpdateProductRequest("New name", "New description");
        when(productService.updateBasicData(1L, request)).thenReturn(productResponse());

        mockMvc.perform(put("/api/v1/products/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New name\",\"description\":\"New description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1));

        verify(productService).updateBasicData(1L, request);
    }

    @Test
    void shouldUpdatePrice() throws Exception {
        UpdatePriceRequest request = new UpdatePriceRequest(new BigDecimal("25.00"));
        when(productService.updatePrice(1L, request))
                .thenReturn(new UpdatePriceResponse(1L, new BigDecimal("30"), new BigDecimal("25")));

        mockMvc.perform(patch("/api/v1/products/1/price").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalPrice\":25.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("previousPrice").value(30))
                .andExpect(jsonPath("newPrice").value(25));

        verify(productService).updatePrice(1L, request);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"originalPrice\":0}", "{\"originalPrice\":-1}",
            "{\"originalPrice\":10.001}"})
    void shouldRejectInvalidPricePayload(String body) throws Exception {
        mockMvc.perform(patch("/api/v1/products/1/price")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void shouldCreatePromotion() throws Exception {
        CreatePromotionalPriceRequest request = new CreatePromotionalPriceRequest(new BigDecimal("20.00"),
                "01/01/2030 10:00:00", "02/01/2030 10:00:00");
        when(productService.createPromotionalPrice(1L, request)).thenReturn(new PromotionalPriceResponse(
                1L, "Test product", "Manufacturer", new BigDecimal("30"), new BigDecimal("20"),
                new BigDecimal("30"), LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 2, 10, 0), PromotionStatus.SCHEDULED));

        mockMvc.perform(patch("/api/v1/products/1/create-promotion")
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"promotionalPrice":20.00,"promotionStartDate":"01/01/2030 10:00:00",
                                 "promotionEndDate":"02/01/2030 10:00:00"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("promotionalPrice").value(20))
                .andExpect(jsonPath("status").value("SCHEDULED"));

        verify(productService).createPromotionalPrice(1L, request);
    }

    @Test
    void shouldRemovePromotionAndReturnResponseBody() throws Exception {
        when(productService.deletePromotionalPrice(1L)).thenReturn(new DeletePromotionalPriceResponse(
                1L, "Test product", "Manufacturer", new BigDecimal("30"), null,
                new BigDecimal("30"), PromotionStatus.NONE));

        mockMvc.perform(patch("/api/v1/products/1/delete-promotion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("NONE"))
                .andExpect(jsonPath("currentPrice").value(30));

        verify(productService).deletePromotionalPrice(1L);
    }

    @Test
    void shouldDeleteProductAndReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(productService).delete(1L);
    }

    private String createBody(String name) {
        return """
                {"name":"%s","description":"Description","manufacturer":"Manufacturer",
                 "costPrice":10.00,"originalPrice":30.00,"barcode":"123456",
                 "categoryId":2,"productType":"COMMON"}
                """.formatted(name);
    }

    private ProductResponse productResponse() {
        return new ProductResponse(1L, "Test product", "Description", "Manufacturer", new BigDecimal("10"),
                new BigDecimal("30"), null, new BigDecimal("30"), null, null, PromotionStatus.NONE,
                true, "TEST-SKU", "123456", null, ProductType.COMMON, null);
    }
}
