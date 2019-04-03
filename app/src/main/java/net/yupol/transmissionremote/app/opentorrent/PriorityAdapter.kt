package net.yupol.transmissionremote.app.opentorrent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.model.PriorityViewModel

object PriorityAdapter : BaseAdapter() {

    override fun getItem(position: Int) = PriorityViewModel.values()[position]

    override fun getItemId(position: Int) = getItem(position).ordinal.toLong()

    override fun getCount() = PriorityViewModel.values().size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.priority_dropdown_item_layout, parent, false)
        }
        val textView = view as TextView

        val priority = getItem(position)
        textView.setText(priority.nameRes)

        val icon = ContextCompat.getDrawable(parent.context, priority.iconRes)
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)

        return view
    }
}
