package com.yb.part6_chapter01.util.mapper

import android.view.LayoutInflater
import android.view.ViewGroup
import com.yb.part6_chapter01.databinding.ViewholderEmptyBinding
import com.yb.part6_chapter01.databinding.ViewholderRestaurantBinding
import com.yb.part6_chapter01.model.CellType
import com.yb.part6_chapter01.model.Model
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.viewholder.EmptyViewHolder
import com.yb.part6_chapter01.widget.adapter.viewholder.ModelViewHolder
import com.yb.part6_chapter01.widget.adapter.viewholder.restaurant.RestaurantViewHolder

object ModelViewHolderMapper {

    @Suppress("UNCHECKED_CAST")
    fun <M : Model> map(
        parent: ViewGroup,
        type: CellType,
        viewModel: BaseViewModel,
        resourcesProvider: ResourcesProvider,
    ): ModelViewHolder<M> {
        val inflater = LayoutInflater.from(parent.context)

        val viewHolder = when (type) {
            CellType.EMPTY_CELL -> EmptyViewHolder(
                ViewholderEmptyBinding.inflate(inflater, parent, false),
                viewModel,
                resourcesProvider
            )
            CellType.RESTAURANT_CELL -> RestaurantViewHolder(
                ViewholderRestaurantBinding.inflate(inflater, parent, false),
                viewModel,
                resourcesProvider
            )
        }
        return viewHolder as ModelViewHolder<M>
    }
}