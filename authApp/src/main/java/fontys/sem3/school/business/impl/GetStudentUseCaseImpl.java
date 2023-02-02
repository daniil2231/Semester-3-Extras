package fontys.sem3.school.business.impl;

import fontys.sem3.school.business.GetStudentUseCase;
import fontys.sem3.school.business.exception.UnauthorizedDataAccessException;
import fontys.sem3.school.domain.AccessToken;
import fontys.sem3.school.domain.Student;
import fontys.sem3.school.repository.StudentRepository;
import fontys.sem3.school.repository.entity.RoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class GetStudentUseCaseImpl implements GetStudentUseCase {

    private StudentRepository studentRepository;
    private AccessToken requestAccessToken;

    @Override
    public Optional<Student> getStudent(long studentId) {
        if(!requestAccessToken.hasRole(RoleEnum.ADMIN.name())) {
            if(requestAccessToken.getStudentId() != studentId) {
                throw new UnauthorizedDataAccessException("STUDENT_ID_NOT_FROM_LOGGED_IN_USER");
            }
        }
        return studentRepository.findById(studentId).map(StudentDTOConverter::convertToDTO);
    }
}
