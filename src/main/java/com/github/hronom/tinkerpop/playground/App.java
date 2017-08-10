package com.github.hronom.tinkerpop.playground;

import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph;
import com.steelbridgelabs.oss.neo4j.structure.providers.Neo4JNativeElementIdProvider;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import static org.apache.tinkerpop.gremlin.process.traversal.P.inside;

public class App {
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
                graph.vertices().forEachRemaining(Element::remove);
                graph.edges().forEachRemaining(Element::remove);
                // use Graph API to create, update and delete Vertices and Edges
                // enable profiler
                Vertex marko = graph.addVertex(T.label, "person", "name", "marko", "age", 29L);
                Vertex vadas = graph.addVertex(T.label, "person", "name", "vadas", "age", 27L);
                Vertex lop = graph.addVertex(T.label, "software", "name", "lop", "lang", "java");
                Vertex josh = graph.addVertex(T.label, "person", "name", "josh", "age", 32L);
                Vertex ripple = graph.addVertex(T.label, "software", "name", "ripple", "lang", "java");
                Vertex peter = graph.addVertex(T.label, "person", "name", "peter", "age", 35L);
                marko.addEdge("knows", vadas, "weight", 0.5D);
                marko.addEdge("knows", josh, "weight", 1.0D);
                marko.addEdge("created", lop, "weight", 0.4D);
                josh.addEdge("created", ripple, "weight", 1.0D);
                josh.addEdge("created", lop, "weight", 0.4D);
                peter.addEdge("created", lop, "weight", 0.2D);

                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check traversing
        try (Graph graph = new Neo4JGraph(driver, vertexIdProvider, edgeIdProvider)) {
            // begin transaction
            try (Transaction transaction = graph.tx()) {
                System.out.println("Traverse by name");
                GraphTraversal<Vertex, Vertex> traversal1 =
                    graph
                        .traversal()
                        .V()
                        .has("name", "marko");
                // commit transaction
                while (traversal1.hasNext()) {
                    Vertex vertex = traversal1.next();
                    vertex.properties()
                        .forEachRemaining(vp -> System.out.println(vp.label() + ": " + vp.value()));
                    System.out.println();
                }

                System.out.println("Traverse by age");
                GraphTraversal<Vertex, Vertex> traversal2 =
                    graph
                        .traversal()
                        .V()
                        .has("age", inside(10L, 30L));
                // commit transaction
                while (traversal2.hasNext()) {
                    Vertex vertex = traversal2.next();
                    vertex.properties()
                        .forEachRemaining(vp -> System.out.println(vp.label() + ": " + vp.value()));
                    System.out.println();
                }
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
