package net.yupol.transmissionremote.app.torrentlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.OnClick
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.empty_server_layout.*
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.utils.ColorUtils

class EmptyServerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.empty_server_layout, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val iconColor = ColorUtils.resolveColor(context!!, R.attr.colorAccent, R.color.accent)
        addButton.setCompoundDrawables(IconicsDrawable(context!!)
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(iconColor).sizeRes(R.dimen.default_button_icon_size), null, null, null)
    }

    @OnClick(R.id.addButton)
    fun onAddButtonClicked() {
        if (activity is OnAddServerClickListener) {
            (activity as OnAddServerClickListener).onAddServerButtonClicked()
        }
    }

    interface OnAddServerClickListener {
        fun onAddServerButtonClicked()
    }
}
