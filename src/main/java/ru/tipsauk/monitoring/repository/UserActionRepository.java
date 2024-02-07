package ru.tipsauk.monitoring.repository;

import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserAction;
import ru.tipsauk.monitoring.model.UserActionType;

import java.util.TreeSet;

/**
 * Интерфейс для взаимодействия с базой данных по действиям пользователей.
 */
public interface UserActionRepository {

    /**
     * Сохраняет действие пользователя в БД.
     *
     * @param user       пользователь, совершивший действие.
     * @param userAction тип действия пользователя (например, вход, выход и т. д.).
     * @param details    дополнительные детали действия.
     */
    void saveUserAction(User user, UserActionType userAction, String details);

    /**
     * Получает все действия указанного пользователя с указанным типом действия.
     *
     * @param user       пользователь, для которого нужно получить действия.
     * @param userAction тип действия пользователя (например, вход, выход и т. д.).
     * @return множество действий пользователя указанного типа.
     */
    TreeSet<UserAction> getByUserAndUserAction(User user, UserActionType userAction);
}
