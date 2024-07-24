package com.zkd.demo.datasource.core.exter;

import com.zkd.demo.basic.core.exceptions.CommonException;
import com.zkd.demo.datasource.entity.DTO.MinioSourceDTO;
import com.zkd.demo.datasource.entity.enums.DataSourceEnum;
import io.minio.MinioClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MinioExterSourceService implements ExterSourceService {
    @Override
    public Boolean connTest(Map<String, String> dataMap) {
        MinioSourceDTO sourceDTO = (MinioSourceDTO) DataSourceEnum.MINIO.getSourceDTO(dataMap, null, null);
        try {
            MinioClient minioClient =
                    minioClient = new MinioClient(
                            sourceDTO.getEndpoint(),
                            sourceDTO.getAccessKey(),
                            sourceDTO.getSecretKey());

            //用来判定是否能连接
            minioClient.bucketExists(sourceDTO.getBucketName());
            return true;
        } catch (Exception e) {
            throw new CommonException(500, e.getMessage());
        }
    }

    @Override
    public Integer getType() {
        return DataSourceEnum.MINIO.getVal();
    }

    @Override
    public Object getClient(Map<String, String> dataMap) {
        MinioSourceDTO sourceDTO = (MinioSourceDTO) DataSourceEnum.MINIO.getSourceDTO(dataMap, null, null);
        try {
            return new MinioClient(
                    sourceDTO.getEndpoint(),
                    sourceDTO.getAccessKey(),
                    sourceDTO.getSecretKey());
        } catch (Exception e) {
            throw new CommonException(500, e.getMessage());
        }
    }
}
