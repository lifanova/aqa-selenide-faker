package ru.netology.delivery.test;

import org.junit.jupiter.api.Test;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class DeliveryTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    /**
     * Положительный сценарий:
     * отправка полностью заполненной формы с корректными значениями (без перепланирования даты)
     * */
    @Test
    void shouldSubmitWithValidData() {
        open("http://localhost:9999");

        DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(DataGenerator.generateDate(5));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        // Assertion: нужно ок. 15 с,
        // чтобы появился div c сообщением об успешной заявке,
        // поэтому нужен таймаут
        Duration timeout = Duration.ofSeconds(15);
        $(withText("Успешно!")).shouldBe(visible, timeout);
        //$(".success-notification").shouldBe(visible, timeout);
    }

    /**
     * Положительный сценарий:
     * отправка полностью заполненной формы с корректными значениями (с перепланированием даты)
     * */
    @Test
    void shouldSubmitWithDateReplanning() {
        open("http://localhost:9999");

        DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(DataGenerator.generateDate(5));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();

        // Assertion: нужно ок. 15 с,
        // чтобы появился div c сообщением об успешной заявке,
        // поэтому нужен таймаут
        Duration timeout = Duration.ofSeconds(15);
        $(withText("Успешно!")).shouldBe(visible, timeout);

        // Достаточно изменить значение даты и кликнуть снова на кнопку "Запланировать"
        $("[data-test-id=date] input").sendKeys(DataGenerator.generateDate(4));
        $("button.button  .button__text").click();
        //$(withText("У вас уже запланирована встреча на другую дату. Перепланировать?")).shouldBe(visible);
        $("[data-test-id=replan-notification] button.button").click();
        $(withText("Успешно")).shouldBe(visible);
    }


    /**
     * Негативный сценарий: некорректный город
     * */
    @Test
    void shouldSubmitWithIncorrectCity() {
        open("http://localhost:9999");

        DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");

        $("[data-test-id=city] input").setValue(DataGenerator.generateCity("en-IND"));
        $("[data-test-id=date] input").sendKeys(DataGenerator.generateDate(5));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();

        //Assertion: появляется сообщение об ошибке
        $("[data-test-id=city] .input__sub").shouldBe(exist);
    }

    /**
     * Негативный сценарий: некорректная дата доставки (5 дней назад)
     * */
    @Test
    void shouldSubmitWithIncorrectDate() {
        open("http://localhost:9999");

        DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=city] input").setValue(user.getCity());
        LocalDate pastDate = LocalDate.now().minusDays(5);
        $("[data-test-id=date] input").doubleClick().sendKeys(formatter.format(pastDate));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();

        //Assertion: появляется сообщение об ошибке
        $("[data-test-id=date] .input__sub").shouldBe(exist);
    }

    /**
     * Негативный сценарий: некорректные имя с фамилией
     * */
    @Test
    void shouldSubmitWithIncorrectName() {
        open("http://localhost:9999");

        DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(DataGenerator.generateDate(5));

        $("[data-test-id=name] input").setValue(DataGenerator.generateName("en-IND"));
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();

        // Assertion: появляется сообщение об ошибке
        $("[data-test-id=name] .input__sub").shouldBe(exist);
    }

    /**
     * Негативный сценарий: некорректный номер телефона
     * */
    @Test
    void shouldSubmitWithIncorrectPhone() {
        open("http://localhost:9999");

        DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(DataGenerator.generateDate(5));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(DataGenerator.generatePhone("en-IND"));
        $("[data-test-id=agreement]").click();
        $(".button").click();

        //Assertion: появляется сообщение об ошибке
        $("[data-test-id=phone] .input__sub").shouldBe(exist);
    }

    /**
     * Негативный сценарий: без чекбокса
     * */
    @Test
    void shouldSubmitWithoutAgreement() {
        open("http://localhost:9999");

        DataGenerator.UserInfo user = DataGenerator.Registration.generateUser("ru");
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(DataGenerator.generateDate(5));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $(".button").click();

        //Assertion: у span появляется класс input_invalid, =>, текст у чекбокса становится красным
        $(".input_invalid").shouldBe(exist);
        //$("data-test-id=agreement]").parent().find(".input_invalid").shouldBe(exist);
    }

}
