package expert.os.demos.travel.assistance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CityService {


    private final CityRepository cityRepository;

    @Inject
    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

     CityService() {
        this.cityRepository = null;
    }

    public List<City> findByCountry(String country) {
        return cityRepository.findByCountry(country);
    }

    public List<City> findAll() {
        return cityRepository.findAll().toList();
    }

    public City save(City city) {
        this.cityRepository.save(city);
        return city;
    }
}
