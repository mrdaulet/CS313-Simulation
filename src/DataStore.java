import java.util.PriorityQueue;

public class DataStore {
	static boolean blocked = false;
	private int gridOffset = 250;
	public int targetX;
	public int targetY;
	
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
	public int[] getShortestPath(){
		int[] headingSequence = new int[100];
		
		PriorityQueue<Junction> Q = new PriorityQueue<Junction>();
		
		return headingSequence;
	}
}

class Junction implements Comparable<Junction>{
	/* North, East, South, West */
	public enum type {NONE, EXPLORED, UNEXPLORED, ORIGIN};
	public Junction[] neghbors = new Junction[4];
	public type[] edges = new type[4];
	
	public int costSoFar = 500;

	@Override
	public int compareTo(Junction o) {
		return (this.costSoFar < o.costSoFar) ? -1 : (this.costSoFar == o.costSoFar) ? 0 : 1;
	}

}