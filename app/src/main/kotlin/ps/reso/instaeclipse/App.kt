package ps.reso.instaeclipse

import android.app.Application
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import ps.reso.instaeclipse.core.ModuleStatus

class App : Application(), XposedServiceHelper.OnServiceListener {

    companion object {
        @Volatile
        var service: XposedService? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        XposedServiceHelper.registerListener(this)
        ModuleStatus.checkStatus()
    }

    override fun onServiceBind(service: XposedService) {
        App.service = service
        ModuleStatus.onServiceConnected(service)
    }

    override fun onServiceDied(service: XposedService) {
        if (App.service == service) {
            App.service = null
            ModuleStatus.onServiceDisconnected()
        }
    }
}
