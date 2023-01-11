package top.ncserver.chatsync.Until;

import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.ncserver.chatsync.main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.ncserver.chatsync.main.bot;
import static top.ncserver.chatsync.main.main;

public class Writer extends Thread implements Runnable{
    public static Logger logger= LogManager.getLogger(Writer.class);
    Pattern p = Pattern.compile("[(/+)\u4e00-\u9fa5(+*)]");
    public static String getEncode() {
        if (Pattern.matches("Linux.*", System.getProperty("os.name")))
            return "UTF-8";
        return "GBK";
    }
    Socket client;
    BufferedWriter bw;
    Listener listener;
    private OutputStream out=null;
    public Writer(Socket s) throws IOException {
        client=s;
        out=  s.getOutputStream();
         bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), getEncode()));
    }
    MiraiConfig miraiConfig = new MiraiConfig();
    @Override
    public void run() {
        logger.info("start catch");
             listener= GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, (event) -> {
                if (event.getGroup().getId()==454785725L) {
                    try {
                        Map<String,Object> msg1 = new HashMap<>();
                        String msgString=event.getMessage().contentToString();
                       // msgString=msgString.replaceAll("\")
                        //At at=new At()
                        Matcher m = p.matcher(msgString.substring(1));
                        if (msgString.startsWith("/chatgpt")){
                            try {
                               String msg= msgString.split(" ")[1];//miraiConfig.getAnswerByChatGPT(msg)
                                if (msg.equals("reset")){
                                    bot.getGroup(454785725L).sendMessage(new PlainText("请在浏览器中完成操作"));
                                    main.chatGPT= new ChatGPT();
                                    if (main.chatGPT.getFlag())
                                        bot.getGroup(454785725L).sendMessage(new PlainText("成功"));
                                    else bot.getGroup(454785725L).sendMessage(new PlainText("失败"));
                                }else if (msg.equals("reload")){
                                    ChatGPTInitTool.reload();
                                }else
                                    ExternalResource.sendAsImage(main.chatGPT.getRe(msg),bot.getGroup(454785725L));
                                //bot.getGroup(454785725L).sendMessage(new PlainText());
                            }catch (Exception e){
                                bot.getGroup(454785725L).sendMessage(new PlainText("输入的内容无效"));
                            }
                        }else
                        if(msgString.equals("/recon"))
                        {
                            bot.getGroup(454785725L).sendMessage(new PlainText("消息同步正在重新连接....."));
                            client.close();
                        }else if (msgString.startsWith("/points")&&!m.lookingAt()){
                            if (event.getSender().getPermission().getLevel()>=2){
                                msg1.put("type","command");
                                msg1.put("sender",event.getSenderName()+"("+event.getSender().getId()+")");
                                msg1.put("command",event.getMessage().contentToString());
                                JSONObject jo= new JSONObject(msg1);
                                logger.info(jo.toJSONString());
                                send(jo.toJSONString());
                            }else {
                                if (event.getSender().getPermission().getLevel()>=1){
                                    bot.getGroup(454785725L).sendMessage(new PlainText("您这是要监守自盗嘛,亲爱的管理员"));
                                    bot.getFriend(748436276L).sendMessage(new PlainText(event.getSenderName()+"("+event.getSender().getId()+")"+"监守自盗"));
                                }

                                else bot.getGroup(454785725L).sendMessage(new PlainText("你无权执行"+msgString));
                            }
                        }else
                            if (msgString.startsWith("/")&&!m.lookingAt()){
                            if(event.getSender().getPermission().getLevel()>=1||msgString.equals("/ls")){
                                msg1.put("type","command");
                                msg1.put("sender",event.getSenderName()+"("+event.getSender().getId()+")");
                                msg1.put("command",event.getMessage().contentToString());
                                JSONObject jo= new JSONObject(msg1);
                                logger.info(jo.toJSONString());
                                send(jo.toJSONString());
                            }else if (msgString.contains("/LS")||msgString.contains("/IS")||msgString.contains("/Is")){
                                bot.getGroup(454785725L).sendMessage(new PlainText("PS:正确的命令为/ls(均为小写.其大写形式为/LS)"));
                            }else{
                                bot.getGroup(454785725L).sendMessage(new PlainText("你无权执行"+msgString));
                            }
                                } else
                                {
                                    msg1.put("type","msg");
                                    msg1.put("permission",event.getSender().getPermission().getLevel());
                                    msg1.put("sender",event.getSenderName());
                                    String miraiCode=event.getMessage().toString();
                                    logger.debug(miraiCode);
                                    StringBuilder msgBuilder=new StringBuilder();
                                    if (miraiCode.contains("[mirai:quote:["))
                                    {
                                        logger.debug("catch");
                                        QuoteReply quote = event.getMessage().get(QuoteReply.Key);
                                        At at=new At(quote.getSource().getFromId());
                                        msgBuilder.append("\u00A75 回复了\n\u00A73");
                                        msgBuilder.append(at.getDisplay(event.getGroup()).replaceFirst("@","["));
                                        msgBuilder.append("]:");
                                        msgBuilder.append(quote.getSource().getOriginalMessage()+"\n\u2191---");
                                    }
                                    if (miraiCode.contains("[mirai:at:")){//at处理辣鸡Mirai BUG不修
                                        int t=0;
                                        while(miraiCode.indexOf("[mirai:at:",t)!=-1)
                                        {
                                            t=miraiCode.indexOf("[mirai:at:",t);
                                            long qqCode= Long.parseLong(miraiCode.substring(t+10,miraiCode.indexOf("]",t)));
                                            At at=new At(qqCode);
                                            msgBuilder.append(msgString.replaceFirst("@"+qqCode,at.getDisplay(event.getGroup())));
                                            t=miraiCode.indexOf("]",t);
                                        }
                                        msg1.put("msg",msgBuilder);
                                    }else
                                    msg1.put("msg",msgString);
                                    JSONObject jo= new JSONObject(msg1);
                                    logger.info(jo.toJSONString());
                                    send(jo.toJSONString());
                                }

                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                        this.stop();
                    }
                }
            });
            //listener.start();
        while(client.isConnected()){try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            listener.complete();
            e.printStackTrace();
        }}
    }
    public void send(String msg) throws InterruptedException, IOException {

        while (out==null||!client.isConnected()){}//

        bw.write(msg+'\n');
        try {
            bw.flush();
        } catch (IOException e) {
            listener.complete();
           this.stop();
        }
    }

}
