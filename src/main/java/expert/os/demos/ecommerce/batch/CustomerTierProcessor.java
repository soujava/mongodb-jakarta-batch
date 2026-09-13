package expert.os.demos.ecommerce.batch;

import expert.os.demos.ecommerce.Customer;
import expert.os.demos.ecommerce.CustomerTier;
import jakarta.annotation.PostConstruct;
import jakarta.batch.api.BatchProperty;
import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Objects;

@Named("customerTierProcessor")
@Dependent
public class CustomerTierProcessor implements ItemProcessor {

    @Inject
    @BatchProperty(name = SegmentationThresholds.JOB_PARAMETER)
    private String thresholdsJson;

    private SegmentationThresholds thresholds;

    public CustomerTierProcessor() {
    }

    CustomerTierProcessor(SegmentationThresholds thresholds) {
        this.thresholds = Objects.requireNonNull(thresholds);
    }

    @PostConstruct
    void initialize() {
        thresholds = SegmentationThresholds.fromJson(thresholdsJson);
    }

    @Override
    public Customer processItem(Object item) {
        if (!(item instanceof Customer customer)) {
            throw new IllegalArgumentException("Expected a Customer item");
        }

        CustomerTier calculatedTier = thresholds.tierFor(customer.getTotalSpent());
        if (calculatedTier == customer.getTier()) {
            return null;
        }

        return Customer.builder()
                .id(customer.getId())
                .name(customer.getName())
                .totalSpent(customer.getTotalSpent())
                .tier(calculatedTier)
                .build();
    }
}
