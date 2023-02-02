package fontys.sem3.school.controller;

import fontys.sem3.school.business.*;
import fontys.sem3.school.configuration.security.isauthenticated.IsAuthenticated;
import fontys.sem3.school.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentsController {
    private final GetStudentUseCase getStudentUseCase;
    private final GetStudentsUseCase getStudentsUseCase;
    private final DeleteStudentUseCase deleteStudentUseCase;
    private final CreateStudentUseCase createStudentUseCase;
    private final UpdateStudentUseCase updateStudentUseCase;

    @IsAuthenticated
    @RolesAllowed({"ROLE_STUDENT", "ROLE_ADMIN"})
    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable(value = "id") final long id) {
        final Optional<Student> studentOptional = getStudentUseCase.getStudent(id);
        if (studentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(studentOptional.get());
    }

    @IsAuthenticated
    @RolesAllowed({"ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<GetAllStudentsResponse> getAllStudents(@RequestParam(value = "country", required = false) String countryCode) {
        GetAllStudentsRequest request = new GetAllStudentsRequest();
        request.setCountryCode(countryCode);
        return ResponseEntity.ok(getStudentsUseCase.getStudents(request));
    }

    @IsAuthenticated
    @RolesAllowed({"ROLE_ADMIN"})
    @DeleteMapping("{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int studentId) {
        deleteStudentUseCase.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping()
    public ResponseEntity<CreateStudentResponse> createStudent(@RequestBody @Valid CreateStudentRequest request) {
        CreateStudentResponse response = createStudentUseCase.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @IsAuthenticated
    @RolesAllowed({"ROLE_STUDENT", "ROLE_ADMIN"})
    @PutMapping("{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") long id, @RequestBody @Valid UpdateStudentRequest request) {
        request.setId(id);
        updateStudentUseCase.updateStudent(request);
        return ResponseEntity.noContent().build();
    }
}
