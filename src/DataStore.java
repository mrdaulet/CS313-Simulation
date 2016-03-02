import java.util.LinkedList;

public class DataStore {
	static boolean currentlyBlocked = false;
	private int gridOffset = 250;
	public int targetX, targetY, currentX,currentY;
	
	private Junction[][] grid;
	
	public DataStore(){
		grid = new Junction[500][500];
	}
	
	public void setJunction(int i,int j, Junction n){
		grid[gridOffset + i][gridOffset + j] = n;
	}
	
	public Junction getJunction(int i,int j){
		return grid[gridOffset + i][gridOffset + j];
	}
	
	/* Bellman-Ford would do, I guess */
	public LinkedList<Integer> getShortestPath(){
//		int[] headingSequence = new int[100];
		LinkedList<Integer> headingSequence = new LinkedList<Integer>();
		
		
		LinkedList<Junction> Q = new LinkedList<Junction>();
		
		
		Junction start = grid[gridOffset][gridOffset];
		start.costSoFar = 0;
		Q.add(start);
		
//		Lee's algorithm, or complete BFS, whatever
		while (!Q.isEmpty()){
			Junction current = Q.pop();
			for(int i = 0; i < 4; i++){
				if (current.neghbors[i] != null && current.neghbors[i].costSoFar > current.costSoFar + 1){
					current.neghbors[i].costSoFar = current.costSoFar + 1;
					current.neghbors[i].cameFrom = i;
					current.neghbors[i].parent = current;
					Q.add(current.neghbors[i]);
				}
			}
		}
		
		// reconstruct best path from the target node.
		Junction current = grid[gridOffset + targetX][gridOffset + targetY];
		
		System.out.println();
		while(current.parent != null){
			headingSequence.addFirst(current.cameFrom);
			System.out.print(current.cameFrom + ", ");
			current = current.parent;
		}
		
		return headingSequence ;
		
	}
}

class Junction{
	/* North, East, South, West */
	public enum type {NONE, EXPLORED, UNEXPLORED, ORIGIN};
	public Junction[] neghbors = new Junction[4];
	public type[] edges = new type[4];
	
	public int costSoFar = 500;
	public int cameFrom = -1;
	public Junction parent = null;

}