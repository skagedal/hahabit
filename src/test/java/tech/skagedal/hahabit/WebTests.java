package tech.skagedal.hahabit;

import static org.assertj.core.api.Assertions.assertThat;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tech.skagedal.hahabit.testing.Containers;
import tech.skagedal.hahabit.testing.TestDataManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebTests {
    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    private ServletWebServerApplicationContext servletContext;

    private TestDataManager testDataManager;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    private final WebClient webClient = new WebClient();

    @BeforeEach
    void setupTestDataManager() {
        testDataManager = new TestDataManager(userDetailsManager);
    }

    @AfterEach
    void closeWebClient() {
        webClient.close();
    }

    @DynamicPropertySource
    static void registerPostgreSQLProperties(DynamicPropertyRegistry registry) {
        Containers.registerDynamicProperties(registry);
    }

    @Test
    void home_redirects_to_login() {
        final var response = send(GET(uri("/")).build());

        assertThat(response.statusCode())
            .isEqualTo(HttpStatus.FOUND.value()); // that's a 302 redirect
        assertThat(response.headers().firstValue("Location"))
            .isPresent()
            .hasValue(uri("/login").toString());
    }

    @Test
    void can_login_and_add_a_habit() throws IOException {
        final var username = testDataManager.createRandomUser();

        final HtmlPage start = webClient.getPage(url("/"));

        // Log in
        final HtmlForm signInForm = start.getForms().get(0);
        final HtmlTextInput usernameField = signInForm.getInputByName("username");
        final HtmlPasswordInput passwordField = signInForm.getInputByName("password");
        final HtmlButton button = signInForm.getFirstByXPath("//button[@type='submit']");

        usernameField.type(username);
        passwordField.type(TestDataManager.PASSWORD);
        final HtmlPage loggedInPage = button.click();

        assertThat(loggedInPage.asNormalizedText()).contains("Manage my habits");

        // Go to "Manage my habits"
        final HtmlAnchor manageHabitsLink = loggedInPage.getFirstByXPath("//a[@id='manage-habits']");

        final HtmlPage manageHabits = manageHabitsLink.click();

        // Add a new habit
        final HtmlForm addHabitForm = manageHabits.getForms().get(0);
        final HtmlTextInput habitDescriptionField = addHabitForm.getInputByName("description");
        final HtmlButton addHabitButton = addHabitForm.getFirstByXPath("//button[@type='submit']");

        habitDescriptionField.type("Go for a walk");
        final HtmlPage manageHabitsPageAfterAddingHabit = addHabitButton.click();

        assertThat(manageHabitsPageAfterAddingHabit.asNormalizedText()).contains("Go for a walk");

        // Go back to home
        final HtmlAnchor homeLink = manageHabitsPageAfterAddingHabit.getAnchorByHref("/");
        final HtmlPage home = homeLink.click();

        System.out.println(home.asXml());
    }

    // Helpers

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + servletContext.getWebServer().getPort() + path);
    }

    private URL url(String path) {
        try {
            return uri(path).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpRequest.Builder GET(URI uri) {
        return HttpRequest.newBuilder(uri).GET();
    }
}
