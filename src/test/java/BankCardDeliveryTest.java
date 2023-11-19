import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.model.DataGenerator;
import ru.netology.model.BankCardItem;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class BankCardDeliveryTest {


    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
        $("[data-test-id='date'] [placeholder='Дата встречи']").sendKeys(Keys.CONTROL, "a" + Keys.DELETE);

    }


    @SneakyThrows
    @Test
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").val(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $(".notification__closer").click();
        $("[data-test-id='date'] [placeholder='Дата встречи']").sendKeys(Keys.CONTROL, "a" + Keys.DELETE);
        $("[data-test-id='date'] input").val(secondMeetingDate);
        $(".button").click();
        $("[data-test-id='replan-notification'] button").click();
        $("[data-test-id='success-notification']").shouldHave(exactText("Успешно!\n" +
                "Встреча успешно запланирована на " + secondMeetingDate));
    }


    @Test
    void lastDateTest() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = -2;
        var incorrectMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var item = new BankCardItem(validUser.getName(), validUser.getCity(), incorrectMeetingDate, validUser.getPhone());
        $("[data-test-id='city'] input").setValue(item.getCity());
        $("[data-test-id='date'] input").val(item.getDate());
        $("[data-test-id='name'] input").setValue(item.getName());
        $("[data-test-id='phone'] input").setValue(item.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='date'] [class='input__sub']").shouldBe(ownText("Заказ на выбранную дату невозможен"));

    }

    @Test
    void cityWithOutCatalog() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        Faker faker = new Faker(new Locale("en"));
        var city = faker.address().city();
        $("[data-test-id='city'] input").setValue(city);
        $("[data-test-id='date'] input").val(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='city'] [class='input__sub']").shouldBe(ownText("Доставка в выбранный город недоступна"));
    }

    @Test
    void incorrectName() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        Faker faker = new Faker(new Locale("en"));
        var name = faker.internet().emailAddress();
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").val(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='name'] [class='input__sub']").shouldHave(ownText("Имя и Фамилия указаные неверно."));
    }

    @Test
    void incorrectPhone() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        Faker faker = new Faker(new Locale("en"));
        var name = faker.phoneNumber().subscriberNumber(5);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").val(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='phone'] [class='input__sub']").shouldHave(ownText("Телефон указан неверно."));
    }

    @Test
    void withOutAgree() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").val(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $(".button").click();
        $("[data-test-id='agreement'] [class='checkbox__text']").shouldHave(ownText("Я соглашаюсь с условиями обработки"));
    }

    @Test
    void shouldEmptyCity() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var incorrectMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var item = new BankCardItem(validUser.getName(), validUser.getCity(), incorrectMeetingDate, validUser.getPhone());
        $("[data-test-id='date'] input").val(item.getDate());
        $("[data-test-id='name'] input").setValue(item.getName());
        $("[data-test-id='phone'] input").setValue(item.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='city'] [class='input__sub']").shouldHave(ownText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldEmptyName() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var incorrectMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var item = new BankCardItem(validUser.getName(), validUser.getCity(), incorrectMeetingDate, validUser.getPhone());
        $("[data-test-id='city'] input").setValue(item.getCity());
        $("[data-test-id='date'] input").val(item.getDate());
        $("[data-test-id='phone'] input").setValue(item.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='name'] [class='input__sub']").shouldHave(ownText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldEmptyPhone() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var incorrectMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var item = new BankCardItem(validUser.getName(), validUser.getCity(), incorrectMeetingDate, validUser.getPhone());
        $("[data-test-id='city'] input").setValue(item.getCity());
        $("[data-test-id='date'] input").val(item.getDate());
        $("[data-test-id='name'] input").setValue(item.getName());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='phone'] [class='input__sub']").shouldHave(ownText("Поле обязательно для заполнения"));
    }

}
