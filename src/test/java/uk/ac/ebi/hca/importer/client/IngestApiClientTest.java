package uk.ac.ebi.hca.importer.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IngestApiClientTest {

    private final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1qTkZRa1U0UWtGRlFqUXdRVVEwUlVZNFJqWkZOa1kxUmtVMk9EWXdORE5EUWprd1FrRTFPQSJ9.eyJpc3MiOiJodHRwczovL2RhbmllbHZhdWdoYW4uZXUuYXV0aDAuY29tLyIsInN1YiI6Ilpkc29nNG5EQW5oUTk5eWlLd01RV0FQYzJxVURsUjk5QGNsaWVudHMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJpYXQiOjE1MTk5ODgyNTMsImV4cCI6MTUyMDA3NDY1MywiYXpwIjoiWmRzb2c0bkRBbmhROTl5aUt3TVFXQVBjMnFVRGxSOTkiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.FJINm7PDG8s3qlzFHmk3Os9vdS1mDdXTsXkDKNZ4z9rRLfd0IICu7b2g81d7YFv6dIrtoCsN3GpE3A4CSysCo3bdtQEZmvyRE1ZQbcqhEGh2sIDqIVd9Q_T_e3eaGBJbs2x0GHnKk-fXtWItjzo5LNddRK1QEGXmggb5eDL60Ms4HCR82YxB84IeshbX5grZvzbLgYbQMjaoPA5aBngcEMrlyLFLccKAGMvu8Gc2GGP9Bpsx5hEZbDGnT4IPag0Sk7Ik8W_jQEcz0AtMfKlFekyYWKwM_22DDsgYm9RQSNVhFY8RKEVFLP-h4wSlncIZ7OWmbkMzSThW9dn172h4Gg";

    @Test
    public void test_create_submission() {
        IngestApiClient ingestApiClient = new IngestApiClient();
        String result = ingestApiClient.createSubmission(token);
        assertTrue(result.startsWith("http://localhost:8080/submissionEnvelopes/"));
    }
}
