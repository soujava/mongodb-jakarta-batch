package expert.os.demos.ecommerce.web;

import expert.os.demos.ecommerce.CustomerDataService;
import expert.os.demos.ecommerce.CustomerTier;
import expert.os.demos.ecommerce.batch.CustomerSegmentationService;
import expert.os.demos.ecommerce.batch.SegmentationThreshold;
import expert.os.demos.ecommerce.batch.SegmentationThresholds;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.flow.FlowScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Named
@FlowScoped("customer-segmentation")
public class CustomerSegmentationFlow implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private CustomerSegmentationService segmentationService;

    @Inject
    private CustomerDataService customerDataService;

    private final CustomerSegmentationFlowState state =
            new CustomerSegmentationFlowState();

    public CustomerSegmentationFlow() {
    }

    CustomerSegmentationFlow(
            CustomerSegmentationService segmentationService,
            CustomerDataService customerDataService) {
        this.segmentationService = Objects.requireNonNull(segmentationService);
        this.customerDataService = Objects.requireNonNull(customerDataService);
    }

    @PostConstruct
    public void initialize() {
        List<ThresholdInput> thresholds =
                segmentationService.currentThresholds().thresholds().stream()
                .map(ThresholdInput::new)
                .toList();
        state.setThresholds(thresholds);
    }

    public String preview() {
        try {
            SegmentationThresholds validated = validatedThresholds();
            List<ThresholdInput> thresholds = validated.thresholds().stream()
                    .map(ThresholdInput::new)
                    .toList();
            state.setThresholds(thresholds);

            CustomerDataService.CustomerStatistics statistics =
                    customerDataService.previewSegmentation(validated);
            List<TierPreview> preview = Arrays.stream(CustomerTier.values())
                    .map(tier -> new TierPreview(
                            tier,
                            statistics.tierCounts().getOrDefault(tier, 0L),
                            percentage(statistics, tier)))
                    .toList();
            state.setPreview(preview, statistics.totalCustomers());

            return "preview";
        } catch (IllegalArgumentException exception) {
            addMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Invalid thresholds",
                    exception.getMessage());
            return null;
        }
    }

    public String review() {
        return "review";
    }

    public String execute() {
        try {
            SegmentationThresholds validated = validatedThresholds();
            long executionId = segmentationService.start(validated);

            addMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Segmentation started",
                    "Batch execution %d started successfully".formatted(executionId),
                    true);

            return "home";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            addMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Unable to start segmentation",
                    exception.getMessage());
            return null;
        }
    }

    public CustomerSegmentationFlowState getState() {
        return state;
    }

    private SegmentationThresholds validatedThresholds() {
        List<SegmentationThreshold> values = state.getThresholds().stream()
                .map(ThresholdInput::toThreshold)
                .toList();
        return new SegmentationThresholds(values);
    }

    private double percentage(
            CustomerDataService.CustomerStatistics statistics,
            CustomerTier tier) {

        if (statistics.totalCustomers() == 0) {
            return 0;
        }

        long count = statistics.tierCounts().getOrDefault(tier, 0L);
        return count * 100.0 / statistics.totalCustomers();
    }

    private void addMessage(
            FacesMessage.Severity severity,
            String summary,
            String detail) {

        addMessage(severity, summary, detail, false);
    }

    private void addMessage(
            FacesMessage.Severity severity,
            String summary,
            String detail,
            boolean keepAfterRedirect) {

        FacesContext context = currentFacesContext();
        if (context != null) {
            if (keepAfterRedirect) {
                context.getExternalContext().getFlash().setKeepMessages(true);
            }
            context.addMessage(null, new FacesMessage(severity, summary, detail));
        }
    }

    FacesContext currentFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
