package com.example.redistest;

import com.example.redistest.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/phone")
public class PhoneNumberController {
    @Autowired
    private RedisService redisService;

    //1.生成验证码
    @GetMapping("/getcode")
    public String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            int rand = random.nextInt(10);
            code += rand;
        }
        return code;
    }
    //2.每个手机每天只能发送三次，验证码放到redis中，设置过期时间120
    @GetMapping("sendCode")
    public String senCode(String phone) {
        //拼接key
        //发送次数key
        String countkey = "user:"+phone+":count";
        //手机验证码key
        String codekey = "user:"+phone+":code";

        //判断次数key
        String count = redisService.get(countkey);
        if (count == null) {
            //没有发送次数，第一次发送
            redisService.set(countkey, "1");
            redisService.expire(countkey,24*60*60);
        } else if (Integer.parseInt(count) <= 2) {
            //发送次数+1
            redisService.increment(countkey, 1);
        } else if (Integer.parseInt(count) > 2) {
            System.out.println("今天发送次数已经超过三次");
            return "今天发送次数已经超过三次";
        }

        //发送验证码到redis中
        redisService.set(codekey, getCode());
        //设置过期时间120秒
        redisService.expire(codekey, 120);
        return "发送成功";
    }
    //3.校验验证码
    @GetMapping("verifyCode")
    public String verifyCode(String code, String phone) {
        String countkey = "user:"+phone+":code";
        String redisCode = redisService.get(countkey);
        if (redisCode == null) {
            System.out.println("111");
            System.out.println("2222");
            System.out.println("33333");
            System.out.println("44444");
            System.out.println("555555");
            return "验证码已过期，请重新获取验证码";
        }
        if (redisCode.equals(code)) {
            return "通过";
        } else {
            return "失败";
        }
    }
}
