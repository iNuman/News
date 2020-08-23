package i.numan.news.application_

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp

@RequiresApi(Build.VERSION_CODES.O)
@HiltAndroidApp
class NewsApplication: Application() {
}