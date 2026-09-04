package com.example.skyreport.activities

import android.Manifest
import android.content.Context
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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import retrofit2.create
import androidx.core.graphics.toColorInt
import com.example.skyreport.data.models.weather.WeatherResponce
import com.example.skyreport.utils.Result

class WeatherPage : BaseActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var viewmodel : WeatherViewmodel
    private lateinit var authViewmodel : AuthViewmodel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityWeatherPageBinding
    private lateinit var locationhelper : LocationHelper

    private val requestpermissitionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        isGranted: Boolean ->
        getDevicelocation()
    }

    private fun getDevicelocation() {
        lifecycleScope.launch {
            val detectCity = locationhelper.getCurrentCity()
            if (!detectCity .isNullOrEmpty()){
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
        //
        checkLocationPermission()
        setupAuthViewmodel()
        setupViewmodel()
        setupUi()
        observeWeather()

        //about search bar
        binding.locationBtn.setOnClickListener {
            observeSearch()
        }
        binding.weathermain.setOnClickListener {
            observeClear()
        }

// temp text bgcolour
        textBgColor()

        val backgroundResource = getWeatherBackgroundResource()
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

        //first complete authViewmodel then it
        auth.currentUser?.let {
        }
    }

    private fun setupUi() {
        binding.searchText.setOnEditorActionListener { _, actionid, event ->
            (if (actionid == EditorInfo.IME_ACTION_SEARCH){
                val quary = binding.searchText.text.toString()
                if (quary .isEmpty()){
                    showLoading()

                    val imm = getSystemService(INPUT_METHOD_SERVICE)as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.searchText.windowToken,0)

                    viewmodel.updateCity(quary)
                }else{
                    false
                }
            }else{
                false
            }) as Boolean
        }
        binding.clearSearchBtn.setOnClickListener {
            observeClear()
        }
    }

    private fun observeWeather(){
        lifecycleScope.launch {
            viewmodel.weatherState.collect {state ->
                when(state){
                    is Result.Loading -> showLoading()
                    is Result.Success -> {
                        hideLoading()              //complete it
                        state.data?.let { updateWeatherUI(it)  }          //complete it
                    }
                    is Result.Error ->{
                        hideLoading()         // //complete it
                        showErrorMessage(state.message)            // //complete it
                    }
                }
            }
        }
    }

    private fun showLoading(){
        binding.weatherprogressbar.visibility = View.VISIBLE
        binding.overlay.background = "#33000000".toColorInt()

    }
    private fun hideLoading(){
        binding.weatherprogressbar.visibility = View.GONE

    }

    fun getWeatherBackgroundResource(weatherCondition: String?): Int{
        return when(weatherCondition?.lowercase()){
            "sunny" -> R.drawable.sunnyday
            "clear day" -> R.drawable.sunnyday
            "cloudy day" -> R.drawable.partly_cloudy
            "rain" -> R.drawable.rainy
            "heavy rain" -> R.drawable.rainyday
            "thunderstorm" -> R.drawable.thander
            "snow" -> R.drawable.snow
            "mist" -> R.drawable.light_snow
            "wind" -> R.drawable.wind
            "clear night" -> R.drawable.night_clear
            "cloudy night" -> R.drawable.heavily_cloudy
            "fog" -> R.drawable.foggy
            "heat wave" -> R.drawable.heat_wave
            else -> R.drawable.sunnyday


        }

    }

























    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getDevicelocation()
            }
            else ->{
                requestpermissitionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
    }

    fun observeClear() {
        TransitionManager.beginDelayedTransition(binding.headerContainer)
        binding.searchText.text.clear()
        binding.locationBtn.visibility = View.VISIBLE
        binding.searchBar.visibility = View.GONE
    }


    fun observeSearch() {
        TransitionManager.beginDelayedTransition((binding.headerContainer))
        binding.locationBtn.visibility = View.GONE
        binding.searchBar.visibility = View.VISIBLE
        binding.searchText.requestFocus()

    }


    private fun textBgColor() {
        val textview = findViewById<TextView>(R.id.tempt)

// Ensure the TextView has been laid out so we can get its exact height
        textview.post {
            val paint = textview.paint
            val height = textview.height.toFloat() // Get height instead of width

            // Create the linear gradient shader (Top to Bottom)
            val textShader =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    height, // Start and end points of the gradient.
                    intArrayOf(
                        Color.parseColor("#FFFFFF"), // Start color
                        Color.parseColor("#89DBCFCF"),
                    ),
                    null,
                    Shader.TileMode.CLAMP,
                )
            // Assign the shader to the TextView's paint object
            paint.shader = textShader
            textview.invalidate() // Redraw the TextView with the vertical gradient
        }
    }
}
