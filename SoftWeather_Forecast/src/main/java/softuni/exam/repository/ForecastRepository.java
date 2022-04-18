package softuni.exam.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import softuni.exam.models.entity.DaysOfWeek;
import softuni.exam.models.entity.Forecast;

import java.util.List;
import java.util.Optional;

public interface ForecastRepository extends JpaRepository<Forecast,Long> {


    Optional<Forecast> findByDayOfWeekAndCityId(DaysOfWeek dayOfWeek, long city);


    @Query("SELECT f FROM Forecast f " +
            " WHERE f.dayOfWeek LIKE 'SUNDAY' AND f.city.population < 150000 " +
            "ORDER BY f.maxTemperature DESC, f.id ASC")
    List<Forecast> getForecastsOnSunday();
}
