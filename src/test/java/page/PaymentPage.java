package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$x;


public class PaymentPage {
    private final SelenideElement dailyTripCard = $x("//div[@id='root']/div/div[contains(@class, 'card')]");

    private final SelenideElement payButton = $x("//span[text()='Купить']//ancestor::button");
    private final SelenideElement creditButton = $x("//span[text()='Купить в кредит']//ancestor::button");
    private final SelenideElement form = $x("//form");
    private final SelenideElement numberLabel = form.$x(".//span[text()='Номер карты']//ancestor::div/span");
    private final SelenideElement numberInput = numberLabel.$x(".//ancestor::span//input");
    private final SelenideElement monthLabel = form.$x(".//span[text()='Месяц']//ancestor::div/span/span[1]/span");
    private final SelenideElement monthInput = monthLabel.$x(".//input");
    private final SelenideElement yearLabel = form.$x(".//span[text()='Год']//ancestor::div/span/span[2]/span");
    private final SelenideElement yearInput = yearLabel.$x(".//input");
    private final SelenideElement holderLabel = form.$x(".//span[text()='Владелец']//ancestor::div/span/span[1]/span");
    private final SelenideElement holderInput = holderLabel.$x(".//input");
    private final SelenideElement cvcLabel = form.$x(".//span[text()='CVC/CVV']//ancestor::div/span/span[2]/span");
    private final SelenideElement cvcInput = cvcLabel.$x(".//input");
    private final SelenideElement continuousButton = form.$x(".//span[text()='Продолжить']//ancestor::button");

    private final SelenideElement successNotification = $x("//div[contains(@class, 'notification_status_ok')]");
    private final SelenideElement successCloseButton = successNotification.$x("./button");
    private final SelenideElement errorNotification = $x("//div[contains(@class, 'notification_status_error')]");
    private final SelenideElement errorCloseButton = errorNotification.$x("./button");

    public PaymentPage() {
        dailyTripCard.should(Condition.visible);
        payButton.should(Condition.visible);
        creditButton.should(Condition.visible);

        form.should(Condition.visible);
        successNotification.should(Condition.hidden);
        errorNotification.should(Condition.hidden);
    }

    public void insertingValueInForm(String number, String month, String year, String holder, String cvc) {
        numberLabel.click();
        numberInput.val(number);
        monthLabel.click();
        monthInput.val(month);
        yearLabel.click();
        yearInput.val(year);
        holderLabel.click();
        holderInput.val(holder);
        cvcLabel.click();
        cvcInput.val(cvc);
        continuousButton.click();
    }

    public void matchesByInsertValue(String number, String month, String year, String holder, String cvc) {
        numberInput.shouldHave(value(number));
        monthInput.shouldHave(value(month));
        yearInput.shouldHave(value(year));
        holderInput.shouldHave(value(holder));
        cvcInput.shouldHave(value(cvc));
    }

    public void assertBuyOperationIsSuccessful() {
        successNotification.should(Condition.visible, Duration.ofSeconds(15));
        successNotification.should(Condition.cssClass("notification_visible"));
        successNotification.$x("./div[@class='notification__title']").should(Condition.text("Успешно"));
        successNotification.$x("./div[@class='notification__content']").should(Condition.text("Операция одобрена Банком."));
        successCloseButton.click();
        successNotification.should(Condition.hidden);
    }

    public void assertBuyOperationWithErrorNotification() {
        errorNotification.should(Condition.visible, Duration.ofSeconds(15));
        errorNotification.should(Condition.cssClass("notification_visible"));
        errorNotification.$x("./div[@class='notification__title']").should(Condition.text("Ошибка"));
        errorNotification.$x("./div[@class='notification__content']").should(Condition.text("Ошибка! Банк отказал в проведении операции."));
        errorCloseButton.click();
        errorNotification.should(Condition.hidden);
    }

    private SelenideElement getLabelElement(String field) {
        switch (field) {
            case "number":
                return numberLabel;
            case "month":
                return monthLabel;
            case "year":
                return yearLabel;
            case "holder":
                return holderLabel;
            case "cvc":
                return cvcLabel;
            default:
                return null;
        }
    }

    public void assertField(String field, String errorMessage) {
        SelenideElement label = getLabelElement(field);

        if (errorMessage.equals("Поле обязательно для заполнения")) {
            assert label != null;
            label.shouldHave(Condition.cssClass("input_invalid"))
                    .shouldNotHave(Condition.cssClass("input_has-value"));
        } else if (errorMessage.equals("Неверный формат")) {
            assert label != null;
            label.shouldHave(Condition.cssClass("input_invalid"))
                    .shouldNotHave(Condition.cssClass("input_has-value"));
        } else if (errorMessage.equals("Истёк срок действия карты")) {
            assert label != null;
            label.shouldHave(Condition.cssClass("input_invalid"))
                    .shouldNotHave(Condition.cssClass("input_has-value"));
        } else if (errorMessage.equals("Неверно указан срок действия карты")) {
            assert label != null;
            label.shouldHave(Condition.cssClass("input_has-value"))
                    .shouldNotHave(Condition.cssClass("input_has-value"));
        }
    }

}