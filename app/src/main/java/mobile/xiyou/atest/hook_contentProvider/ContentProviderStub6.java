package mobile.xiyou.atest.hook_contentProvider;

/**
 * Created by miaojie on 2017/5/15.
 */

public class ContentProviderStub6 extends ContentProviderBase {
    @Override
    public boolean onCreate() {
        this.AUTHORITY="mobile.xiyou.atest.hook_contentProvider.ContentProviderStub6";
        return super.onCreate();
    }
}