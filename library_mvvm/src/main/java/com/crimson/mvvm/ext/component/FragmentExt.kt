package com.crimson.mvvm.ext.component

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * @author crimson
 * @date   2020-01-19
 *  fragment 扩展
 */

inline fun <reified T : Activity> Fragment.startActivity(bundle: Bundle? = null) {
    val intent = Intent(context, T::class.java)
    bundle?.let {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

inline fun <reified T : Service> Fragment.startService(bundle: Bundle? = null) {
    context?.startService<T>(bundle)
}


inline fun Fragment.transaction(function: FragmentTransaction.() -> FragmentTransaction) {
    fragmentManager?.beginTransaction()
        ?.function()
        ?.commitAllowingStateLoss()
}


/**
 * Set arguments to fragment and return current instance
 */
inline fun <reified T : Fragment> T.withArguments(args: Bundle): T {
    this.arguments = args
    return this
}


fun Fragment.isLandscape() =
    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Fragment.isPortrait() =
    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

fun Fragment.dp2px(dpValue: Float): Int {
    return requireActivity().dp2px(dpValue)
}

fun Fragment.dp2px(dpValue: Int): Int {
    return requireActivity().dp2px(dpValue)
}

fun Fragment.px2dp(pxValue: Int): Float {
    return requireActivity().px2dp(pxValue)
}

fun FragmentManager.add(
    addFragment: Fragment,
    @IdRes containerId: Int,
    isHide: Boolean = false,
    isAddStack: Boolean = false,
    tag: String = addFragment::class.java.name
) {
    val ft = this.beginTransaction()
    val fragmentByTag = this.findFragmentByTag(tag)
    if (fragmentByTag != null && fragmentByTag.isAdded) {
        ft.remove(fragmentByTag)
    }
    ft.add(containerId, addFragment, tag)
    if (isHide) ft.hide(addFragment)
    if (isAddStack) ft.addToBackStack(tag)

    ft.commit()
}


fun FragmentManager.add(
    addList: List<Fragment>,
    @IdRes containerId: Int,
    showIndex: Int = 0
) {
    val ft = this.beginTransaction()
    for (i in addList.indices) {
        val addFragment = addList[i]
        val tag = addFragment::class.java.name
        val fragmentByTag = this.findFragmentByTag(tag)
        if (fragmentByTag != null && fragmentByTag.isAdded) {
            ft.remove(fragmentByTag)
        }
        ft.add(containerId, addFragment, tag)

        if (showIndex != i) ft.hide(addFragment)
    }
    ft.commit()
}


fun FragmentManager.hide(vararg hideFragment: Fragment) {
    hide(hideFragment.toList())
}


fun FragmentManager.hide(hideFragment: List<Fragment>) {
    val ft = this.beginTransaction()
    for (fragment in hideFragment) {
        ft.hide(fragment)
    }
    ft.commit()
}


fun FragmentManager.show(showFragment: Fragment) {
    val ft = this.beginTransaction()
    ft.show(showFragment)
    ft.commit()
}


fun FragmentManager.remove(vararg removeFragment: Fragment) {
    val ft = this.beginTransaction()
    for (fragment in removeFragment) {
        ft.remove(fragment)
    }
    ft.commit()
}


fun FragmentManager.removeTo(removeTo: Fragment, isIncludeSelf: Boolean = false) {
    val ft = this.beginTransaction()
    val fragments = this.getFragmentManagerFragments()
    for (i in (fragments.size - 1)..0) {
        val fragment = fragments[i]
        if (fragment == removeTo && isIncludeSelf) {
            ft.remove(fragment)
            break
        }
        ft.remove(fragment)
    }
    ft.commit()
}


fun FragmentManager.removeAll() {
    val frg = getFragmentManagerFragments()
    if (frg.isEmpty()) return

    val ft = this.beginTransaction()
    for (fragment in frg) {
        ft.remove(fragment)
    }
    ft.commit()
}


fun FragmentManager.showHide(
    showFragment: Fragment,
    vararg hideFragment: Fragment,
    transaction: Int = FragmentTransaction.TRANSIT_NONE
) {
    val ft = this.beginTransaction().setTransition(transaction)

    ft.show(showFragment)
    for (fragment in hideFragment) {
        if (fragment != showFragment) {
            ft.hide(fragment)
        }
    }

    ft.commit()
}


fun FragmentManager.replace(
    fragment: Fragment,
    @IdRes containerId: Int,
    isAddStack: Boolean = false,
    tag: String = fragment::class.java.name
) {
    val ft = this.beginTransaction()

    ft.replace(containerId, fragment, tag)
    if (isAddStack) ft.addToBackStack(tag)

    ft.commit()
}


fun FragmentManager.switch(
    showFragment: Fragment,
    @IdRes containerId: Int,
    transaction: Int = FragmentTransaction.TRANSIT_NONE
) {
    val ft = this.beginTransaction().setTransition(transaction)

    val tag = showFragment::class.java.name
    val fragmentByTag = this.findFragmentByTag(tag)
    if (fragmentByTag != null && fragmentByTag.isAdded) {
        ft.show(fragmentByTag)
    } else {
        ft.add(containerId, showFragment, tag)
    }

    for (tempF in this.getFragmentManagerFragments()) {
        if (tempF != fragmentByTag) {
            ft.hide(tempF)
        }
    }
    ft.commit()
}

fun FragmentManager.getTopFragment(): Fragment? {
    val frg = getFragmentManagerFragments()
    return frg.ifEmpty { return null }[frg.size - 1]
}

fun FragmentManager.getFragmentManagerFragments(): List<Fragment> {
    return this.fragments
}

inline fun <reified T : Fragment> FragmentManager.findFragment(): Fragment? {
    return this.findFragmentByTag(T::class.java.name)
}


fun Fragment.colors(@ColorRes stateListRes: Int): ColorStateList? {
    return ContextCompat.getColorStateList(requireContext(), stateListRes)
}

fun Fragment.attribute(value: Int): TypedValue {
    val ret = TypedValue()
    requireContext().theme.resolveAttribute(value, ret, true)
    return ret
}

inline fun <reified T : Any> Fragment.launchActivityAndFinish() {
    launch<T>()
    finish()
}


/**
 * Set target fragment with request code and return current instance
 */
fun Fragment.withTargetFragment(fragment: Fragment, reqCode: Int): Fragment {
    setTargetFragment(fragment, reqCode)
    return this
}

fun Fragment.drawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(requireContext(), id)

/**
 * Get dimension defined by attribute [attr]
 */
fun Fragment.attrDimen(attr: Int): Int {
    return TypedValue.complexToDimensionPixelSize(attribute(attr).data, resources.displayMetrics)
}

/**
 * Get drawable defined by attribute [attr]
 */
fun Fragment.attrDrawable(attr: Int): Drawable? {
    val a = requireContext().theme.obtainStyledAttributes(intArrayOf(attr))
    val attributeResourceId = a.getResourceId(0, 0)
    a.recycle()
    return drawable(attributeResourceId)
}


inline fun <reified T> Fragment.startActivityForResult(
    requestCode: Int,
    bundleBuilder: Bundle.() -> Unit = {},
    intentBuilder: Intent.() -> Unit = {}
) {
    val intent = Intent(requireContext(), T::class.java)
    intent.intentBuilder()
    val bundle = Bundle()
    bundle.bundleBuilder()
    startActivityForResult(intent, requestCode, if (bundle.isEmpty) null else bundle)
}

fun Fragment.finish() {
    requireActivity().finish()
}

inline fun <reified T> Fragment.launch() {
    this.requireContext().startActivity(Intent(this.requireContext(), T::class.java))
}


val Fragment.getAppCompatActivity get() = this.requireContext() as AppCompatActivity


fun FragmentActivity.popFragment() {
    val fm = supportFragmentManager
    if (fm.backStackEntryCount == 0) return
    fm.popBackStack()
}

fun Fragment.ifIsAddedAction(action: () -> Unit = {}) {
    if (isAdded) action()

}

fun Fragment.ifIsAttachedAction(action: () -> Unit = {}) {
    if (isAdded && activity != null) action()

}

fun Fragment.ifIsVisibleAction(action: () -> Unit = {}) {
    if (isVisible) action()

}

fun Fragment.ifIsResumedAction(action: () -> Unit = {}) {
    if (isResumed) action()
}


fun FragmentActivity.popFragment(name: String, flags: Int) {
    val fm = supportFragmentManager
    if (fm.backStackEntryCount == 0) return
    fm.popBackStack(name, flags)
}

fun FragmentActivity.popFragment(id: Int, flags: Int) {
    val fm = supportFragmentManager
    if (fm.backStackEntryCount == 0) return
    fm.popBackStack(id, flags)
}

fun AppCompatActivity.getCurrentActiveFragment(@IdRes frameId: Int): Fragment? {
    return supportFragmentManager.findFragmentById(frameId)
}

fun AppCompatActivity.clearAllFragments() {
    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}



/**
 * Calls fragment's `setHasOptionMenu` with `true` as default
 * @receiver Fragment
 * @param[hasOptionsMenu]: Default `true`, Pass false to not have options menu
 */
fun Fragment.allowOptionsMenu(hasOptionsMenu: Boolean = true) {
    setHasOptionsMenu(hasOptionsMenu)
}

/**
 * Go back to fragment whose tag matches with name
 * @param[name]: Name of the tag.
 * @param[flag]: Flag, Defaults to 0, optionally you can pass POP_BACKSTACK_INCLUSIVE
 * @receiver FragmentActivity
 */
fun FragmentActivity.goBackToFragment(name: String, flag: Int = 0) {
    supportFragmentManager.popBackStackImmediate(name, flag)
}


inline fun <reified T> Fragment.intent(body: Intent.() -> Unit): Intent {
    val intent = Intent(requireContext(), T::class.java)
    intent.body()
    return intent
}

inline fun <reified T> Fragment.startActivity(body: Intent.() -> Unit) {
    val intent = Intent(requireContext(), T::class.java)
    intent.body()
    startActivity(intent)
}

inline fun <reified T> FragmentActivity.intent(body: Intent.() -> Unit): Intent {
    val intent = Intent(this, T::class.java)
    intent.body()
    return intent
}

inline fun <reified T> FragmentActivity.startActivity(body: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.body()
    startActivity(intent)
}

fun Context.getFragmentWithTag(tag: String): Fragment? {
    return (this as AppCompatActivity).supportFragmentManager.findFragmentByTag(tag)
}

fun Context.isFragmentWithTagVisible(tag: String): Boolean {
    (this as AppCompatActivity)
    val presentFragment = this.supportFragmentManager.findFragmentByTag(tag)?.isVisible

    return if (presentFragment != null) {
        this.supportFragmentManager.findFragmentByTag(tag) != null && presentFragment
    } else {
        false
    }
}

fun AppCompatActivity.addFragment(@NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    supportFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .commit()
}



fun Context.replaceFragment(@StringRes title: Int, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    (this as AppCompatActivity)
        .supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .commit()
    if (this.supportActionBar != null) {
        this.supportActionBar?.setTitle(title)
    }
}

fun Context.replaceFragment(@Nullable title: String?, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    (this as AppCompatActivity)
        .supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (this.supportActionBar != null) {
            this.supportActionBar?.title = title
        }
    }
}

fun Context.addFragment(@Nullable title: String?, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    (this as AppCompatActivity)
        .supportFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (this.supportActionBar != null) {
            this.supportActionBar?.title = title
        }
    }
}


