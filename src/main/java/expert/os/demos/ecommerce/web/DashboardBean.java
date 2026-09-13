package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerDataService;
import expert.os.demos.ecommerce.CustomerTier;
import expert.os.demos.ecommerce.batch.CustomerSegmentationService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private CustomerDataService customerDataService;

    @Inject
    private CustomerSegmentationService segmentationService;

    private Map<CustomerTier, Long> tierCounts = new EnumMap<>(CustomerTier.class);
    private long totalCustomers;
    private String latestBatchStatus;
    private boolean segmentationRunning;

    @PostConstruct
    public void initialize() {
        customerDataService.initializeIfEmpty();
        refresh();
    }

    public void refresh() {
        tierCounts = new EnumMap<>(customerDataService.countByTier());
        totalCustomers = tierCounts.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        latestBatchStatus = segmentationService.latestStatus()
                .map(Enum::name)
                .orElse("NOT_STARTED");
        segmentationRunning = segmentationService.isRunning();
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
