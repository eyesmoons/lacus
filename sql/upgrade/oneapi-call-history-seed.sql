-- OneAPI 调用历史模拟数据（用于本地/测试环境）
-- 从 one_api_info 取真实 api_url，覆盖近 7 天，成功/失败混合，延迟有差异
-- 注意：call_status 存的是枚举 name，为大写 SUCCESS / FAIL
-- 使用方式：按需调整每条接口每天生成的行数 @per_day

SET @per_day = 8; -- 每条接口每天生成多少条记录（含成功+失败）

INSERT INTO one_api_call_history
    (call_date, call_ip, api_url, call_status, call_code, error_info, call_delay, call_time, deleted, creator_id, create_time)
SELECT
    DATE_FORMAT(d.dt, '%Y-%m-%d')                              AS call_date,
    ELT(1 + FLOOR(RAND() * 4), '192.168.1.10', '192.168.1.22',
        '10.0.0.5', '172.16.0.8')                              AS call_ip,
    i.api_url                                                  AS api_url,
    CASE WHEN RAND() < 0.15 THEN 'FAIL' ELSE 'SUCCESS' END    AS call_status,
    CASE WHEN RAND() < 0.15 THEN 500 ELSE 0 END               AS call_code,
    CASE WHEN RAND() < 0.15
         THEN ELT(1 + FLOOR(RAND() * 3),
                  'conn timeout', 'datasource unavailable', 'query exceeded timeout')
         ELSE '' END                                           AS error_info,
    CASE WHEN RAND() < 0.15
         THEN 2000 + FLOOR(RAND() * 6000)                      -- 失败：2~8s
         ELSE 50 + FLOOR(RAND() * 950) END                     -- 成功：50~1000ms
                                                               AS call_delay,
    d.dt + INTERVAL FLOOR(RAND() * 86400) SECOND              AS call_time,
    0                                                          AS deleted,
    'seed'                                                     AS creator_id,
    NOW()                                                      AS create_time
FROM one_api_info i
JOIN (
    -- 近 7 天，每天一条日期锚点
    SELECT CURDATE() - INTERVAL (t.n) DAY AS dt
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3
          UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) t
) d
JOIN (
    -- 1..@per_day 的序号，展开为每天多条
    SELECT a.N + b.N * 10 + 1 AS n
    FROM (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
          UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a
    ,(SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
      UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b
) seq ON seq.n <= @per_day
WHERE i.deleted = 0
ORDER BY RAND();
