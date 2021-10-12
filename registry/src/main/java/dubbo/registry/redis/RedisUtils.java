package dubbo.registry.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
public class RedisUtils {
    private static int MAX_ACTIVE = 1024;
    private static int MAX_IDLE = 200;
    private static int MAX_WAIT = 10000;
    private static int TIMEOUT = 10000;
    private static boolean TEST_ON_BORROW = true;
    private static JedisPool jedisPool = null;

    public static void init(String address) {
        if (jedisPool == null) {
            synchronized (RedisUtils.class) {
                if (jedisPool == null) {
                    try {
                        String[] addr = address.split(":");
                        String host = addr[0];
                        Integer ip = Integer.parseInt(addr[1]);
                        JedisPoolConfig config = new JedisPoolConfig();
                        config.setMaxTotal(MAX_ACTIVE);
                        config.setMaxIdle(MAX_IDLE);
                        config.setMaxWaitMillis(MAX_WAIT);
                        config.setTestOnBorrow(TEST_ON_BORROW);
                        jedisPool = new JedisPool(config, host, ip, TIMEOUT);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                return jedisPool.getResource();
            } else {
                throw new RuntimeException("Jedis pool is null");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }
}
