import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class MainRandomGenerator {
    private final Graph graph;

    /** Génère le Random Graph selon le nombre de noeuds et le degré moyen donnés */
    public MainRandomGenerator(int nbNode, int degree) {
        this.graph = new SingleGraph("RandomGenerator");
        this.graph.setAttribute("ui.stylesheet", "url('./src/main/resources/style.css')");

        Generator gen = new RandomGenerator(degree);
        gen.addSink(this.graph);
        gen.begin();
        // i = 1 et nbNode - 1 sinon je me retrouvais avec deux nodes en plus, sans savoir pourquoi ?
        for(int i = 1; i < nbNode-1; i++)
            gen.nextEvents();
        gen.end();

        /* Ajoute un poids random à chaque arêtes du graphe random généré */
        this.graph.edges().forEach(e -> {
            int random = (int) (Math.random() * 10);
            e.setAttribute("length", random);
            e.setAttribute("ui.label", e.getAttribute("length"));
        });

        /* Ajoute le nom des noeuds du graphe random généré */
        for(Node v: this.graph)
            v.setAttribute("ui.label", v.getId());
    }

    public Graph getGraph() {
        return this.graph;
    }

    public void display() {
        this.graph.display();
    }

    /** Version optimisée de Dijkstra disponible dans la doc de GraphStream, avec quelques fonctionnalités supplémentaires de disponible */
    public static void Dijkstra(Graph graph, Node source, Node sink) {
        graph.setAttribute("ui.stylesheet", "url('./src/main/resources/style.css')");
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "result",
                "length");

        /* Calcule les plus courts chemins du graphe de la source jusqu'à chaque noeud du graphe */
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode(source.getId()));
        dijkstra.compute();
        for (Node node : graph)
            System.out.printf("%s -> %s:%6.2f%n", dijkstra.getSource(), node, dijkstra.getPathLength(node));

        /* Colorie en jaune les noeuds du plus court chemin de la source au noeud donné */
        /* for (Node node : dijkstra.getPathNodes(graph.getNode(sink.getId())))
            node.setAttribute("ui.style", "fill-color: yellow;"); */

        /* Colorie en rouge les arêtes des plus courts chemins du graphe */
        /* for (Edge edge : dijkstra.getTreeEdges())
            edge.setAttribute("ui.style", "fill-color: red;"); */

        /* Affiche le plus court chemin de la source au noeud donné */
        /* System.out.println("Plus court chemin de " + source.getId() + " à " + sink.getId() + ": " + dijkstra.getPath(graph.getNode(sink.getId()))); */

        source.setAttribute("ui.style", "fill-color: red;");
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        MainRandomGenerator rg = new MainRandomGenerator(10, 2);
        long depart = System.currentTimeMillis();
        Dijkstra(rg.getGraph(), rg.getGraph().getNode(0), rg.getGraph().getNode(5));
        long arrive = System.currentTimeMillis();
        System.out.println("\nTemps d'exécution: " + (arrive - depart) + " ms.");
        rg.display();
    }
}
