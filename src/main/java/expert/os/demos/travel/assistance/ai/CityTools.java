package expert.os.demos.travel.assistance.ai;

import dev.langchain4j.agent.tool.Tool;
import expert.os.demos.travel.assistance.City;
import expert.os.demos.travel.assistance.CityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class CityTools {

    private static final Logger LOGGER = Logger.getLogger(CityTools.class.getName());

    @Inject
    private CityService service;


    @Tool("""
            Find cities available in a country.
            Use this tool when you need to discover destinations before creating a travel itinerary.
            """)
    public List<City> citiesByCountry(String country) {

        LOGGER.info(() -> "[TOOL] citiesByCountry country=%s"
                .formatted(country));

        List<City> cities = service.findByCountry(country);

        LOGGER.info(() -> "[TOOL] citiesByCountry resultCount=%d cities=%s"
                .formatted(
                        cities.size(),
                        cities.stream()
                                .map(City::getName)
                                .toList()
                ));

        return cities;
    }

    @Tool("""
            List all available cities.
            Use this tool when you need to explore destinations without any country restriction.
            """)
    public List<City> cities() {

        List<City> cities = service.findAll();
        LOGGER.info(() -> "[TOOL] cities resultCount=%d cities=%s"
                .formatted(
                        cities.size(),
                        cities.stream()
                                .map(City::getName)
                                .toList()
                ));

        return cities;
    }
}