package ru.tipsauk.monitoring.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Класс, представляющий конфигурацию приложения.
 * Загружает параметры подключения к базе данных из файла конфигурации "application.properties".
 */
public class ApplicationConfig {

    /**
     * Имя файла конфигурации.
     */
    private static final String CONFIG_FILE = "application.properties";

    /**
     * Настройки приложения.
     */
    private final Properties properties;

    /**
     * URL для подключения к базе данных.
     */
    private final String url;

    /**
     * Имя пользователя для подключения к базе данных.
     */
    private final String username;

    /**
     * Пароль для подключения к базе данных.
     */
    private final String password;

    /**
     * Конструктор класса ApplicationConfig без параметров.
     * Загружает настройки из файла "application.properties" и инициализирует соответствующие поля.
     * Вызывает исключение, если файл конфигурации не найден.
     */
    public ApplicationConfig() {
        this.properties = loadProperties();
        this.url = properties.getProperty("url");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
    }

    /**
     * Конструктор класса ApplicationConfig с явным указанием параметров подключения.
     * Используется для передачи параметров подключения вручную, для тест контейнеров.
     *
     * @param url      URL для подключения к базе данных.
     * @param username Имя пользователя для подключения к базе данных.
     * @param password Пароль для подключения к базе данных.
     */
    public ApplicationConfig(String url, String username, String password) {
        this.properties = loadProperties();
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Загружает настройки из файла конфигурации "application.properties".
     * Вызывает исключение в случае ошибки чтения файла или если файл не найден.
     *
     * @return Объект Properties с загруженными настройками.
     * @throws RuntimeException В случае ошибки чтения файла или если файл не найден.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                throw new RuntimeException("Не найден " + CONFIG_FILE);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения конфигурационного файла", e);
        }
        return props;
    }

    /**
     * Получает URL для подключения к базе данных.
     *
     * @return URL для подключения к базе данных.
     */
    public String getDbUrl() {
        return url;
    }

    /**
     * Получает имя пользователя для подключения к базе данных.
     *
     * @return Имя пользователя для подключения к базе данных.
     */
    public String getDbUsername() {
        return username;
    }

    /**
     * Получает пароль для подключения к базе данных.
     *
     * @return Пароль для подключения к базе данных.
     */
    public String getDbPassword() {
        return password;
    }
}

