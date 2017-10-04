package com.github.guikeller.jettyrunner.util;

import javax.swing.*;
import java.net.URL;

/**
 * IconUtil - Loads the jetty icon
 * @author Gui Keller
 */
public class IconUtil {

    private static final IconUtil INSTANCE = new IconUtil();
    private static final Icon ICON = loadIcon();

    private IconUtil(){
        super();
    }

    public static IconUtil getInstance(){
        return INSTANCE;
    }

    private static Icon loadIcon() {
        URL resource = INSTANCE.getClass().getResource("/jetty-icon.png");
        return new ImageIcon(resource);
    }

    public Icon getIcon(){
        return ICON;
    }

}
