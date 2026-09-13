package expert.os.demos.ecommerce;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AttractionService {


    private final AttractionRepository attractionRepository;

    @Inject
    public AttractionService(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

        AttractionService() {
            this.attractionRepository = null;
        }

    public List<Attraction> findByCity(String name) {
        return attractionRepository.findByCityName(name);
    }

    public Attraction save(Attraction attraction) {
        return attractionRepository.save(attraction);
    }

    public List<Attraction> findByType(String city, AttractionType type) {
        return attractionRepository.findByCityNameAndType(city, type);
    }
}
