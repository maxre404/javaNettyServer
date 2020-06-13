package test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@ChannelHandler.Sharable
public class MyServerHandler extends ChannelInboundHandlerAdapter {
    public static List clinetList=new ArrayList<ChannelHandlerContext>();
    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("server received: " + in.toString(CharsetUtil.UTF_8));
//        String str="good";
//        context.write(in);
//        context.write(Unpooled.wrappedBuffer("大家都还好吗  哈哈哈哈++".getBytes()));
//        try {
//            Thread.sleep(5000);
//            context.writeAndFlush("ice");
//            System.out.println("发送出去");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("读取完毕 准备关闭");
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable couse) {
        System.out.println("异常问题出现");
        couse.printStackTrace();
        context.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("+++++++");

    }
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        Channel channel = ctx.channel();// 获取连接对象
        clinetList.add(ctx);
//        ctx.writeAndFlush(Unpooled.wrappedBuffer("大家都还好吗  哈哈哈哈++".getBytes()));
        System.out.println(channel.remoteAddress()+"上线");
        byte[]data=new byte[16];
        int position=0;
        BaseNetTool.writeInt(101,data,position);
        position+=4;
        BaseNetTool.writeInt(1,data,position);
        position+=4;
        BaseNetTool.writeLong(System.currentTimeMillis(),data,position);
        position+=8;
        try {
            byte[] bytes = BaseNetTool.appendHead2(data);
            ctx.writeAndFlush(Unpooled.wrappedBuffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        Channel channel = ctx.channel();// 获取连接对象
        System.out.println(channel.remoteAddress()+"下线");
        clinetList.remove(ctx);
    }

}
