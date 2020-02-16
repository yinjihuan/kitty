package com.cxytiandi.kitty.servicecall.dubbo;


import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
/**
 * 代码来自官方给出的示列：https://github.com/dianping/cat/tree/master/integration/dubbo
 */
@Activate(group = {CommonConstants.CONSUMER})
public class AppNameAppendFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext.getContext().setAttachment(CommonConstants.APPLICATION_KEY,invoker.getUrl().getParameter(CommonConstants.APPLICATION_KEY));
        return invoker.invoke(invocation);
    }

}