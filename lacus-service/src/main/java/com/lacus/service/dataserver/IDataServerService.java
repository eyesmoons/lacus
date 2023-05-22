package com.lacus.service.dataserver;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataserver.entity.DataServerEntity;


public interface IDataServerService extends IService<DataServerEntity> {

    boolean checkUrlUnique(String apiUrl);


    void add(DataServerEntity dataServerEntity);

}
