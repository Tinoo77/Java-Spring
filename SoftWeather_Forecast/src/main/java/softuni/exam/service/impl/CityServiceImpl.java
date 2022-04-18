package softuni.exam.service.impl;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CityDTO;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CityService;

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
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Path path;
    private final Validator validator;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;

        this.gson = new GsonBuilder().create();
        this.modelMapper = new ModelMapper();
        this.path = Path.of("src","main","resources","files","json","cities.json");
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Override
    public boolean areImported() {
        return this.cityRepository.count() > 0;}

    @Override
    public String readCitiesFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importCities() throws IOException {

        String json = this.readCitiesFileContent();

        List<String> result = new ArrayList<>();

        CityDTO[] cityDTOS = this.gson.fromJson(json,CityDTO[].class);

        for (CityDTO cityDTO:cityDTOS){

            Set<ConstraintViolation<CityDTO>> errors = this.validator.validate(cityDTO);

            if (errors.isEmpty()){

                Optional<City> cityOpt = this.cityRepository.getByCityName(cityDTO.getCityName());

                if (cityOpt.isEmpty()){

                    Country country = this.countryRepository.getById(cityDTO.getCountry());

                    City city = this.modelMapper.map(cityDTO,City.class);

                    city.setCountry(country);

                    this.cityRepository.save(city);

                    result.add(String.format("Successfully imported %s - %d",city.getCityName(),city.getPopulation()));
                }
                else {
                    result.add("Invalid city");
                }
            }
            else {
                result.add("Invalid city");
            }
        }

        return String.join("\n", result);
    }
}
