package expert.os.demos.ecommerce.web;

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
import java.math.BigDecimal;
import java.util.List;

@Named
@FlowScoped("customer-segmentation")
public class CustomerSegmentationFlow implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private CustomerSegmentationService segmentationService;

    private List<ThresholdInput> thresholds;

    @PostConstruct
    public void initialize() {
        thresholds = segmentationService.currentThresholds().thresholds().stream()
                .map(ThresholdInput::new)
                .toList();
    }

    public String review() {
        try {
            SegmentationThresholds validated = validatedThresholds();
            thresholds = validated.thresholds().stream()
                    .map(ThresholdInput::new)
                    .toList();
            return "review";
        } catch (IllegalArgumentException exception) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid thresholds",
                            exception.getMessage()));
            return null;
        }
    }

    public String execute() {
        try {
            SegmentationThresholds validated = validatedThresholds();
            long executionId = segmentationService.start(validated);

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Segmentation started",
                            "Batch execution %d started successfully".formatted(executionId)));

            return "home";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Unable to start segmentation",
                            exception.getMessage()));
            return null;
        }
    }

    public List<ThresholdInput> getThresholds() {
        return thresholds;
    }

    private SegmentationThresholds validatedThresholds() {
        List<SegmentationThreshold> values = thresholds.stream()
                .map(ThresholdInput::toThreshold)
                .toList();
        return new SegmentationThresholds(values);
    }

    public static class ThresholdInput implements Serializable {

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
}
