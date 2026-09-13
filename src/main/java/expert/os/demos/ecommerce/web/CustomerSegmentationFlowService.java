package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerDataService;
import expert.os.demos.ecommerce.CustomerTier;
import expert.os.demos.ecommerce.batch.CustomerSegmentationService;
import expert.os.demos.ecommerce.batch.SegmentationThreshold;
import expert.os.demos.ecommerce.batch.SegmentationThresholds;
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
        state.setThresholds(toInputs(segmentationService.currentThresholds()));
        return state;
    }

    public void preview(CustomerSegmentationFlowState state) {
        SegmentationThresholds thresholds = validatedThresholds(state);
        state.setThresholds(toInputs(thresholds));

        CustomerDataService.SegmentationPreview preview =
                customerDataService.segmentationPreview(thresholds);
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
        return segmentationService.start(validatedThresholds(state));
    }

    private SegmentationThresholds validatedThresholds(
            CustomerSegmentationFlowState state) {

        List<SegmentationThreshold> thresholds = state.getThresholds().stream()
                .map(ThresholdInput::toThreshold)
                .toList();
        return new SegmentationThresholds(thresholds);
    }

    private List<ThresholdInput> toInputs(SegmentationThresholds thresholds) {
        return thresholds.thresholds().stream()
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
