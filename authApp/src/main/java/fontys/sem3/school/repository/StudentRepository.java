package fontys.sem3.school.repository;

import fontys.sem3.school.repository.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    StudentEntity findByPcn(long pcn);

    boolean existsByPcn(long pcn);

    List<StudentEntity> findAllByCountry_CodeOrderByNameAsc(String countryCode);
}
