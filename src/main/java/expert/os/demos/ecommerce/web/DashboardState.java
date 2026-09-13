package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerTier;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

public class DashboardState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<CustomerTier, Long> tierCounts;
    private final long totalCustomers;
    private final String latestBatchStatus;
    private final boolean segmentationRunning;

    public DashboardState(
            Map<CustomerTier, Long> tierCounts,
            long totalCustomers,
            String latestBatchStatus,
            boolean segmentationRunning) {
        this.tierCounts = new EnumMap<>(tierCounts);
        this.totalCustomers = totalCustomers;
        this.latestBatchStatus = latestBatchStatus;
        this.segmentationRunning = segmentationRunning;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public long getBronzeCount() {
        return count(CustomerTier.BRONZE);
    }

    public long getSilverCount() {
        return count(CustomerTier.SILVER);
    }

    public long getGoldCount() {
        return count(CustomerTier.GOLD);
    }

    public long getPlatinumCount() {
        return count(CustomerTier.PLATINUM);
    }

    public double getBronzePercentage() {
        return percentage(CustomerTier.BRONZE);
    }

    public double getSilverPercentage() {
        return percentage(CustomerTier.SILVER);
    }

    public double getGoldPercentage() {
        return percentage(CustomerTier.GOLD);
    }

    public double getPlatinumPercentage() {
        return percentage(CustomerTier.PLATINUM);
    }

    public String getLatestBatchStatus() {
        return latestBatchStatus;
    }

    public boolean isSegmentationRunning() {
        return segmentationRunning;
    }

    public String getTierDistributionChart() {
        return """
                {
                  type: 'doughnut',
                  data: {
                    labels: ['Bronze', 'Silver', 'Gold', 'Platinum'],
                    datasets: [{
                      data: [%d, %d, %d, %d],
                      backgroundColor: ['#a16207', '#94a3b8', '#eab308', '#6366f1']
                    }]
                  },
                  options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: {
                        position: 'bottom'
                      }
                    }
                  }
                }
                """.formatted(
                getBronzeCount(),
                getSilverCount(),
                getGoldCount(),
                getPlatinumCount());
    }

    private long count(CustomerTier tier) {
        return tierCounts.getOrDefault(tier, 0L);
    }

    private double percentage(CustomerTier tier) {
        if (totalCustomers == 0) {
            return 0;
        }

        return count(tier) * 100.0 / totalCustomers;
    }
}
