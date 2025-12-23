package com.liren.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //************************ 操作key ***************************

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置key的过期时间（单位默认为秒）
     */
    public boolean expire(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置key的过期时间（自定义时间单位）
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 删除单个对象
     */
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    //************************ 操作String类型 ***************************

    /**
     * 存储基本的对象，Integer、String、实体类等
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 存储基本的对象，Integer、String、实体类等（带过期时间）
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取对象，直接返回 Object
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取指定类型的基本对象
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null) {
            return null;
        }

        // 需要确保类型一致，否则抛出异常
        if(clazz.isInstance(value)) {
            return (T) value;
        }

        throw new IllegalStateException(
                "Redis value 类型不匹配. key=" + key +
                        ", 期望类型=" + clazz.getName() +
                        ", 实际类型=" + value.getClass().getName()
        );
    }

    //************************ 操作list类型 ************************

    /**
     * 获取list中存储数据数量
     */


    /**
     * 获取list中指定范围数据
     */


    /**
     * 底层使用list结构，存储数据（尾插、批量插入）
     */


    /**
     * 底层使用list结构，存储数据（头插）
     */


    /**
     * 底层使用list结构，删除指定数据
     */

    //************************ 操作hash类型 ************************

    /**
     * 获取单个hash中数据
     */


    /**
     * 获取多个hash中数据
     */


    /**
     * 往hash中存储数据
     */


    /**
     * 缓存map
     */
}
