package expert.os.demos.travel.assistance.web;


import expert.os.demos.travel.assistance.DataLoader;
import expert.os.demos.travel.assistance.ai.TravelService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ViewScoped
public class TravelBean implements Serializable {

    @Inject
    private TravelService travelService;

    @Inject
    private DataLoader dataLoader;

    private String userMessage;

    private String answer;

    @PostConstruct
    public void init() {
        SSLBypass.disableSslVerification();
        dataLoader.load();
    }

    public void send() {
        if (userMessage == null || userMessage.isBlank()) {
            return;
        }
        answer = travelService.chat(userMessage);
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getAnswer() {
        return answer;
    }

    public void availableCities() {
        this.userMessage = "Show me available cities to travel";
    }

    public void historicalTour() {
        this.userMessage = "Create a historical itinerary in Portugal";
    }

    public void museumWeekend() {
        this.userMessage = "Create a museum-focused trip in Europe";
    }

    public void foodAndCulture() {
        this.userMessage = "Create a food and culture itinerary";
    }

}