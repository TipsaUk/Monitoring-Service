package ru.tipsauk.monitoring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import ru.tipsauk.monitoring.model.UserActionType;

import java.util.Date;

/**
 * Класс, представляющий объект данных о действии пользователя.
 *
 */
public class UserActionDto implements Comparable<UserActionDto> {

    /**
     * Дата и время действия пользователя.
     */
    @Schema(description = "Дата и время действия пользователя")
    @NotBlank(message = "Дата не может быть пустой")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date timeAction;

    /**
     * Тип действия пользователя.
     */
    @Schema(description = "Тип действия пользователя")
    @NotEmpty(message = "Действие пользователя не может быть пустыми")
    @NotNull(message = "Действие пользователя не может быть пустыми")
    private UserActionType action;

    /**
     * Дополнительное описание действия.
     */
    @Schema(description = "Дополнительное описание действия")
    private String description;

    /**
     * Конструктор без параметров для создания пустого объекта UserActionDto.
     */
    public UserActionDto() {
    }

    /**
     * Получить дату и время действия пользователя.
     *
     * @return Дата и время действия пользователя.
     */
    public Date getTimeAction() {
        return timeAction;
    }

    /**
     * Установить новую дату и время действия пользователя.
     *
     * @param timeAction Новая дата и время действия пользователя.
     */
    public void setTimeAction(Date timeAction) {
        this.timeAction = timeAction;
    }

    /**
     * Получить тип действия пользователя.
     *
     * @return Тип действия пользователя.
     */
    public UserActionType getAction() {
        return action;
    }

    /**
     * Установить новый тип действия пользователя.
     *
     * @param action Новый тип действия пользователя.
     */
    public void setAction(UserActionType action) {
        this.action = action;
    }

    /**
     * Получить дополнительное описание действия.
     *
     * @return Дополнительное описание действия.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Установить новое дополнительное описание действия.
     *
     * @param description Новое дополнительное описание действия.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Сравнение объектов UserActionDto для сортировки по дате и времени действия.
     *
     * @param o Другой объект UserActionDto.
     * @return Результат сравнения по дате и времени действия.
     */
    @Override
    public int compareTo(UserActionDto o) {
        return this.timeAction.compareTo(o.getTimeAction());
    }
}
