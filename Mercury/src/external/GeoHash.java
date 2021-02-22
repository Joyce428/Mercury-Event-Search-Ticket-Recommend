/*package external;

public class GeoHash {
	private static final String BASE_32 = "0123456789bcdefghjkmnpqrstuvwxyz";
	
	private static int dicideRangeByValue(double value, double[] range) {
		double mid = middle(range);
		if(value >=mid) {
			range[0] = mid;
			return 1;
		} else {
			range[1] = mid;
			return 0;
		}
		
	}
	
	private static String encodeGeohash(double latitude, double longitude, int precision) {
		double[] latRange = new double[]{-90.0,90.0};
		double[] lonRange = new double[]{-180.0,180.0};
		boolean isEven = true;
		int bit = 0;
		int base32CharIndex = 0;
		StringBuilder geohash = new StringBuilder();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
*/