package internal.automator.automator

import android.app.Application
import androidx.room.Room
import cn.vove7.andro_accessibility_api.AccessibilityApi
import internal.automator.automator.automation.CustomAccessibilityService
import internal.automator.automator.repo.local.AppDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AccessibilityApi.init(applicationContext, CustomAccessibilityService::class.java)
        try {
            appDatabase = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "appDatabase"
            ).build()
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    companion object {
        lateinit var appDatabase: AppDatabase
    }
}