package com.crimson.mvvm.binding.recyclerview.bugfix

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @author crimson
 * @date 2020/3/29
 * 修复recyclerview Data modified 时导致的角标越界问题
 */
open class BugFixStaggeredGridLayoutManager : StaggeredGridLayoutManager {


    constructor(spanCount: Int, orientation: Int) :super(spanCount, orientation){
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