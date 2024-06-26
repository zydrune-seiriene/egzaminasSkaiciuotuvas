import org.example.Main;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class skaiciuotuvasTest {
    public static final String USER_NAME = Main.generateRandomUserName();
    public static final int FIRST_NUMBER = Main.generateRandomNumber();
    public static final int NEXT_NUMBER = Main.generateRandomNumber();

    @BeforeAll
    public static void setUp(){
        Main.setup();
    }
    @AfterAll
    public static void tearDown() {
        if (Main.browser != null) {
            Main.browser.quit();
        }
    }
    @Test
    @Order(1)

    public void PositiveCreateNewUser() { //iskviecia metoda kurianti nauja vartotoja,
        Main.createNewUser(USER_NAME, true);
        assertUserLoggedIn(USER_NAME); //patikrina ar prisijunge
        Main.logoutUserWithButton(); //atjungia vartotoja
    }

    private void assertUserLoggedIn(String userName) { //Patikrina ar tekstas atitinka laukiama formata "Logout, " + vartotojo vardas.
        String result = Main.browser.findElement(By.xpath("//nav//ul[2]/a")).getText();
        Assertions.assertEquals("Logout, " + userName, result);
    }

    @Test
    @Order(2)
    public void NegativeNewUser() { //iskviecia metoda kurianti nauja vartotoja
        Main.createNewUser(USER_NAME, false); //su false reiksme kad slaptazodziai nesutaptu
        assertPasswordMismatchError(); //tikrina klaidos pranesima
        Main.browser.get(Main.MAIN_URL);
    }
    private void assertPasswordMismatchError() { //suranda klaidos pranesima pagal jo ID ir juos palygina
        String result = Main.browser.findElement(By.id("passwordConfirm.errors")).getText();
        Assertions.assertEquals("Įvesti slaptažodžiai nesutampa", result);
    }

    @Test
    @Order(3)
    public void Positivelogin(){

        Main.loginUser(USER_NAME); //iskviecia metoda prijunianti vartotoja su sukurtais domenimis
        String result = Main.browser.findElement(By.xpath("/html/body/nav/div/ul[2]/a")).getText();
        Assertions.assertEquals("Logout, " + USER_NAME, result); //pagal zinute patikrina ar prisijungimas pavyko
        Main.logoutUserWithButton(); // atsijungia
    }

    @Test
    @Order(4)
    public void Negativelogin() {
        Main.loginUser("wrongUserName"); //iskviecia metoda prisijungimo su blogu vardu
        assertInvalidLoginError(); //patikrina ar pranesimai sutampa
        Main.browser.get(Main.MAIN_URL); //nepavykus grysta i pagrindini
    }

    private void assertInvalidLoginError() { // randa pranesima pagal jo xpath ir palygina
        String result = Main.browser.findElement(By.xpath("/html/body/div/form/div/span[2]")).getText();
        Assertions.assertEquals("Įvestas prisijungimo vardas ir/ arba slaptažodis yra neteisingi", result);
    }

    @Test
    @Order(5)
    public void PositiveCalculation() {
        Main.loginUser(USER_NAME); //iskviecia metoda prijungia vartotoja
        Main.performCalculation(String.valueOf(FIRST_NUMBER), String.valueOf(NEXT_NUMBER));//iskviecia skaiciavimo metoda naudodami skaicius
        assertCalculationResult(FIRST_NUMBER, NEXT_NUMBER); //patikrina rezultata lygindamas skaicius
        Main.logoutUserWithButton(); // atsijungia
    }

    private void assertCalculationResult(int firstNumber, int nextNumber) { // randa rezultatus pagal xpath ir palygina
        String result = Main.browser.findElement(By.xpath("/html/body/h4")).getText();
        Assertions.assertEquals(firstNumber + " + " + nextNumber + " = " + (firstNumber + nextNumber), result);
    }

    @Test
    @Order(6)
    public void NegativeCalculation() {
        Main.loginUser(USER_NAME); //iskviecia metoda prijungti vartotoja
        Main.performCalculation("-3", "9"); //atlieka skaiciavima su neigiamais skaiciais naudodamas skaiciu su minusu
        assertCalculationError("Validacijos klaida: skaičius negali būti neigiamas");//tikrina pranesima
        Main.logoutUserWithButton(); // atsijungia
    }

    private void assertCalculationError(String expectedError) { //randa klaidos pranesima pagal id ir palygina
        String result = Main.browser.findElement(By.id("sk1.errors")).getText();
        Assertions.assertEquals(expectedError, result);
    }

    @Test
    @Order(7)
    public void PositiveSearch() { //prisijungia atlieka operacijos paieskos metoda
        Main.loginUser(USER_NAME);
        boolean result = Main.performOperationSearch(String.valueOf(FIRST_NUMBER), String.valueOf(NEXT_NUMBER), String.valueOf(FIRST_NUMBER + NEXT_NUMBER));
        assertSearchResult(result); //patikrina ar pranesimai atitinka ir atsijungia
        Main.logoutUserWithButton();
    }

    private void assertSearchResult(boolean result) {
        Assertions.assertTrue(result, "Operation search result should be true"); //patikrina ar rezultatas true
    }

    @Test
    @Order(8)
    public void NegativeSearch() {
        Main.loginUser(USER_NAME); //prijungia vartotoja
        boolean result = Main.performOperationSearch("-7", "-1", "-9"); //iraso blogus skaicius
        assertSearchResultIsFalse(result); //patkrina kad rezultatas neigiamas
        Main.logoutUserWithButton(); // atsijungia
    }

    private void assertSearchResultIsFalse(boolean result) {
        Assertions.assertFalse(result, "Operation search result should be false");
    }
}
