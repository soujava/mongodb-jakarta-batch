package expert.os.demos.ecommerce.batch;

import expert.os.demos.ecommerce.CustomerTier;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record SegmentationThresholds(
        List<SegmentationThreshold> thresholds) {

    public static final String JOB_PARAMETER = "thresholds";

    public SegmentationThresholds {
        Objects.requireNonNull(thresholds, "thresholds are required");

        if (thresholds.isEmpty()) {
            throw new IllegalArgumentException("At least one segmentation threshold is required");
        }

        List<SegmentationThreshold> orderedThresholds = thresholds.stream()
                .map(threshold -> Objects.requireNonNull(threshold, "threshold must not be null"))
                .sorted(Comparator.comparing(SegmentationThreshold::minimumValue))
                .toList();

        validateUniqueMinimumValues(orderedThresholds);
        validateUniqueTiers(orderedThresholds);
        validateTierOrder(orderedThresholds);

        thresholds = List.copyOf(orderedThresholds);
    }

    public static SegmentationThresholds fromJson(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException(
                    "Job parameter '%s' is required".formatted(JOB_PARAMETER));
        }

        try {
            Jsonb jsonb = JsonbBuilder.create();
            SegmentationThreshold[] values =
                    jsonb.fromJson(json, SegmentationThreshold[].class);

            if (values == null) {
                throw new IllegalArgumentException(
                        "Job parameter '%s' must contain a threshold array"
                                .formatted(JOB_PARAMETER));
            }

            return new SegmentationThresholds(List.of(values));
        } catch (JsonbException exception) {
            throw new IllegalArgumentException("Invalid segmentation thresholds JSON", exception);
        }
    }

    public CustomerTier tierFor(BigDecimal totalSpent) {
        Objects.requireNonNull(totalSpent, "totalSpent is required");

        if (totalSpent.signum() < 0) {
            throw new IllegalArgumentException("totalSpent must not be negative");
        }

        return thresholds.stream()
                .filter(threshold -> totalSpent.compareTo(threshold.minimumValue()) >= 0)
                .max(Comparator.comparing(SegmentationThreshold::minimumValue))
                .map(SegmentationThreshold::tier)
                .orElseGet(() -> thresholds.getFirst().tier());
    }

    private static void validateUniqueMinimumValues(List<SegmentationThreshold> thresholds) {
        Set<BigDecimal> minimumValues = new HashSet<>();

        for (SegmentationThreshold threshold : thresholds) {
            BigDecimal normalizedValue = threshold.minimumValue().stripTrailingZeros();
            if (!minimumValues.add(normalizedValue)) {
                throw new IllegalArgumentException(
                        "Duplicate minimumValue: " + threshold.minimumValue());
            }
        }
    }

    private static void validateUniqueTiers(List<SegmentationThreshold> thresholds) {
        Set<CustomerTier> tiers = new HashSet<>();

        for (SegmentationThreshold threshold : thresholds) {
            if (!tiers.add(threshold.tier())) {
                throw new IllegalArgumentException("Duplicate tier: " + threshold.tier());
            }
        }
    }

    private static void validateTierOrder(List<SegmentationThreshold> thresholds) {
        for (int index = 1; index < thresholds.size(); index++) {
            CustomerTier previous = thresholds.get(index - 1).tier();
            CustomerTier current = thresholds.get(index).tier();

            if (previous.compareTo(current) >= 0) {
                throw new IllegalArgumentException(
                        "Tiers must increase as minimumValue increases");
            }
        }
    }
}