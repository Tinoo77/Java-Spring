package softuni.exam.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CountryDTO;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Path path;
    private final Validator validator;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;

        this.gson = new GsonBuilder().create();
        this.modelMapper = new ModelMapper();
        this.path = Path.of("src","main","resources","files","json","countries.json");
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Override
    public boolean areImported() {
        return this.countryRepository.count() > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return Files.readString(this.path);
    }

    @Override
    public String importCountries() throws IOException {

        String json = this.readCountriesFromFile();

        CountryDTO[] countryDTOS = this.gson.fromJson(json,CountryDTO[].class);

        List<String> result = new ArrayList<>();

        for (CountryDTO countryDTO:countryDTOS){

            Set<ConstraintViolation<CountryDTO>> errors = this.validator.validate(countryDTO);

            if (errors.isEmpty()){

                Optional<Country> optCountry = this.countryRepository.findByCountryName(countryDTO.getCountryName());

                if (optCountry.isEmpty()){

                    Country country = this.modelMapper.map(countryDTO,Country.class);

                    this.countryRepository.save(country);

                    result.add(String.format("Successfully imported %s - %s",country.getCountryName(),country.getCurrency()));
                }
                else {

                    result.add("Invalid country");
                }

            }
            else {
                result.add("Invalid country");
            }

        }

        return String.join("\n", result);
    }
}
