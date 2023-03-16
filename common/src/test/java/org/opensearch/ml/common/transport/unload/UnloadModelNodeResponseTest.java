package org.opensearch.ml.common.transport.unload;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensearch.Version;
import org.opensearch.cluster.node.DiscoveryNode;
import org.opensearch.common.io.stream.BytesStreamOutput;
import org.opensearch.common.transport.TransportAddress;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.opensearch.cluster.node.DiscoveryNodeRole.CLUSTER_MANAGER_ROLE;

@RunWith(MockitoJUnitRunner.class)
public class UnloadModelNodeResponseTest {

    @Mock
    private DiscoveryNode localNode;

    private Map<String, Integer> modelWorkerNodeCounts;

    @Before
    public void setUp() throws Exception {
        localNode = new DiscoveryNode(
                "foo0",
                "foo0",
                new TransportAddress(InetAddress.getLoopbackAddress(), 9300),
                Collections.emptyMap(),
                Collections.singleton(CLUSTER_MANAGER_ROLE),
                Version.CURRENT
        );
        modelWorkerNodeCounts = new HashMap<>();
        modelWorkerNodeCounts.put("modelId1", 1);
    }

    @Test
    public void testSerializationDeserialization() throws IOException {
        Map<String, String> modelToLoadStatus = new HashMap<>();
        modelToLoadStatus.put("modelId1", "response");
        UnloadModelNodeResponse response = new UnloadModelNodeResponse(localNode, modelToLoadStatus, modelWorkerNodeCounts);
        BytesStreamOutput output = new BytesStreamOutput();
        response.writeTo(output);
        UnloadModelNodeResponse newResponse = new UnloadModelNodeResponse(output.bytes().streamInput());
        assertEquals(newResponse.getNode().getId(), response.getNode().getId());
    }

    @Test
    public void testSerializationDeserialization_NullModelLoadStatus() throws IOException {
        UnloadModelNodeResponse response = new UnloadModelNodeResponse(localNode, null, null);
        BytesStreamOutput output = new BytesStreamOutput();
        response.writeTo(output);
        UnloadModelNodeResponse newResponse = new UnloadModelNodeResponse(output.bytes().streamInput());
        assertEquals(newResponse.getNode().getId(), response.getNode().getId());
    }

    @Test
    public void testReadProfile() throws IOException {
        UnloadModelNodeResponse response = new UnloadModelNodeResponse(localNode, new HashMap<>(), new HashMap<>());
        BytesStreamOutput output = new BytesStreamOutput();
        response.writeTo(output);
        UnloadModelNodeResponse newResponse = UnloadModelNodeResponse.readStats(output.bytes().streamInput());
        assertNotEquals(newResponse, response);
    }
}
