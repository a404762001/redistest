package com.example.redistest;

import com.example.redistest.service.RedisService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/secKill")
public class SeckillController {

    @Autowired
    public RedisService redisService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DefaultRedisScript<Long> redisScript;

    @GetMapping("/doSecKill")
    public String doSecKill(String pid) {
        //开启事务权限
        //stringRedisTemplate.setEnableTransactionSupport(true);


        String uid = getUid();
        //1 uid和prodid非空判断
        if (uid == null || pid == null) {
            return "uid和pid不能为空";
        }
        //2 拼接key
        // 2.1 库存key
        String kcKey = "sk:" + pid + ":qt";
        // 2.2 秒杀成功用户key
        String userKey = "sk:" + pid + ":user";

        //3 获取库存，如果库存为null,则活动还没开始
        String kc = redisService.get(kcKey);
        if (kc == null) {
            return "活动还没开始";
        }

        //4 判断用户是否重复秒杀操作
        if (redisService.sismember(userKey, uid)) {
            return "已经秒杀成功了，不能重复秒杀";
        }

        //5 判断如果商品数量，库存数量小于1，秒杀结束
        if (Integer.parseInt(kc) <=0) {
            return "秒杀已经结束了";
        }

        //6 开始秒杀流程
        //6.1 使用lua脚本操作 保持原子性
//        List<String> keys = Arrays.asList(uid, pid);
//        Object execute = stringRedisTemplate.execute(redisScript, keys, uid, pid);
//
//        String result = String.valueOf(execute);
//        if ("0".equals(result)) {
//            System.out.println("已抢空");
//        } else if ("1".equals(result)) {
//            System.out.println("抢购成功");
//        } else if ("2".equals(result)) {
//            System.err.println("该用户已抢过！！");
//        } else {
//            System.err.println("抢购异常！！");
//        }

        //stringRedisTemplate.watch(kcKey);

//        try {
//
//        } catch (Exception e) {
//            System.out.println("失败");
//            //stringRedisTemplate.discard();
//            return "失败";
//        }

        //开启事务（列入队伍）



        List<Object> execute = stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                operations.watch(kcKey);
                operations.multi();
                operations.opsForValue().decrement(kcKey);
                operations.opsForSet().add(userKey, uid);
                return stringRedisTemplate.exec();
            }
        });

        if (execute == null) {
            System.out.println("秒杀失败了");
            return "秒杀失败了";
        } else {
            Long execCount = (Long) execute.get(0);

            if (execCount == 0) {
                System.out.println("秒杀失败了");
                return "秒杀失败了";
            }
        }


//        if (execute == null || execCount==0) {
//            System.out.println("秒杀失败了");
//            return "秒杀失败了";
//        }

//        Integer kcCount = Integer.parseInt(stringRedisTemplate.opsForValue().get(kcKey));
//        if (kcCount == null || kcCount <= 0) {
//
//        }

//        SessionCallback<List> sessionCallback = new SessionCallback<List>() {
//            @Override
//            public <K, V> List execute(RedisOperations<K, V> operations) throws DataAccessException {
//                stringRedisTemplate.multi();
//                //6.1 库存-1
//                redisService.decr(kcKey);
//                //6.2 成功秒杀用户列表+1
//                redisService.sadd(userKey, uid);
//                //执行事务
//                return stringRedisTemplate.exec();
//            }
//        };

//        Object result = sessionCallback;
//
//        if (result == null) {
//            System.out.println("秒杀失败了");
//            return "秒杀失败了";
//        }


//        stringRedisTemplate.multi();
//        redisService.decr(kcKey);
//        redisService.sadd(userKey, uid);
//
//        List<Object> exec = stringRedisTemplate.exec();
//
//        if (exec == null || exec.size() <= 0) {
//            System.out.println("秒杀失败了");
//            return "秒杀失败了";
//        }

//        System.out.println("秒杀成功");
        return "秒杀成功";
    }

    public String getUid() {
        Random random = new Random();
        String uid = "";
        for (int i = 0; i < 5; i++) {
            int rand = random.nextInt(10);
            uid += rand;
        }
        return uid;
    }

    @GetMapping("/testv1")
    public void abc() {
        List<String> keys = Arrays.asList("aaa");
        // 10秒内小于或等于3次时返回1，否则返回0
        for (int i = 0; i < 4; i++) {
            Object execute = stringRedisTemplate.execute(redisScript, keys, String.valueOf(10), String.valueOf(3));
            System.out.println(execute);
        }
    }

    @Test
    public void sd () throws NullPointerException {
//
//        String s = stringRedisTemplate.opsForValue().get("sk:1010:qt");
//        System.out.println(s);


//        /**
//         * List设置lua的KEYS
//         */
//        List<String> keyList = new ArrayList();
//        keyList.add("count");
//        keyList.add("rate.limiting:127.0.0.1");
//
///**
// * 用Mpa设置Lua的ARGV[1]
// */
//        Map<String, Object> argvMap = new HashMap<String, Object>();
//        argvMap.put("expire", 10000);
//        argvMap.put("times", 10);
//
///**
// * 调用脚本并执行
// */
//        Long result = stringRedisTemplate.execute(redisScript, keyList, argvMap);
//        if (result == null) {
//            System.out.println("失败了");
//        }
//        System.out.println(result);
    }
}
