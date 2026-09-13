package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerTier;

import java.io.Serial;
import java.io.Serializable;

public class TierComparison implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final CustomerTier tier;
    private final long currentCount;
    private final double currentPercentage;
    private final long projectedCount;
    private final double projectedPercentage;

    public TierComparison(
            CustomerTier tier,
            long currentCount,
            double currentPercentage,
            long projectedCount,
            double projectedPercentage) {
        this.tier = tier;
        this.currentCount = currentCount;
        this.currentPercentage = currentPercentage;
        this.projectedCount = projectedCount;
        this.projectedPercentage = projectedPercentage;
    }

    public CustomerTier getTier() {
        return tier;
    }

    public long getCurrentCount() {
        return currentCount;
    }

    public double getCurrentPercentage() {
        return currentPercentage;
    }

    public long getProjectedCount() {
        return projectedCount;
    }

    public double getProjectedPercentage() {
        return projectedPercentage;
    }

    public long getChange() {
        return projectedCount - currentCount;
    }

    public String getFormattedChange() {
        long change = getChange();
        return change > 0 ? "+" + change : Long.toString(change);
    }

    public String getChangeStyleClass() {
        return getChange() > 0
                ? "change-positive"
                : getChange() < 0 ? "change-negative" : "change-neutral";
    }
}
