package com.zkd.demo.datasource.entity.DTO;

import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Connection;
import java.util.List;


@Data
@Accessors(chain = true)
public class ConnDTO {
    private IClient client;

    private ISourceDTO sourceDTO;

    public Connection getConnection() {
        return client.getCon(sourceDTO);
    }

    public Boolean testConn() {
        return client.testCon(sourceDTO);
    }

    public String getTableMetaComment(SqlQueryDTO sqlQueryDTO) {
        return client.getTableMetaComment(sourceDTO, sqlQueryDTO);
    }

    public List<ColumnMetaDTO> getColumnMetaData(SqlQueryDTO sqlQueryDTO) {
        return client.getColumnMetaData(sourceDTO, sqlQueryDTO);
    }
}
