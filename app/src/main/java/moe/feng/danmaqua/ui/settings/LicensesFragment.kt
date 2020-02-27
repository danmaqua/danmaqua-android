package moe.feng.danmaqua.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.licenses_fragment_layout.*
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.BaseFragment
import moe.feng.danmaqua.ui.settings.list.LicenseItemViewDelegate
import moe.feng.danmaqua.util.ext.apache2
import moe.feng.danmaqua.util.ext.bsd3clause
import moe.feng.danmaqua.util.ext.licenses
import moe.feng.danmaqua.util.ext.mit

class LicensesFragment : BaseFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.LICENSES"

        val LICENSES = licenses {
            apache2 {
                name = "Android Jetpack libraries"
                author = "Google"
                url = "https://android.googlesource.com/platform/frameworks" +
                        "/support/+/androidx-master-dev"
            }
            apache2 {
                name = "Kotlinx Coroutines"
                author = "Jetbrains"
                githubUrl("Kotlin/kotlinx.coroutines")
            }
            apache2 {
                name = "Android Browser Helper"
                author = "Google"
                githubUrl("GoogleChrome/android-browser-helper")
            }
            apache2 {
                name = "Material Components for Android"
                author = "Google"
                githubUrl("material-components/material-components-android")
            }
            mit {
                name = "EventsHelper"
                author = "Siubeng (fython)"
                githubUrl("fython/EventsHelper")
            }
            apache2 {
                name = "MultiType"
                author = "Drakeet"
                githubUrl("drakeet/MultiType")
            }
            apache2 {
                name = "OkHttp"
                author = "Square"
                githubUrl("square/okhttp")
            }
            apache2 {
                name = "Picasso"
                author = "Square"
                githubUrl("square/picasso")
            }
            apache2 {
                name = "Gson"
                author = "Google"
                githubUrl("google/gson")
            }
            bsd3clause {
                name = "MMKV"
                author = "Tencent"
                githubUrl("Tencent/MMKV")
            }
            mit {
                name = "Bilibili Live API (Documentation)"
                author = "lovelyyoshino"
                githubUrl("lovelyyoshino/Bilibili-Live-API")
            }
            license {
                name = "Material Design Icons (Community)"
                author = "Austin Andrews & Other contributors"
                license = "SIL Open Font License 1.1"
                githubUrl("Templarian/MaterialDesign")
            }
            apache2 {
                name = "Material Design Icons (Google)"
                author = "Google"
                githubUrl("google/material-design-icons")
            }
            apache2 {
                name = "Named-regexp"
                author = "Anthony Trinh"
                githubUrl("tony19/named-regexp")
            }
            sortByName()
        }

    }

    private val adapter: MultiTypeAdapter = MultiTypeAdapter(LICENSES).also {
        it.register(LicenseItemViewDelegate())
    }

    override fun getActivityTitle(context: Context): String? {
        return context.getString(R.string.about_open_source_licenses_title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.licenses_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
    }

}