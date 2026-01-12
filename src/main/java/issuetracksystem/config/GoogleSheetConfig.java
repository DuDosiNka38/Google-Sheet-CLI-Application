package issuetracksystem.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;

@Configuration
public class GoogleSheetConfig {

    //private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/spreadsheets");

    @Bean
    public Sheets sheets(@Value("${app.googleSheets.credentialsPath}") String credentialsPath)
            throws IOException, GeneralSecurityException {
        GoogleCredentials credentials;
        try (InputStream in = new FileInputStream(credentialsPath)) {
            credentials = GoogleCredentials.fromStream(in).createScoped(List.of(SheetsScopes.SPREADSHEETS));
        }
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials)).setApplicationName("Issue Track System").build();
    }

}
