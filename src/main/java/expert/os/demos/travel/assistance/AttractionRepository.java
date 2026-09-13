package expert.os.demos.travel.assistance;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttractionRepository extends BasicRepository<Attraction, UUID> {

    @Query("WHERE city.name = :name")
    List<Attraction> findByCityName(@Param("name") String name);

    @Query("WHERE city.name = :name AND type = :type")
    List<Attraction> findByCityNameAndType(@Param("name") String city, @Param("type") AttractionType type);
}
