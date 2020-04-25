package max.lab.rst.domain;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class BookQuery {
    private String title;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    @Min(1)
    private int page = 1;

    @Min(0)
    @Max(500)
    private int size = 10;
}
