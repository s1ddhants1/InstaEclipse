package android.content.res;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Method;

public class XModuleResources extends Resources {
    private final Resources delegate;

    public XModuleResources(Resources delegate) {
        super(delegate.getAssets(), delegate.getDisplayMetrics(), delegate.getConfiguration());
        this.delegate = delegate;
    }

    public static XModuleResources createInstance(String modulePath, XResources res) {
        try {
            Context ctx = AndroidAppHelper.currentApplication();
            if (ctx != null) {
                PackageManager pm = ctx.getPackageManager();
                PackageInfo pi = pm.getPackageArchiveInfo(modulePath, 0);
                if (pi != null) {
                    pi.applicationInfo.sourceDir = modulePath;
                    pi.applicationInfo.publicSourceDir = modulePath;
                    Resources r = pm.getResourcesForApplication(pi.applicationInfo);
                    return new XModuleResources(r);
                }
            }
        } catch (Throwable ignored) {}

        try {
            AssetManager am = AssetManager.class.getDeclaredConstructor().newInstance();
            Method m = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            m.setAccessible(true);
            m.invoke(am, modulePath);
            Resources r = new Resources(am, null, null);
            return new XModuleResources(r);
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public Drawable getDrawable(int id, Theme theme) {
        return delegate != null ? delegate.getDrawable(id, theme) : super.getDrawable(id, theme);
    }
}
