package com.github.jmchilton.blend4j.galaxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;
import org.glassfish.jersey.client.ClientResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolDataClientImpl extends Client implements ToolDataClient {

    ToolDataClientImpl(GalaxyInstanceImpl galaxyInstance) {
        super(galaxyInstance, "tool_data");
    }

    public ClientResponse showDataTableRequest(String dataTableId) {
        return super.show(dataTableId, ClientResponse.class);
    }

    public ClientResponse deleteDataTableRequest(final String dataTableId, final List<String> values) {
        Map requestEntity = new HashMap();
        requestEntity.put("values", String.join("\t", values));
        return deleteResponse(getWebResource(dataTableId), requestEntity);
    }

    public List<TabularToolDataTable> getDataTables() {
        return get(new TypeReference<List<TabularToolDataTable>>() {});
    }

    public ClientResponse reloadDataTableRequest(final String dataTableId) {
        return getResponse(getWebResource(dataTableId).path("reload"));
    }

    public TabularToolDataTable showDataTable(final String dataTableId) {
        return super.show(dataTableId, TabularToolDataTable.class);
    }
}
