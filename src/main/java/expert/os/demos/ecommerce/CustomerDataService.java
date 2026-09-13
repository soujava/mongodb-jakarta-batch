package expert.os.demos.ecommerce;

import expert.os.demos.ecommerce.batch.SegmentationThresholds;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

@ApplicationScoped
public class CustomerDataService {

    private static final Logger LOGGER = Logger.getLogger(CustomerDataService.class.getName());
    private static final String CUSTOMERS_JSON = "/customers.json";

    @Inject
    private CustomerRepository customerRepository;

    List<Customer> loadCustomers() {
        try (InputStream stream =
                     CustomerDataService.class.getResourceAsStream(CUSTOMERS_JSON)) {

            if (stream == null) {
                throw new IllegalStateException(
                        "Resource not found: " + CUSTOMERS_JSON);
            }

            Jsonb jsonb = JsonbBuilder.create();
            CustomerSeed[] customers =
                    jsonb.fromJson(stream, CustomerSeed[].class);

            return List.of(customers).stream()
                    .map(CustomerSeed::toCustomer)
                    .toList();

        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Unable to load customer data", exception);
        }
    }

    public void initializeIfEmpty() {
        try (Stream<Customer> customers = customerRepository.findAll()) {
            if (customers.findAny().isPresent()) {
                LOGGER.info("Customer data already exists; skipping import");
                return;
            }
        }

        List<Customer> customers = loadCustomers();
        customerRepository.saveAll(customers);

        LOGGER.info(() -> "Customer import completed: created=%d"
                .formatted(customers.size()));
    }

    public Map<CustomerTier, Long> countByTier() {
        return statistics().tierCounts();
    }

    public CustomerStatistics statistics() {
        try (Stream<Customer> customers = customerRepository.findAll()) {
            return summarize(customers.toList());
        }
    }

    public CustomerStatistics previewSegmentation(SegmentationThresholds thresholds) {
        Objects.requireNonNull(thresholds, "thresholds are required");

        try (Stream<Customer> customers = customerRepository.findAll()) {
            return calculatePreview(customers::iterator, thresholds);
        }
    }

    static CustomerStatistics summarize(List<Customer> customers) {
        EnumMap<CustomerTier, Long> counts = new EnumMap<>(CustomerTier.class);
        for (CustomerTier tier : CustomerTier.values()) {
            counts.put(tier, 0L);
        }

        long unclassifiedCustomers = 0;
        for (Customer customer : customers) {
            CustomerTier tier = customer.getTier();
            if (tier == null) {
                unclassifiedCustomers++;
                continue;
            }

            counts.merge(tier, 1L, Long::sum);
        }

        if (unclassifiedCustomers > 0) {
            long count = unclassifiedCustomers;
            LOGGER.warning(() -> "Found %d customers without a tier"
                    .formatted(count));
        }

        return new CustomerStatistics(customers.size(), counts);
    }

    static CustomerStatistics calculatePreview(
            Iterable<Customer> customers,
            SegmentationThresholds thresholds) {

        EnumMap<CustomerTier, Long> counts = new EnumMap<>(CustomerTier.class);
        for (CustomerTier tier : CustomerTier.values()) {
            counts.put(tier, 0L);
        }

        long totalCustomers = 0;
        for (Customer customer : customers) {
            CustomerTier tier = thresholds.tierFor(customer.getTotalSpent());
            counts.merge(tier, 1L, Long::sum);
            totalCustomers++;
        }

        return new CustomerStatistics(totalCustomers, counts);
    }

    public record CustomerStatistics(
            long totalCustomers,
            Map<CustomerTier, Long> tierCounts) {

        public CustomerStatistics {
            tierCounts = Map.copyOf(tierCounts);
        }
    }

    public record CustomerSeed(
            String id,
            String name,
            BigDecimal totalSpent,
            CustomerTier tier) {

        Customer toCustomer() {
            return Customer.builder()
                    .id(id)
                    .name(name)
                    .totalSpent(totalSpent)
                    .tier(tier)
                    .build();
        }
    }
}