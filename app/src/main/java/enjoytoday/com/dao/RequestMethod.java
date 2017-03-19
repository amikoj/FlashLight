package enjoytoday.com.dao;
/**
 * request method.
 */
public  enum  RequestMethod {
    POST("POST"), GET("GET");
    private String value = "GET";

    RequestMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}