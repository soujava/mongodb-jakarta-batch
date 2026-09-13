package expert.os.demos.ecommerce.batch;

import expert.os.demos.ecommerce.CustomerTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Segmentation thresholds")
class SegmentationThresholdsTest {

    @Nested
    @DisplayName("Given valid thresholds")
    class ValidThresholds {

        private final SegmentationThresholds thresholds = thresholdsInOrder();

        @Test
        @DisplayName("When spending is in the Bronze range, then Bronze is selected")
        void shouldCalculateBronzeTier() {
            assertEquals(CustomerTier.BRONZE, thresholds.tierFor(new BigDecimal("500")));
        }

        @Test
        @DisplayName("When spending is in the Silver range, then Silver is selected")
        void shouldCalculateSilverTier() {
            assertEquals(CustomerTier.SILVER, thresholds.tierFor(new BigDecimal("2500")));
        }

        @Test
        @DisplayName("When spending is in the Gold range, then Gold is selected")
        void shouldCalculateGoldTier() {
            assertEquals(CustomerTier.GOLD, thresholds.tierFor(new BigDecimal("7500")));
        }

        @Test
        @DisplayName("When spending is in the Platinum range, then Platinum is selected")
        void shouldCalculatePlatinumTier() {
            assertEquals(CustomerTier.PLATINUM, thresholds.tierFor(new BigDecimal("15000")));
        }

        @ParameterizedTest(name = "{0} starts at {1}")
        @MethodSource("expert.os.demos.ecommerce.batch.SegmentationThresholdsTest#thresholdBoundaries")
        @DisplayName("When spending equals a threshold, then its tier is selected")
        void shouldRespectExactThresholdBoundaries(CustomerTier tier, String value) {
            assertEquals(tier, thresholds.tierFor(new BigDecimal(value)));
        }

        @Test
        @DisplayName("When thresholds are unordered, then calculation is unchanged")
        void shouldIgnoreInputOrdering() {
            SegmentationThresholds unordered = new SegmentationThresholds(List.of(
                    threshold("10000", CustomerTier.PLATINUM),
                    threshold("10", CustomerTier.BRONZE),
                    threshold("5000", CustomerTier.GOLD),
                    threshold("1000", CustomerTier.SILVER)
            ));

            assertEquals(CustomerTier.GOLD, unordered.tierFor(new BigDecimal("7500")));
        }
    }

    @Nested
    @DisplayName("Given invalid thresholds")
    class InvalidThresholds {

        @Test
        @DisplayName("When minimum values are duplicated, then configuration fails")
        void shouldRejectDuplicateMinimumValues() {
            List<SegmentationThreshold> values = List.of(
                    threshold("10", CustomerTier.BRONZE),
                    threshold("10.00", CustomerTier.SILVER)
            );

            assertThrows(IllegalArgumentException.class,
                    () -> new SegmentationThresholds(values));
        }

        @Test
        @DisplayName("When tiers do not increase with values, then configuration fails")
        void shouldRejectInconsistentTierOrder() {
            List<SegmentationThreshold> values = List.of(
                    threshold("10", CustomerTier.SILVER),
                    threshold("1000", CustomerTier.BRONZE)
            );

            assertThrows(IllegalArgumentException.class,
                    () -> new SegmentationThresholds(values));
        }
    }

    private static Stream<Arguments> thresholdBoundaries() {
        return Stream.of(
                Arguments.of(CustomerTier.BRONZE, "10"),
                Arguments.of(CustomerTier.SILVER, "1000"),
                Arguments.of(CustomerTier.GOLD, "5000"),
                Arguments.of(CustomerTier.PLATINUM, "10000")
        );
    }

    private static SegmentationThresholds thresholdsInOrder() {
        return new SegmentationThresholds(List.of(
                threshold("10", CustomerTier.BRONZE),
                threshold("1000", CustomerTier.SILVER),
                threshold("5000", CustomerTier.GOLD),
                threshold("10000", CustomerTier.PLATINUM)
        ));
    }

    private static SegmentationThreshold threshold(String value, CustomerTier tier) {
        return new SegmentationThreshold(new BigDecimal(value), tier);
    }
}
