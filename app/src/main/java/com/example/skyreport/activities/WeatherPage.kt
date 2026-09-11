package com.example.skyreport.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.skyreport.R
import com.example.skyreport.data.api.WeatherApi
import com.example.skyreport.data.models.weather.WeatherResponce
import com.example.skyreport.data.repo.WeatherRepo
import com.example.skyreport.databinding.ActivityWeatherPageBinding
import com.example.skyreport.service.LocationHelper
import com.example.skyreport.ui.viewmodels.AuthViewmodel
import com.example.skyreport.ui.viewmodels.AuthViewmodelFactory
import com.example.skyreport.ui.viewmodels.WeatherViewmodel
import com.example.skyreport.ui.viewmodels.WeatherViewmodelFactory
import com.example.skyreport.utils.NatworkUtils
import com.example.skyreport.utils.Resources
import com.google.android.material.snackbar.Snackbar
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.skyreport.BuildConfig
import com.example.skyreport.service.WeatherCheackworker
import com.example.skyreport.utils.AuthUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class WeatherPage : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewmodel: WeatherViewmodel
    private lateinit var authViewmodel: AuthViewmodel
    private lateinit var binding: ActivityWeatherPageBinding
    private lateinit var locationhelper: LocationHelper

    private lateinit var toggol : ActionBarDrawerToggle

    private val requestpermissitionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { result ->
            if (result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                getDevicelocation()
            }
        }

    @SuppressLint("MissingPermission")
    private fun getDevicelocation() {
        lifecycleScope.launch {
            val detectCity = locationhelper.getCurrentCity()
            if (!detectCity.isNullOrEmpty()) {
                viewmodel.updateCity(detectCity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWeatherPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.fade_in, 0)

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchWeatherData()
        }

        binding.version.text = "v${BuildConfig.VERSION_NAME}"            // Set the version name in the TextView

        observeUiState()

        locationhelper = LocationHelper(this)

        checkLocationPermission()      // Check for location permission before getting the device location
        setupAuthViewmodel()          // Initialize the authentication viewmodel
        setupViewmodel()             // Initialize the weather viewmodel
        setupUi()                   // Set up the UI elements
        observeWeather()           // Observe the weather data from the viewmodel
        setupDrawer()
        fetchuserdata()


        binding.locationBtn.setOnClickListener {      // Show the search bar when clicking on the location button
            observeSearch()
        }
        binding.weathermain.setOnClickListener {      // Clear the search bar when clicking outside
            observeClear()
        }

        textBgColor()   // Set the background color of the TextView

        checkNetworkandConnection(this)     // Check for internet connection before making API calls

        scheduledWeatherUpdate()                    // Schedule the weather update notification

        checkNotificationPermission()               // Check for notification permission before showing notifications
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(
                this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            ){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    private fun scheduledWeatherUpdate() {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val weatherRequest = PeriodicWorkRequestBuilder<WeatherCheackworker>(
            15, TimeUnit.MINUTES
        ).setConstraints(constraint).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WeatherCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,
            weatherRequest
        )
    }

    fun checkNetworkandConnection(context: WeatherPage) {
        if (!isNetworkAvailable(context)){
            Toast.makeText(this, "No internet connection ", Toast.LENGTH_LONG).show()

        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        // Corrected variable name and used the more modern way to get services
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val network = connectivityManager?.activeNetwork ?: return false
        val capability = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capability.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun fetchWeatherData() {
        lifecycleScope.launch {
            try {
                viewmodel.refreshData()
            } catch (e: Exception) {
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    private fun setupDrawer() {
        toggol = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.drawerLayout.addDrawerListener(toggol)
        toggol.syncState()

        binding.menuBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            binding.overlay.setBackgroundColor("#33000000".toColorInt())

        }

        binding.logoutbtn.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            val alartbox : AlertDialog.Builder = AlertDialog.Builder(this, R.style.dialogUi)
            alartbox.setTitle("Logout")
            alartbox.setMessage("Are you sure you want to logout?")
            alartbox.setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            alartbox.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
            alartbox.show()
        }

//        val isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
//        binding.themeswitch.isChecked = isNightMode
//
//        binding.themeswitch.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked){
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            }else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//        }

    }



    private fun fetchuserdata(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                authViewmodel.userProfile.collect {state ->
                    when(state){
                        is AuthUiState.Success ->{
//                            val user = state.user
                            state.user?.let { user ->
                                user.profileImage?.let { url ->
                                    Glide.with(this@WeatherPage)
                                        .load(url)
                                        .circleCrop()
                                        .placeholder(R.drawable.baseline_person_24)
                                        .into(binding.menuBtn)
                                }
                            }
                        }
                        is AuthUiState.Error -> {
                            binding.menuBtn.setImageResource(R.drawable.baseline_person_24)
                            Log.e("weather page", "Error fetch profile: ${state.message}")
                        }
                        else -> {}
                    }
                }
            }
        }

    }














    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.weatherState.collect { state ->
                    when (state) {
                        is Resources.Loading -> {
                            if (!binding.swipeRefreshLayout.isRefreshing) {
                                binding.swipeRefreshLayout.isRefreshing = true
                            }
                        }

                        is Resources.Success -> {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }

                        is Resources.Error -> {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            }
        }
    }

    private fun updateWindStatus(currentWind: Int): String =
        when {
            currentWind < 0 -> "Invalid Reading"
            currentWind == 0 -> "It's calm"
            currentWind in 1..3 -> "There is a light breeze"
            currentWind in 4..6 -> "There is a moderate breeze"
            currentWind in 7..9 -> "There is a strong gale"
            currentWind in 10..12 -> "It's a storm"
            else -> "Invalid Reading"
        }

    private fun updateHumidityStatus(currentHumidity: Int): String =
        when {
            currentHumidity < 0 -> "Invalid Reading"
            currentHumidity in 0..30 -> "Dry Weather"
            currentHumidity in 31..50 -> "Comfortable Weather"
            currentHumidity in 51..60 -> "Humid Weather"
            currentHumidity in 61..64 -> "Very Humid Weather"
            currentHumidity in 65..80 -> "Muggy and Sticky Weather"
            currentHumidity in 81..100 -> "Saturated Weather"
            else -> "Invalid Reading"
        }

    private fun setupViewmodel() {
        val weatherApi = NatworkUtils.getRetrofitInstance().create(WeatherApi::class.java)
        val repository = WeatherRepo(weatherApi)
        val factory = WeatherViewmodelFactory(repository)
        viewmodel = ViewModelProvider(this, factory)[WeatherViewmodel::class.java]
    }

    private fun setupAuthViewmodel() {
        val factory = AuthViewmodelFactory()
        authViewmodel = ViewModelProvider(this, factory)[AuthViewmodel::class.java]
        auth = Firebase.auth

        auth.currentUser?.let { user ->
            authViewmodel.fatchUserdata(user.uid)
        }
    }

    private fun setupUi() {
        binding.searchText.setOnEditorActionListener { _, actionid, _ ->
            if (actionid == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchText.text.toString()
                if (query.isNotEmpty()) {
                    showLoading()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
                    viewmodel.updateCity(query)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
        binding.clearSearchBtn.setOnClickListener {
            observeClear()
        }
    }

    private fun observeWeather() {
        lifecycleScope.launch {
            viewmodel.weatherState.collect { state ->
                when (state) {
                    is Resources.Loading -> {
                        showLoading()
                    }

                    is Resources.Success -> {
                        hideLoading()
                        state.data?.let { updateWeatherUI(it) }
                    }

                    is Resources.Error -> {
                        hideLoading()
                        showErrorMessage(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.weatherprogressbar.visibility = View.VISIBLE
        binding.overlay.setBackgroundColor("#33000000".toColorInt())
    }

    private fun hideLoading() {
        binding.weatherprogressbar.visibility = View.GONE
    }

    fun getWeatherBackgroundResource(responce: WeatherResponce): Int {
        val currentTime = responce.dt
        val sunriseTime = responce.sys.sunrise
        val sunsetTime = responce.sys.sunset

        val isDaytime = currentTime in sunriseTime..sunsetTime
        val condition =
            responce.weather
                .firstOrNull()
                ?.main
                ?.lowercase() ?: ""
        return if (isDaytime) {
            when (condition) {
                // sunny
                "sunny" -> R.drawable.sunnyday

                "clear" -> R.drawable.clear_day

                // clouds
                "clouds", "scattered clouds" -> R.drawable.partly_cloudy

                "broken clouds", "overcast clouds" -> R.drawable.heavy_cloude_day

                // rain
                "rain", "light rain", "moderate rain" -> R.drawable.rainy

                "heavy rain" -> R.drawable.rainyday

                // snow
                "snow", "light snow" -> R.drawable.light_snow

                "heavy snow" -> R.drawable.snow

                // foggy
                "mist", "fog", "haze" -> R.drawable.foggy

                // thunder
                "thunderstorm" -> R.drawable.thander_2

                else -> R.drawable.sunnyday
            }
        } else {
            when (condition) {
                // sunny
                "sunny", "clear" -> R.drawable.night_clear

                // clouds
                "clouds", "scattered clouds" -> R.drawable.cloudy_night

                "broken clouds", "overcast clouds" -> R.drawable.heavily_cloudy

                // rain
                "rain", "light rain", "moderate rain" -> R.drawable.rainy

                "heavy rain" -> R.drawable.rainyday

                // snow
                "light snow", "snow" -> R.drawable.light_snow

                "heavy snow" -> R.drawable.snow_night

                // foggy
                "mist", "fog", "haze" -> R.drawable.foggy

                // thunder
                "thunderstorm" -> R.drawable.thander

                else -> R.drawable.night_clear
            }
        }
    }

    private fun updateWeatherUI(weather: WeatherResponce) {
        binding.apply {
            // Add this line to update the location button text
            locationBtn.text = weather.name
            searchlocation.text = weather.name
            tempt.text = "${weather.main.temp.toInt()}°"
            weatherConditionText.text = "It's ${weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }}"
            feeltemp.text = "Feels like ${weather.main.feels_like.toInt()}°"
            humidityValue.text = "${weather.main.humidity}%"
            windValue.text = weather.wind.speed.toString()
            pressureValue.text = weather.main.pressure.toString()
            visibilityValue.text = formatVisibility(weather.visibility)

            humidityText.text = updateHumidityStatus(weather.main.humidity)
            windText.text = updateWindStatus(weather.wind.speed.toInt())
            visibilityText.text = updateVisibilityStatus(weather.visibility)
            pressureText.text = updatePressureStatus(weather.main.pressure)
        }

        val backgroundResource = getWeatherBackgroundResource(weather)

        Glide
            .with(this@WeatherPage)
            .load(backgroundResource)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.weatherBgImage)
    }

    fun updatePressureStatus(pressure: Int): String =
        when{
            pressure > 1020 -> "High pressure \n possibility Clear"
            pressure < 1010 -> "Low pressure \n possibility Storm"
            else -> "Average atmospheric pressure"
        }

    fun updateVisibilityStatus(visibility: Int): String =
        when{
            visibility > 10 ->  " crystal-clear visibility"
            visibility in 4..9 -> "Good visibility"
            visibility in 1.. 4 -> "Moderate visibility"
            visibility.toDouble() in 0.5..1.0 -> "Poor visibility"
            else -> "Very poor visibility"
        }



    fun formatVisibility(visibility: Int): CharSequence = if (visibility >= 1000) "${visibility / 1000}" else "$visibility"

    private fun showErrorMessage(message: String?) {
        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_LONG).apply {
            setAction("Retry") {
                val query = binding.searchText.text.toString()
                if (query.isNotEmpty()) viewmodel.updateCity(query)
            }
            show()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getDevicelocation()
        } else {
            requestpermissitionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    fun observeClear() {
        TransitionManager.beginDelayedTransition(binding.headerContainer)
        binding.searchText.text.clear()
        binding.locationBtn.setTextColor("#F2FFFFFF".toColorInt())
        binding.locationBtn.iconTint = ColorStateList.valueOf("#F2FFFFFF".toColorInt())
        binding.searchBar.visibility = View.GONE
        binding.searchlocation.visibility = View.GONE
        getDevicelocation()
    }

    fun observeSearch() {
        TransitionManager.beginDelayedTransition(binding.headerContainer)
        binding.locationBtn.setTextColor(ContextCompat.getColorStateList(this, R.color.transparent))
        binding.locationBtn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.transparent))
        binding.searchBar.visibility = View.VISIBLE
        binding.searchlocation.visibility = View.VISIBLE
        binding.searchText.requestFocus()
    }

    private fun textBgColor() {
        val textview = findViewById<TextView>(R.id.tempt)
        textview.post {
            val paint = textview.paint
            val height = textview.height.toFloat()
            val textShader =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    height,
                    intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#89DBCFCF")),
                    null,
                    Shader.TileMode.CLAMP,
                )
            paint.shader = textShader
            textview.invalidate()
        }
    }
}
