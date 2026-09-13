package expert.os.demos.ecommerce;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.math.BigDecimal;

@Entity
public class Customer {

    @Id
    private String id;

    @Column
    private String name;

    @Column
    private BigDecimal totalSpent;

    @Column
    private CustomerTier tier;
}
