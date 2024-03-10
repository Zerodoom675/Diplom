package test.ui;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.DataHelperSQL;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.CardPage;
import page.PaymentPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

@Epic("UI тестирование функционала Путешествие дня")
@Feature("Покупка тура в кредит")
public class CreditUiTests {
    private static final String appUrl = System.getenv("APP_URL") != null ? System.getenv("APP_URL") : "http://localhost:8080";
    private static DataHelper.CardData cardData;
    private static CardPage travelCard;
    private static PaymentPage travelForm;
    private static List<DataHelperSQL.PaymentOrganization> payments;
    private static List<DataHelperSQL.CreditRequestEntity> credits;
    private static List<DataHelperSQL.OrderEntity> orders;

    @BeforeAll
    public static void setupClass() {
        DataHelperSQL.setDown();
        SelenideLogger.addListener("allure", new AllureSelenide().screenshots(true).savePageSource(true));
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    public void setupMethod() {
        open(appUrl);
        travelCard = new CardPage();
    }

    @AfterEach
    public void setDownMethod() {
        DataHelperSQL.setDown();
    }

    @DisplayName("Позитивный сценарий")
    @Test
    public void shouldHappyPath() {
        cardData = DataHelper.getValidApprovedCard();

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertBuyOperationIsSuccessful();

        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());

        assertEquals(travelCard.getAmount() * 100, payments.get(0).getAmount());
        assertTrue(payments.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        assertNull(orders.get(0).getCredit_id());
    }

    @DisplayName("Негативный сценарий")
    @Test
    public void shouldSadPath() {
        cardData = DataHelper.getValidDeclinedCard();

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertBuyOperationWithErrorNotification();

        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());

        assertEquals(travelCard.getAmount() * 100, payments.get(0).getAmount());
        assertTrue(payments.get(0).getStatus().equalsIgnoreCase("declined"));
        assertEquals(payments.get(0).getTransaction_id(), orders.get(0).getPayment_id());
        assertNull(orders.get(0).getCredit_id());
    }

    @DisplayName("Переключение с формы оплаты на форму кредита")
    @Test
    public void switchingFromPaymentFormToCreditForm() {
        cardData = DataHelper.getValidApprovedCard();

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelCard.clickCreditButton();
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
    }

