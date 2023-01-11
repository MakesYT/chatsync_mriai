package top.ncserver.chatsync.Until;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Pattern;

import static top.ncserver.chatsync.Until.ColorCodeCulling.CullColorCode;
import static top.ncserver.chatsync.main.bot;

public class Reader extends Thread implements Runnable{
    Socket in;
    Writer out;
    BufferedReader b;
    public static String getEncode() {
        if (Pattern.matches("Linux.*", System.getProperty("os.name")))
            return "UTF-8";
        return "GBK";
    }
    public Reader(Socket in, Writer out) throws IOException, ClassNotFoundException {
        this.in=in;
        this.out=out;
        this.b=new BufferedReader(new InputStreamReader(in.getInputStream(), getEncode()));
    }
    @Override
    public void run() {
        while(true)
        {
            try {
                String echo = b.readLine();
                    System.out.println(echo);
                    JSONObject jsonObject = JSONObject.parseObject(echo);
                    switch (jsonObject.getString("type")){
                        case "msg":
                            System.out.println("["+jsonObject.getString("sender")+"]:"+jsonObject.getString("msg"));
                            bot.getGroup(454785725L).sendMessage("みさか("+jsonObject.getString("sender")+"):\""+jsonObject.getString("msg")+"\"");
                            break;
                        case "playerJoinAndQuit":
                            bot.getGroup(454785725L).sendMessage("玩家"+CullColorCode(jsonObject.getString("player"))+jsonObject.getString("msg"));
                            break;
                        case "playerList":
                            bot.getGroup(454785725L).sendMessage("当前有"+jsonObject.getString("online")+"位玩家在线\n"+jsonObject.getString("msg"));
                            break;
                        case "command":
                            bot.getGroup(454785725L).sendMessage("接收到命令回馈,正在渲染和上传图片");

                            long start = System.currentTimeMillis();
                            File file=TextToImg.toImg(jsonObject.getString("command"));
                            ExternalResource.sendAsImage(file,bot.getGroup(454785725L));

                            long finish = System.currentTimeMillis();
                            long timeElapsed = finish - start;
                            bot.getGroup(454785725L).sendMessage("完成,耗时"+timeElapsed+"ms");
                            System.gc();
                           // bot.getGroup(454785725L).sendMessage(Image.fromId(
                           // bot.getGroup(454785725L).sendMessage(Image.fromId(ExternalResource.create(TextToImg.toImg(jsonObject.getString("command"))).toAutoCloseable().calculateResourceId()));
                                break;
                        case "serverCommand":
                            bot.getGroup(454785725L).sendMessage("注意服务器执行："+jsonObject.getString("command")+"\n注意服务器安全");
                            break;
                        case "playerDeath":
                        case "obRe":
                            bot.getGroup(454785725L).sendMessage(CullColorCode(jsonObject.getString("msg")));
                            break;
                    }
            } catch (IOException e) {
                e.printStackTrace();
                this.stop();
                break;
            }


        }
    }
}
