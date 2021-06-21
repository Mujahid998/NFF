package fragments



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.nff.R
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : BaseFragment() {





    override fun onCreateView(inflater:
                              LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return getPersistentView(inflater, container, savedInstanceState, R.layout.fragment_settings)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true

            initViews()
        }






    }



    private fun initViews(){

        val navController=findNavController()

        lateinit var toManageData:NavDirections

        back_btn_settings.setOnClickListener { navController.navigateUp() }

        manageData.setOnClickListener {
            toManageData=SettingsFragmentDirections.settingsToManageData()
            navController.navigate(toManageData)}




    }



}
