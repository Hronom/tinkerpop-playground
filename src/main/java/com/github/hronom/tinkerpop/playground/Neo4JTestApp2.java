package com.github.hronom.tinkerpop.playground;

import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph;
import com.steelbridgelabs.oss.neo4j.structure.providers.Neo4JNativeElementIdProvider;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

public class Neo4JTestApp2 {
    public static void main(String[] args) {
        // create driver instance
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1234567890"));

        // create id provider
        Neo4JElementIdProvider<?> vertexIdProvider = new Neo4JNativeElementIdProvider();
        Neo4JElementIdProvider<?> edgeIdProvider = new Neo4JNativeElementIdProvider();

        // create graph instance
        try (Graph graph = new Neo4JGraph(driver, vertexIdProvider, edgeIdProvider)) {
            // begin transaction
            try (Transaction transaction = graph.tx()) {
                // Clean all
                graph.vertices().forEachRemaining(Element::remove);
                graph.edges().forEachRemaining(Element::remove);

                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
