package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerTier;
import expert.os.demos.ecommerce.batch.SegmentationThreshold;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class ThresholdInput implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private CustomerTier tier;
    private BigDecimal minimumValue;

    public ThresholdInput() {
    }

    ThresholdInput(SegmentationThreshold threshold) {
        tier = threshold.tier();
        minimumValue = threshold.minimumValue();
    }

    public CustomerTier getTier() {
        return tier;
    }

    public BigDecimal getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(BigDecimal minimumValue) {
        this.minimumValue = minimumValue;
    }

    SegmentationThreshold toThreshold() {
        return new SegmentationThreshold(minimumValue, tier);
    }
}
