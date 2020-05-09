package max.lab.rst.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table("book")
public class Book {
    @Id
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