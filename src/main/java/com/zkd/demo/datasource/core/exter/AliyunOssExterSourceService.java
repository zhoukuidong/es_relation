package com.zkd.demo.datasource.core.exter;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.BucketInfo;
import com.zkd.demo.basic.core.exceptions.CommonException;
import com.zkd.demo.datasource.entity.DTO.AliyunOssSourceDTO;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class AliyunOssExterSourceService implements ExterSourceService {

    @Override
    public Boolean connTest(Map<String, String> dataMap) {
        AliyunOssSourceDTO sourceDTO = (AliyunOssSourceDTO) DataSourceEnum.ALIYUN_OSS.getSourceDTO(dataMap, null, null);
        try {
            ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
            //取消重试 timout为5s
            clientBuilderConfiguration.setMaxErrorRetry(0);
            clientBuilderConfiguration.setSocketTimeout(5 * 1000);
            clientBuilderConfiguration.setRequestTimeout(5 * 1000);
            OSS ossFake = new OSSClientBuilder().build(
                    sourceDTO.getEndpoint(),
                    sourceDTO.getAccessKeyId(),
                    sourceDTO.getSecretAccessKey(), clientBuilderConfiguration);

            //用来判定是否能连接
            BucketInfo bucketInfo = ossFake.getBucketInfo(sourceDTO.getBucketName());
            return true;
        } catch (Exception e) {
            throw new CommonException(500, e.getMessage());
        }
    }

    @Override
    public Integer getType() {
        return DataSourceEnum.ALIYUN_OSS.getVal();
    }

    @Override
    public Object getClient(Map<String, String> dataMap) {
        AliyunOssSourceDTO sourceDTO = (AliyunOssSourceDTO) DataSourceEnum.ALIYUN_OSS.getSourceDTO(dataMap, null, null);
        return new OSSClientBuilder().build(
                sourceDTO.getEndpoint(),
                sourceDTO.getAccessKeyId(),
                sourceDTO.getSecretAccessKey());
    }
}
