package expert.os.demos.ecommerce.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.flow.FlowScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Named
@FlowScoped("customer-segmentation")
public class CustomerSegmentationFlow implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private CustomerSegmentationFlowService flowService;

    private CustomerSegmentationFlowState state;

    public CustomerSegmentationFlow() {
    }

    CustomerSegmentationFlow(CustomerSegmentationFlowService flowService) {
        this.flowService = Objects.requireNonNull(flowService);
    }

    @PostConstruct
    public void initialize() {
        state = flowService.initializeState();
    }

    public String preview() {
        try {
            flowService.preview(state);
            return "preview";
        } catch (IllegalArgumentException | IllegalStateException exception) {
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
            long executionId = flowService.start(state);

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
