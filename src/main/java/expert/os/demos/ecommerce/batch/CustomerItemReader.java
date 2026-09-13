package expert.os.demos.ecommerce.batch;

import expert.os.demos.ecommerce.Customer;
import expert.os.demos.ecommerce.CustomerRepository;
import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Named("customerItemReader")
@Dependent
public class CustomerItemReader extends AbstractItemReader {

    private static final Logger LOGGER = Logger.getLogger(CustomerItemReader.class.getName());

    @Inject
    private CustomerRepository customerRepository;

    private List<Customer> customers = List.of();
    private int nextIndex;

    @Override
    public void open(Serializable checkpoint) {
        try (Stream<Customer> customerStream = customerRepository.findAll()) {
            customers = customerStream
                    .sorted(Comparator.comparing(Customer::getId))
                    .toList();
        }

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
