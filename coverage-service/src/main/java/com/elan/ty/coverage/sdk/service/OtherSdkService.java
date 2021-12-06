package com.elan.ty.coverage.sdk.service;
import org.springframework.stereotype.Service;

/**
 * 这个类没有太大的实际含义，可以是连接任何第三方服务的地方，但是非常不好实现，比如某个服务只有在线上才有
 */
@Service
public class OtherSdkService {

    private String getSome(String id){
        return "success";
    }
}
