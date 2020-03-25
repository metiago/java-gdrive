package io.zbx.services;


import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import io.zbx.configs.Constants;
import io.zbx.dto.TokenDTO;
import io.zbx.models.Token;
import io.zbx.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    private HttpSession session;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private Environment env;

    public TokenService(HttpSession session) {
        this.session = session;
    }

    private GoogleTokenResponse getAccessToken(TokenDTO tokenDTO) throws Exception {

        InputStream in = FileService.class.getResourceAsStream(Constants.CREDENTIALS_FILE_PATH);

        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + Constants.CREDENTIALS_FILE_PATH);
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(getDefaultInstance(), new InputStreamReader(in));

        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        getDefaultInstance(),
                        Constants.TOKEN_URL,
                        clientSecrets.getDetails().getClientSecret(),
                        env.getProperty("CLIENT_SECRET"),
                        tokenDTO.getCode(),
                        clientSecrets.getDetails().getRedirectUris().get(0))  // Specify the same redirect URI that you use with your web
                        // app. If you don't have a web version of your app, you can
                        // specify an empty string.
                        .execute();

        return tokenResponse;
    }

    public Drive getDrive() throws Exception {

        String session = (String) this.session.getAttribute(Constants.SESSION_NAME);

        Optional<Token> token = tokenRepository.findBySubjectID(session);

        String subjectID = token.map(Token::getAccessToken).orElse(null);

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
        token.setEmailVerified(payload.getEmailVerified());
        token.setName((String) payload.get("name"));
        token.setPictureUrl((String) payload.get("picture"));
        token.setLocale((String) payload.get("locale"));
        token.setFamilyName((String) payload.get("family_name"));
        token.setGivenName((String) payload.get("given_name"));

        tokenRepository.save(token);

        request.getSession().setAttribute(Constants.SESSION_NAME, payload.getSubject());
    }
}
