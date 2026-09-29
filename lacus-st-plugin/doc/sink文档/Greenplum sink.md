# Greenplum

> Greenplum sink connector

## Description

Write data to Greenplum using [Jdbc connector](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/sink/Jdbc.md).

## Key Features

-  [exactly-once](https://seatunnel.apache.org/zh-CN/docs/2.3.11/concept/connector-v2-features.md)

提示

Not support exactly-once semantics (XA transaction is not yet supported in Greenplum database).

## Options

### driver [string]

Optional jdbc drivers:

- `org.postgresql.Driver`
- `com.pivotal.jdbc.GreenplumDriver`

Warn: for license compliance, if you use `GreenplumDriver` the have to provide Greenplum JDBC driver yourself, e.g. copy greenplum-xxx.jar to $SEATUNNEL_HOME/lib for Standalone.

### url [string]

The URL of the JDBC connection. if you use postgresql driver the value is `jdbc:postgresql://${yous_host}:${yous_port}/${yous_database}`, or you use greenplum driver the value is `jdbc:pivotal:greenplum://${yous_host}:${yous_port};DatabaseName=${yous_database}`

### common options

Sink plugin common parameters, please refer to [Sink Common Options](https://seatunnel.apache.org/zh-CN/docs/2.3.11/connector-v2/sink-common-options.md) for details

