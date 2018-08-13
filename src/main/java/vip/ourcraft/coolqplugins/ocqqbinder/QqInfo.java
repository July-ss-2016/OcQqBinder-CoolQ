package vip.ourcraft.coolqplugins.ocqqbinder;

public class QqInfo {
    private long qq;
    private String playerName;

    public QqInfo(long qq, String playerName) {
        this.qq = qq;
        this.playerName = playerName;
    }

    public long getQq() {
        return qq;
    }

    public String getPlayerName() {
        return playerName;
    }
}
