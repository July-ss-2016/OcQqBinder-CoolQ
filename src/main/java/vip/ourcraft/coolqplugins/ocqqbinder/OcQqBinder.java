package vip.ourcraft.coolqplugins.ocqqbinder;

import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqAppAbstract;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import vip.ourcraft.coolqplugins.ocqqbinder.managers.QqBinderManager;
import vip.ourcraft.coolqplugins.ocqqbinder.managers.SecondaryPasswordAccountManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class OcQqBinder extends JcqAppAbstract {
    private QqBinderManager qqBinderManager;
    private SecondaryPasswordAccountManager secondaryPasswordAccountManager;
    private Settings settings;
    private HashMap<Long, Integer> pwdWrongTimes;
    private HashMap<Long, Long> pardonTimes;

    private MySQLSettings getMySQLSettings(Config config) {
        MySQLSettings result = new MySQLSettings();

        result.setMySQLHost(config.getString("host"));
        result.setMySQLPort(config.getInt("port"));
        result.setMySQLUserName(config.getString("username"));
        result.setMySQLPassword(config.getString("password"));
        result.setMySQLDatabase(config.getString("database"));
        result.setMySQLTableName(config.getString("tablename"));
        return result;
    }

    private void loadConfig() {
        File file = new File(new File(CQ.getAppDirectory().substring(0, CQ.getAppDirectory().length() - 1)).getParentFile().getAbsolutePath() + File.separator + "vip.ourcraft.coolqplugins.ocqqbinder" + File.separator + "config.conf");

        if (!file.exists()) {
            try {
                Files.copy(getClass().getClassLoader().getResourceAsStream("config.conf"), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Config config = ConfigFactory.parseFile(file);
        MySQLSettings qqBinderMySQLSettings = getMySQLSettings(config.getConfig("mysql_qqbinder"));
        MySQLSettings secondaryPwdMysqlSettings = getMySQLSettings(config.getConfig("mysql_secondary_pwd"));
        this.qqBinderManager = new QqBinderManager(qqBinderMySQLSettings);
        this.secondaryPasswordAccountManager = new SecondaryPasswordAccountManager(secondaryPwdMysqlSettings);
        this.settings = new Settings();

        settings.setMinBindQqLevel(config.getInt("min_qq_level"));
        settings.setMaxPwdWrong(config.getInt("max_pwd_wrong"));
        settings.setMaxPwdWrongBanSec(config.getInt("max_pwd_wrong_ban_sec"));
    }

    @Override
    public String appInfo() {
        return "1.0.1,vip.ourcraft.coolqplugins.ocqqbinder";
    }

    @Override
    public int startup() {
        this.pwdWrongTimes = new HashMap<>();
        this.pardonTimes = new HashMap<>();

        loadConfig();
        CQ.logInfo("OcQqBinder-CoolQ", "初始化完毕!");
        return 0;
    }

    @Override
    public int exit() {
        return 0;
    }

    @Override
    public int enable() {
        return 0;
    }

    @Override
    public int disable() {
        return 0;
    }

    @Override
    public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
        if (msg.toLowerCase().startsWith("绑定qq")) {
            String[] msgArray = msg.replace("<", "").replace(">", "").split(" ");

            if (msgArray.length != 3) {
                CQ.sendPrivateMsg(fromQQ, "格式错误, 格式示范: 绑定QQ Notch 123456");
                return 1;
            }

            if (pardonTimes.containsKey(fromQQ)) {
                if (pardonTimes.get(fromQQ) > System.currentTimeMillis()) {
                    CQ.sendPrivateMsg(fromQQ, "您的QQ已被封禁, 暂时无法绑定QQ!");
                    return 1;
                }

                pardonTimes.remove(fromQQ);
            }

            if (qqBinderManager.isBoundQq(msgArray[1])) {
                CQ.sendPrivateMsg(fromQQ, msgArray[1] + " 已绑定QQ!");
                return 1;
            }

            if (QqLevelChecker.getQqLevel(fromQQ, CQ.getCookies()) < settings.getMinBindQqLevel()) {
                CQ.sendPrivateMsg(fromQQ, "您的QQ等级过低, 无法绑定QQ!");
                return 1;
            }

            if (!secondaryPasswordAccountManager.isRegistered(msgArray[1])) {
                CQ.sendPrivateMsg(fromQQ, msgArray[1] + " 还未设置二级密码, 请在游戏内输入指令 /sp reg <六位数字密码> 来设置二级密码. 如果您已设置二级密码, 请检查ID的正确性!");
                return 1;
            }

            if (!msgArray[2].matches("\\d{6}")) {
                CQ.sendPrivateMsg(fromQQ, "二级密码为六位数字!");
                return 1;
            }

            if (!secondaryPasswordAccountManager.isRightPassword(msgArray[1], msgArray[2])) {
                pwdWrongTimes.put(fromQQ, pwdWrongTimes.getOrDefault(fromQQ, 0) + 1);

                if (pwdWrongTimes.getOrDefault(fromQQ, 0) > settings.getMaxPwdWrong()) {
                    pwdWrongTimes.remove(fromQQ);
                    pardonTimes.put(fromQQ, System.currentTimeMillis() + settings.getMaxPwdWrongBanSec() * 1000L);
                }

                CQ.sendPrivateMsg(fromQQ, "二级密码输入错误, 请重试!");
                return 1;
            }

            qqBinderManager.bindQq(msgArray[1], fromQQ);
            CQ.sendPrivateMsg(fromQQ, "QQ绑定成功!在游戏中输入 /qq info 来确认绑定状态.");
            return 1;
        }

        return 0;
    }

    @Override
    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg, int font) {
        return 0;
    }

    @Override
    public int discussMsg(int subtype, int msgId, long fromDiscuss, long fromQQ, String msg, int font) {
        return privateMsg(subtype, msgId, fromQQ, msg, font);
    }

    @Override
    public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
        return 0;
    }

    @Override
    public int groupAdmin(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
        return 0;
    }

    @Override
    public int groupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        return 0;
    }

    @Override
    public int groupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        return 0;
    }

    @Override
    public int friendAdd(int subtype, int sendTime, long fromQQ) {
        return 0;
    }

    @Override
    public int requestAddFriend(int subtype, int sendTime, long fromQQ, String msg, String responseFlag) {
        CQ.setFriendAddRequest(responseFlag, IRequest.REQUEST_ADOPT, null);
        return 1;
    }

    @Override
    public int requestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg, String responseFlag) {
        return 0;
    }
}
