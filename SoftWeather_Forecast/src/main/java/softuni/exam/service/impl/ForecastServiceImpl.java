package softuni.exam.service.impl;

import org.modelmapper.AbstractProvider;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ForecastDTO;
import softuni.exam.models.dto.ForecastDTORoot;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.ForecastService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ForecastServiceImpl implements ForecastService {

    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final Path path;

    @Autowired
    public ForecastServiceImpl(ForecastRepository forecastRepository, CityRepository cityRepository) throws JAXBException {
        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;

        JAXBContext context = JAXBContext.newInstance(ForecastDTORoot.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
        this.modelMapper = new ModelMapper();
        this.path = Path.of("src","main","resources","files","xml","forecasts.xml");

        ModelMapper modelmapper = new ModelMapper();

        this.modelMapper.addConverter(ctx -> LocalTime.parse(ctx.getSource(), DateTimeFormatter.ofPattern("HH:mm:ss")),
                String.class, LocalTime.class);

        }



    @Override
    public boolean areImported() {
        return this.forecastRepository.count() > 0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {

        ForecastDTORoot forecastDTOs = (ForecastDTORoot) this.unmarshaller.unmarshal
                (new FileReader(path.toAbsolutePath().toString()));
        return forecastDTOs
                .getForecasts()
                .stream()
                .map(this::importForecast)
                .collect(Collectors.joining("\n"));
    }

    private String importForecast(ForecastDTO dto){

        Set<ConstraintViolation<ForecastDTO>> errors = this.validator.validate(dto);

        if (!errors.isEmpty()){
            return "Invalid Forecast";
        }

        Optional<Forecast> optForecast =
                this.forecastRepository.findByDayOfWeekAndCityId(dto.getDayOfWeek(),dto.getCity());


        if (optForecast.isPresent()){

            return "Invalid Forecast";
        }

        Optional<City> city = this.cityRepository.findById(dto.getCity());



        Forecast forecast = this.modelMapper.map(dto, Forecast.class);

        forecast.setCity(city.get());

        this.forecastRepository.save(forecast);

        String dayOfWeek = String.valueOf(forecast.getDayOfWeek());

        return "Successfully imported "+ dayOfWeek +" - " + forecast.getMaxTemperature();

    }

    @Override
    public String exportForecasts() {

        List<String> result = new ArrayList<>();

        List<Forecast> forecasts = this.forecastRepository.getForecastsOnSunday();

        for (Forecast forecast:forecasts){

            result.add(String.format("City: %s:%n" +
                    "-min temperature: %.02f%n" +
                    "--max temperature: %.02f%n" +
                    "---sunrise: " + forecast.getSunrise()+"%n"+
                            "----sunset: " + forecast.getSunset(),forecast.getCity()
                    .getCityName(),forecast.getMinMaxTemperature(),forecast.getMaxTemperature()));

        }

        return String.join("\n", result);
    }
}
