//package com.easy.utility;
//
//import com.easy.access.Datas;
//import com.easy.constants.RedisKey;
//import com.easy.entity.ServicesBean;
//import com.easy.sequence.SequenceFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class RedisUtility {
//    @Resource
//    private RedisTemplate redisTemplate;
//
//    private void setRedisRegNoByDLS(String indx) throws Exception{
//        String key = RedisKey.dls_agent+indx;
//        if(SysUtility.isEmpty(redisTemplate.opsForValue().get(key))){
//            String regNo = SequenceFactory.getSequence("AGENT_REG_NO", "000");
//            redisTemplate.opsForValue().set(key, regNo, 3, TimeUnit.DAYS);
//        }
//        return;
//    }
//
//    public String getRedisRegNoByDLS(String indx) throws Exception{
//        String key = RedisKey.dls_agent+indx;
//        if(SysUtility.isEmpty(redisTemplate.opsForValue().get(key))){
//            setRedisRegNoByDLS(indx);
//        }
//        return ""+redisTemplate.opsForValue().get(key);
//    }
//
//}
