package enjoytoday.com.views.Bubble;

import java.util.List;

/**
 * Created by hfcai on 11/03/17.
 */

public class BubbleManager {
    private List<Bubble> bubbles;
    private static BubbleManager bubbleManager;

    private BubbleManager(){

    }


    /**
     *
     * @return  气泡移动管理类
     */
    public static BubbleManager getInstance(){

        synchronized (BubbleManager.class){
            if (bubbleManager==null){
                bubbleManager=new BubbleManager();
            }
        }
        return bubbleManager;
    }

}
