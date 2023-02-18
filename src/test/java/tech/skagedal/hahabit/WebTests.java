package tech.skagedal.hahabit;

import static org.assertj.core.api.Assertions.assertThat;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tech.skagedal.hahabit.testing.Containers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebTests {
    @Autowired
    private ServletWebServerApplicationContext servletContext;

    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

    private final WebClient webClient = new WebClient();

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

    static final class MyExtendedPage extends HtmlPage {
        public MyExtendedPage(WebResponse webResponse,
                              WebWindow webWindow) {
            super(webResponse, webWindow);
        }

        public void foobar() {}
    }

    @Test
    void can_login() throws IOException {
        final HtmlPage start = webClient.getPage(url("/"));
        final HtmlForm signInForm = start.getForms().get(0);
        final HtmlTextInput username = signInForm.getInputByName("username");
        final HtmlPasswordInput password = signInForm.getInputByName("password");
        final HtmlButton button = signInForm.getFirstByXPath("//button[@type='submit']");

        username.type("admin");
        password.type("admin");
        final HtmlPage loggedInPage = button.click();

        assertThat(loggedInPage.asNormalizedText()).contains("Manage my habits");
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
