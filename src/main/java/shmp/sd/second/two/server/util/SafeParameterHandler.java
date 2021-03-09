package shmp.sd.second.two.server.util;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;

import java.util.List;


public class SafeParameterHandler {
    public static String getRequestParamSafe(HttpServerRequest<ByteBuf> req, String paramName) {
        List<String> params = req.getQueryParameters().get(paramName);
        String param = null;

        if (params != null) {
            param = params.stream()
                    .findFirst()
                    .orElse(null);
        }

        return param;
    }
}
