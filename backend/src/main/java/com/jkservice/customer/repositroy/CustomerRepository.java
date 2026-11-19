package com.jkservice.customer.repository;

import com.jkservice.customer.entity.CustomerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

  Optional<CustomerEntity> findByIdAndBusinessId(Long customerId, Long businessId);

  boolean existsByBusinessIdAndCustomerCode(Long businessId, String customerCode);
}
