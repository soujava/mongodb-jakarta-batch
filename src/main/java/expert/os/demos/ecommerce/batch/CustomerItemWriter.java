package expert.os.demos.ecommerce.batch;

import expert.os.demos.ecommerce.Customer;
import jakarta.batch.api.chunk.AbstractItemWriter;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.nosql.Template;

import java.util.List;
import java.util.logging.Logger;

@Named("customerItemWriter")
@Dependent
public class CustomerItemWriter extends AbstractItemWriter {

    private static final Logger LOGGER = Logger.getLogger(CustomerItemWriter.class.getName());

    @Inject
    private Template template;

    @Override
    public void writeItems(List<Object> items) {
        List<Customer> customers = items.stream()
                .map(this::toCustomer)
                .toList();

        template.update(customers);
        LOGGER.info(() -> "Updated %d customer tiers".formatted(customers.size()));
    }

    private Customer toCustomer(Object item) {
        if (item instanceof Customer customer) {
            return customer;
        }

        throw new IllegalArgumentException("Expected a Customer item");
    }
}
