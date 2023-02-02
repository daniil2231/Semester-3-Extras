package fontys.sem3.school.business.impl;

import fontys.sem3.school.domain.Student;
import fontys.sem3.school.repository.entity.StudentEntity;

final class StudentDTOConverter {
    private StudentDTOConverter() {
    }

    public static Student convertToDTO(StudentEntity student) {
        return Student.builder()
                .id(student.getId())
                .pcn(student.getPcn())
                .name(student.getName())
                .country(CountryDTOConverter.convertToDTO(student.getCountry()))
                .build();
    }
}
