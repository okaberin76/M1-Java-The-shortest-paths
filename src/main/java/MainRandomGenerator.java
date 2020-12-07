import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MainRandomGenerator {
    private final Graph graph;
    private final int random = 100;
    private static int nbFichier = 1;

    /**
     * Génère le Random Graph selon le nombre de nœuds et le degré moyen donnés en argument
     * @param nbNode, nombre de nœuds du graphe
     * @param degree, degré moyen des nœuds du graphe
     */
    public MainRandomGenerator(int nbNode, int degree) {
        this.graph = new SingleGraph("RandomGenerator");
        this.graph.setAttribute("ui.stylesheet", "url('./src/main/resources/style.css')");

        Generator gen = new RandomGenerator(degree);
        gen.addSink(this.graph);
        gen.begin();
        /* i = 1 et nbNode - 1, sinon je me retrouvais avec deux nodes en plus, sans savoir pourquoi ? */
        for(int i = 1; i < nbNode-1; i++)
            gen.nextEvents();
        gen.end();

        /* Ajoute un poids random à chaque arête du graphe random généré */
        this.graph.edges().forEach(e -> {
            int r = (int) (Math.random() * random) + 1;
            e.setAttribute("length", r);
            e.setAttribute("ui.label", e.getAttribute("length"));
        });

        /* Ajoute le nom des nœuds du graphe random généré */
        for(Node v: this.graph)
            v.setAttribute("ui.label", v.getId());
    }

    /**
     * Permet à notre objet d'accéder au graphe
     * @return le graphe
     */
    public Graph getGraph() {
        return this.graph;
    }

    /**
     * Version naïve de Dijkstra selon l'algorithme vu en cours
     * @param source, nœud source du graphe
     */
    public void DijkstraNaif(Node source) {
        /* Liste de priorité des nœuds, chaque nœud du graphe sera associé à une valeur définissant sa priorité */
        Map<Node, Integer> map = new HashMap<>();

        /* Debut de l'algorithme. Tous les nœuds possèdent une priorité négative et une distance égale à infini car
            les nœuds n'ont pas encore été parcouru (distance encore inconnue) */
        for (Node node : this.graph)
            node.setAttribute("result", "Infinity");

        /* nœud source qui est à une distance 0 */
        source.setAttribute("result", 0);

        /* On insère source dans la map */
        map.put(source, (Integer) source.getAttribute("result"));

        while (!map.isEmpty()) {
            Node u = null;

            /* Début de ExtractMin() qui va trouver la plus petite valeur de priorité stockée dans la map */
            for (Map.Entry<Node, Integer> entry : map.entrySet())
                u = entry.getKey();
            /* On retire le nœud de priorité minimal de la map */
            map.remove(u);
            /* Fin de ExtractMin() */

            /* Pour chaque nœud voisin v du nœud u, on va regarder si dist(source, v) > dist(source, u) + dist(u, v) */
            for (Edge v : u) {
                int x = (int) u.getAttribute("result");
                int y = (int) v.getAttribute("length");

                /* Si v n'a jamais été visité, alors sa distance est égale à infini, donc v = dist(source, u) + dist(u, v) */
                if (v.getOpposite(u).getAttribute("result").equals("Infinity")) {
                    v.getOpposite(u).setAttribute("result", (x + y));
                    v.getOpposite(u).setAttribute("parent", u);
                    /* Ajout de v et de sa priorité dans la map */
                    map.put(v.getOpposite(u), (Integer) v.getOpposite(u).getAttribute("result"));
                    /* Colorie en rouge les arêtes des plus courts chemins du graphe */
                    /*
                    v.setAttribute("ui.style", "fill-color: red;");
                     */

                /* Sinon, v possède déjà une distance, donc on la compare avec dist(source, u) + dist(u, v) */
                } else {
                    int z = (int) v.getOpposite(u).getAttribute("result");
                    if (z > x + y) {
                        v.getOpposite(u).setAttribute("result", (x + y));
                        v.getOpposite(u).setAttribute("parent", u);
                        map.put(v.getOpposite(u), (Integer) v.getOpposite(u).getAttribute("result"));
                        /* Colorie en rouge les arêtes des plus courts chemins du graphe */
                        /*
                        v.setAttribute("ui.style", "fill-color: red;");
                         */
                    }
                }
            }
        }
        /* Affiche le résultat du calcul des plus courts chemins de la source jusqu'à chaque nœud du graphe */
        /*
        System.out.println("\nDijkstra naif:");
        for (Node node : graph)
            System.out.printf("%s -> %s: %s%n", source.getId(), node.getId(), node.getAttribute("result"));
         */

        /* Affiche le nœud source en rouge */
        /*
        source.setAttribute("ui.style", "fill-color: red;");
         */
    }

    /**
     * Version optimisée de Dijkstra disponible dans la doc de GraphStream, avec quelques fonctionnalités visuelles
     * supplémentaires
     * @param source, nœud source du graphe
     */
    public void Dijkstra(Node source) {
        this.graph.setAttribute("ui.stylesheet", "url('./src/main/resources/style.css')");
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, "result", "length");

        /* Calcule les plus courts chemins de la source jusqu'à chaque nœud du graphe */
        dijkstra.init(this.graph);
        dijkstra.setSource(this.graph.getNode(source.getId()));
        dijkstra.compute();

        /* Affiche le résultat du calcul des plus courts chemins de la source jusqu'à chaque nœud du graphe */
        /*
        System.out.println("\nDijkstra optimisé:");
        for (Node node : this.graph)
            System.out.printf("%s -> %s: %s%n", dijkstra.getSource(), node, dijkstra.getPathLength(node));
         */

        /* Colorie en rouge les arêtes des plus courts chemins du graphe */
        /*
        for (Edge edge : dijkstra.getTreeEdges())
            edge.setAttribute("ui.style", "fill-color: red;");
         */

        /* Affiche le nœud source en rouge */
        /*
        source.setAttribute("ui.style", "fill-color: red;");
         */
    }

    /**
     * Calcule le temps d'exécution des deux algorithmes de Dijkstra
     * @param nbNode, nombre de nœuds du graphe
     * @param degree, degré moyen des nœuds du graphe
     * @return le temps d'exécution des algorithmes de Dijkstra
     */
    public static String calculTempsExecution(int nbNode, int degree) {
        MainRandomGenerator rg = new MainRandomGenerator(nbNode, degree);

        long depart1 = System.currentTimeMillis();
        rg.Dijkstra(rg.getGraph().getNode(0));
        long arrive1 = System.currentTimeMillis();
        long resultat1 = arrive1 - depart1;

        long depart2 = System.currentTimeMillis();
        rg.DijkstraNaif(rg.getGraph().getNode(0));
        long arrive2 = System.currentTimeMillis();
        long resultat2 = arrive2 - depart2;

        return resultat1 + " " + resultat2;
    }

    /**
     * Créer un fichier contenant les résultats de la comparaison entre les deux algorithmes
     * @param nbNode, nombre de nœuds du graphe
     * @param degree, degré moyen des nœuds du graphe
     * @param n, nombre de fois que l'on va tester le temps d'exécution des algorithmes
     */
    public static void generateFile(int nbNode, int degree, int n) {
        try {
            File file = new File("Results/fichierResultat" + nbFichier + "-" + nbNode + "-" + degree + ".dat");
            FileWriter writer = new FileWriter(file);

            for (int i = 1; i <= n; i++) {
                String stringBuilder = String.format("%s", calculTempsExecution(nbNode, degree)) + "\n";
                writer.write(stringBuilder.replace(",", "."));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lis un fichier.dat où sont stockés x valeurs de temps d'exécution d'algorithmes de Dijsktra, puis calcule la
     * valeur moyenne de ces temps d'exécution
     * @param n, nombre de fois que l'on va tester le temps d'exécution des algorithmes
     * @param dijkstra, false = Dijkstra naif et true = Dijsktra optimisé
     * @return le temps d'exécution moyen de ces algorithmes
     */
    public static double tempsExecMoyen(int nbNode, int degree, int n, boolean dijkstra) {
        BufferedReader br = null;
        String sCurrentLine;
        String[] tab;
        double resDijkstraOpti = 0;
        double resDijkstraNaif = 0;

        try {
            br = new BufferedReader(new FileReader("Results/fichierResultat" + nbFichier + "-" + nbNode + "-" + degree + ".dat"));
            while ((sCurrentLine = br.readLine()) != null) {
                tab = sCurrentLine.split(" ");
                resDijkstraOpti += Integer.parseInt(tab[0]);
                resDijkstraNaif += Integer.parseInt(tab[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (dijkstra)
            return resDijkstraOpti / n;
        return resDijkstraNaif / n;
    }

    public static void main(String[] args) {
        // System.setProperty("org.graphstream.ui", "swing");

        /* Créer un graphe random avec 10 nœuds et un degré moyen de 2 */
        // MainRandomGenerator rg = new MainRandomGenerator(10, 2);

        /* Test de Dijkstra naif */
        // rg.DijkstraNaif(rg.getGraph().getNode(0));

        /* Test de Dijkstra optimisé */
        //rg.Dijkstra(rg.getGraph().getNode(0));

        //rg.getGraph().display();

        /* Créer un graphe random avec 20 nœuds et un degré moyen de 5 */
        /*
        MainRandomGenerator rg2 = new MainRandomGenerator(10, 5);
        rg2.getGraph().display();
         */

        /* Nombre de nœuds du graphe */
        int nbNode = 1000;
        /* Degré moyen des nœuds du graphe */
        int degree = 3;
        /* Nombre de fois que l'on va tester le temps d'exécution des algorithmes */
        int n = 10;
        /* Créer le fichier contenant les résultats */
        generateFile(nbNode, degree, n);
        System.out.println("\nTemps d'exécution moyen des algorithmes sur un intervalle de " + n + " pour " + nbNode + " nœuds et un degré moyen de " + degree + ":");
        System.out.println("Dijkstra optimisé: " + tempsExecMoyen(nbNode, degree, n, true) + " ms");
        System.out.println("Dijkstra naif: " + tempsExecMoyen(nbNode, degree, n, false) + " ms");
        nbFichier ++;

        for(int i = 10000; i <= 100000; i += 10000) {
            generateFile(i, degree, n);
            System.out.println("\nTemps d'exécution moyen des algorithmes sur un intervalle de " + n + " pour " + i + " nœuds et un degré moyen de " + degree + ":");
            System.out.println("Dijkstra optimisé: " + tempsExecMoyen(i, degree, n, true) + " ms");
            System.out.println("Dijkstra naif: " + tempsExecMoyen(i, degree, n, false) + " ms");
            nbFichier ++;
        }
    }
}
