package max.lab.rst.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Book {
    @NotEmpty
    @Size(min = 3, max = 20)
    private String isbn;

    @NotEmpty
    @Size(min = 3, max = 500)
    private String title;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @NotEmpty
    @Size(min = 3, max = 50)
    private String category;
}