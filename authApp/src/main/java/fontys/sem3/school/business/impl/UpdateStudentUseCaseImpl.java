package fontys.sem3.school.business.impl;

import fontys.sem3.school.business.CountryIdValidator;
import fontys.sem3.school.business.UpdateStudentUseCase;
import fontys.sem3.school.business.exception.InvalidStudentException;
import fontys.sem3.school.business.exception.UnauthorizedDataAccessException;
import fontys.sem3.school.domain.AccessToken;
import fontys.sem3.school.domain.UpdateStudentRequest;
import fontys.sem3.school.repository.StudentRepository;
import fontys.sem3.school.repository.entity.CountryEntity;
import fontys.sem3.school.repository.entity.RoleEnum;
import fontys.sem3.school.repository.entity.StudentEntity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UpdateStudentUseCaseImpl implements UpdateStudentUseCase {
    private final StudentRepository studentRepository;
    private final CountryIdValidator countryIdValidator;

    private AccessToken requestAccessToken;

    @Transactional
    @Override
    public void updateStudent(UpdateStudentRequest request) {
        if(!requestAccessToken.hasRole(RoleEnum.ADMIN.name())) {
            if(requestAccessToken.getStudentId() != request.getId()) {
                throw new UnauthorizedDataAccessException("STUDENT_ID_NOT_FROM_LOGGED_IN_USER");
            }
        }

        Optional<StudentEntity> studentOptional = studentRepository.findById(request.getId());
        if (studentOptional.isEmpty()) {
            throw new InvalidStudentException("STUDENT_ID_INVALID");
        }

        countryIdValidator.validateId(request.getCountryId());

        StudentEntity student = studentOptional.get();
        updateFields(request, student);
    }

    private void updateFields(UpdateStudentRequest request, StudentEntity student) {
        student.setCountry(CountryEntity.builder().id(request.getCountryId()).build());
        student.setName(request.getName());

        studentRepository.save(student);
    }
}
