package com.vanced.manager.ui.fragments

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.viewpager2.widget.ViewPager2
import com.dezlum.codelabs.getjson.GetJson
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vanced.manager.R
import com.vanced.manager.adapter.SectionPageAdapter
import com.vanced.manager.adapter.SectionPageRootAdapter
import com.vanced.manager.core.fragments.Home
import com.vanced.manager.databinding.FragmentHomeBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

@Suppress("DEPRECATION")
class HomeFragment : Home() {

    private lateinit var sectionPageAdapter: SectionPageAdapter
    private lateinit var sectionPageRootAdapter: SectionPageRootAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var binding: FragmentHomeBinding
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = getString(R.string.title_home)
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNetworkFun()

        val variantPref = getDefaultSharedPreferences(activity).getString("vanced_variant", "Nonroot")

        /*val microgWrapper = view.findViewById<MaterialCardView>(R.id.home_microg_wrapper)
        if (variantPref == "Root") {
            activity?.runOnUiThread {
                microgWrapper.visibility = View.GONE
            }
        } else {
            activity?.runOnUiThread {
                microgWrapper.visibility = View.VISIBLE
            }
        }
         */

        if (variantPref == "Root")
            attachRootChangelog()
        else
            attachNonrootChangelog()

    }

    private fun initNetworkFun() {
        val pm = activity?.packageManager
        val microgStatus = pm?.let { isPackageInstalled("com.mgoogle.android.gms", it) }
        //val vancedStatus = pm?.let { isPackageInstalled("com.vanced.android.youtube", it) }
        val vancedinstallbtn = view?.findViewById<MaterialButton>(R.id.vanced_installbtn)
        val vancedLatestTxt = view?.findViewById<TextView>(R.id.vanced_latest_version)
        val networkErrorLayout = view?.findViewById<MaterialCardView>(R.id.home_network_wrapper)
        val variant = getDefaultSharedPreferences(activity).getString("vanced_variant", "Nonroot")

        disposable = ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                run {
                    if (isConnectedToInternet) {
                        vancedinstallbtn?.visibility = View.VISIBLE

                        val vancedRemoteVer =
                            GetJson().AsJSONObject("https://x1nto.github.io/VancedFiles/vanced.json")
                                .get("version").asString
                        val microgRemoteVer =
                            GetJson().AsJSONObject("https://x1nto.github.io/VancedFiles/microg.json")
                                .get("version").asString
                        vancedLatestTxt?.text = vancedRemoteVer

                        //val vancedRemoteCode =
                          //  GetJson().AsJSONObject("https://x1nto.github.io/VancedFiles/vanced.json")
                            //    .get("versionCode").asInt
                        val microgRemoteCode =
                            GetJson().AsJSONObject("https://x1nto.github.io/VancedFiles/microg.json")
                                .get("versionCode").asInt

                        if (variant == "Nonroot") {
                            val microgLatestTxt =
                                view?.findViewById<TextView>(R.id.microg_latest_version)
                            val microginstallbtn =
                                view?.findViewById<MaterialButton>(R.id.microg_installbtn)
                            microginstallbtn?.visibility = View.VISIBLE
                            microgLatestTxt?.text = microgRemoteVer

                            if (microgStatus!!) {
                                val microgVerCode =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                                        pm.getPackageInfo("com.mgoogle.android.gms", 0).longVersionCode.and(0xFFFFFFFF).toInt()
                                    else
                                        pm.getPackageInfo("com.mgoogle.android.gms", 0).versionCode
                                when {
                                    microgRemoteCode > microgVerCode -> {
                                        microginstallbtn?.text =
                                            activity?.getString(R.string.update)
                                        microginstallbtn?.icon =
                                            activity?.getDrawable(R.drawable.ic_cloud_upload_black_24dp)
                                    }

                                    microgRemoteCode == microgVerCode -> {
                                        microginstallbtn?.text =
                                            activity?.getString(R.string.button_installed)
                                        microginstallbtn?.icon =
                                            activity?.getDrawable(R.drawable.outline_cloud_done_24)
                                    }
                                }
                            }
                        }

                        /*
                        if (vancedStatus!!) {
                            val vanPkgName =
                                if (variant == "Root") {
                                    "com.google.android.youtube"
                                } else {
                                  "com.vanced.android.youtube"
                                }

                            val vancedVerCode =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    pm.getPackageInfo(
                                        vanPkgName,
                                        0
                                    ).longVersionCode.and(0xFFFFFFFF).toInt()
                                }
                                else {
                                    pm.getPackageInfo(
                                        vanPkgName,
                                        0
                                    ).versionCode
                                }

                            /*
                            when {
                                vancedRemoteCode > vancedVerCode -> {
                                    vancedinstallbtn?.text =
                                        activity?.getString(R.string.update)
                                    vancedinstallbtn?.icon =
                                        activity?.getDrawable(R.drawable.ic_cloud_upload_black_24dp)
                                }

                                vancedRemoteCode == vancedVerCode -> {
                                    vancedinstallbtn?.text =
                                        activity?.getString(R.string.button_installed)
                                    vancedinstallbtn?.icon =
                                        activity?.getDrawable(R.drawable.outline_cloud_done_24)
                                }

                            }
                             */
                        }
                         */

                        val oa2 = ObjectAnimator.ofFloat(networkErrorLayout, "yFraction", 0f, 0.3f)
                        val oa3 = ObjectAnimator.ofFloat(networkErrorLayout, "yFraction", 0.3f, -1f)

                        oa2.start()
                        oa3.apply {
                            oa3.addListener(onEnd = {
                                networkErrorLayout?.visibility = View.GONE
                            })
                            start()
                        }
                    } else {
                        if (variant == "nonroot") {
                            val microgLatestTxt =
                                view?.findViewById<TextView>(R.id.microg_latest_version)
                            val microginstallbtn =
                                view?.findViewById<MaterialButton>(R.id.microg_installbtn)
                            microginstallbtn?.visibility = View.INVISIBLE
                            microgLatestTxt?.text = getString(R.string.unavailable)
                        }

                        vancedinstallbtn?.visibility = View.INVISIBLE
                        vancedLatestTxt?.text = getString(R.string.unavailable)

                        val oa2 = ObjectAnimator.ofFloat(networkErrorLayout, "yFraction", -1f, 0.3f)
                        val oa3 = ObjectAnimator.ofFloat(networkErrorLayout, "yFraction", 0.3f, 0f)

                        oa2.apply {
                            oa2.addListener(onStart = {
                                networkErrorLayout?.visibility = View.VISIBLE
                            })
                            start()
                        }
                        oa3.start()

                    }


                }
            }
    }

    private fun attachNonrootChangelog() {
        sectionPageAdapter = SectionPageAdapter(this)
        val tabLayout = view?.findViewById(R.id.tablayout) as TabLayout
        viewPager = view?.findViewById(R.id.viewpager)!!
        viewPager.adapter = sectionPageAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Vanced"
                1 -> tab.text = "MicroG"
                2 -> tab.text = "Manager"
            }
        }.attach()
    }

    private fun attachRootChangelog() {
        sectionPageRootAdapter = SectionPageRootAdapter(this)
        val tabLayout = view?.findViewById(R.id.tablayout) as TabLayout
        viewPager = view?.findViewById(R.id.viewpager)!!
        viewPager.adapter = sectionPageRootAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Vanced"
                1 -> tab.text = "Manager"
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super .onCreateOptionsMenu(menu, inflater)
    }

}

