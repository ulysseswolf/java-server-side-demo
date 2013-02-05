package com.lenovo.ics.filter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 过滤GZIP请求的数据
 */
public class GzipFilter implements Filter {

    private static final Logger sLogger = LoggerFactory.getLogger(GzipFilter.class);

    private static final String DEFAULT_CHARSET = "utf-8";

    private String mCharset;

    /**
     * 初始化传输内容的编码
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        sLogger.debug("GzipFilter.init");
        String charset = config.getInitParameter("charset");
        if (StringUtils.isBlank(charset) || !Charset.isSupported(charset)) {
            mCharset = DEFAULT_CHARSET;
        } else {
            mCharset = charset;
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) resp;
            if (isGzipSupported(request)) {
                sLogger.info("客户端启用gzip压缩格式传输");
                chain.doFilter(new GzipHttpServletRequest(request), new GzipHttpServletResponse(response));
                return;
            } else {
            	sLogger.info("客户端未启用gzip压缩格式传输");
				request.setCharacterEncoding(mCharset);
				resp.setCharacterEncoding(mCharset);
				resp.setContentType("text/json;charset=".concat(mCharset));
				chain.doFilter(req, resp);
            }
        }
    }

    /**
     * 检测客户端是否支持GZIP格式编码
     */
    private boolean isGzipSupported(HttpServletRequest request) {
        String encoding = request.getHeader("accept-encoding");
        return (!StringUtils.isBlank(encoding) && (encoding.indexOf("gzip") != -1));
    }

    @Override
    public void destroy() {
    }

    /**
     * 自定义的Request，以便使用GZIP格式读取客户端上报的数据
     */
    class GzipHttpServletRequest extends HttpServletRequestWrapper {

        public GzipHttpServletRequest(HttpServletRequest request) throws IOException {
            super(request);
            request.setCharacterEncoding(mCharset);
        }

        /**
         * 返回GZIP格式的数据流对象
         */
        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new GzipInputStream((HttpServletRequest) getRequest());
        }

    }

    /**
     * 自定义输入流，采用GZIP方式来读取数据
     */
    class GzipInputStream extends ServletInputStream {

        private GZIPInputStream stream;

        public GzipInputStream(HttpServletRequest req) throws IOException {
            this.stream = new GZIPInputStream(req.getInputStream());
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return stream.read(b, off, len);
        }

        @Override
        public void close() throws IOException {
            stream.close();
        }

    }

    /**
     * 自定义Response，以便使用GZIP格式向客户端传输数据
     */
    class GzipHttpServletResponse extends HttpServletResponseWrapper {

        public GzipHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new GzipOutputStream((HttpServletResponse) getResponse());
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(new OutputStreamWriter(getOutputStream(), mCharset));
        }

    }

    /**
     * 自定义输出流，采用GZIP方式往客户端返回数据
     */
    class GzipOutputStream extends ServletOutputStream {

        private GZIPOutputStream stream;

        public GzipOutputStream(HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding(mCharset);
            resp.setContentType("text/json;charset=".concat(mCharset));
            resp.setHeader("content-encoding", "gzip");

            this.stream = new GZIPOutputStream(resp.getOutputStream());
        }

        @Override
        public void write(int b) throws IOException {
            stream.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            stream.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            stream.flush();
        }

        @Override
        public void close() throws IOException {
            stream.close();
        }

    }
}
