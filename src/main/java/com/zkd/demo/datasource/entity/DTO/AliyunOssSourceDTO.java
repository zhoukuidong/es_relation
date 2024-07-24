package com.zkd.demo.datasource.entity.DTO;

import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliyunOssSourceDTO implements ISourceDTO {

    private String endpoint;

    private String accessKeyId;

    private String secretAccessKey;

    private String bucketName;


    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Integer getSourceType() {
        return 200;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void setConnection(Connection connection) {

    }


}
