package de.robv.android.xposed;

import android.content.SharedPreferences;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import ps.reso.instaeclipse.Xposed.Module;

public class XSharedPreferences implements SharedPreferences {

    private final String prefName;
    private SharedPreferences delegate;

    public XSharedPreferences(String packageName, String prefName) {
        this.prefName = prefName;
        resolveDelegate();
    }

    public XSharedPreferences(String packageName) {
        this(packageName, packageName + "_preferences");
    }

    public XSharedPreferences(File prefFile) {
        String name = prefFile.getName();
        if (name.endsWith(".xml")) {
            name = name.substring(0, name.length() - 4);
        }
        this.prefName = name;
        resolveDelegate();
    }

    private void resolveDelegate() {
        if (Module.INSTANCE != null) {
            try {
                this.delegate = Module.INSTANCE.getRemotePreferences(prefName);
            } catch (Throwable ignored) {}
        }
    }

    public void reload() {
        resolveDelegate();
    }

    public boolean hasFileChanged() {
        return false;
    }

    public boolean makeWorldReadable() {
        return true;
    }

    @Override
    public Map<String, ?> getAll() {
        return delegate != null ? delegate.getAll() : Collections.emptyMap();
    }

    @Override
    public String getString(String key, String defValue) {
        return delegate != null ? delegate.getString(key, defValue) : defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return delegate != null ? delegate.getStringSet(key, defValues) : defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        return delegate != null ? delegate.getInt(key, defValue) : defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        return delegate != null ? delegate.getLong(key, defValue) : defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return delegate != null ? delegate.getFloat(key, defValue) : defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return delegate != null ? delegate.getBoolean(key, defValue) : defValue;
    }

    @Override
    public boolean contains(String key) {
        return delegate != null && delegate.contains(key);
    }

    @Override
    public Editor edit() {
        return delegate != null ? delegate.edit() : null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (delegate != null) {
            delegate.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (delegate != null) {
            delegate.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }
}
