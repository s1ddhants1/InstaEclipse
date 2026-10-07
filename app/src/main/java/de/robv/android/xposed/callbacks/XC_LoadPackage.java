package de.robv.android.xposed.callbacks;

import android.content.pm.ApplicationInfo;

public class XC_LoadPackage {
    public static class LoadPackageParam {
        public String packageName;
        public String processName;
        public ClassLoader classLoader;
        public ApplicationInfo appInfo;
        public boolean isFirstPackage;

        public LoadPackageParam() {}

        public LoadPackageParam(String packageName, String processName, ClassLoader classLoader, ApplicationInfo appInfo, boolean isFirstPackage) {
            this.packageName = packageName;
            this.processName = processName;
            this.classLoader = classLoader;
            this.appInfo = appInfo;
            this.isFirstPackage = isFirstPackage;
        }
    }
}
