package test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class NettyServer {
    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }
    public static void main(String[] args) {
        try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        new NettyServer(8888).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        new HttpServer(8686).run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void start() throws Exception{
        final MyServerHandler serverHandler = new MyServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group).channel(NioServerSocketChannel.class)
                   .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
//                    socketChannel.pipeline().addLast(serverHandler);
                    socketChannel.pipeline().addLast("myHandler", new MyServerHandler());
                }
            }).option(ChannelOption.SO_BACKLOG, 1024)
                    //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

                    //将小的数据包包装成更大的帧进行传送，提高网络的负载,即TCP延迟传输
                    .childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = b.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
