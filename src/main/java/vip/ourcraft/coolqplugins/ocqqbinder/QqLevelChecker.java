package vip.ourcraft.coolqplugins.ocqqbinder;

import vip.ourcraft.coolqplugins.ocqqbinder.utils.HttpUtil;

/**
 * Created by July on 2018/2/2.
 */
public class QqLevelChecker {
    public static int getQqLevel(long targetQq, String cookie) {
        String response = HttpUtil.sendGet("http://vip.qq.com/pk/index?param=" + targetQq, cookie);
        int startIndex = response.indexOf("var GUEST_LEVEL_INFO = {\"");
        String qqInfo = response.substring(startIndex, response.length());
        String level = qqInfo.substring(qqInfo.indexOf("iQQLevel\":\"") + 11, qqInfo.indexOf("\",\"iQQSportStep"));

        return Integer.valueOf(level);
    }
}
