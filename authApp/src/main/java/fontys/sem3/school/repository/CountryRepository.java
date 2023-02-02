package fontys.sem3.school.repository;

import fontys.sem3.school.repository.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    boolean existsByCode(String code);
}
