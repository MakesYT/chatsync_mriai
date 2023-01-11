package top.ncserver.chatsync.Until;

import net.mamoe.mirai.console.command.CommandContext;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.console.data.AbstractPluginData;
import net.mamoe.mirai.console.data.PluginDataExtensions;
import net.mamoe.mirai.console.data.Value;
import top.ncserver.Chatsync;

public final  class Command extends JCompositeCommand {
    public Command() {
        super(Chatsync.INSTANCE, "chatsync");
        this.setDescription("设置消息同步有关的命令输入/chatsync help获取帮助");

        // ...
    }
    @SubCommand("help")
    public void help(CommandContext context) {
        System.out.println("/chatsync port设置消息同步监听端口");
        System.out.println("/chatsync groupid <id> 设置消息同步的QQ群ID");
    }
    @SubCommand("port")
    public void port(CommandContext context,int port) {
        Config.INSTANCE.setPort(port);
        System.out.println("端口更改为"+port+"请重启Mirai以应用更改");
    }
    @SubCommand("groupid")
    public void port(CommandContext context,Long id) {
        Config.INSTANCE.setGroupID(id);
        System.out.println("消息同步群已更改为"+id);
    }
}
