package com.blg.rtu.frmFunction.util;

import android.content.Context;
import com.blg.rtu.frmFunction.bean.AccountInfo;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
public class AccountUtil {
    private static AccountUtil single;
    private AccountInfo mAccountInfo;

    public static AccountUtil getInstance()
    {
        if (single == null) {
            single = new AccountUtil();
        }
        return single;
    }

    public void addHistoryPhone(Context paramContext, String paramString)
    {
        List localList = getHistoryPhones(paramContext);
        Object localObject = localList;
        if (localList == null) {
            localObject = new ArrayList();
        }
        ((List)localObject).remove(paramString);
        ((List)localObject).add(0, paramString);
        PreferencesUtil.putString(paramContext, "historyPhones", new Gson().toJson(localObject));
    }

    public AccountInfo getAccountInfo(Context paramContext)
    {
        if (this.mAccountInfo == null) {
            this.mAccountInfo = ((AccountInfo)JsonUtil.getModelFromJSON(PreferencesUtil.getString(paramContext, "accountInfo"), AccountInfo.class));
        }
        return this.mAccountInfo;
    }

    public String getHistoryAccount(Context paramContext)
    {
        return PreferencesUtil.getString(paramContext, "historyAccount");
    }

    public List<String> getHistoryPhones(Context paramContext)
    {
        return (List)JsonUtil.getModelFromJSON(StrUtil.nullToStr(PreferencesUtil.getString(paramContext, "historyPhones")), ArrayList.class);
    }

    public String getLoginPhone(Context paramContext)
    {
        List<String> list = getHistoryPhones(paramContext);
        if ((list != null) && (list.size() > 0)) {
            return StrUtil.nullToStr(list.get(0));
        }
        return "";
    }

    public String getToken(Context paramContext)
    {
        return PreferencesUtil.getString(paramContext, "token");
    }

    public String getUnreadMsg(Context paramContext)
    {
        return PreferencesUtil.getString(paramContext, "unreadMsg");
    }

    public boolean isAutoLoginValid(Context paramContext)
    {
        return true;
    }

    public boolean isLogin(Context paramContext)
    {
        return PreferencesUtil.getBoolean(paramContext, "isLogin");
    }

    public void login(Context paramContext)
    {
        PreferencesUtil.putBoolean(paramContext, "isLogin", true);
    }

    public void logout(Context paramContext)
    {
        PreferencesUtil.removeSharedPreferenceByKey(paramContext, "token");
        PreferencesUtil.removeSharedPreferenceByKey(paramContext, "isLogin");
        PreferencesUtil.removeSharedPreferenceByKey(paramContext, "expiry");
    }

    public void saveAccountInfo(Context paramContext, AccountInfo paramAccountInfo)
    {
        PreferencesUtil.putString(paramContext, "accountInfo", paramAccountInfo.toString());
        this.mAccountInfo = null;
    }

    public void saveAutoLogin(Context paramContext, boolean paramBoolean)
    {
        PreferencesUtil.putBoolean(paramContext, "autoLogin", paramBoolean);
    }

    public void saveExpiry(Context paramContext, long paramLong)
    {
        PreferencesUtil.putLong(paramContext, "expiry", paramLong);
    }

    public void saveHistoryAccount(Context paramContext, String paramString)
    {
        PreferencesUtil.putString(paramContext, "historyAccount", paramString);
    }

    public void saveLoginStatus(Context paramContext, boolean paramBoolean)
    {
        PreferencesUtil.putBoolean(paramContext, "isLogin", paramBoolean);
    }

    public void saveToken(Context paramContext, String paramString)
    {
        PreferencesUtil.putString(paramContext, "token", paramString);
    }

    public void saveUnreadMsg(Context paramContext, String paramString)
    {
        PreferencesUtil.putString(paramContext, "unreadMsg", paramString);
    }
}
