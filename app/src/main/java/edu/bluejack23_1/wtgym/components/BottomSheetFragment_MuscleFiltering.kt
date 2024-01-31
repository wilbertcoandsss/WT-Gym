package edu.bluejack23_1.wtgym.components

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.model.Exercise
import edu.bluejack23_1.wtgym.viewmodel.ExercisesViewModel

class BottomSheetFragment_MuscleFiltering : BottomSheetDialogFragment() {

    val exerciseView: ExercisesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}