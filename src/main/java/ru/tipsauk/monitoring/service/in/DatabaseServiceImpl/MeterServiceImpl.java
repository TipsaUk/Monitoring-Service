package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.tipsauk.monitoring.dto.MeterDto;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.mapper.MeterMapper;
import ru.tipsauk.monitoring.mapper.MeterValueMapper;
import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.model.MeterValue;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.repository.MeterRepository;
import ru.tipsauk.monitoring.repository.MeterValueRepository;
import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.starter_logger.annotations.Loggable;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса MeterService с использованием базы данных для web-версии приложения.
 */

@Loggable
@Service
@RequiredArgsConstructor
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;

    private final MeterValueRepository meterValueRepository;

    private final MeterValueMapper meterValueMapper;

    private final MeterMapper meterMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MeterDto> getAllMeters() {
        return meterRepository.getAllMeters().stream()
                .map(meterMapper::meterToMeterDto)
                .collect(Collectors.toSet());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean transmitMeterValueWeb(User user, MeterValueDto meterValueDto) {
        MeterValue meterValue = meterValueMapper.meterValueDtoToMeterValue(meterValueDto);
        Optional<Map.Entry<Meter, Integer>> entryMeterValue = meterValue.getMeterValues().entrySet().stream().findFirst();
        if (entryMeterValue.isEmpty()) {
            return false;
        }
        Meter meter = meterRepository.getMeterByName(entryMeterValue.get().getKey().getName());
        if (meter == null) {
            return false;
        }
        MeterValue actualValue = meterValueRepository
                .getValueMeterByDateAndUser(LocalDate.now().withDayOfMonth(1), user);
        if (actualValue.getMeterValues().containsKey(meter)) {
            return false;
        }
        return meterValueRepository.saveMeterValue(meter, entryMeterValue.get().getValue(), user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeterValueDto getValueMeter(User user, LocalDate dateValue) {
        MeterValue meterValue = meterValueRepository.getValueMeterByDateAndUser(dateValue, user);
        return meterValue == null ? new MeterValueDto()
                : meterValueMapper.meterValueToMeterValueDto(meterValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeterValueDto getLastValueMeter(User user) {
        return meterValueMapper.meterValueToMeterValueDto(meterValueRepository.getLastValueMeter(user));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<MeterValueDto> getValueMeterHistory(User user) {
        return meterValueRepository.getAllMeterValuesOrderedByDateValue(user).stream()
                .map(meterValueMapper::meterValueToMeterValueDto).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addNewMeter(String nameMeter) {
        Meter meter = new Meter(nameMeter);
        if (meterRepository.getAllMeters().contains(meter)) {
            System.out.println("Данный счетчик уже добавлен!");
            return false;
        }
        return meterRepository.saveMeter(meter);
    }
}
