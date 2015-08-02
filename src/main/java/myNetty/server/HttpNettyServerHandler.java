package myNetty.server;

/**
 * @author Yurii Solopko
 */

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import myNetty.domain.RedirectRequest;
import myNetty.domain.Request;
import myNetty.domain.RequestsByIp;
import myNetty.service.RequestService;
import myNetty.service.RequestServiceImpl;

import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpNettyServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };
    private Request incomingRequest;
    private long start;
    private long end;
    private boolean error404;
    private RequestService requestService;

    public HttpNettyServerHandler() {
        requestService = new RequestServiceImpl();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        //if request URI is valid
        if(!error404) {
            end = System.currentTimeMillis();
            if (incomingRequest != null) {
                Double speed = ( 1000 * ( incomingRequest.getReceivedBytes() +
                        incomingRequest.getSentBytes() ) ) / ((double)(end - start));
                incomingRequest.setSpeed(speed);
                requestService.saveRequest(incomingRequest);
            }
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest request) {
        start = System.currentTimeMillis();
        String uri = request.getUri();

        String remoteAddress = ctx.channel().remoteAddress().toString();

        String sourceIp = remoteAddress.substring(1, remoteAddress.indexOf(":"));
        incomingRequest = new Request();
        incomingRequest.setSourceIP(sourceIp);
        incomingRequest.setUri(uri);
        incomingRequest.setReceivedBytes(uri.getBytes().length);

        FullHttpResponse response = null;
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);

        if(uri.equalsIgnoreCase("/hello")) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
            response.headers().set(CONTENT_TYPE, "text/plain");
            Integer sentBytes = response.content().readableBytes();
            incomingRequest.setSentBytes(sentBytes);
            response.headers().set(CONTENT_LENGTH, sentBytes);

        } else if (uri.toLowerCase().startsWith("/redirect") && queryStringDecoder.parameters().size() == 1
                && !queryStringDecoder.parameters().get("url").get(0).isEmpty()) {

            incomingRequest.setSentBytes(0);

            String url = queryStringDecoder.parameters().get("url").get(0);
            if(!url.toLowerCase().startsWith("http://")) {
                url = "http://" + url;
            }
            response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            response.headers().set(LOCATION, url);

        } else if (uri.equalsIgnoreCase("/status")) {

            response = createStatusResponse();
            Integer sentBytes = response.content().readableBytes();
            incomingRequest.setSentBytes(sentBytes);
            response.headers().set(CONTENT_LENGTH, sentBytes);
        } else {
            response = new DefaultFullHttpResponse(
                    HTTP_1_1, NOT_FOUND, Unpooled.copiedBuffer("Failure: " + NOT_FOUND + "\r\n", CharsetUtil.UTF_8));
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            error404 = true;
        }
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        requestService.incrementActiveConnections();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        requestService.decrementActiveConnections();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private FullHttpResponse createStatusResponse() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("<html><head><title>Status page</title></head><body>" +
                "<div><span>Total number of requests : ");
        //Total number of requests
        buffer.append(requestService.getRequestCount());
        buffer.append("</span></div><br><div><span>Unique number of requests : ");
        //Unique number of requests
        buffer.append(requestService.getUniqueRequestCount());
        buffer.append("</span></div><br>");
        //Requests by IP table
        buffer.append("<table border=\"1\"><tr><th width=\"100\">IP</th><th width=\"75\">No. of requests" +
                "</th><th width=\"220\">Last request time</th></tr>");
        for (RequestsByIp requestByIp : requestService.getRequestsByIP()) {
            buffer.append("<tr><td>" + requestByIp.getIp() + "</td><td>" + requestByIp.getCount() +"</td><td>" +
                    requestByIp.getLastRequestTime() + "</td></tr>");
        }
        buffer.append("</table><br>");
        //Redirects table
        buffer.append("<table border=\"1\"><tr><th>URL</th><th>Redirect count</th></tr>");
        for (RedirectRequest redirectRequest : requestService.getRedirectRequests()) {
            buffer.append("<tr><td>" + redirectRequest.getUrl() + "</td><td>" + redirectRequest.getCount() + "</td></tr>");
        }
        buffer.append("</table><br>");
        //Active connections count
        buffer.append("<div><span>No. of currently opened connections : ");
        buffer.append(requestService.getActiveConnectionsCount());
        buffer.append("</span></div><br>");
        //Last 16 requests table
        buffer.append("<table border=\"1\"><tr><th>Source IP</th><th width=\"200\">URI</th><th>Timestamp</th><th>Sent bytes</th>" +
                "<th width=\"75\">Received bytes</th><th>Speed(bytes/sec)</th></tr>");
        for (Request request : requestService.getLast16Requests()) {
            buffer.append("<tr><td>" + request.getSourceIP() + "</td><td>" + request.getUri() + "</td><td>" +
                    request.getTime() + "</td><td>" + request.getSentBytes() + "</td><td>" + request.getReceivedBytes() +
                    "</td><td>" + request.getSpeed() + "</td><tr>");
        }
        buffer.append("</table><br></body><html>");

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(buffer.toString(),
                CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

        return response;
    }
}