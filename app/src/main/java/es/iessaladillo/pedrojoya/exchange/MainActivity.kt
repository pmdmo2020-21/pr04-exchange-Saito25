package es.iessaladillo.pedrojoya.exchange

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import es.iessaladillo.pedrojoya.exchange.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    /*
        Variable de acceso global a la vista, con las diferentes vistas.
     */
    private lateinit var binding: MainActivityBinding

    /*
        Determina la acción cuando el texto del EditText es cambiado
     */
    private lateinit var etxtAmountTextWatcher: TextWatcher

    /*
        Obtener valores iniciales de las monedas seleccionadas
     */
    private var fromCuerrencyCoinValue = Currency.EURO
    private var toCuerrencyCoinValue = Currency.DOLLAR


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicialización de binding y posterior inflado de la vista raíz.
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        setupInitialState()
    }


    private fun setupViews() {

        // Acción cuando el usuario cambie el foco a EditText que controla el nombre.
        binding.etxtMainAmount.setOnFocusChangeListener { v, hasFocus ->
            changeTextViewColorIf(
                hasFocus,
                binding.txtMainAmount,
            )
        }

        etxtAmountTextWatcher = binding.etxtMainAmount.doAfterTextChanged { s ->
            validAmount(
                s.toString(),
                binding.etxtMainAmount
            )
        }

        binding.rgMainFromCuerrency.setOnCheckedChangeListener { mainRadioGroup: RadioGroup, i: Int ->
            changeFromCoinSelect(
                mainRadioGroup,
                i,
                binding.ivMainFromCuerrencyImage,
            )
        }
        binding.rgMainToCuerrency.setOnCheckedChangeListener { mainRadioGroup: RadioGroup, i: Int ->
            changeToCoinSelect(
                mainRadioGroup,
                i,
                binding.ivMainToCuerrencyImage,
            )

        }

        binding.rbMainFromCuerrencyDollar.tag = Currency.DOLLAR
        binding.rbMainFromCuerrencyEuro.tag = Currency.EURO
        binding.rbMainFromCuerrencyPound.tag = Currency.POUND

        binding.rbMainToCuerrencyDollar.tag = Currency.DOLLAR
        binding.rbMainToCuerrencyEuro.tag = Currency.EURO
        binding.rbMainToCuerrencyPound.tag = Currency.POUND

        binding.btnMain.setOnClickListener {
            hideSoftKeyboard(binding.root)
            toFromCuerrency()
        }

        binding.etxtMainAmount.setOnEditorActionListener { _, actionId: Int, _ ->
           if(actionId == EditorInfo.IME_ACTION_DONE) {
               toFromCuerrency()
               false
           } else {
               true
           }
        }
    }

    private fun changeTextViewColorIf(hasFocus: Boolean, viewToModify: TextView) {
        if(hasFocus) {
            viewToModify.setTextColor(resources.getColor(R.color.pink_200))
        } else {
            viewToModify.setTextColor(resources.getColor(R.color.design_default_color_on_primary))
        }
    }

    private fun toFromCuerrency(){

        var valueInEditText = binding.etxtMainAmount.text.toString().toDouble()
        var valueInEditTextConverted = fromCuerrencyCoinValue.toDollar(valueInEditText)
        valueInEditTextConverted = toCuerrencyCoinValue.fromDollar(valueInEditTextConverted)

        Toast.makeText(
            this,
            String.format(
                "%.2f %s = %.2f %s",
                valueInEditText,
                fromCuerrencyCoinValue.symbol,
                valueInEditTextConverted,
                toCuerrencyCoinValue.symbol
            ),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupInitialState() {
        binding.etxtMainAmount.selectAll()
    }

    private fun validAmount(textToValidate: String, editText: EditText) {

        if (textToValidate.isBlank()) {
            editText.text = Editable.Factory.getInstance().newEditable("0")
            editText.selectAll()
        }
    }

    private fun changeFromCoinSelect(
        radioGroup: RadioGroup,
        selectedRadioButton: Int,
        imageView: ImageView,
    ) {

        var tag = changeCoinSelect(radioGroup, selectedRadioButton).tag
        fromCuerrencyCoinValue = tag as Currency

        imageView.setImageDrawable(getDrawable(fromCuerrencyCoinValue.drawableResId))

        for (i in 0 until binding.rgMainToCuerrency.childCount) {
            var hijo: RadioButton = binding.rgMainToCuerrency.getChildAt(i) as RadioButton

            hijo.isEnabled = tag != hijo.tag
        }
    }

    private fun changeToCoinSelect(
        radioGroup: RadioGroup,
        selectedRadioButton: Int,
        imageView: ImageView,
    ) {

        var tag = changeCoinSelect(radioGroup, selectedRadioButton).tag
        toCuerrencyCoinValue = tag as Currency

        imageView.setImageDrawable(getDrawable(toCuerrencyCoinValue.drawableResId))

        for (i in 0 until binding.rgMainFromCuerrency.childCount) {
            var hijo: RadioButton = binding.rgMainFromCuerrency.getChildAt(i) as RadioButton

            hijo.isEnabled = tag != hijo.tag

        }
    }

    private fun changeCoinSelect(
        radioGroup: RadioGroup,
        selectedRadioButton: Int,
    ): RadioButton {

        val radioButton = when (selectedRadioButton) {
            radioGroup.getChildAt(0).id -> {
                radioGroup.getChildAt(0) as RadioButton
            }

            radioGroup.getChildAt(1).id -> {
                radioGroup.getChildAt(1) as RadioButton
            }

            radioGroup.getChildAt(2).id -> {
                radioGroup.getChildAt(2) as RadioButton
            }

            else -> {
                throw Exception("Id inválido")
            }
        }

        return radioButton
    }
}
