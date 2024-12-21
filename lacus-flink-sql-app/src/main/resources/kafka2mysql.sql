CREATE TABLE input_table
(
    user_id STRING,
    item_id STRING,
    behavior STRING
)
WITH ( 'connector' = 'kafka',
    'topic' = 'source',
    'properties.bootstrap.servers' = '10.10.80.70:6667,10.10.80.71:6667,10.10.80.72:6667',
    'scan.startup.mode' = 'earliest-offset',
    'properties.group.id' = 'testGroup002',
    'format' = 'json');

CREATE TABLE output_table
(
    `user_id` STRING,
    `item_id` STRING,
    `behavior` STRING
)
WITH ( 'connector' = 'jdbc',
    'url' = 'jdbc:mysql://localhost:3306/demo',
    'username' = 'shengyu',
    'password' = '123456',
    'table-name' = 'sink_table');

INSERT INTO output_table
SELECT user_id, item_id, behavior
FROM input_table
WHERE behavior IS NOT NULL;