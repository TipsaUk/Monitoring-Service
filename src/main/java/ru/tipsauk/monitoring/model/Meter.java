package ru.tipsauk.monitoring.model;

import java.util.Objects;

/**
 * Класс, сущности вида счетчика в системе.
 */
public class Meter {

    /** id счетчика в системе. */
    private long id;

    /** Наименование счетчика. */
    private String name;

    public Meter() {
    }

    public Meter(String name) {
        this.name = name;
    }

    public Meter(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meter meter = (Meter) o;
        return Objects.equals(name, meter.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }


}