fun Fragment.getFragmentWithTag(tag: String): Fragment? {
    val activity = this.requireContext() as AppCompatActivity
    return activity.supportFragmentManager.findFragmentByTag(tag)
}

fun Fragment.isFragmentWithTagVisible(tag: String): Boolean {
    val activity = this.requireContext() as AppCompatActivity

    val presentFragment = activity.supportFragmentManager.findFragmentByTag(tag)?.isVisible

    return if (presentFragment != null) {
        activity.supportFragmentManager.findFragmentByTag(tag) != null && presentFragment
    } else {
        false
    }
}

fun Fragment.replaceFragment(@StringRes title: Int, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    val activity = this.requireContext() as AppCompatActivity

    activity.supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .commit()
    if (activity.supportActionBar != null) {
        activity.supportActionBar?.setTitle(title)
    }
}

fun Fragment.replaceFragment(@Nullable title: String?, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    val activity = this.requireContext() as AppCompatActivity

    activity.supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (activity.supportActionBar != null) {
            activity.supportActionBar?.title = title
        }
    }
}

fun Fragment.addFragment(@Nullable title: String?, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    val activity = this.requireContext() as AppCompatActivity

    activity.supportFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (activity.supportActionBar != null) {
            activity.supportActionBar?.title = title
        }
    }
}


