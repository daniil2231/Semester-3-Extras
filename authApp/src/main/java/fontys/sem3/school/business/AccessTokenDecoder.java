package fontys.sem3.school.business;

import fontys.sem3.school.domain.AccessToken;

public interface AccessTokenDecoder {
    AccessToken decode(String accessTokenEncoded);
}
