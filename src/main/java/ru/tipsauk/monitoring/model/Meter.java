package ru.tipsauk.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, сущности вида счетчика в системе.
 */
@Getter
@Setter
@AllArgsConstructor
public class Meter {

    /** Наименование счетчика. */
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
