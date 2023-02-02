package fontys.sem3.school.controller;

import fontys.sem3.school.business.*;
import fontys.sem3.school.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class StudentsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetStudentUseCase getStudentUseCase;
    @MockBean
    private GetStudentsUseCase getStudentsUseCase;
    @MockBean
    private DeleteStudentUseCase deleteStudentUseCase;
    @MockBean
    private CreateStudentUseCase createStudentUseCase;
    @MockBean
    private UpdateStudentUseCase updateStudentUseCase;

    @Test
    @WithMockUser(username = "10@fontys.nl", roles = {"STUDENT"})
    void getStudent_shouldReturn200WithStudent_whenStudentFound() throws Exception {
        Student student = Student.builder()
                .country(getBrazilDTO())
                .name("Rivaldo Vítor Borba Ferreira")
                .pcn(222L)
                .id(10L)
                .build();
        when(getStudentUseCase.getStudent(10L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/students/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(content().json("""
                            {"id":10,"pcn":222,"name":"Rivaldo Vítor Borba Ferreira","country":{"id":1,"code":"BR","name":"Brazil"}}
                        """));

        verify(getStudentUseCase).getStudent(10L);
    }

    @Test
    @WithMockUser(username = "10@fontys.nl", roles = {"STUDENT"})
    void getStudent_shouldReturn404Error_whenStudentNotFound() throws Exception {
        when(getStudentUseCase.getStudent(10L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/students/10"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(getStudentUseCase).getStudent(10L);
    }

    @Test
    @WithMockUser(username = "admin@fontys.nl", roles = {"ADMIN"})
    void getAllStudents_shouldReturn200WithStudentsList_WhenNoFilterProvided() throws Exception {
        GetAllStudentsResponse responseDTO = GetAllStudentsResponse.builder()
                .students(List.of(
                        Student.builder().id(1L).name("Romario").pcn(111L).country(getBrazilDTO()).build(),
                        Student.builder().id(1L).name("Ronaldo").pcn(222L).country(getBrazilDTO()).build()
                ))
                .build();
        GetAllStudentsRequest request = GetAllStudentsRequest.builder().build();
        when(getStudentsUseCase.getStudents(request)).thenReturn(responseDTO);

        mockMvc.perform(get("/students"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(content().json("""
                            {
                                "students":[
                                    {"id":1,"pcn":111,"name":"Romario","country":{"id":1,"code":"BR","name":"Brazil"}},
                                    {"id":1,"pcn":222,"name":"Ronaldo","country":{"id":1,"code":"BR","name":"Brazil"}}
                                ]
                            }
                        """));

        verify(getStudentsUseCase).getStudents(request);
    }

    @Test
    @WithMockUser(username = "admin@fontys.nl", roles = {"ADMIN"})
    void getAllStudents_shouldReturn200WithStudentsList_WhenCountryFilterProvided() throws Exception {
        Country country = Country.builder()
                .code("NL")
                .name("Netherlands")
                .id(2L)
                .build();
        GetAllStudentsResponse responseDTO = GetAllStudentsResponse.builder()
                .students(List.of(
                        Student.builder().id(1L).name("Dennis Bergkamp").pcn(111L).country(country).build(),
                        Student.builder().id(1L).name("Johan Cruyff").pcn(222L).country(country).build()
                ))
                .build();
        GetAllStudentsRequest request = GetAllStudentsRequest.builder()
                .countryCode("NL")
                .build();
        when(getStudentsUseCase.getStudents(request)).thenReturn(responseDTO);

        mockMvc.perform(get("/students").param("country", "NL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(content().json("""
                            {
                                "students":[
                                    {"id":1,"pcn":111,"name":"Dennis Bergkamp","country":{"id":2,"code":"NL","name":"Netherlands"}},
                                    {"id":1,"pcn":222,"name":"Johan Cruyff","country":{"id":2,"code":"NL","name":"Netherlands"}}
                                ]
                            }
                        """));

        verify(getStudentsUseCase).getStudents(request);
    }

    @Test
    @WithMockUser(username = "admin@fontys.nl", roles = {"ADMIN"})
    void deleteStudent_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/students/100"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deleteStudentUseCase).deleteStudent(100L);
    }

    @Test
    void createStudent_shouldReturn201_whenRequestIsValid() throws Exception {
        CreateStudentRequest expectedRequest = CreateStudentRequest.builder()
                .pcn(222L)
                .password("test123")
                .name("James")
                .countryId(1L)
                .build();
        when(createStudentUseCase.createStudent(expectedRequest))
                .thenReturn(CreateStudentResponse.builder()
                        .studentId(200L)
                        .build());

        mockMvc.perform(post("/students")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                    "pcn": "222",
                                    "name": "James",
                                    "password": "test123",
                                    "countryId": "1"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                            { "studentId":  200 }
                        """));

        verify(createStudentUseCase).createStudent(expectedRequest);
    }

    @Test
    @WithMockUser(username = "10@fontys.nl", roles = {"STUDENT"})
    void updateStudent_shouldReturn204() throws Exception {
        mockMvc.perform(put("/students/100")
                        .contentType(APPLICATION_JSON_VALUE)
                        .content("""
                                {
                                    "name": "James",
                                    "countryId": "1"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isNoContent());

        UpdateStudentRequest expectedRequest = UpdateStudentRequest.builder()
                .id(100L)
                .name("James")
                .countryId(1L)
                .build();
        verify(updateStudentUseCase).updateStudent(expectedRequest);
    }

    private Country getBrazilDTO() {
        return Country.builder()
                .id(1L)
                .code("BR")
                .name("Brazil")
                .build();
    }
}