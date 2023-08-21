package com.edumento.core.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 2/18/16. */
@ConfigurationProperties(prefix = "mint")
@Component
public class MintProperties {

  private final Async async = new Async();
  private final Security security = new Security();
  private final Upload upload = new Upload();

  private String url;
  private String apiDomain;

  public Upload getUpload() {
    return upload;
  }

  public Async getAsync() {
    return async;
  }

  public Security getSecurity() {
    return security;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getApiDomain() {
    return apiDomain;
  }

  public void setApiDomain(String apiDomain) {
    this.apiDomain = apiDomain;
  }

  public static class Async {

    private int corePoolSize = 2;

    private int maxPoolSize = 50;

    private int queueCapacity = 10000;

    public int getCorePoolSize() {
      return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
      return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
      return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
    }
  }

  public static class Security {

    private final Rememberme rememberme = new Rememberme();

    private final Authentication authentication = new Authentication();

    public Rememberme getRememberme() {
      return rememberme;
    }

    public Authentication getAuthentication() {
      return authentication;
    }

    public static class Authentication {

      private final Oauth oauthAndroid = new Oauth();
      private final Oauth oauthWeb = new Oauth();
      private final Oauth oauthControlPanel = new Oauth();
      private final Oauth oauthQuestionBank = new Oauth();
      private final Oauth oauthIos = new Oauth();

      public Oauth getOauthAndroid() {
        return oauthAndroid;
      }

      /**
       * @return the oauthIosOauth
       */
      public Oauth getOauthIos() {
        return oauthIos;
      }

      public Oauth getOauthControlPanel() {
        return oauthControlPanel;
      }

      public Oauth getOauthWeb() {
        return oauthWeb;
      }

      public Oauth getOauthQuestionBank() {
        return oauthQuestionBank;
      }

      public static class Oauth {

        private String clientid;

        private String secret;

        private int tokenValidityInSeconds = 1800;

        public String getClientid() {
          return clientid;
        }

        public void setClientid(String clientid) {
          this.clientid = clientid;
        }

        public String getSecret() {
          return secret;
        }

        public void setSecret(String secret) {
          this.secret = secret;
        }

        public int getTokenValidityInSeconds() {
          return tokenValidityInSeconds;
        }

        public void setTokenValidityInSeconds(int tokenValidityInSeconds) {
          this.tokenValidityInSeconds = tokenValidityInSeconds;
        }
      }
    }

    public static class Rememberme {

      @NotNull private String key;

      public String getKey() {
        return key;
      }

      public void setKey(String key) {
        this.key = key;
      }
    }
  }

  public static class Upload {

    private final Img img = new Img();
    private final Data data = new Data();

    public Data getData() {
      return data;
    }

    public Img getImg() {
      return img;
    }

    public static class Img {

      private String path;

      public String getPath() {
        return path;
      }

      public void setPath(String path) {
        this.path = path;
      }
    }

    public static class Data {

      private String path;

      public String getPath() {
        return path;
      }

      public void setPath(String path) {
        this.path = path;
      }
    }
  }
}
