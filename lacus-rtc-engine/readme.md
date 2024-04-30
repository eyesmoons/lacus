## 实时采集引擎二次开发流程
在 *com.lacus.reader* 和 *com.lacus.writer* 包下编写自己的 reader 和 writer，需要注意两点：

① 继承 *BaseReader* 和 *BaseWriter* 类

② 在构造函数中传入name