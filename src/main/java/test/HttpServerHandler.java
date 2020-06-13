package test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // 获取请求的uri
        String uri = req.uri();
        String msg = "<html><head><title>DEMO</title></head><body>你请求uri为：" + uri+"</body></html>";
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        // 将html write到客户端
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        if (uri.contains("send")){
            QueryStringDecoder decoder = new QueryStringDecoder(uri);
            String msg1 = decoder.parameters().get("msg").get(0);
            System.out.println("收到的msg:"+msg1);
            for (int i = 0; i < MyServerHandler.clinetList.size(); i++) {
                System.out.println("发送消息:"+msg1+"长度:"+msg1.getBytes().length);
                ChannelHandlerContext context = (ChannelHandlerContext) MyServerHandler.clinetList.get(i);
//                context.writeAndFlush("helloword  good name");
//                context.writeAndFlush(Unpooled.wrappedBuffer(msg1.getBytes()));
                byte[] msgBytes = msg1.getBytes();
                byte[]data=new byte[8+msgBytes.length];
                int position=0;
                BaseNetTool.writeInt(102,data,position);
                position+=4;
                BaseNetTool.writeInt(msgBytes.length,data,position);
                position+=4;
                BaseNetTool.writeUTF8_2(msg1,data,position);
                try {
                    byte[] bytes = BaseNetTool.appendHead2(data);
                    context.writeAndFlush(Unpooled.wrappedBuffer(bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }



    }
}
