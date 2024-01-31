package edu.bluejack23_1.wtgym.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.wtgym.model.Exercise

class ExercisesViewModel : ViewModel() {


    val nameError = MutableLiveData<String?>()
    val weightError = MutableLiveData<String?>()
    val heightError = MutableLiveData<String?>()
}