    //Валидные значения
    @DisplayName("Имя и фамилия на латинице состоящие из 2 символов")
    @Test
    public void firstAndLastNameInLatinConsistingOf2Characters() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "Qw Qw";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.assertBuyOperationIsSuccessful();
    }

    @DisplayName("Имя и фамилия на латинице состоящие из 35 символов")
    @Test
    public void firstAndLastNameInLatinConsistingOf35Characters() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "Qwertyqwertyqwertyqwertyqwertyqwert Qwertyqwertyqwertyqwertyqwertyqwert";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.assertBuyOperationIsSuccessful();
    }

    @DisplayName("Имя через дефис и фамилия на латинице")
    @Test
    public void hyphenatedFirstNameAndLastNameInLatin() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolder();

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.assertBuyOperationIsSuccessful();
    }

    @DisplayName("Пустое поле номер карты")
    @Test
    public void emptyCardNumberField() {
        cardData = DataHelper.getValidApprovedCard();
        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                "",
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                "",
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("number", "Поле обязательно для заполнения");
    }


    @DisplayName("Заполнение поля номера карты c пробелами вначале и в конце")
    @Test
    public void fillingOutTheCardNumberFieldWithSpacesAtTheBeginningAndAtTheEnd() {
        cardData = DataHelper.getValidApprovedCard();
        var number = " " + cardData.getNumber() + " ";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("number", "Неверный формат");
    }

    @DisplayName("13 цифр в поле номера карты")
    @Test
    public void theCardNumberFieldHas13Digits() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateAValid13DigitCardNumber();

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("number", "Неверный формат");
    }

    @DisplayName("Нули в поле номера карты")
    @Test
    public void zerosInTheCardNumberField() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith0Digits();

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("number", "Неверный формат");
    }

    // Поле "Месяц"
    @DisplayName("Пустое поле месяц")
    @Test
    public void emptyMonthField() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "";
        var matchesMonth = "";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                month,
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                matchesMonth,
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("month", "Поле обязательно для заполнения");
    }

    @DisplayName("Заполнение поля месяц значением 1")
    @Test
    public void fillingTheMonthFieldWithOne() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "1";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                month,
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                month,
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("month", "Неверный формат");
    }

    @DisplayName("Заполнение поля месяц значением 00")
    @Test
    public void fillingTheMonthFieldWithZeros() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "00";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                month,
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                month,
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("month", "Неверно указан срок действия карты");
    }

    @DisplayName("Заполнение поля месяц значением 13")
    @Test
    public void fillingTheMonthFieldWithTheValue13() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "13";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                month,
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                month,
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("month", "Неверно указан срок действия карты");
    }

    @DisplayName("Пустое поле год")
    @Test
    public void emptyYearField() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                year,
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                year,
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("year", "Поля обязательно для заполнения");
    }

    @DisplayName("Нули в поле год")
    @Test
    public void zerosInTheYearField() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "00";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                year,
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                year,
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("year", "Истёк срок действия карты");
    }

    @DisplayName("Пустое поле владелец")
    @Test
    public void emptyOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.assertField("holder", "Поле обязательно для заполнения");
    }

    @DisplayName("Пробелы вначале и в конце поля владелец")
    @Test
    public void spacesAtTheBeginningAndAtTheEndOfTheOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = " " + cardData.getHolder() + " ";


        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.assertField("holder", "Неверный формат");
    }

    @DisplayName("Кириллица в поле владелец")
    @Test
    public void cyrillicInTheOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.createInvalidHolderWithCyrillicCharacters();

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.assertField("holder", "Неверный формат");
    }

    @DisplayName("Кириллица + цифры в поле владелец")
    @Test
    public void cyrillicAndNumbersInTheOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.createInvalidHolderWith45CyrillicCharacters();
        var matchesHolder = "";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                matchesHolder,
                cardData.getCvc());
        travelForm.assertField("holder", "Неверный формат");
    }

    @DisplayName("Латинское имя без фамилии в поле владелец")
    @Test
    public void latinNameWithoutSurnameInTheOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.createInvalidOwnerNameEn();
        var matchesHolder = "";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                matchesHolder,
                cardData.getCvc());
        travelForm.assertBuyOperationIsSuccessful();
    }

    @DisplayName("Имя на кириллице, фамилия на латинице в поле владелец")
    @Test
    public void firstNameInCyrillicLastNameInLatinInTheOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.createIncorrectOwnerNameRuLastNameEn();
        var matchesHolder = "";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                matchesHolder,
                cardData.getCvc());
        travelForm.assertField("holder", "Неверный формат");
    }

    @DisplayName("Имя на латинице, фамилия на кириллице в поле владелец")
    @Test
    public void firstNameInLatinLastNameInCyrillicInTheOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.createIncorrectOwnerNameEnLastNameRu();
        var matchesHolder = "";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                holder,
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                matchesHolder,
                cardData.getCvc());
        travelForm.assertField("holder", "Неверный формат");
    }

    @DisplayName("Пустое поле CVC/CVV")
    @Test
    public void blankCVCField() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cvc);
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cvc);
        travelForm.assertField("cvc", "Поле обязательно для заполнения");
    }

    @DisplayName("4 цифры в поле CVC/CVV")
    @Test
    public void CVCFieldHas4Digits() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = cardData.getCvc() + DataHelper.generateRandomSingleDigit();
        var matchesCvc = cardData.getCvc();

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cvc);
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                matchesCvc);
        travelForm.assertBuyOperationIsSuccessful();
    }

    @DisplayName("Нули в поле CVC/CVV")
    @Test
    public void zerosInTheCVCField() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "000";

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cvc);
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cvc);
        travelForm.assertBuyOperationIsSuccessful();
    }
}