package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	private String itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;
	
	//public Item(ItemBuilder builder){
		//this.itemId = builder.itemId;
		//this.name = builder.name;
	//}
	
	
	
	

	public Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	
	//this inner class has to be static
	//if it is non-static, you cannot have a builder object before you have an Item OBJECT
	public static class ItemBuilder{
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setRating(double rating) {
			this.rating = rating;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}	
		
		
		public Item build() {
			return new Item(this);//call the constructor of item, itemBuilder object as a parameter
		}
		
	}
	
	
	public JSONObject toJSONObject()
	{
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id",itemId);
			obj.put("name",name);
			obj.put("rating",rating);
			obj.put("address",address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url",imageUrl);
			obj.put("url",url);
			obj.put("distance",distance);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return obj;
		
	}
	
	
	public static void main(){
		ItemBuilder builder = new ItemBuilder();
		builder.setItemId("1234");
		builder.setAddress("abcd");
		builder.setName("Sun");
		
		//using builder, user will not have access to modify the field of Item class, after initialization of Item instance.
		//if you use public set method in Item class, user can modify the fields of Item object after this object's initialization
		
		Item item = builder.build();
	}
	
	
}










