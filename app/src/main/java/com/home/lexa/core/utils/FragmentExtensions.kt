// FragmentExtensions.kt
package com.home.lexa.core.ui

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.setTopBarTitle(title: String) {
    findNavController().currentBackStackEntry
        ?.savedStateHandle
        ?.set(TopBarKeys.TITLE, title)
}