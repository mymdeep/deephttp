package deep.com.deephttp;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by wangfei on 2018/1/29.
 */

public class MyQueue {
    private static ThreadPoolExecutor
        executor = new ThreadPoolExecutor(3, 6, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    private static Handler uiHandler;
    public static void runInBack(Runnable  runnable){
        executor.execute(runnable);
    }
    public static void runInMain(Runnable runnable) {
        if (uiHandler == null) {
            uiHandler = new Handler(Looper.getMainLooper());
        }
        uiHandler.post(runnable);
    }

}
