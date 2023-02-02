package fontys.sem3.school.business.impl;

import fontys.sem3.school.business.GetStudentsUseCase;
import fontys.sem3.school.domain.GetAllStudentsRequest;
import fontys.sem3.school.domain.GetAllStudentsResponse;
import fontys.sem3.school.domain.Student;
import fontys.sem3.school.repository.StudentRepository;
import fontys.sem3.school.repository.entity.StudentEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class GetStudentsUseCaseImpl implements GetStudentsUseCase {
    private StudentRepository studentRepository;

    @Override
    public GetAllStudentsResponse getStudents(final GetAllStudentsRequest request) {
        List<StudentEntity> results;
        if (StringUtils.hasText(request.getCountryCode())) {
            results = studentRepository.findAllByCountry_CodeOrderByNameAsc(request.getCountryCode());
        } else {
            results = studentRepository.findAll();
        }

        final GetAllStudentsResponse response = new GetAllStudentsResponse();
        List<Student> studentsDTO = results
                .stream()
                .map(StudentDTOConverter::convertToDTO)
                .toList();
        response.setStudents(studentsDTO);

        return response;
    }
}
