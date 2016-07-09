/**
 * Created by CMVirtusio on 2016-07-06.
 */
import com.sun.jmx.remote.internal.ArrayQueue;
import com.sun.org.apache.xpath.internal.functions.FuncFalse;

import java.util.*;

public class Maze123 {
    public static final int MazeSize = 25;
    int[][] hardcodemaze = {
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,0},
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,0},
            {0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,0},
            {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,0,0,1,1,1,1,0},
            {0,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,0,1,0,0,1,1,1,1,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,0,1,1},
            {0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0},
            {0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,1,1,1,1,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,0,1,0},
            {0,0,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,1,1,1,0,0,0,0},
            {0,0,1,1,1,1,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
            {0,0,1,1,1,1,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,0,0},
            {0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,1,0,1,1,1,0,0},
            {0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,1,0,1,1,1,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,0,1},
            {0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,0,1,1,1,0,1},
            {0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,0,0},
            {0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,0,0},
            {0,0,0,0,0,1,1,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0}
    };

    public ArrayList<int[]> get_neighbor_nodes(int[] curnode){
        ArrayList<int[]> neighbornodes = new ArrayList<int[]>();
        int x = curnode[0];
        int y = curnode[1];
        //lookbottom
        if(y-1 > 0 && y-1 < MazeSize){
            if(hardcodemaze[x][y-1] == 0) {
                neighbornodes.add(new int[]{x,y-1});
            }
        }
        //lookleft
        if(x-1 > 0 && x-1 < MazeSize){
            if(hardcodemaze[x-1][y] == 0) {
                neighbornodes.add(new int[]{x-1,y});
            }
        }
        //lookright
        if(x+1 > 0 && x+1 < MazeSize){
            if(hardcodemaze[x+1][y] == 0) {
                neighbornodes.add(new int[]{x+1,y});
            }
        }
        //looktop
        if(y+1 > 0 && y+1 < MazeSize){
            if(hardcodemaze[x][y+1] == 0) {
                neighbornodes.add(new int[]{x,y+1});
            }
        }
        return neighbornodes;
    }

    public boolean bfs(int[] startnode, int[] endnode){
        //IsSolved?
        boolean IsSolved = false;
        //Path
        ArrayDeque<int[]> path;
        //Queue
        ArrayDeque<ArrayDeque<int[]>> queue = new ArrayDeque<ArrayDeque<int[]>>();
        //Visited
        ArrayList<int[]> visited = new ArrayList<int[]>();

        //Init
        path = new ArrayDeque<>();
        path.addLast(startnode);
        //add path to Stack
        queue.addLast(path);

        while(!queue.isEmpty()){
            //<ArrayDeque<int[]> toppath = stack.pop();
            //path = queue.pop();
            //path = queue.getLast();
            path = queue.removeFirst();
            //int[] current_node = path.getFirst();
            int[] current_node = path.getLast();

            //if visited contains
            int countv = 0;
            for(int j = 0; j < visited.size(); j++){
                if(visited.get(j)[0] == current_node[0] &&
                        visited.get(j)[1] == current_node[1]) {
                    countv++;
                    break;
                }else{
                    //do nothing
                }
            }

            if(countv == 0){
                visited.add(current_node);
            } else {
                continue;
            }

            if(current_node[0] == endnode[0] && current_node[1] == endnode[1]){
                //solved
                IsSolved = true;
                break;
            }

            ArrayList<int[]> neighbors = get_neighbor_nodes(current_node);
            ArrayDeque<int[]> temppath;
            for(int i = 0 ; i < neighbors.size() ; i++){
                //if visited contains
                int count = 0;
                for(int j = 0; j < visited.size(); j++){
                    if(visited.get(j)[0] == neighbors.get(i)[0] &&
                            visited.get(j)[1] == neighbors.get(i)[1]) {
                        count++;
                        break;
                    }else{
                        //do nothing
                    }
                }
                if(count == 0){
                    temppath = new ArrayDeque<int[]>(path);
                    //temppath.push(neighbors.get(i));
                    temppath.addLast(neighbors.get(i));
                    //add path to Stack
                    queue.addLast(temppath);
                }
            }
        }
        if(IsSolved){

            //print path
            System.out.println("The Path : ");
            ArrayDeque<int[]> temppath = new ArrayDeque<int[]>(path);
            while(!temppath.isEmpty()){
                System.out.println("(" + temppath.getLast()[0] + "," + temppath.getLast()[1] + ")");
                temppath.removeLast();
            }
            //print cost
            System.out.println("The Cost : ");
            System.out.println(path.size());
            //print visited
            System.out.println("The Explored : ");
            System.out.println(visited.size());

            return true;
        } else {
            return false;
        }
    }

    public boolean dfs(int[] startnode, int[] endnode){
        //IsSolved?
        boolean IsSolved = false;
        //Path
        ArrayDeque<int[]> path;
        //Stack
        ArrayDeque<ArrayDeque<int[]>> stack = new ArrayDeque<ArrayDeque<int[]>>();
        //Visited
        ArrayList<int[]> visited = new ArrayList<int[]>();

        //Init
        path = new ArrayDeque<>();
        path.push(startnode);
        //add path to Stack
        stack.push(path);

        while(!stack.isEmpty()){
            //<ArrayDeque<int[]> toppath = stack.pop();
            path = stack.pop();
            int[] current_node = path.getFirst();
            visited.add(current_node);

            if(current_node[0] == endnode[0] && current_node[1] == endnode[1]){
                //solved
                IsSolved = true;
                break;
            }

            ArrayList<int[]> neighbors = get_neighbor_nodes(current_node);
            ArrayDeque<int[]> temppath;
            for(int i = 0 ; i < neighbors.size() ; i++){
                //if(!visited.contains(int[]{neighbors.get(i)[0],neighbors.get(i)[1]})){
                //if(!visited.contains(neighbors.get(i))){
                int count = 0;
                for(int j = 0; j < visited.size(); j++){
                    if(visited.get(j)[0] == neighbors.get(i)[0] &&
                            visited.get(j)[1] == neighbors.get(i)[1]) {
                        count++;
                        break;
                    }else{
                        //do nothing
                    }
                }
                if(count == 0){
                    temppath = new ArrayDeque<int[]>(path);
                    temppath.push(neighbors.get(i));
                    //add path to Stack
                    stack.push(temppath);
                }
            }
        }
        if(IsSolved){

            //print path
            System.out.println("The Path : ");
            ArrayDeque<int[]> temppath = new ArrayDeque<int[]>(path);
            while(!temppath.isEmpty()){
                System.out.println("(" + temppath.getLast()[0] + "," + temppath.getLast()[1] + ")");
                temppath.removeLast();
            }
            //print cost
            System.out.println("The Cost : ");
            System.out.println(path.size());
            //print visited
            System.out.println("The Explored : ");
            System.out.println(visited.size());

            return true;
        } else {
            return false;
        }
    }

    public int get_heuristic(int[] curnode, int[] endnode){
        //Manhattan
        int x = endnode[0] - curnode[0];
        int y = endnode[1] - curnode[1];
        int heuristic = Math.abs(y) + Math.abs(x);
        return heuristic;
    }

    public boolean astar(int[] startnode, int[] endnode){
        //IsSolved?
        boolean IsSolved = false;
        //Path
        ArrayList<int[]> path;
        //Queue
        ArrayList<ArrayList<int[]>> astarqueue = new ArrayList<ArrayList<int[]>>();
        //Visited
        ArrayList<int[]> visited = new ArrayList<int[]>();

        //Init
        path = new ArrayList<>();

        int[] astarstartnode = new int[3];
        astarstartnode[0] = startnode[0];
        astarstartnode[1] = startnode[1];
        astarstartnode[2] = 0 + get_heuristic(startnode,endnode) + 1;  //Cost + heuristics

        path.add(astarstartnode);
        //add path to astarqueue
        astarqueue.add(path);

        while(!astarqueue.isEmpty()){
            //pop min in astarqueue
            int curmin = 100000;
            int curminindex = 0;
            for(int i = 0;i < astarqueue.size() ;i++){
                ArrayList<int[]> curpath = astarqueue.get(i);
                int getvalue = curpath.get(curpath.size()-1)[2];
                if(getvalue < curmin)
                {
                    curmin = getvalue;
                    curminindex = i;
                }
            }
            path = astarqueue.remove(curminindex);

            int[] current_node = path.get(path.size()-1);     //get current node (last node of path)

            //if visited contains
            int countv = 0;
            for(int j = 0; j < visited.size(); j++){
                if(visited.get(j)[0] == current_node[0] &&
                        visited.get(j)[1] == current_node[1]) {
                    countv++;
                    break;
                }else{
                    //do nothing
                }
            }

            //if visited already, just ignore else add to visited list
            if(countv == 0){
                visited.add(current_node);
            } else {
                continue;
            }

            //Solved
            if(current_node[0] == endnode[0] && current_node[1] == endnode[1]){
                //solved
                IsSolved = true;
                break;
            }

            ArrayList<int[]> neighbors = get_neighbor_nodes(current_node);
            int pathcost = path.size();

            ArrayList<int[]> temppath;
            for(int i = 0 ; i < neighbors.size() ; i++){

                //if visited contains
                int count = 0;
                for(int j = 0; j < visited.size(); j++){
                    if(visited.get(j)[0] == neighbors.get(i)[0] &&
                            visited.get(j)[1] == neighbors.get(i)[1]) {
                        count++;
                        break;
                    }else{
                        //do nothing
                    }
                }

                if(count == 0){
                    //insertion sort to astarqueue
                    temppath = new ArrayList<int[]>(path);
                    int[] newnode = new int[3];
                    newnode[0] = neighbors.get(i)[0];
                    newnode[1] = neighbors.get(i)[1];
                    newnode[2] = pathcost + get_heuristic(neighbors.get(i),endnode) + 1;  //Cost + heuristics
                    temppath.add(temppath.size(),newnode);      //Add to end of queue
                    astarqueue.add(astarqueue.size(),temppath); //Add to end of queue, no need to sort
                }
            }
        }

//        if(IsSolved){
//            return true;
//        } else {
//            return false;
//        }
        if(IsSolved){

            //print path
            System.out.println("The Path : ");
            ArrayDeque<int[]> temppath = new ArrayDeque<int[]>(path);
            while(!temppath.isEmpty()){
                System.out.println("(" + temppath.getFirst()[0] + "," + temppath.getFirst()[1] + ")");
                temppath.removeFirst();
            }
            //print cost
            System.out.println("The Cost : ");
            System.out.println(path.size());
            //print visited
            System.out.println("The Explored : ");
            System.out.println(visited.size());

            return true;
        } else {
            return false;
        }

    }

    public static void main(String[] arg) {
        Maze123 lel = new Maze123(){};
        int[] s = new int[]{2,11};
        //int[] e1 = new int[]{2,16};
        int[] e1 = new int[]{23,19};
        int[] e2 = new int[]{2,21};
        int[] bl = new int[]{0,0};
        int[] tr = new int[]{24,24};
        lel.dfs(s,e1);
        lel.dfs(s,e2);
        lel.dfs(bl,tr);
        lel.bfs(s,e1);
        lel.bfs(s,e2);
        lel.bfs(bl,tr);
        lel.astar(s,e1);
        lel.astar(s,e2);
        lel.astar(bl,tr);
    }


}

