package vip.ourcraft.coolqplugins.ocqqbinder;

public class Settings {
    private int minBindQqLevel;
    private int maxPwdWrong;
    private int maxPwdWrongBanSec;

    public int getMinBindQqLevel() {
        return minBindQqLevel;
    }

    public void setMinBindQqLevel(int minBindQqLevel) {
        this.minBindQqLevel = minBindQqLevel;
    }

    public int getMaxPwdWrong() {
        return maxPwdWrong;
    }

    public void setMaxPwdWrong(int maxPwdWrong) {
        this.maxPwdWrong = maxPwdWrong;
    }

    public int getMaxPwdWrongBanSec() {
        return maxPwdWrongBanSec;
    }

    public void setMaxPwdWrongBanSec(int maxPwdWrongBanSec) {
        this.maxPwdWrongBanSec = maxPwdWrongBanSec;
    }
}
