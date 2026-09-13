package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.Customer;
import expert.os.demos.ecommerce.CustomerDataService;
import expert.os.demos.ecommerce.CustomerTier;
import expert.os.demos.ecommerce.batch.CustomerSegmentationPolicy;
import expert.os.demos.ecommerce.batch.CustomerSegmentationService;
import expert.os.demos.ecommerce.batch.SegmentationThreshold;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Customer segmentation flow")
class CustomerSegmentationFlowTest {

    private StubSegmentationService segmentationService;
    private CustomerSegmentationFlow flow;

    @BeforeEach
    void setUp() {
        segmentationService = new StubSegmentationService(policy());
        CustomerSegmentationFlowService flowService =
                new CustomerSegmentationFlowService(
                        segmentationService,
                        new StubCustomerDataService(List.of(
                                customer("CUST-001", "500"),
                                customer("CUST-002", "2500"),
                                customer("CUST-003", "7500"),
                                customer("CUST-004", "15000")
                        )));
        flow = new TestCustomerSegmentationFlow(
                flowService);
        flow.initialize();
    }

    @Nested
    @DisplayName("Given edited thresholds")
    class Preview {

        @Test
        @DisplayName("When preview is requested, then current edited values are used")
        void shouldUseEditedThresholds() {
            thresholdInput(CustomerTier.GOLD).setMinimumValue(new BigDecimal("8000"));

            String outcome = flow.preview();

            assertEquals("preview", outcome);
            assertEquals(2, comparison(CustomerTier.SILVER).getProjectedCount());
        }

        @Test
        @DisplayName("When preview is calculated, then every tier and total are retained")
        void shouldRetainCompletePreview() {
            flow.preview();

            assertEquals(4, flow.getState().getComparisons().size());
            assertEquals(4, flow.getState().getCurrentTotal());
            assertEquals(4, flow.getState().getProjectedTotal());
            assertNotNull(comparison(CustomerTier.PLATINUM));
        }

        @Test
        @DisplayName("When thresholds change, then a repeated preview recalculates the result")
        void shouldRecalculatePreview() {
            flow.preview();
            long initialGold = comparison(CustomerTier.GOLD).getProjectedCount();

            thresholdInput(CustomerTier.GOLD).setMinimumValue(new BigDecimal("8000"));
            flow.preview();

            assertEquals(1, initialGold);
            assertEquals(0, comparison(CustomerTier.GOLD).getProjectedCount());
        }

        @Test
        @DisplayName("When continuing to review, then flow-scoped configuration and preview remain available")
        void shouldRetainStateForReview() {
            thresholdInput(CustomerTier.GOLD).setMinimumValue(new BigDecimal("8000"));
            flow.preview();

            String outcome = flow.review();

            assertEquals("review", outcome);
            assertEquals(new BigDecimal("8000"), thresholdInput(CustomerTier.GOLD).getMinimumValue());
            assertFalse(flow.getState().getComparisons().isEmpty());
        }
    }

    @Nested
    @DisplayName("Given a reviewed preview")
    class Execution {

        @Test
        @DisplayName("When execution starts, then the flow returns home")
        void shouldReturnHome() {
            flow.preview();

            String outcome = flow.execute();

            assertEquals("home", outcome);
            assertNotNull(segmentationService.startedPolicy);
        }
    }

    private ThresholdInput thresholdInput(CustomerTier tier) {
        return flow.getState().getThresholds().stream()
                .filter(threshold -> threshold.getTier() == tier)
                .findFirst()
                .orElseThrow();
    }

    private TierComparison comparison(CustomerTier tier) {
        return flow.getState().getComparisons().stream()
                .filter(item -> item.getTier() == tier)
                .findFirst()
                .orElseThrow();
    }

    private static Customer customer(String id, String totalSpent) {
        return Customer.builder()
                .id(id)
                .name("Customer")
                .totalSpent(new BigDecimal(totalSpent))
                .tier(CustomerTier.BRONZE)
                .build();
    }

    private static CustomerSegmentationPolicy policy() {
        return new CustomerSegmentationPolicy(List.of(
                threshold("10", CustomerTier.BRONZE),
                threshold("1000", CustomerTier.SILVER),
                threshold("5000", CustomerTier.GOLD),
                threshold("10000", CustomerTier.PLATINUM)
        ));
    }

    private static SegmentationThreshold threshold(String value, CustomerTier tier) {
        return new SegmentationThreshold(new BigDecimal(value), tier);
    }

    private static class StubSegmentationService extends CustomerSegmentationService {

        private final CustomerSegmentationPolicy current;
        private CustomerSegmentationPolicy startedPolicy;

        private StubSegmentationService(CustomerSegmentationPolicy current) {
            this.current = current;
        }

        @Override
        public CustomerSegmentationPolicy currentPolicy() {
            return current;
        }

        @Override
        public long start(CustomerSegmentationPolicy policy) {
            startedPolicy = policy;
            return 42L;
        }
    }

    private static class StubCustomerDataService extends CustomerDataService {

        private final List<Customer> customers;

        private StubCustomerDataService(List<Customer> customers) {
            this.customers = customers;
        }

        @Override
        public SegmentationPreview segmentationPreview(CustomerSegmentationPolicy policy) {
            EnumMap<CustomerTier, Long> currentCounts = new EnumMap<>(CustomerTier.class);
            EnumMap<CustomerTier, Long> projectedCounts = new EnumMap<>(CustomerTier.class);
            for (CustomerTier tier : CustomerTier.values()) {
                currentCounts.put(tier, 0L);
                projectedCounts.put(tier, 0L);
            }

            for (Customer customer : customers) {
                currentCounts.merge(customer.getTier(), 1L, Long::sum);
                CustomerTier projectedTier = policy.tierFor(customer.getTotalSpent());
                projectedCounts.merge(projectedTier, 1L, Long::sum);
            }

            return new SegmentationPreview(
                    new CustomerStatistics(customers.size(), currentCounts),
                    new CustomerStatistics(customers.size(), projectedCounts));
        }
    }

    private static class TestCustomerSegmentationFlow extends CustomerSegmentationFlow {

        private TestCustomerSegmentationFlow(
                CustomerSegmentationFlowService flowService) {
            super(flowService);
        }

        @Override
        FacesContext currentFacesContext() {
            return null;
        }
    }
}
