package com.cxytiandi.kitty.common.context;


public class ContextHolder {
    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<RequestContext>() {
        @Override
        protected RequestContext initialValue() {
            return new DefaultContext();
        }
    };


    public static RequestContext getCurrentContext() {
        return contextHolder.get();
    }
    
    public static void setCurrentContext(RequestContext requestContext) {
        contextHolder.set(requestContext);
    }


    public static void clearCurrentContext() {
        contextHolder.remove();
    }
}
