package softuni.exam.models.dto;

import javax.validation.constraints.Size;

public class CountryDTO {

    @Size(min = 2,max = 60)
    private String countryName;

    @Size(min = 2,max = 20)
    private String currency;

    public CountryDTO(String countryName, String currency) {
        this.countryName = countryName;
        this.currency = currency;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
