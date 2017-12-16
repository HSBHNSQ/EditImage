package com.lafonapps.common.preferences;

public enum RunState {

    Unknown, //表示此值尚未初始化
    NewlyInstalled, //新安装
    Upgrade, //新升级
    Latest //最新版
    ;

    @Override
    public String toString() {
        String runStateString = "";
        switch (this) {
            case Unknown:
                runStateString = "RunStateUnknown";
                break;
            case NewlyInstalled:
                runStateString = "RunStateNewlyInstalled";
                break;
            case Upgrade:
                runStateString = "RunStateUpgrade";
                break;
            case Latest:
                runStateString = "RunStateLatest";
                break;

        }
        return runStateString;
    }
}
