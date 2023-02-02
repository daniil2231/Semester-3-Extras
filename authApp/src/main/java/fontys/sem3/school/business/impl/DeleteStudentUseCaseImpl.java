package fontys.sem3.school.business.impl;

import fontys.sem3.school.business.DeleteStudentUseCase;
import fontys.sem3.school.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteStudentUseCaseImpl implements DeleteStudentUseCase {
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void deleteStudent(long studentId) {
        this.studentRepository.deleteById(studentId);
    }
}
