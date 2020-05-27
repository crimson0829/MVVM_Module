package com.crimson.mvvm.binding.recyclerview.bugfix

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

/**
 * @author crimson
 * @date 2020/3/29
 * 修复recyclerview Data modified 时导致的角标越界问题
 */
open class BugFixLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context?) : super(context) {}
    constructor(
        context: Context?,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(context, orientation, reverseLayout) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }


    override fun onLayoutChildren(
        recycler: Recycler,
        state: RecyclerView.State
    ) {

        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
//            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }
}