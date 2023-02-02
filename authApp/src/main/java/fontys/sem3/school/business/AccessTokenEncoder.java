package fontys.sem3.school.business;

import fontys.sem3.school.domain.AccessToken;

public interface AccessTokenEncoder {
    String encode(AccessToken accessToken);
}
