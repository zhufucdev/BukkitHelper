package com.zhufucdev.bukkithelper.ui.key

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zhufucdev.bukkit_helper.Key
import com.zhufucdev.bukkithelper.R
import com.zhufucdev.bukkithelper.manager.KeyManager

class KeyEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_key_edit, container, false)
        val contentLayout: TextInputLayout = root.findViewById(R.id.edit_content_layout)
        val contentEdit: TextInputEditText = root.findViewById(R.id.edit_content)
        val nameLayout: TextInputLayout = root.findViewById(R.id.edit_name_layout)
        val nameEdit: TextInputEditText = root.findViewById(R.id.edit_name)

        nameEdit.doAfterTextChanged {
            nameLayout.error = if (nameEdit.text.isNullOrEmpty()) {
                requireContext().getString(R.string.text_content_empty)
            } else {
                null
            }
        }
        contentEdit.doAfterTextChanged {
            val text = contentEdit.text
            contentLayout.error = if (text.let { !it.isNullOrEmpty() &&!Key.isKey(it.toString()) }) {
                requireContext().getString(R.string.text_content_not_key)
            } else {
                null
            }
        }
        contentLayout.setEndIconOnClickListener { contentEdit.setText(Key().toString()) }

        val fab: ExtendedFloatingActionButton = requireActivity().findViewById(R.id.fab_main)
        fab.apply {
            hide()
            setText(R.string.action_done)
            setIconResource(R.drawable.ic_baseline_done_24)
            show()
        }
        fab.setOnClickListener {
            val name = nameEdit.text?.toString()
            val content = contentEdit.text?.toString()

            // <editor-fold desc="Check input">
            if (name.isNullOrEmpty()) {
                nameLayout.error = requireContext().getString(R.string.text_content_empty)
                return@setOnClickListener
            }
            if (KeyManager[name] != null) {
                nameLayout.error = requireContext().getString(R.string.text_key_exists)
                return@setOnClickListener
            }
            if (content.isNullOrEmpty()) {
                contentLayout.error = requireContext().getString(R.string.text_content_empty)
                return@setOnClickListener
            }
            // </editor-fold>
            // <editor-fold desc="Navigate up">
            findNavController().apply {
                previousBackStackEntry?.arguments?.apply {
                    putString("content", content)
                    putString("keyName", name)
                }
                navigateUp()
            }
            // </editor-fold>
        }
        return root
    }

}