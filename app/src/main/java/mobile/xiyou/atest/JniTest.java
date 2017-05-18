package mobile.xiyou.atest;

/**
 * Created by user on 2017/4/16.
 */

public class JniTest {

    static{
        System.loadLibrary("aaa");
    }

    public static native int j_fork();

}
