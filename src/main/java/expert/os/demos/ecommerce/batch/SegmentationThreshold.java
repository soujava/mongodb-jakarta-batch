package expert.os.demos.ecommerce.batch;

import expert.os.demos.ecommerce.CustomerTier;

import java.math.BigDecimal;
import java.util.Objects;

public record SegmentationThreshold(
        BigDecimal minimumValue,
        CustomerTier tier) {

    public SegmentationThreshold {
        Objects.requireNonNull(minimumValue, "minimumValue is required");
        Objects.requireNonNull(tier, "tier is required");

        if (minimumValue.signum() < 0) {
            throw new IllegalArgumentException("minimumValue must not be negative");
        }
    }
}