package io.zbx.services;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import io.zbx.dto.TokenDTO;
import io.zbx.exceptions.SessionNotFoundException;
import io.zbx.models.Token;
import io.zbx.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import static com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance;


@Service
public class TokenService {

    private static final String REDIRECT_URI = "http://localhost:8001";

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final String SESSION_NAME = "SUBJECT_ID";

    private HttpSession session;

    @Autowired
    private TokenRepository tokenRepository;

    public TokenService(HttpSession session) {
        this.session = session;
    }

    private GoogleTokenResponse getAccessToken(TokenDTO tokenDTO) throws Exception {

        InputStream in = FileService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);

        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        // Exchange auth code for access token
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(getDefaultInstance(), new InputStreamReader(in));

        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        getDefaultInstance(),
                        "https://oauth2.googleapis.com/token",
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret(),
                        tokenDTO.getCode(),
                        REDIRECT_URI)  // Specify the same redirect URI that you use with your web
                        // app. If you don't have a web version of your app, you can
                        // specify an empty string.
                        .execute();

        return tokenResponse;
    }

    public Drive getDrive() throws Exception {

        String session = (String) this.session.getAttribute(SESSION_NAME);

        Optional<Token> token = tokenRepository.findBySubjectID(session);

        String subjectID = token.isPresent() ? token.get().getAccessToken() : null;

        GoogleCredential credential = new GoogleCredential().setAccessToken(subjectID);
        return new Drive.Builder(new NetHttpTransport(), getDefaultInstance(), credential).setApplicationName("ZBX").build();
    }

    public void save(TokenDTO tokenDTO, HttpServletRequest request) throws Exception {

        GoogleTokenResponse googleTokenResponse = this.getAccessToken(tokenDTO);

        GoogleIdToken idToken = googleTokenResponse.parseIdToken();

        GoogleIdToken.Payload payload = idToken.getPayload();

        Token token = new Token();
        token.setId(payload.getSubject());
        token.setCode(tokenDTO.getCode());
        token.setAccessToken(googleTokenResponse.getAccessToken());
        token.setEmail(payload.getEmail());
        token.setEmailVerified(Boolean.valueOf(payload.getEmailVerified()));
        token.setName((String) payload.get("name"));
        token.setPictureUrl((String) payload.get("picture"));
        token.setLocale((String) payload.get("locale"));
        token.setFamilyName((String) payload.get("family_name"));
        token.setGivenName((String) payload.get("given_name"));

        tokenRepository.save(token);

        request.getSession().setAttribute(SESSION_NAME, payload.getSubject());
    }
}
