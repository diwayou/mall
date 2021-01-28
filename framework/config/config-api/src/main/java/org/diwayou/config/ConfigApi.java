package org.diwayou.config;

import org.diwayou.core.spi.ServiceRegister;

/**
 * @author gaopeng 2021/1/29
 */
public class ConfigApi {

    private static IConfig iConfig;

    static {
        ServiceRegister.register(IConfig.class);

        iConfig = ServiceRegister.newAnyInstance(IConfig.class);
    }

    public static String getProperty(String namespace, String key) {
        return iConfig.getProperty(namespace, key);
    }

    public static void addListener(String namespace, String key, ConfigListener listener) {
        iConfig.addListener(namespace, key, listener);
    }
}
