package cn.fyupeng.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: fyp
 * @Date: 2022/4/13
 * @Description:
 * @Package: com.crop.utils
 * @Version: 1.0
 */
public class RedisUtils {

    public static final String Blog = "blog";

    public static final String USER_REDIS_SESSION = Blog + ":" + "user-redis-session";
    public static final String ADMIN_REDIS_SESSION = Blog + ":" + "admin-redis-session";


    public static final String IS_VIEW = Blog + ":" + "isView";

    public static final String VIEW_COUNT = Blog + ":" + "view-count";

    public static final String SEARCH_HISTORY = Blog + ":" + "search-history";

    public static final String SEARCH_SCORE = Blog + ":" + "search-score";


    public static String getUserRedisSession(String userId) {
        return USER_REDIS_SESSION + ":" + userId;
    }

    public static String getAllUserRedisSession() {
        return USER_REDIS_SESSION;
    }

    public static String getAdminRedisSession(String userId) {
        return ADMIN_REDIS_SESSION + ":" + userId;
    }

    public static String getIdView(String id, HttpServletRequest request) {
        return IS_VIEW + ":" + id + ":" + RequestAddr.getClientIpAddress(request);
    }

    public static String getViewCount() {
        return VIEW_COUNT + "*";
    }

    public static String getIdViewCount(String id) {
        return VIEW_COUNT + ":" + id;
    }

    public static String getSearchHistoryKey(String userId) {
        return SEARCH_HISTORY + ":" + userId;
    }

    public static String getSearchHistoryKeyWithSearchKey(String userId, String key) {
        return SEARCH_HISTORY + ":" + userId + ":" + key;
    }

    public static String getSearchScoreKey() {
        return SEARCH_SCORE;
    }

    public static String getSearchScoreKeyWithSearchKey(String key) {
        return SEARCH_SCORE + ":" + key;
    }


    static class RequestAddr {

        /**
         * 获取客户端ip地址(可以穿透代理)
         *
         * @param request
         * @return
         */
        public static String getRemoteAddr(HttpServletRequest request) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        }
        private static final String[] HEADERS_TO_TRY = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR",
                "X-Real-IP"};

        /***
         * 获取客户端ip地址(可以穿透代理)
         * @param request
         * @return
         */
        public static String getClientIpAddress(HttpServletRequest request) {
            for (String header : HEADERS_TO_TRY) {
                String ip = request.getHeader(header);
                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
            }
            return request.getRemoteAddr();
        }

        public static String getIpAddr(HttpServletRequest request) {
            String ip = request.getHeader("X-Real-IP");
            if (null != ip && !"".equals(ip.trim())
                    && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
            ip = request.getHeader("X-Forwarded-For");
            if (null != ip && !"".equals(ip.trim())
                    && !"unknown".equalsIgnoreCase(ip)) {
                // get first ip from proxy ip
                int index = ip.indexOf(',');
                if (index != -1) {
                    return ip.substring(0, index);
                } else {
                    return ip;
                }
            }
            return request.getRemoteAddr();
        }

    }

}
