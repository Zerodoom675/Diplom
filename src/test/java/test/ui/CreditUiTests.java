package test.ui;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.DataHelperSQL;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.*;
import page.CardPage;
import page.PaymentPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.testng.AssertJUnit.*;

@Epic("UI тестирование функционала Путешествие дня")
@Feature("Покупка тура в кредит")
public class CreditUiTests {
    private static DataHelper.CardData cardData;
    private static CardPage travelCard;
    private static PaymentPage travelForm;
    private static List<DataHelperSQL.PaymentOrganization> payments;
    private static List<DataHelperSQL.CreditRequestEntity> credits;
    private static List<DataHelperSQL.OrderEntity> orders;

    @BeforeClass
    public void setupClass() {
        DataHelperSQL.setDown();
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true).savePageSource(true));
    }

    @BeforeMethod
    public void setupMethod() {
        open("http://localhost:8080/");
        travelCard = new CardPage();
    }

    @AfterMethod
    public void setDownMethod() {
        DataHelperSQL.setDown();
    }

    @AfterClass
    public void setDownClass() {
        SelenideLogger.removeListener("allure");
    }

    @Story("Позитивный сценарий")
    @Severity(SeverityLevel.BLOCKER)
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

    @Story("Негативный сценарий")
    @Severity(SeverityLevel.BLOCKER)
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

    @Story("Переключение с формы оплаты на форму кредита")
    @Severity(SeverityLevel.MINOR)
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
    @Story("Имя и фамилия на латинице состоящие из 2 символов")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void firstAndLastNameInLatinConsistingOf2Characters() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "Qw Qw";
        var matchesHolder = holder;

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

    @Story("Имя и фамилия на латинице состоящие из 35 символов")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void firstAndLastNameInLatinConsistingOf35Characters() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "Qwertyqwertyqwertyqwertyqwertyqwert Qwertyqwertyqwertyqwertyqwertyqwert";
        var matchesHolder = holder;

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

    @Story("Имя через дефис и фамилия на латинице")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void hyphenatedFirstNameAndLastNameInLatin() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolder();
        var matchesHolder = holder;

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

    //Невалидные значения
    // Поле "Номер карты"
    @Story("Пустое поле номер карты")
    @Severity(SeverityLevel.NORMAL)
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
        travelForm.assertField("number","Поле обязательно для заполнения");
    }


    @Story("Заполнение поля номера карты c пробелами вначале и в конце")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void fillingOutTheCardNumberFieldWithSpacesAtTheBeginningAndAtTheEnd() {
        cardData = DataHelper.getValidApprovedCard();
        var number = " " + cardData.getNumber() + " ";
        var matchesNumber = number;

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                matchesNumber,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("number","Неверный формат");
    }

    @Story("13 цифр в поле номера карты")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void theCardNumberFieldHas13Digits() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateAValid13DigitCardNumber();
        var matchesNumber = number;

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                matchesNumber,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("number","Неверный формат");
    }

    @Story("Нули в поле номера карты")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void zerosInTheCardNumberField() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith0Digits();
        var matchesNumber = number;

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                number,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.matchesByInsertValue(
                matchesNumber,
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("number","Неверный формат");
    }

    // Поле "Месяц"
    @Story("Пустое поле месяц")
    @Severity(SeverityLevel.NORMAL)
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
        travelForm.assertField("month","Поле обязательно для заполнения");
    }

    @Story("Заполнение поля месяц значением 1")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void fillingTheMonthFieldWithOne() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "1";
        var matchesMonth = month;

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
        travelForm.assertField("month","Неверный формат");
    }

    @Story("Заполнение поля месяц значением 00")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void fillingTheMonthFieldWithZeros() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "00";
        var matchesMonth = month;

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
        travelForm.assertField("month","Неверно указан срок действия карты");
    }

    @Story("Заполнение поля месяц значением 13")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void fillingTheMonthFieldWithTheValue13() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "13";
        var matchesMonth = month;

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
        travelForm.assertField("month","Неверно указан срок действия карты");
    }

    // Поле "Год"
    @Story("Пустое поле год")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void emptyYearField() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "";
        var matchesYear = year;

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
                matchesYear,
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("year","Поля обязательно для заполнения");
    }

    @Story("Нули в поле год")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void zerosInTheYearField() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "00";
        var matchesYear = year;

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
                matchesYear,
                cardData.getHolder(),
                cardData.getCvc());
        travelForm.assertField("year","Истёк срок действия карты");
    }

    // Поле "Владелец"
    @Story("Пустое поле владелец")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void emptyOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "";
        var matchesHolder = holder;

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
                matchesHolder, cardData.getCvc());
        travelForm.assertField("holder","Поле обязательно для заполнения");
    }

    @Story("Пробелы вначале и в конце поля владелец")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void spacesAtTheBeginningAndAtTheEndOfTheOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = " " + cardData.getHolder() + " ";
        var matchesHolder = holder;

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
        travelForm.assertField("holder","Неверный формат");
    }

    @Story("Кириллица в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void cyrillicInTheOwnerField() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.createInvalidHolderWithCyrillicCharacters();
        var matchesHolder = holder;

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
        travelForm.assertField("holder","Неверный формат");
    }

    @Story("Кириллица + цифры в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
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
        travelForm.assertField("holder","Неверный формат");
    }

    @Story("Латинское имя без фамилии в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
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

    @Story("Имя на кириллице, фамилия на латинице в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
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
        travelForm.assertField("holder","Неверный формат");
    }

    @Story("Имя на латинице, фамилия на кириллице в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
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
        travelForm.assertField("holder","Неверный формат");
    }

    // CVC/CVV
    @Story("Пустое поле CVC/CVV")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void blankCVCField() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "";
        var matchesCvc = cvc;

        travelForm = travelCard.clickPayButton();
        travelForm.insertingValueInForm(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(), cvc);
        travelForm.matchesByInsertValue(
                cardData.getNumber(),
                cardData.getMonth(),
                cardData.getYear(),
                cardData.getHolder(),
                matchesCvc);
        travelForm.assertField("cvc","Поле обязательно для заполнения");
    }

    @Story("4 цифры в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
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

    @Story("Нули в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void zerosInTheCVCField() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "000";
        var matchesCvc = cvc;

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
}