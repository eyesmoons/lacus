## 实时采集引擎二次开发流程
在 *com.lacus.processors* 包下编写自己的 processor，需要注意两点：

① 继承 *AbsFlinkProcessor* 类

② 在构造函数中传入processorName