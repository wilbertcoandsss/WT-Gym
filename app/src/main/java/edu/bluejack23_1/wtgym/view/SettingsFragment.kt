package edu.bluejack23_1.wtgym.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.DialogViewBinding
import edu.bluejack23_1.wtgym.databinding.FragmentSettingsBinding
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.viewmodel.AccountViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: AccountViewModel

    private lateinit var bindingPopup: DialogViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        bindingPopup = DialogViewBinding.inflate(inflater, container, false);

        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameLblBtn.setOnClickListener {
            var bottomSheetDialog: BottomSheetDialog

            val bottomView = layoutInflater.inflate(R.layout.dialog_view, null)

            val userRepo = UserRepository(this.requireContext())
            Log.d("Mukjizat", userRepo.getCurrentUser().toString())

            bottomView.findViewById<EditText>(R.id.txtInput).hint =
                userRepo.getCurrentUser()?.userName.toString()
            bottomView.findViewById<TextView>(R.id.titleLbl).text = "Change Name"
            bottomView.findViewById<EditText>(R.id.txtInput2).visibility = View.GONE
            bottomView.findViewById<EditText>(R.id.txtInput3).visibility = View.GONE
            bottomView.findViewById<TextView>(R.id.messageLbl).visibility = View.GONE

            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()

            bottomView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
                Log.d("CANCEL", "KEPENCET CANCLE")
                bottomSheetDialog.dismiss()
            }

            bottomView.findViewById<Button>(R.id.saveBtn).setOnClickListener {
                Log.d("SAVE", "KEPENCET SAVE")
                viewModel.nameAcc.value =
                    bottomView.findViewById<TextView>(R.id.txtInput).text.toString()

                val isValid = this.context?.let { it1 ->
                    viewModel.onChangeNameClicked(
                        userRepo.getCurrentUser()?.userId.toString(),
                        userRepo.getCurrentUser()?.userName.toString(),
                        it1
                    )
                }

                if (isValid == true) {
                    bottomSheetDialog.dismiss()
                    val snackbar =
                        Snackbar.make(
                            requireView(),
                            "Name succesfully changed!",
                            Snackbar.LENGTH_SHORT
                        )


                    snackbar.setBackgroundTint(Color.parseColor("#3B5CFA"))
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                    snackbar.show()
                }
            }

            viewModel.nameError.observe(viewLifecycleOwner, Observer { nameError ->
                if (nameError != null) {
                    bottomView.findViewById<TextView>(R.id.lblErrorChange).text = nameError
                    bottomView.findViewById<EditText>(R.id.txtInput).error = nameError
                } else {
                    bottomView.findViewById<TextView>(R.id.lblErrorChange).text = ""
                    bottomView.findViewById<EditText>(R.id.txtInput).error = null
                }
            })
        }

        binding.heightLblBtn.setOnClickListener {
            var bottomSheetDialog: BottomSheetDialog

            val bottomView = layoutInflater.inflate(R.layout.dialog_view, null)

            val userRepo = UserRepository(this.requireContext())

            bottomView.findViewById<EditText>(R.id.txtInput).hint =
                userRepo.getCurrentUser()?.userHeight.toString()
            bottomView.findViewById<TextView>(R.id.titleLbl).text = "Change Height"
            bottomView.findViewById<EditText>(R.id.txtInput2).visibility = View.GONE
            bottomView.findViewById<EditText>(R.id.txtInput3).visibility = View.GONE
            bottomView.findViewById<TextView>(R.id.messageLbl).visibility = View.GONE

            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()

            bottomView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
                Log.d("CANCEL", "KEPENCET CANCLE")
                bottomSheetDialog.dismiss()
            }

            bottomView.findViewById<Button>(R.id.saveBtn).setOnClickListener {
                Log.d("SAVE", "KEPENCET SAVE")
                val charSequence = bottomView.findViewById<TextView>(R.id.txtInput).text
                val heightInt =
                    charSequence.toString().toIntOrNull() // Convert to Int or null if invalid

                viewModel.heightAcc.value = heightInt

                val userh = userRepo.getCurrentUser()?.userHeight

                val isValid = this.context?.let { it1 ->
                    userRepo.getCurrentUser()?.userHeight?.let { it2 ->
                        viewModel.onChangeHeightClicked(
                            userRepo.getCurrentUser()?.userId.toString(),
                            it2,
                            it1
                        )
                    }
                }

                if (isValid == true) {
                    bottomSheetDialog.dismiss()
                    val snackbar =
                        Snackbar.make(
                            requireView(),
                            "Height succesfully changed!",
                            Snackbar.LENGTH_SHORT
                        )


                    snackbar.setBackgroundTint(Color.parseColor("#3B5CFA"))
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                    snackbar.show()
                }
            }

            viewModel.heightError.observe(viewLifecycleOwner, Observer { heightError ->
                if (heightError != null) {
                    bottomView.findViewById<TextView>(R.id.lblErrorChange).text = heightError
                    bottomView.findViewById<EditText>(R.id.txtInput).error = heightError
                } else {
                    bottomView.findViewById<TextView>(R.id.lblErrorChange).text = ""
                    bottomView.findViewById<EditText>(R.id.txtInput).error = null
                }
            })
        }

        binding.weightLblBtn.setOnClickListener {

            var bottomSheetDialog: BottomSheetDialog

            val bottomView = layoutInflater.inflate(R.layout.dialog_view, null)

            val userRepo = UserRepository(this.requireContext())

            bottomView.findViewById<EditText>(R.id.txtInput).hint =
                userRepo.getCurrentUser()?.userWeight.toString()
            bottomView.findViewById<TextView>(R.id.titleLbl).text = "Change Weight"
            bottomView.findViewById<EditText>(R.id.txtInput2).visibility = View.GONE
            bottomView.findViewById<EditText>(R.id.txtInput3).visibility = View.GONE
            bottomView.findViewById<TextView>(R.id.messageLbl).visibility = View.GONE

            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()

            bottomView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
                Log.d("CANCEL", "KEPENCET CANCLE")
                bottomSheetDialog.dismiss()
            }

            bottomView.findViewById<Button>(R.id.saveBtn).setOnClickListener {
                val charSequence = bottomView.findViewById<TextView>(R.id.txtInput).text
                val weightInt =
                    charSequence.toString().toIntOrNull() // Convert to Int or null if invalid

                viewModel.weightAcc.value = weightInt
                val userw = userRepo.getCurrentUser()?.userWeight

                val isValid = this.context?.let { it1 ->
                    userRepo.getCurrentUser()?.userWeight?.let { it2 ->
                        viewModel.onChangeWeightClicked(
                            userRepo.getCurrentUser()?.userId.toString(),
                            it2,
                            it1
                        )
                    }
                }

                if (isValid == true) {
                    bottomSheetDialog.dismiss()
                    val snackbar =
                        Snackbar.make(
                            requireView(),
                            "Weight succesfully changed!",
                            Snackbar.LENGTH_SHORT
                        )


                    snackbar.setBackgroundTint(Color.parseColor("#3B5CFA"))
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                    snackbar.show()
                }
            }

            viewModel.weightError.observe(viewLifecycleOwner, Observer { weightError ->
                if (weightError != null) {
                    bottomView.findViewById<TextView>(R.id.lblErrorChange).text = weightError
                    bottomView.findViewById<EditText>(R.id.txtInput).error = weightError
                } else {
                    bottomView.findViewById<TextView>(R.id.lblErrorChange).text = ""
                    bottomView.findViewById<EditText>(R.id.txtInput).error = null
                }
            })
        }

        binding.changePwBtn.setOnClickListener {
            var bottomSheetDialog: BottomSheetDialog

            val bottomView = layoutInflater.inflate(R.layout.dialog_view, null)

            val userRepo = UserRepository(this.requireContext())

            bottomView.findViewById<EditText>(R.id.txtInput).hint = "Enter your current password"
            val editText = bottomView.findViewById<EditText>(R.id.txtInput)
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            bottomView.findViewById<EditText>(R.id.txtInput2).hint = "Enter your new password"
            val editText1 = bottomView.findViewById<EditText>(R.id.txtInput2)
            editText1.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            bottomView.findViewById<EditText>(R.id.txtInput3).hint = "Confirm your new password"
            val editText2 = bottomView.findViewById<EditText>(R.id.txtInput3)
            editText2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            bottomView.findViewById<TextView>(R.id.titleLbl).text = "Change Password"
            bottomView.findViewById<TextView>(R.id.messageLbl).visibility = View.GONE

            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()

            bottomView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
                Log.d("CANCEL", "KEPENCET CANCLE")
                bottomSheetDialog.dismiss()
            }

            bottomView.findViewById<Button>(R.id.saveBtn).setOnClickListener {
                val currentPassword = bottomView.findViewById<EditText>(R.id.txtInput).text
                val newPassword = bottomView.findViewById<EditText>(R.id.txtInput2).text
                val confirmPassword = bottomView.findViewById<EditText>(R.id.txtInput3).text

                viewModel.password.value = currentPassword.toString()
                viewModel.newPassword.value = newPassword.toString()
                viewModel.confirmPassword.value = confirmPassword.toString()

                val userRepo = UserRepository(this.requireContext())
                val userId = userRepo.getCurrentUser()?.userId

                val isValid =
                    this.context?.let { it1 -> viewModel.onChangePwClicked(userId.toString(), it1) }

                if (isValid == true) {
                    bottomSheetDialog.dismiss()
                    val snackbar =
                        Snackbar.make(
                            requireView(),
                            "Password succesfully changed!",
                            Snackbar.LENGTH_SHORT
                        )

                    snackbar.setBackgroundTint(Color.parseColor("#3B5CFA"))
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                    snackbar.show()
                }
            }

            viewModel.pwError.observe(viewLifecycleOwner, Observer { pwError ->
                if (pwError != null) {
                    bottomView.findViewById<TextView>(R.id.lblErrorChange).text = pwError
                } else {
                    bottomView.findViewById<TextView>(R.id.lblErrorChange).text = ""
                }
            })

        }

        binding.logoutBtn.setOnClickListener{
            var bottomSheetDialog: BottomSheetDialog

            val bottomView = layoutInflater.inflate(R.layout.dialog_view, null)

            val userRepo = UserRepository(this.requireContext())

            bottomView.findViewById<EditText>(R.id.txtInput).visibility = View.GONE
            bottomView.findViewById<EditText>(R.id.txtInput2).visibility = View.GONE
            bottomView.findViewById<EditText>(R.id.txtInput3).visibility = View.GONE
            bottomView.findViewById<TextView>(R.id.lblErrorChange).visibility = View.GONE

            bottomView.findViewById<TextView>(R.id.titleLbl).text = "Log out"

            bottomView.findViewById<TextView>(R.id.messageLbl).text = "Are you sure want to Log Out ?"

            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()

            bottomView.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
                Log.d("CANCEL", "KEPENCET CANCLE")
                bottomSheetDialog.dismiss()
            }

            bottomView.findViewById<Button>(R.id.saveBtn).text = "Logout"

            bottomView.findViewById<Button>(R.id.saveBtn).setOnClickListener {
                bottomSheetDialog.dismiss()
                val intent = Intent(context, LandingPage::class.java)
                val sharedPreferences: SharedPreferences? =
                    context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()
                editor?.clear()
                editor?.apply()

                startActivity(intent)
                requireActivity().finish()
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}