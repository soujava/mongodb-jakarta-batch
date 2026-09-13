package expert.os.demos.travel.assistance;


import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class DataLoader {

    private static final Logger LOGGER = Logger.getLogger(DataLoader.class.getName());
    @Inject
    private CityService cityService;

    @Inject
    private AttractionService attractionService;

    @PostConstruct
    public void load() {

        LOGGER.info("Loading sample travel data");

        if (!cityService.findAll().isEmpty()) {
            LOGGER.info("Sample travel data already loaded");
            return;
        }

        City lisbon = cityService.save(new City(
                UUID.randomUUID(),
                "Lisbon",
                "Portugal",
                "Portugal's capital city."
        ));

        City porto = cityService.save(new City(
                UUID.randomUUID(),
                "Porto",
                "Portugal",
                "Historic city in northern Portugal."
        ));

        City paris = cityService.save(new City(
                UUID.randomUUID(),
                "Paris",
                "France",
                "Capital of France."
        ));

        City rome = cityService.save(new City(
                UUID.randomUUID(),
                "Rome",
                "Italy",
                "The Eternal City."
        ));

        loadAttractions(lisbon, porto, paris, rome);
        LOGGER.info("Sample travel data loaded successfully");

    }

    private void loadAttractions(
            City lisbon,
            City porto,
            City paris,
            City rome) {

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(lisbon.getId(), lisbon.getName()),
                "Belém Tower",
                AttractionType.HISTORICAL,
                "UNESCO World Heritage Site."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(lisbon.getId(), lisbon.getName()),
                "Jerónimos Monastery",
                AttractionType.RELIGIOUS,
                "Manueline monastery."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(lisbon.getId(), lisbon.getName()),
                "Alfama",
                AttractionType.ARCHITECTURE,
                "Historic neighborhood."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(porto.getId(), porto.getName()),
                "Ribeira",
                AttractionType.ARCHITECTURE,
                "Riverside district."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(porto.getId(), porto.getName()),
                "Livraria Lello",
                AttractionType.MUSEUM,
                "Historic bookstore."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(porto.getId(), porto.getName()),
                "Port Wine Cellars",
                AttractionType.FOOD,
                "Wine tasting experience."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(paris.getId(), paris.getName()),
                "Eiffel Tower",
                AttractionType.ARCHITECTURE,
                "Paris landmark."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(paris.getId(), paris.getName()),
                "Louvre Museum",
                AttractionType.MUSEUM,
                "World-famous museum."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(paris.getId(), paris.getName()),
                "Notre-Dame Cathedral",
                AttractionType.RELIGIOUS,
                "Gothic cathedral."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(rome.getId(), rome.getName()),
                "Colosseum",
                AttractionType.HISTORICAL,
                "Ancient amphitheater."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(rome.getId(), rome.getName()),
                "Roman Forum",
                AttractionType.HISTORICAL,
                "Center of ancient Rome."
        ));

        attractionService.save(new Attraction(
                UUID.randomUUID(),
                new CityReference(rome.getId(), rome.getName()),
                "Vatican Museums",
                AttractionType.MUSEUM,
                "Art and history collections."
        ));
    }
}