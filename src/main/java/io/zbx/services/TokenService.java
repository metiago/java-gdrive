package io.zbx.services;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import io.zbx.dto.Session;
import io.zbx.dto.TokenDTO;
import io.zbx.models.Token;
import io.zbx.repositories.MemoryDB;
import io.zbx.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance;


@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    private static final String REDIRECT_URI = "http://localhost:8001";

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

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

        Iterable<Token> tokens = tokenRepository.findAll();

        List<Token> list = new ArrayList<>();
        tokens.forEach(o -> list.add(o));

        String accessToken = list.stream().findFirst().get().getAccessToken();

        // TODO
//        for (Token token : tokens) {
//
//            for (Session session : MemoryDB.instance().all()) {
//
//                if (token.getId().equals(session.getId())) {
//                    accessToken = token.getAccessToken();
//                    break;
//                } else {
//                    throw new IllegalStateException("User not logged in.");
//                }
//            }
//        }

        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        return new Drive.Builder(new NetHttpTransport(), getDefaultInstance(), credential).setApplicationName("ZBX").build();
    }

    public void save(TokenDTO tokenDTO) throws Exception {

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

        MemoryDB.instance().add(new Session(payload.getSubject(), googleTokenResponse.getAccessToken()));
    }
}
