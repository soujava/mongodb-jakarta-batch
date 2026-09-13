package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerDataService;
import expert.os.demos.ecommerce.CustomerTier;
import expert.os.demos.ecommerce.batch.CustomerSegmentationPolicy;
import expert.os.demos.ecommerce.batch.CustomerSegmentationService;
import expert.os.demos.ecommerce.batch.SegmentationThreshold;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class CustomerSegmentationFlowService {

    @Inject
    private CustomerSegmentationService segmentationService;

    @Inject
    private CustomerDataService customerDataService;

    public CustomerSegmentationFlowService() {
    }

    CustomerSegmentationFlowService(
            CustomerSegmentationService segmentationService,
            CustomerDataService customerDataService) {
        this.segmentationService = Objects.requireNonNull(segmentationService);
        this.customerDataService = Objects.requireNonNull(customerDataService);
    }

    public CustomerSegmentationFlowState initializeState() {
        CustomerSegmentationFlowState state = new CustomerSegmentationFlowState();
        state.setThresholds(toInputs(segmentationService.currentPolicy()));
        return state;
    }

    public void preview(CustomerSegmentationFlowState state) {
        CustomerSegmentationPolicy policy = validatedPolicy(state);
        state.setThresholds(toInputs(policy));

        CustomerDataService.SegmentationPreview preview =
                customerDataService.segmentationPreview(policy);
        List<TierComparison> comparisons = Arrays.stream(CustomerTier.values())
                .map(tier -> new TierComparison(
                        tier,
                        count(preview.current(), tier),
                        percentage(preview.current(), tier),
                        count(preview.projected(), tier),
                        percentage(preview.projected(), tier)))
                .toList();

        state.setComparison(
                comparisons,
                preview.current().totalCustomers(),
                preview.projected().totalCustomers());
    }

    public long start(CustomerSegmentationFlowState state) {
        return segmentationService.start(validatedPolicy(state));
    }

    private CustomerSegmentationPolicy validatedPolicy(
            CustomerSegmentationFlowState state) {

        List<SegmentationThreshold> thresholds = state.getThresholds().stream()
                .map(ThresholdInput::toThreshold)
                .toList();
        return new CustomerSegmentationPolicy(thresholds);
    }

    private List<ThresholdInput> toInputs(CustomerSegmentationPolicy policy) {
        return policy.thresholds().stream()
                .map(ThresholdInput::new)
                .toList();
    }

    private long count(
            CustomerDataService.CustomerStatistics statistics,
            CustomerTier tier) {
        return statistics.tierCounts().getOrDefault(tier, 0L);
    }

    private double percentage(
            CustomerDataService.CustomerStatistics statistics,
            CustomerTier tier) {

        if (statistics.totalCustomers() == 0) {
            return 0;
        }

        return count(statistics, tier) * 100.0 / statistics.totalCustomers();
    }
}
