package moe.feng.danmaqua.ui.list

abstract class ItemBasedSimpleViewBinder<T : Any, VH: ItemBasedViewHolder<T>>()
    : SimpleViewBinder<T, VH>() {

    override fun onBindViewHolder(holder: VH, item: T) {
        holder.bind(item)
    }

    override fun onBindViewHolder(holder: VH, item: T, payloads: List<Any>) {
        holder.bind(item, payloads)
    }

    override fun onViewRecycled(holder: VH) {
        holder.onRecycled()
    }

}