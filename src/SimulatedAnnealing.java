import sun.plugin2.liveconnect.ArgumentHelper;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class SimulatedAnnealing {
    public static final Random rand = new Random();

    String name;
    String comment;
    String type;
    String dimension;

    int[][] nodes;
    int[] demands;
    int dimensionInt;
    int capacityInt;

    public static void main(String[] args) {
        SimulatedAnnealing sa = new SimulatedAnnealing("data/A-VRP/A-n32-k5.vrp");
        sa.run(1000, 1, 0.85, 100);
        System.out.println("####");
        sa.run(600, 100, 0.96, 100);
        System.out.println("####");
        sa.run(5000, 0.001, 0.99, 1000);
    }

    public void readFile(String file) {
        try {
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
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private SimulatedAnnealing(String file) {
        readFile(file);
    }

    public void run(double T, double Tfinal, double alpha, int maxIterations) {
        System.out.println("Problem:");
        System.out.println("Capacity=" + capacityInt);
        System.out.println("Nodes=" + Arrays.deepToString(nodes));
        System.out.println("Demands=" + Arrays.toString(demands));

        List<List<Integer>> routes = generateInitialCondition();

        System.out.println("Initial condition:");
        System.out.println("Routes=" + Arrays.toString(routes.toArray()));
        System.out.println("Cost=" + calculateRoutesCost(routes));

        int iterations = 0;
        double beta = 1.05; // from paper
        double M0 = 5; // from paper

        // simulated annealing start
        List<List<Integer>> currentRoute = routes;
        List<List<Integer>> bestRoute = currentRoute;
        do {
            double M = M0;
            do {
                List<List<Integer>> newRoutes = neighbourhoodTransform(currentRoute);
                double dC = calculateRoutesCost(newRoutes) - calculateRoutesCost(currentRoute);
                if(dC < 0 || rand.nextDouble() < Math.exp(-dC / T)) {
                    currentRoute = newRoutes;
                    if(calculateRoutesCost(newRoutes) < calculateRoutesCost(bestRoute)) {
                        bestRoute = currentRoute;
                    }
                }
                M--;
            } while(M >= 0);
            iterations++;
            T *= alpha;
            M0 *= beta; // from paper
        } while(iterations < maxIterations && T > Tfinal);


        System.out.println("Solution:");
        System.out.println("Routes=" + Arrays.toString(bestRoute.toArray()));
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
        /*
         * move()
         */
        if(rand.nextDouble() < 0.8) {
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
            Collections.sort(distances, (o1, o2) -> (Double)o2[2] > (Double)o1[2] ? -1 :  1); // ascending
            // exclude nodes that are the depot or in the 5 shortest distances
            Set<Integer> excludeSet = new HashSet<>();
            excludeSet.add(0);
            for(int i = 0; i < 5; i++) {
                excludeSet.add((Integer)distances.get(i)[0]);
                excludeSet.add((Integer)distances.get(i)[1]);
            }
            // remove random nodes that aren't in the exclude set
            Object[] removedNodes = new Object[5];
            int nodeIndex = 0;
            while(nodeIndex < removedNodes.length) {
                int randomNode = rand.nextInt(dimensionInt);
                if(!excludeSet.contains(randomNode)) {
                    for(List<Integer> route : routes) {
                        route.remove((Object)randomNode);
                    }
                    excludeSet.add(randomNode);
                    removedNodes[nodeIndex] = randomNode;
                    nodeIndex++;
                }
            }
            // select random route and insert each node into the route, if it satisfies the capacity constraint
            for (int i = 0; i < removedNodes.length; i++) {
                int node = (Integer) removedNodes[i];
                while(true) {
                    List<Integer> randomRoute = routes.get(rand.nextInt(routes.size()));
                    if (demands[node] + calculateRouteDemand(randomRoute) <= capacityInt) {
                        randomRoute.add(node);
                        break;
                    }
                }
            }
        }
        replaceHighestAverage(routes);
        return routes;
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
        Collections.sort(averageDistance, (o1, o2) -> (Double)o2[1] > (Double)o1[1] ? 1 : -1); // descending
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
        Collections.sort(randomRoutes, (o1, o2) -> calculateRouteCost(o2) > calculateRouteCost(o1) ? -1 : 1); // ascending
        // Add removed nodes to random routes, from lowest cost to highest
        for(int i = 0; i < removedNodes.length; i++) {
            int node = (Integer)removedNodes[i];
            boolean added = false;
            for(int j = 0; j < randomRoutes.size(); j++) {
                List<Integer> randomRoute = randomRoutes.get(j);
                if(demands[node] + calculateRouteDemand(randomRoute) <= capacityInt) {
                    randomRoute.add(node);
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
        for(int i = 0; i < route.size(); i++) {
            demand += demands[route.get(i)];
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
