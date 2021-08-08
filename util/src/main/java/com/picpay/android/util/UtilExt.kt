package com.picpay.android.util

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/** Fragment binding delegate, may be used since onViewCreated up to onDestroyView (inclusive) */
fun <T : ViewBinding> Fragment.viewBinding(factory: (View) -> T): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {
        private var binding: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T =
            binding ?: factory(requireView()).also {
                // if binding is accessed after Lifecycle is DESTROYED, create new instance, but don't cache it
                if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                    viewLifecycleOwner.lifecycle.addObserver(this)
                    binding = it
                }
            }

        override fun onDestroy(owner: LifecycleOwner) {
            binding = null
        }
    }

fun ImageView.loadImage(url: String, viewLoader: View? = null) {

    if (url.isNullOrEmpty()) {
        viewLoader?.visibility = View.GONE
        this.setImageResource(R.drawable.ic_round_account_circle)
        return
    }

    Picasso
        .get()
        .load(url)
        .error(R.drawable.ic_round_account_circle)
        .into(this, object : Callback {
            override fun onSuccess() {
                viewLoader?.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                viewLoader?.visibility = View.GONE
            }
        })
}