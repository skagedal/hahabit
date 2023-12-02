package tech.skagedal.hahabit;

import static org.assertj.core.api.Assertions.assertThat;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tech.skagedal.hahabit.testing.HahabitTest;
import tech.skagedal.hahabit.testing.TestDataManager;
import tech.skagedal.hahabit.testing.TestServer;

@HahabitTest
class WebTests {
    private final TestServer server;
    private final TestDataManager testDataManager;
    private final WebClient webClient = new WebClient();

    public WebTests(@Autowired TestServer server, @Autowired TestDataManager testDataManager) {
        this.server = server;
        this.testDataManager = testDataManager;
    }

    @AfterEach
    void closeWebClient() {
        webClient.close();
    }

    @Test
    void can_login_add_a_habit_and_track_it() throws IOException {
        final var username = testDataManager.createRandomUser();

        final HtmlPage start = webClient.getPage(server.url("/"));

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

        // Achieve the habit
        final HtmlForm trackingForm = home.getForms().get(0);
        final HtmlSubmitInput trackHabit = trackingForm.getFirstByXPath("//input[@type='submit']");
        assertThat(trackHabit.isDisabled()).isFalse();

        final HtmlPage pageAfterTrackingHabit = trackHabit.click();

        final HtmlForm trackingFormAfterTracked = pageAfterTrackingHabit.getForms().get(0);
        final HtmlSubmitInput trackHabitAfterTracked = trackingFormAfterTracked.getFirstByXPath("//input[@type='submit']");
        assertThat(trackHabitAfterTracked.isDisabled()).isTrue();
    }
}
