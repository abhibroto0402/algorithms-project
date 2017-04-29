import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.generator.BananaTreeGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.Random;

public class bananaTest {
    public static void main(String[] args) {
        final int EVENT_MAX   = 1000;

        SingleGraph graph = new SingleGraph("banana test");
        BananaTreeGenerator gen = new BananaTreeGenerator();

        gen.addSink(graph);

        gen.begin();
        graph.display();

        for (int i = 0; i<EVENT_MAX; i++) {
            gen.nextEvents();
        }

        gen.end();

        //graph.display();

        for (Node node : graph) {
            System.out.println(node.getId());
            node.addAttribute("ui.label", node.getId());
        }

        Random rn = new Random();


        for(Edge e:graph.getEachEdge()) {
            e.addAttribute("weight", rn.nextInt(10) + 1);
            System.out.println(e.getId());
            e.addAttribute("ui.label", e.getAttribute("weight"));
        }


        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "weight");

        // Compute the shortest paths in g from A to all nodes
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode("S43_03"));
        dijkstra.compute();



        for (Node node : graph)
            System.out.printf("%s->%s:%10.2f%n", dijkstra.getSource(), node,
                    dijkstra.getPathLength(node));

        // Color in blue all the nodes on the shortest path form A to B
        for (Node node : dijkstra.getPathNodes(graph.getNode("S29_02")))
            node.addAttribute("ui.style", "fill-color: blue;");

    }
}