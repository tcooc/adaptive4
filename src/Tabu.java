import java.util.*;

public class Tabu {
    public static final int permSize = 20;

    int[][] naked_matrix = {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    };

    public static void main(String[] arg) {
        Tabu tabu = new Tabu(){};
        int[] init_perm;

        init_perm= new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
        System.out.println("Initial Solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0};
        System.out.println("change (1) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{0,19,1,18,2,17,3,16,4,15,5,14,6,13,7,12,8,11,9,10};
        System.out.println("change (2) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{10,9,11,8,12,7,13,6,14,5,15,4,16,3,17,2,18,1,19,0};
        System.out.println("change (3) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{0,1,2,3,4,10,11,12,13,14,5,6,7,8,9,15,16,17,18,19};
        System.out.println("change (4) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{19,18,17,16,15,9,8,7,6,5,14,13,12,11,10,4,3,2,1,0};
        System.out.println("change (5) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{11,17,7,10,5,16,4,9,2,14,19,15,1,12,6,8,3,0,18,13};
        System.out.println("change (6) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{10,9,12,1,3,19,5,0,2,16,17,6,8,15,4,11,14,18,7,13};
        System.out.println("change (7) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{11,4,19,15,10,7,16,13,5,6,17,8,18,12,9,2,14,1,0,3};
        System.out.println("change (8) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{3,2,14,4,1,5,16,7,12,18,6,10,11,19,0,8,13,9,17,15};
        System.out.println("change (9) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        init_perm = new int[]{8,3,6,10,16,17,5,7,9,12,18,2,11,4,14,0,19,15,1,13};
        System.out.println("change (10) solution");
        tabu.tabu_search(50,init_perm,5,false,false,false,false);

        //Original
        init_perm = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
        System.out.println("Larger Tabu List Size");
        tabu.tabu_search(50,init_perm,7,false,false,false,false);
        System.out.println("Smaller Tabu List Size");
        tabu.tabu_search(50,init_perm,3,false,false,false,false);
        System.out.println("Dynamic Tabu List Size");
        tabu.tabu_search(50,init_perm,5,true,false,false,false);
        System.out.println("Use Aspiration");
        tabu.tabu_search(50,init_perm,5,false,true,false,false);
        System.out.println("Use less than all swaps/neighborhood");
        tabu.tabu_search(50,init_perm,5,false,false,true,false);
        System.out.println("Use Diversification");
        tabu.tabu_search(50,init_perm,5,false,false,false,true);
    }

    public void tabu_search(int iterations,int[] init_perm,int listsize,
                            boolean isDynamic,boolean useAspiration,
                            boolean useLessthanAllSwaps,boolean diversification){
        int[] cur_best_perm = init_perm.clone();
        int[][] tabumatrix = naked_matrix.clone();
        int cost;
        int bestcost = 10000;
        int[] bestperm = cur_best_perm;
        int currentlistsize = listsize;
        for(int i = 0; i< iterations; i++){
            //change list size every 10% of total iterations
            if(i % (iterations/10) == 0 && isDynamic){
                Random rand = new Random();
                int max = listsize*3/2;
                int min = listsize/2;
                currentlistsize = rand.nextInt((max - min) + 1) + min;
            }
            //get best swap
            int[]swap = get_best_swap(cur_best_perm,tabumatrix,
                    useAspiration,bestcost,useLessthanAllSwaps);
            //discourage points, incremented when visited;
            int discourage = 0;
            //update frequencymatrix
            if(diversification){
                discourage = tabumatrix[swap[1]][swap[0]];
                tabumatrix[swap[1]][swap[0]] += 1;
            }
            //update tabu
            for (int itl = 0; itl < tabumatrix.length; itl++) {
                for (int jtl = 0; jtl < tabumatrix.length; jtl++) {
                    if(tabumatrix[itl][jtl] > 0 && itl < jtl){
                        tabumatrix[itl][jtl] -= 1;
                    }
                }
            }
            tabumatrix[swap[0]][swap[1]] = currentlistsize + discourage;
            //update current best permutation
            cur_best_perm = swapperoo(swap,cur_best_perm);
            //calculate cost for print
            cost = calculate_total_cost(get_flow_mx(),
                    get_distance_mx(cur_best_perm));

            //Print Every Iteration
            /*
            System.out.print("Permutation: { ");
            for(int j = 0; j < cur_best_perm.length; j++) {
                System.out.print(" " + cur_best_perm[j] + " ");
            }
            System.out.println("}");
            System.out.print("Cost: ");
            System.out.println(cost);
            */

            if(cost < bestcost){
                bestcost = cost;
                bestperm = cur_best_perm;
            }
        }
        System.out.print("Best of All Permutation: {");
        for(int j = 0; j < bestperm.length-1; j++) {
            System.out.print(" " + bestperm[j] + ",");
        }
        System.out.print(" " + bestperm[bestperm.length-1] + " ");
        System.out.println("}");
        System.out.print("Best of All Cost: ");
        System.out.println(bestcost);
    }
    //get all neighbors/swaps
    public ArrayList<int[]> get_all_swaps() {
        ArrayList<int[]> allswaps = new ArrayList<int[]>();
        for (int i = 0; i < permSize; i++) {
            for (int j = 0; j < permSize; j++) {
                if(i==j || i>j){
                    //do nothing
                }
                else{
                    int[] newnode = new int[]{i, j};
                    allswaps.add(newnode);
                }
            }
        }
        return allswaps;
    }
    //Get best swap
    public int[] get_best_swap(int[]currentpermutation,int[][]tabulist,
                               boolean useAspiration,int bestCost,
                               boolean useLessthanAllSwaps){
        int[] bestswap = {0,0};
        int curbestcost = 1000000;

        int[] clonecurperm;
        int[] nodestoswap;
        int[] swappedperm;
        int curcost;
        ArrayList<int[]> swaps = get_all_swaps();

        int i=0;
        int increments = 1;

        while(i<swaps.size()){
        //for(int i = 0; i<swaps.size();i++){
            clonecurperm = currentpermutation.clone();
            nodestoswap = swaps.get(i);
            swappedperm = swapperoo(nodestoswap,clonecurperm);
            curcost = calculate_total_cost(get_flow_mx(),
                    get_distance_mx(swappedperm));

            //if it is in the tabu list
            if( tabulist[swaps.get(i)[0]][swaps.get(i)[1]] > 0){
                //do nothing;
            }else{
                if(curcost < curbestcost){
                    curbestcost = curcost;
                    bestswap = swaps.get(i);
                }
            }
            if(useAspiration){
                if(curcost < bestCost){
                    bestCost = curcost;
                    curbestcost = curcost;
                    bestswap = swaps.get(i);
                }
            }
            //less than all swaps/neighbors
            if(useLessthanAllSwaps){
                Random rand = new Random();
                int max = 10;
                int min = 1;
                increments = rand.nextInt((max - min) + 1) + min;
            }
            i += increments;
        }
        return bestswap;
    }
    //Get index of department in Array
    public int get_index(int[] array, int value){
        int index = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] == value){
                index = i;
                break;
            }
        }
        return index;
    }
    //Swaps the two Departments
    public int[] swapperoo(int[] thenodes, int[]currentpermutation){
        int[] swappedperm = currentpermutation.clone();

        for(int i = 0; i < currentpermutation.length; i++){
            swappedperm[i] = currentpermutation[i];
        }

        int firstnum = thenodes[0];
        int secondnum = thenodes[1];

        int firstnumindex = get_index(currentpermutation,firstnum);
        int secondnumindex = get_index(currentpermutation,secondnum);

        swappedperm[firstnumindex] = secondnum;
        swappedperm[secondnumindex] = firstnum;
        return swappedperm;
    }
    //Calculate Distance of two departments
    public int calculate_distance(int firstnum, int secondnum, int[] permutation){
        int rowSize = 5;
        //get index of first
        int ifn = get_index(permutation,firstnum);
        //get index of second
        int isn = get_index(permutation,secondnum);
        //calculate

        int rowdiff = Math.abs((ifn)/rowSize - (isn)/rowSize);
        int coldiff = Math.abs((ifn)%rowSize - (isn)%rowSize);

        return coldiff + rowdiff;
    }
    //Get Distance Matrix (Distances are calculated based on permutation)
    public int[][] get_distance_mx(int[] permutation){
        int[][] dist_matrix = new int[permutation.length][permutation.length];
        for (int i = 0; i < permutation.length; i++) {
            for (int j = 0; j < permutation.length; j++) {
                dist_matrix[permutation[i]][permutation[j]] =
                    calculate_distance(permutation[i],permutation[j],permutation);
            }
        }
        return dist_matrix;
    }
    //Get Flow Matrix (Hardcoded)
    public int[][] get_flow_mx(){
        int[][] flowmatrixhardcode = {
                {0,0,5,0,5,2,10,3,1,5,5,5,0,0,5,4,4,0,0,1},
                {0,0,3,10,5,1,5,1,2,4,2,5,0,10,10,3,0,5,10,5},
                {5,3,0,2,0,5,2,4,4,5,0,0,0,5,1,0,0,5,0,0},
                {0,10,2,0,1,0,5,2,1,0,10,2,2,0,2,1,5,2,5,5},
                {5,5,0,1,0,5,6,5,2,5,2,0,5,1,1,1,5,2,5,1},
                {2,1,5,0,5,0,5,2,1,6,0,0,10,0,2,0,1,0,1,5},
                {10,5,2,5,6,5,0,0,0,0,5,10,2,2,5,1,2,1,0,10},
                {3,1,4,2,5,2,0,0,1,1,10,10,2,0,10,2,5,2,2,10},
                {1,2,4,1,2,1,0,1,0,2,0,3,5,5,0,5,0,0,0,2},
                {5,4,5,0,5,6,0,1,2,0,5,5,0,5,1,0,0,5,5,2},
                {5,2,0,10,2,0,5,10,0,5,0,5,2,5,1,10,0,2,2,5},
                {5,5,0,2,0,0,10,10,3,5,5,0,2,10,5,0,1,1,2,5},
                {0,0,0,2,5,10,2,2,5,0,2,2,0,2,2,1,0,0,0,5},
                {0,10,5,0,1,0,2,0,5,5,5,10,2,0,5,5,1,5,5,0},
                {5,10,1,2,1,2,5,10,0,1,1,5,2,5,0,3,0,5,10,10},
                {4,3,0,1,1,0,1,2,5,0,10,0,1,5,3,0,0,0,2,0},
                {4,0,0,5,5,1,2,5,0,0,0,1,0,1,0,0,0,5,2,0},
                {0,5,5,2,2,0,1,2,0,5,2,1,0,5,5,0,5,0,1,1},
                {0,10,0,5,5,1,0,2,0,5,2,2,0,5,10,2,2,1,0,6},
                {1,5,0,5,1,5,10,10,2,2,5,5,5,0,10,0,0,1,6,0}
        };

        return flowmatrixhardcode;
    }
    //Calculate Total Cost of the permutation (flow*distance)
    public int calculate_total_cost(int[][]flowmatrix, int[][]distancematrix){
        int curtotal = 0;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                curtotal = curtotal + flowmatrix[i][j] * distancematrix[i][j];
            }
        }
        return curtotal;
    }
}
