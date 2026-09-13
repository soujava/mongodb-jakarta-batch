package expert.os.demos.ecommerce;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.math.BigDecimal;
import java.util.Objects;

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

    public Customer() {
    }

    Customer(String id, String name, BigDecimal totalSpent, CustomerTier tier) {
        this.id = id;
        this.name = name;
        this.totalSpent = totalSpent;
        this.tier = tier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public CustomerTier getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Customer customer)) {
            return false;
        }
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", totalSpent=" + totalSpent +
                ", tier=" + tier +
                '}';
    }

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }
}
