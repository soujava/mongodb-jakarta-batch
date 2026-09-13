package expert.os.demos.travel.assistance;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.UUID;

@Entity
public class Attraction {

    @Id
    private UUID id;

    @Column
    private CityReference city;

    @Column
    private String name;

    @Column
    private AttractionType type;

    @Column
    private String description;

    Attraction() {
    }

     public Attraction(UUID id, CityReference city, String name, AttractionType type, String description) {
        this.id = id;
        this.city = city;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public CityReference getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public AttractionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
