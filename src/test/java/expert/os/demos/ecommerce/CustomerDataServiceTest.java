package expert.os.demos.ecommerce;

import expert.os.demos.ecommerce.batch.SegmentationThreshold;
import expert.os.demos.ecommerce.batch.SegmentationThresholds;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Customer data service")
class CustomerDataServiceTest {

    @Test
    @DisplayName("Given seed JSON, when customers are loaded, then records are converted to entities")
    void shouldConvertSeedRecordsToCustomers() {
        List<Customer> customers = new CustomerDataService().loadCustomers();

        assertAll(
                () -> assertEquals(100, customers.size()),
                () -> assertNotNull(customers.getFirst().getId()),
                () -> assertNotNull(customers.getFirst().getTier())
        );
    }

    @Test
    @DisplayName("Given an unclassified customer, when statistics are calculated, then it is included without failing")
    void shouldIncludeUnclassifiedCustomerInTotal() {
        List<Customer> customers = List.of(
                customer("CUST-001", CustomerTier.BRONZE),
                customer("CUST-002", null)
        );

        CustomerDataService.CustomerStatistics statistics =
                CustomerDataService.summarize(customers);

        assertAll(
                () -> assertEquals(2, statistics.totalCustomers()),
                () -> assertEquals(1, statistics.tierCounts().get(CustomerTier.BRONZE))
        );
    }

    @Test
    @DisplayName("Given new thresholds, when previewing, then the shared classification rule is applied")
    void shouldPreviewWithSharedClassificationRule() {
        List<Customer> customers = List.of(
                customer("CUST-001", "7500", CustomerTier.SILVER)
        );
        SegmentationThresholds thresholds = thresholds();

        CustomerDataService.CustomerStatistics preview =
                CustomerDataService.calculatePreview(customers, thresholds);

        assertEquals(1, preview.tierCounts().get(CustomerTier.GOLD));
    }

    private static Customer customer(String id, CustomerTier tier) {
        return customer(id, "10", tier);
    }

    private static Customer customer(
            String id,
            String totalSpent,
            CustomerTier tier) {

        return Customer.builder()
                .id(id)
                .name("Customer")
                .totalSpent(new BigDecimal(totalSpent))
                .tier(tier)
                .build();
    }

    private static SegmentationThresholds thresholds() {
        return new SegmentationThresholds(List.of(
                threshold("10", CustomerTier.BRONZE),
                threshold("1000", CustomerTier.SILVER),
                threshold("5000", CustomerTier.GOLD),
                threshold("10000", CustomerTier.PLATINUM)
        ));
    }

    private static SegmentationThreshold threshold(String value, CustomerTier tier) {
        return new SegmentationThreshold(new BigDecimal(value), tier);
    }
}
