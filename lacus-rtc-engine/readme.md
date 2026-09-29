## 实时采集引擎二次开发流程
在 *com.lacus.source* 和 *com.lacus.sink* 包下编写自己的 source 和 sink，需要注意两点：

① 继承 *BaseSource* 和 *BaseSink* 类

② 在构造函数中传入name
