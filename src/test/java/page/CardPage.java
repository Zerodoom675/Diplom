package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class CardPage {
    private final SelenideElement dailyTripHeading = $x("//div[@id='root']/div/h2");
    private final SelenideElement dailyTripCard = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private final SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");
    private final SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");

    private final SelenideElement formHeading = $x("//form//preceding-sibling::h3");
    private final SelenideElement form = $x("//form");
    private final SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
    private final SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");

    public CardPage() {
        dailyTripHeading.should(Condition.visible, Condition.text("Путешествие дня"));
        dailyTripCard.should(Condition.visible);

        payButton.should(Condition.visible);
        creditButton.should(Condition.visible);

        formHeading.should(Condition.hidden);
        form.should(Condition.hidden);
        successNotification.should(Condition.hidden);
        errorNotification.should(Condition.hidden);
    }

    public PaymentPage clickPayButton() {
        payButton.click();
        formHeading.should(Condition.visible, Condition.text("Оплата по карте"));
        return new PaymentPage();
    }

    public PaymentPage clickCreditButton() {
        creditButton.click();
        formHeading.should(Condition.visible, Condition.text("Кредит по данным карты"));
        return new PaymentPage();
    }

    public int getAmount() {
        var str = dailyTripCard.$x(".//ul/li[contains(text(), 'руб')]").getText().split(" ");
        return Integer.valueOf(str[1] + str[2]);
    }
}