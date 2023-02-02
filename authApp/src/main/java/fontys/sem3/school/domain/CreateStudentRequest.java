package fontys.sem3.school.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentRequest {
    @NotNull
    @Min(1)
    @Max(100000)
    private Long pcn;

    @NotBlank
    private String name;

    @NotBlank
    @Length(max = 50)
    private String password;

    @NotNull
    private Long countryId;
}
