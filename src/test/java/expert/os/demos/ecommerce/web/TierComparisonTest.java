package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tier comparison")
class TierComparisonTest {

    @Test
    @DisplayName("When projected count increases, then change is positive")
    void shouldCalculatePositiveChange() {
        TierComparison comparison =
                new TierComparison(CustomerTier.GOLD, 25, 25, 27, 27);

        assertEquals(2, comparison.getChange());
        assertEquals("+2", comparison.getFormattedChange());
    }

    @Test
    @DisplayName("When projected count decreases, then change is negative")
    void shouldCalculateNegativeChange() {
        TierComparison comparison =
                new TierComparison(CustomerTier.BRONZE, 30, 30, 22, 22);

        assertEquals(-8, comparison.getChange());
        assertEquals("-8", comparison.getFormattedChange());
    }

    @Test
    @DisplayName("When projected count is unchanged, then change is zero")
    void shouldCalculateZeroChange() {
        TierComparison comparison =
                new TierComparison(CustomerTier.SILVER, 30, 30, 30, 30);

        assertEquals(0, comparison.getChange());
        assertEquals("0", comparison.getFormattedChange());
    }
}
