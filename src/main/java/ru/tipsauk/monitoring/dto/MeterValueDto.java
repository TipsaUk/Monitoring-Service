package ru.tipsauk.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tipsauk.monitoring.model.Meter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Класс, представляющий объект данных о показаниях счетчика.
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class MeterValueDto implements Comparable<MeterValueDto> {

    /**
     * Дата показаний.
     */
    @Schema(description = "Дата показаний")
    @NotNull(message = "Дата не может быть пустой")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateValue;

    /**
     * Показания счетчиков.
     */
    @Schema(description = "Показания счетчиков")
    @NotEmpty(message = "Показания не могут быть пустыми")
    @NotNull(message = "Показания не могут быть пустыми")
    private final Map<Meter, Integer> meterValues = new HashMap<>();

    public void addMeterReading(Meter meter, Integer value) {
        meterValues.put(meter, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterValueDto that = (MeterValueDto) o;
        return Objects.equals(dateValue, that.dateValue) && Objects.equals(meterValues, that.meterValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateValue, meterValues);
    }

    @Override
    public int compareTo(MeterValueDto o) {
        return o.dateValue.compareTo(this.dateValue);
    }
}
