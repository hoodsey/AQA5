import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.DataGenerator;
import ru.netology.model.BankCardItem;


import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class BankCardDeliveryTest {
    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }


    @BeforeEach
    public void setUp() {
        Faker faker = new Faker();
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
        Thread.sleep(5000);
        $("[data-test-id='city'] input").clear();
        $("[data-test-id='date'] input").val(secondMeetingDate);
        $(".button").click();
        $("[data-test-id='replan-notification']").shouldHave(exactText("Необходимо подтверждение\n" +
                "У вас уже запланирована встреча на другую дату. Перепланировать?\n" +
                "\n" +
                "Перепланировать"));
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
    void CityWithOutCatalog() {
        var item = new BankCardItem("Кабакова Анастасия", "Канадлакша", generateDate(6).toString(), "+79818042544");
        $("[data-test-id='city'] input").setValue(item.getCity());
        $("[data-test-id='date'] input").val(item.getDate());
        $("[data-test-id='name'] input").setValue(item.getName());
        $("[data-test-id='phone'] input").setValue(item.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='city'] [class='input__sub']").shouldBe(ownText("Доставка в выбранный город недоступна"));
    }

    @Test
    @DisplayName("Имя и Фамилия указаные неверно.")
    void IncorrectName() {
        var item = new BankCardItem("Anastasiia 123", "Краснодар", generateDate(6).toString(), "+79818042544");
        $("[data-test-id='city'] input").setValue(item.getCity());
        $("[data-test-id='date'] input").val(item.getDate());
        $("[data-test-id='name'] input").setValue(item.getName());
        $("[data-test-id='phone'] input").setValue(item.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='name'] [class='input__sub']").shouldHave(ownText("Имя и Фамилия указаные неверно."));
    }

    @Test
    void IncorrectPhone() {
        var item = new BankCardItem("Кабакова Анастасия", "Краснодар", generateDate(6).toString(), "98818042544");
        $("[data-test-id='city'] input").setValue(item.getCity());
        $("[data-test-id='date'] input").val(item.getDate());
        $("[data-test-id='name'] input").setValue(item.getName());
        $("[data-test-id='phone'] input").setValue(item.getPhone());
        $("[data-test-id='agreement']").click();
        $(".button").click();
        $("[data-test-id='phone'] [class='input__sub']").shouldHave(ownText("Телефон указан неверно."));
    }

    @Test
    void WithOutAgree() {
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
