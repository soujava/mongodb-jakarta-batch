package expert.os.demos.ecommerce;

import java.math.BigDecimal;

public class CustomerBuilder {
    private String id;
    private String name;
    private BigDecimal totalSpent;
    private CustomerTier tier;

    public CustomerBuilder id(String id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CustomerBuilder totalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
        return this;
    }

    public CustomerBuilder tier(CustomerTier tier) {
        this.tier = tier;
        return this;
    }

    public Customer build() {
        return new Customer(id, name, totalSpent, tier);
    }
}