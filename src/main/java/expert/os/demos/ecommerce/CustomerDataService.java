package expert.os.demos.ecommerce;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.nosql.Template;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class CustomerDataService {

    private static final Logger LOGGER = Logger.getLogger(CustomerDataService.class.getName());
    private static final String CUSTOMERS_JSON = "/customers.json";

    @Inject
    private Template template;

    private List<Customer> loadCustomers() {
        try (InputStream stream =
                     CustomerDataService.class.getResourceAsStream(CUSTOMERS_JSON)) {

            if (stream == null) {
                throw new IllegalStateException(
                        "Resource not found: " + CUSTOMERS_JSON);
            }

            Jsonb jsonb = JsonbBuilder.create();
            Customer[] customers = jsonb.fromJson(stream, Customer[].class);

            return Arrays.asList(customers);

        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Unable to load customer data", exception);
        }
    }

    public void initializeIfEmpty() {
        if (template.select(Customer.class).limit(1).singleResult().isPresent()) {
            LOGGER.info("Customer data already exists; skipping import");
            return;
        }

        List<Customer> customers = loadCustomers();
        template.insert(customers);

        LOGGER.info(() -> "Customer import completed: created=%d"
                .formatted(customers.size()));
    }

    public Map<CustomerTier, Long> countByTier() {
        EnumMap<CustomerTier, Long> counts = new EnumMap<>(CustomerTier.class);
        for (CustomerTier tier : CustomerTier.values()) {
            counts.put(tier, 0L);
        }

        template.select(Customer.class)
                .<Customer>result()
                .forEach(customer -> counts.merge(customer.getTier(), 1L, Long::sum));

        return Map.copyOf(counts);
    }
}