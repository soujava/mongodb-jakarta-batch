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
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CustomerDataService {

    private static final Logger LOGGER = Logger.getLogger(CustomerDataService.class.getName());
    private static final String CUSTOMERS_JSON = "/customers.json";

    @Inject
    private Template template;

    private List<Customer> loadCustomers() throws Exception {
        try (InputStream stream =
                     CustomerDataService.class.getResourceAsStream(CUSTOMERS_JSON);
             Jsonb jsonb = JsonbBuilder.create()) {

            if (stream == null) {
                throw new IllegalStateException(
                        "Resource not found: " + CUSTOMERS_JSON);
            }

            Customer[] customers =
                    jsonb.fromJson(stream, Customer[].class);

            return Arrays.asList(customers);

        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Unable to load customer data", exception);
        }
    }

    void saveCustomers() throws Exception {
        if (template.select(Customer.class).limit(1).singleResult().isPresent()) {
            LOGGER.info("Customer data already exists; skipping import");
            return;
        }

        List<Customer> customers = loadCustomers();
        template.insert(customers);

        LOGGER.info(() -> "Customer import completed: created=%d"
                .formatted(customers.size()));
    }
}