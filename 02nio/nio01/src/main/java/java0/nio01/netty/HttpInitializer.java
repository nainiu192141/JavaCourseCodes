package java0.nio01.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		//p.addLast(new HttpServerCodec());//http请求
		//p.addLast(new HttpServerExpectContinueHandler());
		//p.addLast(new HttpObjectAggregator(1024 * 1024));

		// marshalling 序列化对象的解码
		//socketChannel.pipeline().addLast(MarshallingCodefactory.buildDecoder());
		// marshalling 序列化对象的编码
		//socketChannel.pipeline().addLast(MarshallingCodefactory.buildEncoder());
		// 网络超时时间
		//socketChannel.pipeline().addLast(new ReadTimeoutHandler(5));
		p.addLast(new HttpHandler());
		p.addLast(new StringDecoder());
	}
}
/**
 *
 * 解决数据传输中中的拆包和粘包问题方案：
 *
 * 一 . 用特定字符当做分隔符，例如：$_
 *  （1） 将下列代码添加到 initChannel方法内
 //将双方约定好的分隔符转成buf
 ByteBuf bb = Unpooled.copiedBuffer("$_".getBytes(Constant.charset));
 socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, bb));
 //将接收到信息进行解码，可以直接把msg转成字符串
 socketChannel.pipeline().addLast(new StringDecoder());

 （2） 在 ServerHandler中的 channelRead方法中应该替换内容为
 // 如果把msg直接转成字符串，必须在服务中心添加 socketChannel.pipeline().addLast(new StringDecoder());
 String reqStr = (String)msg;
 System.err.println("server 接收到请求信息是："+reqStr);
 String respStr = new StringBuilder("来自服务器的响应").append(reqStr).append("$_").toString();
 // 返回给客户端响应
 ctx.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes()));

 (3) 因为分隔符是双方约定好的，在ClientNetty和channelRead中也应该有响应的操作


 二. 双方约定好是定长报文
 // 双方约定好定长报文为6，长度不足时服务端会一直等待直到6个字符，所以客户端不足6个字符时用空格补充；其余操作，参考分隔符的情况
 socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(6));


 三. 请求分为请求头和请求体，请求头放的是请求体的长度；一般生产上常用的

 （1）通信双方约定好报文头的长度，先截取改长度，
 （2）根据报文头的长度读取报文体的内容
 *
 *
 */
