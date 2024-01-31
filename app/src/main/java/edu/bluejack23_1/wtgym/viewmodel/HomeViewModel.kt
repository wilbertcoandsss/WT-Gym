package edu.bluejack23_1.wtgym.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _navigateToHomePlan = MutableLiveData<Boolean>()

    val navigateToHomePlan: LiveData<Boolean>
        get() = _navigateToHomePlan

    private val _navigateToGymPlan = MutableLiveData<Boolean>()

    val navigateToGymPlan: LiveData<Boolean>
        get() = _navigateToGymPlan

    fun onGoToHomePlan(view: View) {
        _navigateToHomePlan.value = true
    }

    fun onGoToHomePlanComplete() {
        _navigateToHomePlan.value = false
    }

    fun onGoToGymPlan(view: View) {
        _navigateToGymPlan.value = true
    }

    fun onGoToGymPlanComplete() {
        _navigateToGymPlan.value = false
    }
}