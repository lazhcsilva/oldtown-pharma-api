package br.com.oldtown.pharma.product.integration;

import br.com.oldtown.pharma.category.entity.Category;
import br.com.oldtown.pharma.category.repository.CategoryRepository;
import br.com.oldtown.pharma.product.entity.enums.PromotionStatus;
import br.com.oldtown.pharma.product.repository.ProductRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductApiIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private CategoryRepository categories;
    @Autowired private ProductRepository products;
    @Autowired private ObjectMapper json;
    private Long categoryId;

    @BeforeEach
    void setUp() {
        categoryId = categories.saveAndFlush(new Category("AB", "Integration category")).getId();
    }

    @Test
    void shouldCreateActiveCommonProductWithShortNameAndCategory() throws Exception {
        String result = mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content(body("COMMON", "", "10", "30")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("active").value(true))
                .andExpect(jsonPath("status").value("NONE"))
                .andReturn().getResponse().getContentAsString();

        var product = products.findById(json.readTree(result).path("id").asLong()).orElseThrow();
        assertThat(product.getSku()).startsWith("AB-A-");
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"cost", "original"})
    void shouldRejectNullPricesWithBadRequest(String missing) throws Exception {
        long count = products.count();
        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content(body("COMMON", "", missing.equals("cost") ? "null" : "10",
                                missing.equals("original") ? "null" : "30")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("details").isNotEmpty());
        assertThat(products.count()).isEqualTo(count);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "9.99", "10"})
    void shouldRejectPromotionAtOrBelowCostDuringCreation(String price) throws Exception {
        long count = products.count();
        String promotion = ",\"promotionalPrice\":" + price
                + ",\"promotionStartDate\":\"" + LocalDateTime.now().minusDays(1)
                + "\",\"promotionEndDate\":\"" + LocalDateTime.now().plusDays(1) + "\"";
        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content(body("COMMON", promotion, "10", "30")))
                .andExpect(status().isBadRequest());
        assertThat(products.count()).isEqualTo(count);
    }

    @Test
    void shouldRejectExpiredPromotionDuringCreation() throws Exception {
        String promotion = ",\"promotionalPrice\":20,\"promotionStartDate\":\""
                + LocalDateTime.now().minusDays(2) + "\",\"promotionEndDate\":\""
                + LocalDateTime.now().minusDays(1) + "\"";
        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content(body("COMMON", promotion, "10", "30")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateNestedMedicineDetails() throws Exception {
        mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content(body("MEDICINE", ",\"medicineDetails\":{}", "10", "30")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("details").isNotEmpty());
    }

    @Test
    void shouldCreateMedicineWithSingleDigitDosageAndItsRelationship() throws Exception {
        String details = """
                ,"medicineDetails":{"anvisaRegistration":"TEST-123","dosage":"2","unitMeasure":"MG",
                "controlled":false,"presentation":"TABLET","brandName":"Test","genericName":"Test",
                "expirationControlRequired":true,"therapeuticClass":"ANALGESIC"}
                """;
        String response = mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content(body("MEDICINE", details, "10", "30")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("active").value(true))
                .andExpect(jsonPath("medicineDetails.dosage").value("2"))
                .andReturn().getResponse().getContentAsString();
        var product = products.findById(json.readTree(response).path("id").asLong()).orElseThrow();
        assertThat(product.getMedicineDetails().getProduct()).isSameAs(product);
        assertThat(product.getMedicineDetails().getId()).isEqualTo(product.getId());
        assertThat(product.getSku()).startsWith("AB-A-2-TAB-");
    }

    @Test
    void shouldRejectImpossibleCalendarDateWithoutChangingPromotion() throws Exception {
        String response = mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content(body("COMMON", "", "10", "30")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(response).path("id").asLong();
        mvc.perform(patch("/api/v1/products/{id}/create-promotion", id)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"promotionalPrice":20,"promotionStartDate":"31/02/2030 10:00:00",
                                 "promotionEndDate":"01/03/2030 10:00:00"}
                                """))
                .andExpect(status().isBadRequest());
        assertThat(products.findById(id).orElseThrow().getStatus()).isEqualTo(PromotionStatus.NONE);
    }

    @Test
    void shouldReplaceExpiredPromotionAndReturnNewCalculatedStatus() throws Exception {
        String response = mvc.perform(post("/api/v1/products").contentType(MediaType.APPLICATION_JSON)
                        .content(body("COMMON", "", "10", "30")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(response).path("id").asLong();
        var product = products.findById(id).orElseThrow();
        product.setPromotionalPrice(new java.math.BigDecimal("20"));
        product.setPromotionStartDate(LocalDateTime.now().minusDays(2));
        product.setPromotionEndDate(LocalDateTime.now().minusDays(1));
        products.saveAndFlush(product);
        var format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String request = """
                {"promotionalPrice":15,"promotionStartDate":"%s","promotionEndDate":"%s"}
                """.formatted(LocalDateTime.now().minusDays(1).format(format),
                LocalDateTime.now().plusDays(1).format(format));

        mvc.perform(patch("/api/v1/products/{id}/create-promotion", id)
                        .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value("ACTIVE"))
                .andExpect(jsonPath("currentPrice").value(15));
    }

    @Test
    void shouldRejectInvalidQueryParameterWithBadRequest() throws Exception {
        mvc.perform(get("/api/v1/products/search").param("maxPrice", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"minPrice=-1", "maxPrice=-1", "minPrice=30&maxPrice=20"})
    void shouldRejectInvalidPriceBounds(String query) throws Exception {
        mvc.perform(get("/api/v1/products/search?" + query))
                .andExpect(status().isBadRequest());
    }

    private String body(String type, String extra, String cost, String price) {
        return """
                {"name":"A","description":"Test","manufacturer":"Test","costPrice":%s,
                 "originalPrice":%s,"barcode":"TEST-123","categoryId":%d,"productType":"%s"%s}
                """.formatted(cost, price, categoryId, type, extra);
    }
}
