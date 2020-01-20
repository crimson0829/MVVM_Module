package com.crimson.mvvm.binding.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

object LineManagers {

    @JvmStatic
    fun both(): LineManagerFactory {
        return object : LineManagerFactory {
            override fun create(recyclerView: RecyclerView): ItemDecoration {
                return AdapterDividerLine(recyclerView.context, AdapterDividerLine.LineDrawMode.BOTH)
            }
        }
    }

    @JvmStatic
    fun horizontal(): LineManagerFactory {
        return object : LineManagerFactory {
            override fun create(recyclerView: RecyclerView): ItemDecoration {
                return AdapterDividerLine(recyclerView.context, AdapterDividerLine.LineDrawMode.HORIZONTAL)
            }
        }
    }

    @JvmStatic
    fun vertical(): LineManagerFactory {
        return object : LineManagerFactory {
            override fun create(recyclerView: RecyclerView): ItemDecoration {
                return AdapterDividerLine(recyclerView.context, AdapterDividerLine.LineDrawMode.VERTICAL)
            }
        }
    }

    interface LineManagerFactory {
        fun create(recyclerView: RecyclerView): ItemDecoration
    }
}