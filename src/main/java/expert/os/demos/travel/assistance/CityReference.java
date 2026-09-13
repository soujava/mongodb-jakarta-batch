package expert.os.demos.travel.assistance;

import jakarta.nosql.Column;
import jakarta.nosql.Embeddable;

import java.util.UUID;

@Embeddable(Embeddable.EmbeddableType.GROUPING)
public record CityReference(@Column UUID id, @Column String name) {
}