package TNTmusic.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONObject;

import java.time.Instant;

public class TokenManager {
  private static final Dotenv dotenv = Dotenv.load();
  private static final String CLIENT_ID = dotenv.get("CLIENT_ID");
  private static final String CLIENT_SECRET = dotenv.get("CLIENT_SECRET");

  private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
  private static final String API_BASE_URL = "https://api.spotify.com/v1";

  private String accessToken;
  private Instant tokenExpiration;

  public TokenManager() {
    this.accessToken = null;
    this.tokenExpiration = Instant.MIN; 
  }

  public String getAccessToken() {
    if (accessToken == null || Instant.now().isAfter(tokenExpiration)) {
      refreshToken();
    }
    return accessToken;
  }

  private synchronized void refreshToken() {
    try {
      HttpResponse<JsonNode> response = Unirest.post(TOKEN_URL)
          .header("Content-Type", "application/x-www-form-urlencoded")
          .field("grant_type", "client_credentials")
          .field("client_id", CLIENT_ID)
          .field("client_secret", CLIENT_SECRET)
          .asJson();
      System.out.println("Request del token de acceso: " + response.getBody());

      JSONObject jsonResponse = response.getBody().getObject();
      accessToken = jsonResponse.getString("access_token");
      int expiresIn = jsonResponse.getInt("expires_in");
      tokenExpiration = Instant.now().plusSeconds(expiresIn);

      System.out.println("Token renovado. Nuevo Access Token: " + accessToken);
    } catch (Exception e) {
      System.err.println("Error al refrescar el token: " + e.getMessage());
    }
  }

  public String getApiBaseUrl() {
    return API_BASE_URL;
  }
}
