package data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class DataHelper {
    public static final String APPROVED_CARD_NUMBER = "4444 4444 4444 4441";
    public static final String DECLINED_CARD_NUMBER = "4444 4444 4444 4442";
    private static final Faker faker = new Faker(Locale.ENGLISH);
    private static final Faker fakerWithCyrillicLocale = new Faker(new Locale(
            "ru",
            "RU"));

    public static CardData getValidApprovedCard() {
        return new CardData(
                APPROVED_CARD_NUMBER,
                generateMonth(1),
                generateYear(2),
                generateValidHolder(),
                generateValidCVC());
    }

    public static CardData getValidDeclinedCard() {
        return new CardData(
                DECLINED_CARD_NUMBER,
                generateMonth(1),
                generateYear(2),
                generateValidHolder(),
                generateValidCVC());
    }

    public static String getNumberByStatus(String status) {
        if (status.equalsIgnoreCase("APPROVED")) {
            return APPROVED_CARD_NUMBER;
        } else if (status.equalsIgnoreCase("DECLINED")) {
            return DECLINED_CARD_NUMBER;
        }
        return null;
    }

    public static String generateAValid13DigitCardNumber() {
        return faker.numerify("4444 4444 4444 4");
    }

    public static String generateValidCardNumberWith0Digits() {
        return faker.numerify("0000 0000 0000 0000");
    }

    public static String generateMonth(int shiftMonth) {
        return LocalDate.now().plusMonths(shiftMonth).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateYear(int shiftYear) {
        return LocalDate.now().plusYears(shiftYear).format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateValidHolder() {
        return faker.name().fullName().toUpperCase();
    }

    public static String generateInvalidHolder() {
        return faker.name().firstName().toUpperCase() + "-" + "al" + "-"
                + faker.name().lastName().toUpperCase();
    }

    public static String createInvalidHolderWithCyrillicCharacters() {
        return fakerWithCyrillicLocale.name().firstName().toUpperCase() + " "
                + fakerWithCyrillicLocale.name().lastName().toUpperCase();
    }

    public static String createInvalidHolderWith45CyrillicCharacters() {
        return fakerWithCyrillicLocale.name().firstName().toUpperCase() + 45 + " "
                + fakerWithCyrillicLocale.name().lastName().toUpperCase() + 45;
    }

    public static String createIncorrectOwnerNameRuLastNameEn() {
        return fakerWithCyrillicLocale.name().firstName().toUpperCase() + faker.name().lastName().toUpperCase();
    }

    public static String createIncorrectOwnerNameEnLastNameRu() {
        return faker.name().firstName().toUpperCase() + fakerWithCyrillicLocale.name().lastName().toUpperCase();
    }

    public static String createInvalidOwnerNameEn() {
        return faker.name().firstName().toUpperCase();
    }

    public static String generateValidCVC() {
        return faker.numerify("###");
    }

    public static String generateRandomSingleDigit() {
        return faker.numerify("#");
    }

    @Value
    public static class CardData {
        String number;
        String month;
        String year;
        String holder;
        String cvc;
    }

}