package test.api;


import com.codeborne.selenide.logevents.SelenideLogger;
import data.APIHelper;
import data.DataHelper;
import data.DataHelperSQL;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("API тестирование функционала Путешествие дня")
@Feature("Покупка тура по карте")
public class PaymentApiTests {
    private static final String paymentUrl = "/api/v1/pay";
    private static DataHelper.CardData cardData;
    private static List<DataHelperSQL.PaymentOrganization> payments;
    private static List<DataHelperSQL.CreditRequestEntity> credits;
    private static List<DataHelperSQL.OrderEntity> orders;

    @BeforeAll
    public static void setUpAll() {
        DataHelperSQL.setDown();
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true).savePageSource(true));
    }

    @BeforeAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    public void teardrop() {
        DataHelperSQL.setDown();
    }

    @DisplayName("Пустое тело запроса")
    @Test
    public void statusShouldBe400WithEmptyBody() {
        cardData = DataHelper.getValidApprovedCard();
        APIHelper.executeRequest(cardData, paymentUrl);
        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());
    }

    @DisplayName("Пустое значение у атрибута number в теле запроса")
    @Test
    public void statusShouldBe400WithEmptyNumber() {
        cardData = new DataHelper.CardData(
                null,
                DataHelper.generateMonth(1),
                DataHelper.generateYear(2),
                DataHelper.generateValidHolder(),
                DataHelper.generateValidCVC());
        APIHelper.executeRequest500(cardData, paymentUrl);
        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(0, payments.size());
        assertEquals(0, credits.size());
        assertEquals(0, orders.size());
    }

    @DisplayName("Пустое значение у атрибута month в теле запроса")
    @Test
    public void statusShouldBe400WithEmptyMonth() {
        cardData = new DataHelper.CardData(
                DataHelper.getNumberByStatus("approved"),
                null,
                DataHelper.generateYear(2),
                DataHelper.generateValidHolder(),
                DataHelper.generateValidCVC());
        APIHelper.executeRequest(cardData, paymentUrl);
        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());
    }

    @DisplayName("Пустое значение у атрибута year в теле запроса")
    @Test
    public void statusShouldBe400WithEmptyYear() {
        cardData = new DataHelper.CardData(
                DataHelper.getNumberByStatus("approved"),
                DataHelper.generateMonth(1),
                null,
                DataHelper.generateValidHolder(),
                DataHelper.generateValidCVC());
        APIHelper.executeRequest(cardData, paymentUrl);
        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());
    }

    @DisplayName("Пустое значение у атрибута holder в теле запроса")
    @Test
    public void statusShouldBe400WithEmptyHolder() {
        cardData = new DataHelper.CardData(
                DataHelper.getNumberByStatus("approved"),
                DataHelper.generateMonth(1),
                DataHelper.generateYear(2),
                null,
                DataHelper.generateValidCVC());
        APIHelper.executeRequest(cardData, paymentUrl);
        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());
    }

    @DisplayName("Пустое значение у атрибута cvc в теле запроса")
    @Test
    public void statusShouldBe400WithEmptyCvc() {
        cardData = new DataHelper.CardData(
                DataHelper.getNumberByStatus("approved"),
                DataHelper.generateMonth(1),
                DataHelper.generateYear(2),
                DataHelper.generateValidHolder(),
                null);
        APIHelper.executeRequest(cardData, paymentUrl);
        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(1, payments.size());
        assertEquals(0, credits.size());
        assertEquals(1, orders.size());
    }
}
