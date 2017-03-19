package enjoytoday.com.dao;

/**
 * Created by hfcai on 19/03/17.
 */

public interface HttpResponseListener {

    /**
     *
     *
     * @param responseCode   请求返回状态码,200为请求成功
     * @param response 请求返回状态信息,请求成功返回OK,否则返回错误信息
     * @param content   请求成功后的返回信息,若未成功,则返回为null
     * http请求回调
     */
    void callBack(int responseCode,String response,String content);


}
