import io.dropwizard.Configuration;

import javax.validation.Valid;

public class TicketServiceConfiguration extends Configuration {

    @Valid
    private Integer numberOfRows;

    @Valid
    private Integer numberOfColumns;

    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public Integer getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(Integer numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }
}
