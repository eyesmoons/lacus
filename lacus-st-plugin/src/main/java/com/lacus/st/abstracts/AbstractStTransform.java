package com.lacus.st.abstracts;

import com.lacus.st.interfaces.StTransformInterface;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * ST数据转换组件抽象基类
 * 提供数据转换组件的通用功能实现
 * 
 * @author lacus
 */
@Slf4j
public abstract class AbstractStTransform extends AbstractStComponent implements StTransformInterface {
}
