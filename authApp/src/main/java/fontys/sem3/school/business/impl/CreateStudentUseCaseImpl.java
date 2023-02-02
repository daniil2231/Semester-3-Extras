package fontys.sem3.school.business.impl;

import fontys.sem3.school.business.CountryIdValidator;
import fontys.sem3.school.business.CreateStudentUseCase;
import fontys.sem3.school.business.exception.PcnAlreadyExistsException;
import fontys.sem3.school.domain.CreateStudentRequest;
import fontys.sem3.school.domain.CreateStudentResponse;
import fontys.sem3.school.repository.StudentRepository;
import fontys.sem3.school.repository.UserRepository;
import fontys.sem3.school.repository.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreateStudentUseCaseImpl implements CreateStudentUseCase {
    private static final String USERNAME_SUFFIX = "@fontys.nl";

    private final StudentRepository studentRepository;
    private final CountryIdValidator countryIdValidator;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public CreateStudentResponse createStudent(CreateStudentRequest request) {
        if (studentRepository.existsByPcn(request.getPcn())) {
            throw new PcnAlreadyExistsException();
        }

        countryIdValidator.validateId(request.getCountryId());

        StudentEntity savedStudent = saveNewStudent(request);

        saveNewUser(savedStudent, request.getPassword());

        return CreateStudentResponse.builder()
                .studentId(savedStudent.getId())
                .build();
    }

    private void saveNewUser(StudentEntity student, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        UserEntity newUser = UserEntity.builder()
                .username(student.getPcn().toString() + USERNAME_SUFFIX)
                .password(encodedPassword)
                .student(student)
                .build();

        newUser.setUserRoles(Set.of(
                UserRoleEntity.builder()
                        .user(newUser)
                        .role(RoleEnum.STUDENT)
                        .build()));
        userRepository.save(newUser);
    }

    private StudentEntity saveNewStudent(CreateStudentRequest request) {
        StudentEntity newStudent = StudentEntity.builder()
                .country(CountryEntity.builder().id(request.getCountryId()).build())
                .name(request.getName())
                .pcn(request.getPcn())
                .build();
        return studentRepository.save(newStudent);
    }
}
