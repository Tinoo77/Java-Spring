package softuni.exam.models.dto;


import org.springframework.lang.Nullable;
import softuni.exam.models.entity.DaysOfWeek;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalTime;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForecastDTO {

    @XmlElement(name = "day_of_week")
    @NotNull
    private DaysOfWeek dayOfWeek;


    @Min(-20)
    @Max(60)
    @XmlElement(name = "max_temperature")
    private double maxTemperature;

    @Min(-50)
    @Max(40)
    @XmlElement(name = "min_temperature")
    private double minMaxTemperature;

    @XmlElement
    private String sunrise;

    @XmlElement
    private String sunset;

    @XmlElement
    private long city;

    public ForecastDTO() {
    }

    public DaysOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DaysOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getMinMaxTemperature() {
        return minMaxTemperature;
    }

    public void setMinMaxTemperature(double minMaxTemperature) {
        this.minMaxTemperature = minMaxTemperature;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public long getCity() {
        return city;
    }

    public void setCity(long city) {
        this.city = city;
    }
}
