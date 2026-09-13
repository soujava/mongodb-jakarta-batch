package expert.os.demos.ecommerce.web;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomerSegmentationFlowState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<ThresholdInput> thresholds = List.of();
    private List<TierComparison> comparisons = List.of();
    private long currentTotal;
    private long projectedTotal;

    public List<ThresholdInput> getThresholds() {
        return thresholds;
    }

    void setThresholds(List<ThresholdInput> thresholds) {
        this.thresholds = List.copyOf(thresholds);
    }

    public List<TierComparison> getComparisons() {
        return comparisons;
    }

    public long getCurrentTotal() {
        return currentTotal;
    }

    public long getProjectedTotal() {
        return projectedTotal;
    }

    void setComparison(
            List<TierComparison> comparisons,
            long currentTotal,
            long projectedTotal) {
        this.comparisons = List.copyOf(comparisons);
        this.currentTotal = currentTotal;
        this.projectedTotal = projectedTotal;
    }
}
