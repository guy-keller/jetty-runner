package com.github.guikeller.jettyrunner.util;

import javax.swing.*;
import java.net.URL;

/**
 * Created by Gui on 12/07/2014.
 */
public class IconUtil {

    private static final IconUtil INSTANCE = new IconUtil();

    private IconUtil(){
        super();
    }

    public static IconUtil getInstance(){
        return INSTANCE;
    }

    public Icon getIcon(){
        URL resource = INSTANCE.getClass().getResource("/jetty-icon.png");
        return new ImageIcon(resource);
    }

}
