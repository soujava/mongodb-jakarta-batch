package expert.os.demos.ecommerce;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;

@Repository
public interface CustomerRepository extends BasicRepository<Customer, String> {
}
