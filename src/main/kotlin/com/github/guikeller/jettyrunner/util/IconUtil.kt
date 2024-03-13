package com.github.guikeller.jettyrunner.util

import javax.swing.Icon
import javax.swing.ImageIcon

/**
 * IconUtil - Loads the jetty icon
 * @author Guy Keller
 */
class IconUtil private constructor() {

    companion object {
        private var instance: IconUtil? = null
        fun getInstance(): IconUtil {
            if (instance == null) {
                instance = IconUtil()
            }
            return instance as IconUtil
        }
    }

    private var icon: Icon? = null

    private fun loadIcon(): Icon {
        if (icon == null) {
            val resource = javaClass.getResource("/META-INF/jetty-icon.png")
            icon = ImageIcon(resource)
        }
        return icon as Icon
    }

    fun getIcon() = icon ?: loadIcon()

}
