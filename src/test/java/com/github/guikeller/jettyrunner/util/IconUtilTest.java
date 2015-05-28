package com.github.guikeller.jettyrunner.util;

import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IconUtilTest {

    @Test
    public void testGetInstance() throws Exception {
        IconUtil instanceA = IconUtil.getInstance();
        IconUtil instanceB = IconUtil.getInstance();
        assertNotNull(instanceA);
        assertNotNull(instanceB);
        assertEquals(instanceA, instanceB);
    }

    @Test
    public void testGetIcon(){
        IconUtil instance = IconUtil.getInstance();
        Icon iconA = instance.getIcon();
        Icon iconB = instance.getIcon();
        assertNotNull(iconA);
        assertNotNull(iconB);
        assertEquals(iconA, iconB);
    }
}