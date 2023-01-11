package top.ncserver;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

public final class Chatsync extends JavaPlugin {
    public static final Chatsync INSTANCE = new Chatsync();

    private Chatsync() {
        super(new JvmPluginDescriptionBuilder("top.ncserver.chatsync", "0.1.0")
                .name("chatsync")
                .author("makesyt")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
    }
}