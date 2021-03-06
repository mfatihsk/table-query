package com.isik.rest.jpa.test.repos;

import com.isik.rest.jpa.repository.RestJpaRepository;
import com.isik.rest.jpa.test.models.Office;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository  extends RestJpaRepository<Office, String> {
}
