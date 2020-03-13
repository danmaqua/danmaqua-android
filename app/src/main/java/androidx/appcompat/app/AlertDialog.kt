package androidx.appcompat.app

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

fun Context.buildAlertDialog(block: AlertDialog.Builder.() -> Unit): AlertDialog {
    return AlertDialog.Builder(this).apply(block).create()
}

fun Context.showAlertDialog(block: AlertDialog.Builder.() -> Unit): AlertDialog {
    return AlertDialog.Builder(this).apply(block).show()
}

fun Fragment.buildAlertDialog(block: AlertDialog.Builder.() -> Unit): AlertDialog {
    return AlertDialog.Builder(context!!).apply(block).create()
}

fun Fragment.showAlertDialog(block: AlertDialog.Builder.() -> Unit): AlertDialog {
    return AlertDialog.Builder(context!!).apply(block).show()
}

var AlertDialog.Builder.title: String
    get() = throw UnsupportedOperationException()
    set(value) {
        setTitle(value)
    }

var AlertDialog.Builder.titleRes: Int
    get() = throw UnsupportedOperationException()
    set(@StringRes value) {
        setTitle(value)
    }

var AlertDialog.Builder.message: CharSequence
    get() = throw UnsupportedOperationException()
    set(value) {
        setMessage(value)
    }

var AlertDialog.Builder.messageRes: Int
    get() = throw UnsupportedOperationException()
    set(@StringRes value) {
        setMessage(value)
    }

var AlertDialog.Builder.view: View
    get() = throw UnsupportedOperationException()
    set(value) {
        setView(value)
    }

inline fun AlertDialog.Builder.inflateView(
    @LayoutRes layoutRes: Int,
    themedContext: Context? = null,
    onViewCreate: (view: View) -> Unit
) {
    val view = LayoutInflater.from(themedContext ?: context).inflate(layoutRes, null)
    onViewCreate(view)
    setView(view)
}

inline fun <DB : ViewDataBinding> AlertDialog.Builder.inflateDataBindingView(
    @LayoutRes layoutRes: Int,
    themedContext: Context? = null,
    onViewCreate: (binding: DB) -> Unit
) {
    val inflater = LayoutInflater.from(themedContext ?: context)
    val binding = DataBindingUtil.inflate<DB>(inflater, layoutRes, null, false)
    onViewCreate(binding)
    setView(binding.root)
}

fun AlertDialog.Builder.positiveButton(title: CharSequence,
                                       onClick: (DialogInterface) -> Unit = {}) {
    setPositiveButton(title) { dialog, _ -> onClick(dialog) }
}

fun AlertDialog.Builder.negativeButton(title: CharSequence,
                                       onClick: (DialogInterface) -> Unit = {}) {
    setNegativeButton(title) { dialog, _ -> onClick(dialog) }
}

fun AlertDialog.Builder.neutralButton(title: CharSequence,
                                      onClick: (DialogInterface) -> Unit = {}) {
    setNeutralButton(title) { dialog, _ -> onClick(dialog) }
}

fun AlertDialog.Builder.positiveButton(@StringRes titleRes: Int,
                                       onClick: (DialogInterface) -> Unit = {}) {
    setPositiveButton(titleRes) { dialog, _ -> onClick(dialog) }
}

fun AlertDialog.Builder.negativeButton(@StringRes titleRes: Int,
                                       onClick: (DialogInterface) -> Unit = {}) {
    setNegativeButton(titleRes) { dialog, _ -> onClick(dialog) }
}

fun AlertDialog.Builder.neutralButton(@StringRes titleRes: Int,
                                      onClick: (DialogInterface) -> Unit = {}) {
    setNeutralButton(titleRes) { dialog, _ -> onClick(dialog) }
}

fun AlertDialog.Builder.okButton(onClick: (DialogInterface) -> Unit = {}) {
    positiveButton(android.R.string.ok, onClick)
}

fun AlertDialog.Builder.cancelButton(onClick: (DialogInterface) -> Unit = {}) {
    negativeButton(android.R.string.cancel, onClick)
}

fun AlertDialog.Builder.yesButton(onClick: (DialogInterface) -> Unit = {}) {
    positiveButton(android.R.string.yes, onClick)
}

fun AlertDialog.Builder.noButton(onClick: (DialogInterface) -> Unit = {}) {
    negativeButton(android.R.string.no, onClick)
}

val AlertDialog.positiveButton: Button get() = getButton(AlertDialog.BUTTON_POSITIVE)

val AlertDialog.negativeButton: Button get() = getButton(AlertDialog.BUTTON_NEGATIVE)

val AlertDialog.neutralButton: Button get() = getButton(AlertDialog.BUTTON_NEUTRAL)
