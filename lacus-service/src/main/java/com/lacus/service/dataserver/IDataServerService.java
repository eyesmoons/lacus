package com.lacus.service.dataserver;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataserver.entity.DataServerEntity;

/**
 * Created by:
 *
 * @Author: lit
 * @Date: 2023/04/27/11:23
 * @Description:
 */
public interface IDataServerService extends IService<DataServerEntity> {

    boolean checkUrlUnique(String apiUrl);


    void add(DataServerEntity dataServerEntity);

}
