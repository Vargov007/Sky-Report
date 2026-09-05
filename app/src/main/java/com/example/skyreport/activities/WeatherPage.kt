package com.example.skyreport.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.example.skyreport.R
import com.example.skyreport.data.api.WeatherApi
import com.example.skyreport.data.repo.WeatherRepo
import com.example.skyreport.databinding.ActivityWeatherPageBinding
import com.example.skyreport.service.LocationHelper
import com.example.skyreport.ui.viewmodels.AuthViewmodel
import com.example.skyreport.ui.viewmodels.AuthViewmodelFactory
import com.example.skyreport.ui.viewmodels.WeatherViewmodel
import com.example.skyreport.ui.viewmodels.WeatherViewmodelFactory
import com.example.skyreport.utils.NatworkUtils
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.skyreport.data.models.weather.WeatherResponce
import com.example.skyreport.utils.Resources
import com.google.android.material.snackbar.Snackbar

//class WeatherPage : BaseActivity() {
//    private lateinit var auth : FirebaseAuth
//    private lateinit var viewmodel : WeatherViewmodel
//    private lateinit var authViewmodel : AuthViewmodel
////    private lateinit var toggle: ActionBarDrawerToggle
//    private lateinit var binding: ActivityWeatherPageBinding
//    private lateinit var locationhelper : LocationHelper
//
//    private val requestpermissitionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ){ result ->
//        if (result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)){
//            getDevicelocation()
//        }
//
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun getDevicelocation() {
//        lifecycleScope.launch {
//            val detectCity = locationhelper.getCurrentCity()
//            if (!detectCity.isNullOrEmpty()){
//                viewmodel.updateCity(detectCity)
//            }
//        }
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityWeatherPageBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        locationhelper = LocationHelper(this)
//        //
//        checkLocationPermission()
//        setupAuthViewmodel()
//        setupViewmodel()
//        setupUi()
//        observeWeather()
//
//        //about search bar
//        binding.locationBtn.setOnClickListener {
//            observeSearch()
//        }
//        binding.weathermain.setOnClickListener {
//            observeClear()
//        }
//
//// temp text bgcolour
//        textBgColor()
//
//// About humidity text
//        val humidity = binding.humidityText
//        val cleantext = binding.humidityValue.text.toString().trim()
//        val currentHumidity = cleantext.toIntOrNull() ?: 0
//        updateHumidityStatus(currentHumidity)
//        humidity.text = updateHumidityStatus(currentHumidity)
//
//        // About wind text
//        val wind = binding.windText
//        val windtext = binding.windValue.text.toString().trim()
//        val currentWind = windtext.toIntOrNull()?:0
//        updateWindStatus(currentWind)
//        wind.text = updateWindStatus(currentWind)
//    }
//    // About wind text
//    private fun updateWindStatus(currentWind: Int): String {
//        val statustext = when {
//            currentWind < 0 ->"Invalid Reading"
//            currentWind == 0 -> "it's calm"
//            currentWind in  1..3 -> "There is a light breeze"
//            currentWind in 4.. 6 -> "There is a moderate breeze"
//            currentWind in 7..9 -> "There is a strong gale"
//            currentWind in 10..12 -> "It's storm"
//            else -> "Invalid Reading"
//        }
//        return statustext
//    }
//
//    // About humidity text
//    private fun updateHumidityStatus(currentHumidity: Int): String {
//        val statustext = when {
//            currentHumidity < 0  -> "Invalid Reading"
//            currentHumidity in 0..30 -> " Dry Weather"
//            currentHumidity in 31..50 -> "Comfortable Weather"
//            currentHumidity in 51..60 -> "Humid Weather"
//            currentHumidity in 61..64 -> "Vary Humid Weather"
//            currentHumidity in 65..80 -> "Muggys and Sticky Weather"
//            currentHumidity in 81..100 -> "saturated Weather"
//            else -> "Invalid Reading"
//        }
//        return statustext
//    }
//
//
//    private fun setupViewmodel() {
//        val weatherApi = NatworkUtils.getRetrofitInstance().create(WeatherApi::class.java)
//        val repository = WeatherRepo(weatherApi)
//        val factory = WeatherViewmodelFactory(repository)
//         viewmodel = ViewModelProvider(this, factory)[WeatherViewmodel::class.java]
//    }
//
//    private fun setupAuthViewmodel(){
//        val factory = AuthViewmodelFactory()
//        authViewmodel = ViewModelProvider(this, factory)[AuthViewmodel::class.java]
////        auth = Firebase.auth
//
//        //first complete authViewmodel then it
////        auth.currentUser?.let {
////        }
//    }
//
//    private fun setupUi() {
//        binding.searchText.setOnEditorActionListener { _, actionid, event ->
//            (if (actionid == EditorInfo.IME_ACTION_SEARCH){
//                val quary = binding.searchText.text.toString()
//                if (quary .isEmpty()){
//                    showLoading()
//
//                    val imm = getSystemService(INPUT_METHOD_SERVICE)as InputMethodManager
//                    imm.hideSoftInputFromWindow(binding.searchText.windowToken,0)
//
//                    viewmodel.updateCity(quary)
//                }else{
//                    false
//                }
//            }else{
//                false
//            }) as Boolean
//        }
//        binding.clearSearchBtn.setOnClickListener {
//            observeClear()
//        }
//    }
//
//    private fun observeWeather(){
//        lifecycleScope.launch {
//            viewmodel.weatherState.collect {state ->
//                when(state){
//                    is Resources.Loading -> showLoading()
//                    is Resources.Success -> {
//                        hideLoading()
//                        state.data?.let { updateWeatherUI(it) }
//                    }
//                    is Resources.Error ->{
//                        hideLoading()         // //complete it
//                        showErrorMessage(state.message)            // //complete it
//                    }
//                }
//            }
//        }
//    }
//
//    private fun showLoading(){
//        binding.weatherprogressbar.visibility = View.VISIBLE
//        binding.overlay.setBackgroundColor("#33000000".toColorInt())
//
//    }
//    private fun hideLoading(){
//        binding.weatherprogressbar.visibility = View.GONE
//
//    }
//
//    fun getWeatherBackgroundResource(weatherCondition: String?): Int{
//        return when(weatherCondition?.lowercase()){
//            "sunny" -> R.drawable.sunnyday
//            "clear day" -> R.drawable.sunnyday
//            "cloudy day" -> R.drawable.partly_cloudy
//            "rain" -> R.drawable.rainy
//            "heavy rain" -> R.drawable.rainyday
//            "thunderstorm" -> R.drawable.thander
//            "snow" -> R.drawable.snow
//            "mist" -> R.drawable.light_snow
//            "wind" -> R.drawable.wind
//            "clear night" -> R.drawable.night_clear
//            "overcast clouds" -> R.drawable.heavily_cloudy
//            "fog" -> R.drawable.foggy
//            "heat wave" -> R.drawable.heat_wave
//            else -> R.drawable.sunnyday
//        }
//    }
//
//    private fun updateWeatherUI(weather : WeatherResponce){
//
//        binding.apply {
//            tempt.text = "${weather.main.temp.toInt()}°"
//            weatherConditionText.text = "IT's ${weather.weather[0].description?.capitalize()}"
//            feeltemp.text = "Feels like ${weather.main.feels_like.toInt()}°"
//            humidityValue.text = "${weather.main.humidity}%"
//            windValue.text = "${weather.wind.speed}"
//            pressureValue.text = "${weather.main.pressure}"
//            visibilityValue.text = formatVisibility(weather.visibility)
//
//
//            //sunset and sunrise if you add
//
//        }
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                viewmodel.weatherState.collect{ resource ->
//                    when(resource){
//                        is Resources.Loading -> showLoading()
//                        is Resources.Success -> {
//                            val apiResponce : WeatherResponce? = resource.data
//                            val weatherCondition = apiResponce?.weather[0]?.description
//                            val backgroundResource = getWeatherBackgroundResource(weatherCondition)
//
//                            Glide.with(this@WeatherPage)
//                                .load(backgroundResource)
//                                .transition(DrawableTransitionOptions.withCrossFade())
//                                .into(binding.weatherBgImage)
//                        }
//                        is Resources.Error -> {
//                            hideLoading()
//                            showErrorMessage(resource.message)
//                        }
//                    }
//
//                }
//            }
//        }
//    }
//
//    fun formatVisibility(visibility: Int): CharSequence {
//        return when{
//            visibility >= 1000 -> "${visibility/1000}"
//            else -> "${visibility}"
//        }
//    }
//
//    private fun showErrorMessage(message: String?) {
//        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_LONG).apply {
//            setAction("Retry"){
//                viewmodel.weatherState
//            }
//            show()
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    private fun checkLocationPermission() {
//        when {
//            ContextCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                getDevicelocation()
//            }
//            else ->{
//                requestpermissitionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
//            }
//        }
//    }
//
//    fun observeClear() {
//        TransitionManager.beginDelayedTransition(binding.headerContainer)
//        binding.searchText.text.clear()
//        binding.locationBtn.visibility = View.VISIBLE
//        binding.searchBar.visibility = View.GONE
//    }
//
//
//    fun observeSearch() {
//        TransitionManager.beginDelayedTransition((binding.headerContainer))
//        binding.locationBtn.visibility = View.GONE
//        binding.searchBar.visibility = View.VISIBLE
//        binding.searchText.requestFocus()
//
//    }
//
//
//    private fun textBgColor() {
//        val textview = findViewById<TextView>(R.id.tempt)
//
//// Ensure the TextView has been laid out so we can get its exact height
//        textview.post {
//            val paint = textview.paint
//            val height = textview.height.toFloat() // Get height instead of width
//
//            // Create the linear gradient shader (Top to Bottom)
//            val textShader =
//                LinearGradient(
//                    0f,
//                    0f,
//                    0f,
//                    height, // Start and end points of the gradient.
//                    intArrayOf(
//                        Color.parseColor("#FFFFFF"), // Start color
//                        Color.parseColor("#89DBCFCF"),
//                    ),
//                    null,
//                    Shader.TileMode.CLAMP,
//                )
//            // Assign the shader to the TextView's paint object
//            paint.shader = textShader
//            textview.invalidate() // Redraw the TextView with the vertical gradient
//        }
//    }
//}







class WeatherPage : BaseActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var viewmodel : WeatherViewmodel
    private lateinit var authViewmodel : AuthViewmodel
    private lateinit var binding: ActivityWeatherPageBinding
    private lateinit var locationhelper : LocationHelper

    private val requestpermissitionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            getDevicelocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDevicelocation() {
        lifecycleScope.launch {
            val detectCity = locationhelper.getCurrentCity()
            if (!detectCity.isNullOrEmpty()){
                viewmodel.updateCity(detectCity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWeatherPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationhelper = LocationHelper(this)

        checkLocationPermission()
        setupAuthViewmodel()
        setupViewmodel()
        setupUi()
        observeWeather()
        binding.locationBtn.setOnClickListener {
            observeSearch()
        }
        binding.weathermain.setOnClickListener {
            observeClear()
        }

        textBgColor()
    }

    private fun updateWindStatus(currentWind: Int): String {
        return when {
            currentWind < 0 -> "Invalid Reading"
            currentWind == 0 -> "It's calm"
            currentWind in 1..3 -> "There is a light breeze"
            currentWind in 4..6 -> "There is a moderate breeze"
            currentWind in 7..9 -> "There is a strong gale"
            currentWind in 10..12 -> "It's a storm"
            else -> "Invalid Reading"
        }
    }

    private fun updateHumidityStatus(currentHumidity: Int): String {
        return when {
            currentHumidity < 0 -> "Invalid Reading"
            currentHumidity in 0..30 -> "Dry Weather"
            currentHumidity in 31..50 -> "Comfortable Weather"
            currentHumidity in 51..60 -> "Humid Weather"
            currentHumidity in 61..64 -> "Very Humid Weather"
            currentHumidity in 65..80 -> "Muggy and Sticky Weather"
            currentHumidity in 81..100 -> "Saturated Weather"
            else -> "Invalid Reading"
        }
    }

    private fun setupViewmodel() {
        val weatherApi = NatworkUtils.getRetrofitInstance().create(WeatherApi::class.java)
        val repository = WeatherRepo(weatherApi)
        val factory = WeatherViewmodelFactory(repository)
        viewmodel = ViewModelProvider(this, factory)[WeatherViewmodel::class.java]
    }
    private fun setupAuthViewmodel(){
        val factory = AuthViewmodelFactory()
        authViewmodel = ViewModelProvider(this, factory)[AuthViewmodel::class.java]
        auth = Firebase.auth
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
    private fun observeWeather(){
        lifecycleScope.launch {
            viewmodel.weatherState.collect { state ->
                when(state){
                    is Resources.Loading -> showLoading()
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

    private fun showLoading(){
        binding.weatherprogressbar.visibility = View.VISIBLE
        binding.overlay.setBackgroundColor("#33000000".toColorInt())
    }

    private fun hideLoading(){
        binding.weatherprogressbar.visibility = View.GONE
    }
    fun getWeatherBackgroundResource(weatherCondition: String?): Int{
        return when(weatherCondition?.lowercase()){
            "sunny", "clear day" -> R.drawable.sunnyday
            "cloudy day" -> R.drawable.partly_cloudy
            "rain" -> R.drawable.rainy
            "heavy rain" -> R.drawable.rainyday
            "thunderstorm" -> R.drawable.thander
            "snow" -> R.drawable.snow
            "mist" -> R.drawable.light_snow
            "wind" -> R.drawable.wind
            "clear night" -> R.drawable.night_clear
            "overcast clouds" -> R.drawable.heavily_cloudy
            "fog" -> R.drawable.foggy
            "heat wave" -> R.drawable.heat_wave
            else -> R.drawable.sunnyday
        }
    }

    private fun updateWeatherUI(weather : WeatherResponce){
        binding.apply {
            locationBtn.text = weather.name
            tempt.text = "${weather.main.temp.toInt()}°"
            weatherConditionText.text = "It's ${weather.weather[0].description?.replaceFirstChar { it.uppercase() }}"
            feeltemp.text = "Feels like ${weather.main.feels_like.toInt()}°"
            humidityValue.text = "${weather.main.humidity}%"
            windValue.text = weather.wind.speed.toString()
            pressureValue.text = weather.main.pressure.toString()
            visibilityValue.text = formatVisibility(weather.visibility)

            humidityText.text = updateHumidityStatus(weather.main.humidity)
            windText.text = updateWindStatus(weather.wind.speed.toInt())
        }

        val weatherCondition = weather.weather[0].description
        val backgroundResource = getWeatherBackgroundResource(weatherCondition)

        Glide.with(this@WeatherPage)
            .load(backgroundResource)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.weatherBgImage)
    }
    fun formatVisibility(visibility: Int): CharSequence {
        return if (visibility >= 1000) "${visibility / 1000}" else "$visibility"
    }

    private fun showErrorMessage(message: String?) {
        Snackbar.make(binding.root, message ?: "Unknown Error", Snackbar.LENGTH_LONG).apply {
            setAction("Retry"){
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
        binding.locationBtn.visibility = View.VISIBLE
        binding.searchBar.visibility = View.GONE
    }

    fun observeSearch() {
        TransitionManager.beginDelayedTransition(binding.headerContainer)
        binding.locationBtn.visibility = View.GONE
        binding.searchBar.visibility = View.VISIBLE
        binding.searchText.requestFocus()
    }
    private fun textBgColor() {
        val textview = findViewById<TextView>(R.id.tempt)
        textview.post {
            val paint = textview.paint
            val height = textview.height.toFloat()
            val textShader = LinearGradient(
                0f, 0f, 0f, height,
                intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#89DBCFCF")),
                null, Shader.TileMode.CLAMP
            )
            paint.shader = textShader
            textview.invalidate()
        }
    }
}
