package top.ncserver.chatsync.Until;

import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.PlainText;
import top.ncserver.chatsync.main;

import static top.ncserver.chatsync.main.bot;

public class ServerOffineTool {
    static Listener listener;
    public static void stop(){
        listener.complete();
    }
    public static void init(){
        listener= GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, (event) -> {
            if (event.getGroup().getId()==454785725L){
                String msg=event.getMessage().contentToString();
                if (msg.contains("/ls")||msg.contains("/list")) {
                    bot.getGroup(454785725L).sendMessage(new PlainText("抱歉,服务器处于离线状态"));
                }else if (msg.contains("/LS")||msg.contains("/IS")||msg.contains("/Is")){
                    bot.getGroup(454785725L).sendMessage(new PlainText("抱歉,服务器处于离线状态\nPS:正确的命令为/ls(均为小写.其大写形式为/LS)"));
                }
            }


        });
    }
}
