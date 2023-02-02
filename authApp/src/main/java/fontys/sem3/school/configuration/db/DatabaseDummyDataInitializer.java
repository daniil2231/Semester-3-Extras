package fontys.sem3.school.configuration.db;

import fontys.sem3.school.business.CreateStudentUseCase;
import fontys.sem3.school.domain.CreateStudentRequest;
import fontys.sem3.school.repository.CountryRepository;
import fontys.sem3.school.repository.UserRepository;
import fontys.sem3.school.repository.entity.CountryEntity;
import fontys.sem3.school.repository.entity.RoleEnum;
import fontys.sem3.school.repository.entity.UserEntity;
import fontys.sem3.school.repository.entity.UserRoleEntity;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Set;

@Component
@AllArgsConstructor
public class DatabaseDummyDataInitializer {

    private CountryRepository countryRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private CreateStudentUseCase createStudentUseCase;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void populateDatabaseInitialDummyData() {
        if (isDatabaseEmpty()) {
            insertSomeCountries();
            insertAdminUser();
            insertStudent();
        }
    }

    private boolean isDatabaseEmpty() {
        return countryRepository.count() == 0;
    }

    private void insertAdminUser() {
        UserEntity adminUser = UserEntity.builder()
                .username("admin@fontys.nl")
                .password(passwordEncoder.encode("test123"))
                .build();
        UserRoleEntity adminRole = UserRoleEntity.builder().role(RoleEnum.ADMIN).user(adminUser).build();
        adminUser.setUserRoles(Set.of(adminRole));
        userRepository.save(adminUser);
    }

    private void insertStudent() {
        CreateStudentRequest createStudentRequest = CreateStudentRequest.builder()
                .pcn(1234L)
                .password("test123")
                .name("Linda")
                .countryId(1L)
                .build();
        createStudentUseCase.createStudent(createStudentRequest);
    }

    private void insertSomeCountries() {
        countryRepository.save(CountryEntity.builder().code("NL").name("Netherlands").build());
        countryRepository.save(CountryEntity.builder().code("BG").name("Bulgaria").build());
        countryRepository.save(CountryEntity.builder().code("RO").name("Romania").build());
        countryRepository.save(CountryEntity.builder().code("BR").name("Brazil").build());
        countryRepository.save(CountryEntity.builder().code("CN").name("China").build());
    }
}
