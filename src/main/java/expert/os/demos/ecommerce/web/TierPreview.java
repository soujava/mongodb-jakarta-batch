package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerTier;

import java.io.Serial;
import java.io.Serializable;

public class TierPreview implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final CustomerTier tier;
    private final long count;
    private final double percentage;

    public TierPreview(CustomerTier tier, long count, double percentage) {
        this.tier = tier;
        this.count = count;
        this.percentage = percentage;
    }

    public CustomerTier getTier() {
        return tier;
    }

    public long getCount() {
        return count;
    }

    public double getPercentage() {
        return percentage;
    }
}
