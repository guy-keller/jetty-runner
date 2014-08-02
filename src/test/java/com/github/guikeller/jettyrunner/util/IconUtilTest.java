package com.github.guikeller.jettyrunner.util;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IconUtilTest {

    @Test
    public void testGetInstance() throws Exception {
        IconUtil instance = IconUtil.getInstance();
        assertNotNull(instance);
    }
}