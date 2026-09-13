package expert.os.demos.travel.assistance;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CityRepository extends BasicRepository<City, UUID> {

    List<City> findByCountry(String country);
}
