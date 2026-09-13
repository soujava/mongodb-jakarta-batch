package expert.os.demos.ecommerce.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private DashboardService dashboardService;

    private DashboardState state;


    @PostConstruct
    public void initialize() {
        state = dashboardService.initializeState();
    }

    public void refresh() {
        state = dashboardService.currentState();
    }

    public DashboardState getState() {
        return state;
    }
}
