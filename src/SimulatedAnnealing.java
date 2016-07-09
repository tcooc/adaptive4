import java.io.*;
import java.util.*;

public class SimulatedAnnealing {
    private final Random rand = new Random();

    public final String name;
    public final String comment;
    public final String type;
    public final String dimension;

    private int[][] nodes;
    private int[] demands;
    private int dimensionInt;
    private int capacityInt;

    public static void main(String[] args) {
        File[] problems = new File("data/A-VRP").listFiles();
        SimulatedAnnealing[] models = new SimulatedAnnealing[problems.length];
        for (int i = 0; i < problems.length; i++) {
            try {
                models[i] = new SimulatedAnnealing(problems[i]);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        for(SimulatedAnnealing model : models) {
            System.out.println("#### " + model.name + ": ####");
            System.out.println(model.comment);
            System.out.println("# Control scenario i:");
            model.run(1000, 1, 0.85, 100);
        }
        System.out.println("################################################################################");
        for(SimulatedAnnealing model : models) {
            System.out.println("#### " + model.name + ": ####");
            System.out.println(model.comment);
            System.out.println("# Control scenario ii:");
            model.run(600, 100, 0.96, 100);
        }
        System.out.println("################################################################################");
        for(SimulatedAnnealing model : models) {
            System.out.println("#### " + model.name + ": ####");
            System.out.println(model.comment);
            System.out.println("# Parameters used in report by Harmanani et al.:");
            model.run(5000, 0.001, 0.99, 10000);
        }
    }

    private SimulatedAnnealing(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        name = br.readLine().trim();
        comment = br.readLine().trim();
        type = br.readLine().trim();
        assert "TYPE : CVRP".equals(type); // assume CVRP
        dimension = br.readLine().trim();
        dimensionInt = Integer.parseInt(dimension.substring(dimension.lastIndexOf(' ') + 1));
        nodes = new int[dimensionInt][];
        demands = new int[dimensionInt];

        String edgeWeightType = br.readLine().trim();
        assert "EDGE_WEIGHT_TYPE : EUC_2D".equals(edgeWeightType); // assume EUC_2D
        String capacity = br.readLine().trim();
        capacityInt = Integer.parseInt(capacity.substring(capacity.lastIndexOf(' ') + 1));

        br.readLine();

        String line = br.readLine().trim();
        while(!line.startsWith("DEMAND_SECTION")) {
            String[] coor = line.split(" ");
            nodes[Integer.parseInt(coor[0]) - 1] = new int[]{Integer.parseInt(coor[1]), Integer.parseInt(coor[2])};
            line = br.readLine().trim();
        }
        line = br.readLine().trim();
        while(!line.startsWith("DEPOT_SECTION")) {
            String[] coor = line.split(" ");
            demands[Integer.parseInt(coor[0]) - 1] = Integer.parseInt(coor[1]);
            line = br.readLine().trim();
        }
        assert demands[0] == 0; // assume depot is always node "1"
        // assume DEPOT_SECTION always 1
    }

    public void run(double T, double Tfinal, double alpha, int iterationsPerT) {
//        System.out.println("Nodes=" + Arrays.deepToString(nodes));
//        System.out.println("Demands=" + Arrays.toString(demands));

        List<List<Integer>> routes = generateInitialCondition();

//        System.out.println("Routes=" + Arrays.toString(routes.toArray()));
//        System.out.println("Cost=" + calculateRoutesCost(routes));

        // simulated annealing start
        List<List<Integer>> currentRoute = routes;
        List<List<Integer>> bestRoute = currentRoute;
        do {
            int iterations = 0;
            do {
                List<List<Integer>> newRoutes = neighbourhoodTransform(currentRoute);
                double dC = calculateRoutesCost(newRoutes) - calculateRoutesCost(currentRoute);
                if(dC <= 0 || rand.nextDouble() < Math.exp(-dC / T)) {
                    currentRoute = newRoutes;
                    if(calculateRoutesCost(newRoutes) < calculateRoutesCost(bestRoute)) {
                        bestRoute = currentRoute;
                    }
                }
                iterations++;
            } while(iterations < iterationsPerT);
            T *= alpha;
        } while(T > Tfinal);


//        System.out.println("Solution:");
//        System.out.println("Routes=" + Arrays.toString(bestRoute.toArray()));
        System.out.println("Cost=" + calculateRoutesCost(bestRoute));
        validateRoutes(bestRoute);
    }

    private List<List<Integer>> generateInitialCondition() {
        List<List<Integer>> routes = new ArrayList<>();
        boolean[] visited = new boolean[dimensionInt];
        visited[0] = true;
        while(true) {
            // get first unvisited
            int currentNode = -1;
            for(int i = 0; i < dimensionInt; i++) {
                if(!visited[i]) {
                    currentNode = i;
                    break;
                }
            }
            if(currentNode == -1) {
                break;
            }

            List<Integer> route = new ArrayList<>();
            route.add(currentNode);
            visited[currentNode] = true;
            for(int i = currentNode + 1; i < dimensionInt; i++) {
                if(!visited[i]) {
                    if(calculateRouteDemand(route) + demands[i] <= capacityInt) {
                        route.add(i);
                        visited[i] = true;
                    }
                }
            }
            routes.add(route);
        }
        return routes;
    }

    private List<List<Integer>> neighbourhoodTransform(List<List<Integer>> preTransform) throws IllegalStateException {
        // deep copy routes
        List<List<Integer>> routes = new ArrayList<>();
        for(List<Integer> route : preTransform) {
            routes.add(new ArrayList<>(route));
        }
        try {
            if(rand.nextDouble() < 0.8) {
                move(routes);
            }
            replaceHighestAverage(routes);
        } catch(IllegalStateException e) {
            return preTransform;
        }
        return routes;
    }

    private void move(List<List<Integer>> routes) {
        // calculate all distances
        List<Object[]> distances = new ArrayList<>();
        for (List<Integer> route : routes) {
            for (int i = 0; i < route.size(); i++) {
                int previousNode = i > 0 ? route.get(i - 1) : 0;
                distances.add(new Object[]{
                        previousNode,
                        route.get(i),
                        calculateDistance(previousNode, route.get(i))
                });
            }
            distances.add(new Object[]{
                    route.get(route.size() - 1),
                    0,
                    calculateDistance(route.get(route.size() - 1), 0)
            });
        }
        Collections.sort(distances, (o1, o2) -> {
            double d1 = (Double) o1[2];
            double d2 = (Double) o2[2];
            return d1 == d2 ? 0 : (d2 > d1 ? 1 : -1);
        }); // ascending
        // exclude nodes that are the depot or in the 5 shortest distances
        Set<Integer> includeSet = new HashSet<>();
        for(int i = 0; i < dimensionInt; i++) {
            includeSet.add(i);
        }
        includeSet.remove(0);
        for(int i = 0; i < 5; i++) {
            includeSet.remove((Integer)distances.get(i)[0]);
            includeSet.remove((Integer)distances.get(i)[1]);
        }
        // remove random nodes that aren't in the exclude set
        Object[] removedNodes = new Object[5];
        for(int i = 0; i < 5; i++) {
            int randomIndex = rand.nextInt(includeSet.size());
            int randomNode = (Integer)includeSet.toArray()[randomIndex];
            for(List<Integer> route : routes) {
                route.remove((Object)randomNode);
            }
            includeSet.remove(randomNode);
            removedNodes[i] = randomNode;
        }
        // select random route and insert each node into the route, if it satisfies the capacity constraint
        for(Object node : removedNodes) {
            int attempts = 0;
            while(attempts < routes.size() * 1000) {
                List<Integer> randomRoute = routes.get(rand.nextInt(routes.size()));
                if (demands[(Integer)node] + calculateRouteDemand(randomRoute) <= capacityInt) {
                    randomRoute.add((Integer)node);
                    break;
                }
                attempts++;
            }
            if(attempts == routes.size() * 1000) {
                throw new IllegalStateException("failed to add back something");
            }
        }
    }

    private void replaceHighestAverage(List<List<Integer>> routes) {
        // find average distance of all non-depot nodes
        List<Object[]> averageDistance = new ArrayList<>();
        for(List<Integer> route : routes) {
            for(int i = 0; i < route.size(); i++) {
                int previousNode = i > 0 ? route.get(i - 1) : 0;
                int nextNode = i < route.size() - 1 ? route.get(i + 1) : 0;
                averageDistance.add(new Object[]{
                        route.get(i),
                        (calculateDistance(previousNode, route.get(i)) + calculateDistance(nextNode, route.get(i))) / 2
                });
            }
        }
        Collections.sort(averageDistance, (o1, o2) -> {
            double d1 = (Double) o1[1];
            double d2 = (Double) o2[1];
            return d1 == d2 ? 0 : (d2 > d1 ? 1 : -1);
        }); // descending
        Object[] removedNodes = new Object[5];
        for(int i = 0; i < removedNodes.length; i++) {
            removedNodes[i] = averageDistance.get(i)[0];
            for(List<Integer> route : routes) {
                route.remove(removedNodes[i]);
            }
        }
        // get 5 random routes
        List<List<Integer>> randomRoutes = new ArrayList<>(routes);
        while(randomRoutes.size() > 5) {
            randomRoutes.remove(rand.nextInt(randomRoutes.size()));
        }
//        Collections.sort(randomRoutes, (o1, o2) -> calculateRouteCost(o2) > calculateRouteCost(o1) ? -1 : 1); // ascending
        Collections.shuffle(randomRoutes);
        // Add removed nodes to random routes, from lowest cost to highest
        for(Object node : removedNodes) {
            boolean added = false;
            for(List<Integer> randomRoute : randomRoutes) {
                if(demands[(Integer)node] + calculateRouteDemand(randomRoute) <= capacityInt) {
                    randomRoute.add((Integer)node);
                    added = true;
                    break;
                }
            }
            if(!added) {
                throw new IllegalStateException("failed to add back something");
            }
        }
    }

    private int calculateRouteDemand(List<Integer> route) {
        int demand = 0;
        for(Integer node : route) {
            demand += demands[node];
        }
        return demand;
    }

    private double calculateRoutesCost(List<List<Integer>> routes) {
        double cost = 0;
        for(List<Integer> route : routes) {
            cost += calculateRouteCost(route);
        }
        return cost;
    }

    private double calculateRouteCost(List<Integer> route) {
        if(route.size() == 0) {
            return 0;
        }
        double cost = calculateDistance(route.get(0), 0);
        for(int i = 1; i < route.size(); i++) {
            cost += calculateDistance(route.get(i - 1), route.get(i));
        }
        return cost + calculateDistance(route.get(route.size() - 1), 0);
    }

    private double calculateDistance(int nodeA, int nodeB) {
        int[] cA = nodes[nodeA];
        int[] cB = nodes[nodeB];
        int dx = cA[0] - cB[0];
        int dy = cA[1] - cB[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void validateRoutes(List<List<Integer>> routes) {
        boolean[] visited = new boolean[dimensionInt];
        visited[0] = true;
        for(List<Integer> route : routes) {
            if(calculateRouteDemand(route) > capacityInt) {
                throw new IllegalStateException("Route exceeds capacity");
            }
            for(Integer node : route) {
                if(visited[node]) {
                    throw new IllegalStateException("Node visited twice " + node);
                }
                visited[node] = true;
            }
        }
        for(int i = 0; i < visited.length; i++) {
            if(!visited[i]) {
                throw new IllegalStateException("Node not visited " + i);
            }
        }
    }
}
