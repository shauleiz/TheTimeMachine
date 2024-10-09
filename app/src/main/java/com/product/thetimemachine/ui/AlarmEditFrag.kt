package com.product.thetimemachine.ui
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import androidx.fragment.app.Fragment
import com.product.thetimemachine.R


class AlarmEditFrag  : Fragment() {
    private var parent: MainActivity? = null
    private var fragmentView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the MainActivity as Parent
        parent = activity as MainActivity?
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Start the compose display
        return ComposeView(requireContext()).apply { setContent { AlarmEditFragDisplayTop() } }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val AppToolbar = requireActivity().findViewById<Toolbar>(com.product.thetimemachine.R.id.app_toolbar)
        AppToolbar.setTitle(com.product.thetimemachine.R.string.alarmsetup_title)
        (activity as AppCompatActivity?)!!.setSupportActionBar(AppToolbar)
        parent!!.isDeleteAction = false
        parent!!.isSettingsAction = true
        parent!!.isEditAction = false
        parent!!.isDuplicateAction = false
        parent!!.setCheckmarkAction(true)
        parent!!.invalidateOptionsMenu()
    }


    @Composable
    private fun AlarmEditFragDisplayTop(){

        var value by rememberSaveable { mutableStateOf("")}
        var isFocused by rememberSaveable { mutableStateOf(false) }

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val refLabel = createRef()


            OutlinedTextField(
                value = value,
                onValueChange = {value = it},
                label = {
                    Text(text = stringResource(id = R.string.label_hint),
                    color = if (!isFocused && value.isEmpty()) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface
                    ) },
                singleLine = true,
                placeholder = { Text(
                    text =stringResource(id = R.string.label_hint),
                    color = MaterialTheme.colorScheme.outlineVariant,
                ) },
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused}
                    .heightIn(1.dp, Dp.Infinity)
                    .padding(8.dp)
                    .constrainAs(refLabel) {
                        width = fillToConstraints
                        top.linkTo(parent.top)
                        linkTo(
                            start = parent.start,
                            end = parent.end,
                            startMargin = 8.dp,
                            endMargin = 8.dp,
                            bias = 0.0f,
                        )
                    },
            )
        }
    }

}

private fun getLabel(txt: String){}

@Composable
fun SimpleComposable() {
    Scaffold() {it
        TextField(value = "Text" , onValueChange = {})
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    SimpleComposable()
}