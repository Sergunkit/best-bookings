package aim.hotel_booking.impl;

import aim.hotel_booking.service.TokenService;
import org.openapitools.api.TokensApiDelegate;
import org.openapitools.model.AuthInfo;
import org.openapitools.model.TokenInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CustomTokensApiDelegate implements TokensApiDelegate {
    private final TokenService tokenService;

    public CustomTokensApiDelegate(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<TokenInfo> tokensCreate(AuthInfo authInfo) {
        TokenInfo tokenInfo = tokenService.createToken(authInfo);
        return ResponseEntity.status(201).body(tokenInfo);
    }
}