fun AppCompatActivity.getFragmentWithTag(tag: String): Fragment? {
    return this.supportFragmentManager.findFragmentByTag(tag)
}

fun AppCompatActivity.isFragmentWithTagVisible(tag: String): Boolean {
    val presentFragment = this.supportFragmentManager.findFragmentByTag(tag)?.isVisible

    return if (presentFragment != null) {
        this.supportFragmentManager.findFragmentByTag(tag) != null && presentFragment
    } else {
        false
    }
}

fun AppCompatActivity.replaceFragment(@StringRes title: Int, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .commit()
    if (this.supportActionBar != null) {
        this.supportActionBar?.setTitle(title)
    }
}

fun AppCompatActivity.replaceFragment(@Nullable title: String?, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (this.supportActionBar != null) {
            this.supportActionBar?.title = title
        }
    }
}

fun AppCompatActivity.addFragment(@Nullable title: String?, @NonNull fragment: Fragment, @Nullable tag: String, @IdRes layoutId: Int) {
    supportFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .commit()
    if (title != null) {
        if (this.supportActionBar != null) {
            this.supportActionBar?.title = title
        }
    }
}


fun AppCompatActivity.removeFragmentBackstack(fragment: Fragment) {
    supportFragmentManager.beginTransaction().remove(fragment).commitNow()
    supportFragmentManager.popBackStack(fragment.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Context.removeFragmentBackstack(fragment: Fragment) {
    this as AppCompatActivity
    supportFragmentManager.beginTransaction().remove(fragment).commitNow()
    supportFragmentManager.popBackStack(fragment.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun Fragment.removeFragmentBackstack() {
    val activity = this.requireContext() as AppCompatActivity
    activity.supportFragmentManager.beginTransaction().remove(this).commitNow()
    activity.supportFragmentManager.popBackStack(this.tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

fun AppCompatActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().remove(fragment).commitNow()
}

fun Context.removeFragment(fragment: Fragment) {
    this as AppCompatActivity
    supportFragmentManager.beginTransaction().remove(fragment).commitNow()
}

fun Fragment.removeFragment() {
    val activity = this.requireContext() as AppCompatActivity
    activity.supportFragmentManager.beginTransaction().remove(this).commitNow()
}


fun AppCompatActivity.currentFragment(@IdRes container: Int): Fragment? {
    return supportFragmentManager.findFragmentById(container)
}

fun FragmentActivity.isFragmentAtTheTop(fragment: Fragment): Boolean =
    supportFragmentManager.fragments.last() == fragment

fun AppCompatActivity.isFragmentAtTheTop(fragment: Fragment): Boolean =
    supportFragmentManager.fragments.last() == fragment


inline fun FragmentActivity.inTransaction(
    allowStateLoss: Boolean = false,
    block: FragmentTransaction.() -> Unit
) {
    with(supportFragmentManager) {
        beginTransaction().apply {
            block(this)

            if (!isStateSaved) {
                commit()
            } else if (allowStateLoss) {
                commitAllowingStateLoss()
            }
        }
    }
}

inline fun Fragment.inTransaction(
    allowStateLoss: Boolean = false,
    block: FragmentTransaction.() -> Unit
) {
    activity?.inTransaction(allowStateLoss, block)
}

fun Fragment.navigateBack() {
    activity?.onBackPressed()
}


/**
 * Get the activity's rootView
 */
val Fragment.rootView: View? get() = requireActivity().findViewById(android.R.id.content)