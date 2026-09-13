package expert.os.demos.ecommerce.batch;

import expert.os.demos.ecommerce.Customer;
import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.nosql.Template;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Named("customerItemReader")
@Dependent
public class CustomerItemReader extends AbstractItemReader {

    private static final Logger LOGGER = Logger.getLogger(CustomerItemReader.class.getName());

    @Inject
    private Template template;

    private List<Customer> customers = List.of();
    private int nextIndex;

    @Override
    public void open(Serializable checkpoint) {
        customers = template.select(Customer.class).result();
        nextIndex = checkpoint instanceof Integer index ? index : 0;

        if (nextIndex < 0 || nextIndex > customers.size()) {
            throw new IllegalArgumentException("Invalid customer reader checkpoint: " + nextIndex);
        }

        LOGGER.info(() -> "Customer reader opened: total=%d, nextIndex=%d"
                .formatted(customers.size(), nextIndex));
    }

    @Override
    public Customer readItem() {
        if (nextIndex >= customers.size()) {
            return null;
        }

        return customers.get(nextIndex++);
    }

    @Override
    public Serializable checkpointInfo() {
        return nextIndex;
    }
}
