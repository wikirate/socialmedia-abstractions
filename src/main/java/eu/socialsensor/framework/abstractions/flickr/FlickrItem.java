package eu.socialsensor.framework.abstractions.flickr;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.aetrion.flickr.FlickrException;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.GeoData;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.tags.Tag;

import eu.socialsensor.framework.common.domain.Feed;
import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.MediaItemLight;
import eu.socialsensor.framework.common.domain.Source;

/**
 * Class that holds the information regarding the flickr photo
 * @author ailiakop
 * @email  ailiakop@iti.gr
 */
public class FlickrItem extends Item {

	public FlickrItem(String id, Operation operation) {
		super(Source.Type.Flickr.toString(), operation);
		setId(Source.Type.Flickr+"::"+id);
	}
	
	public FlickrItem(Photo photo) {
		super(Source.Type.Flickr.toString(), Operation.NEW_UPDATE);
		if (photo == null || photo.getId() == null) return;
		
		id = Source.Type.Flickr + "::" + photo.getId();
		
		source = "Flickr";
		
		User user = photo.getOwner();
		if(user != null) {
			streamUser = new FlickrStreamUser(user);
			uid = streamUser.getId();
			author = streamUser.getId();
		}
		
		Date datePosted = photo.getDatePosted();
		publicationTime = datePosted.getTime();
		
		description = photo.getDescription();
		title = photo.getTitle();
		
		tags = null;
		@SuppressWarnings("unchecked")
		Collection<Tag> photoTags = photo.getTags();
		if (photoTags != null) {
			tags = new String[photoTags.size()];
			int i = 0;
			for(Tag tag : photoTags) {
				tags[i++] = tag.getValue();
			}
		}
		
		comments = new String[0];
		
		mediaLinks = new ArrayList<MediaItemLight>();
		Set<URL> mlinks = new HashSet<URL>();
		String url = null;
		
		try {
			String thumbnail = photo.getMediumUrl();
			if(thumbnail==null) {
				thumbnail = photo.getThumbnailUrl();
			}
			URL mediaUrl = null;
			if((url = photo.getLargeUrl()) != null) {
				mediaUrl = new URL(url);
				
			}
			else if ((url = photo.getMediumUrl()) != null) {
				mediaUrl = new URL(url);
			}
			else if ((url = photo.getSmallUrl()) != null) {
				mediaUrl = new URL(url);
			}
			else if ((url = photo.getOriginalUrl()) != null) {
				mediaUrl = new URL(url);
				
			}
			
			if(mediaUrl!=null){
				mlinks.add(mediaUrl);
				
				MediaItem mediaItem = new MediaItem(mediaUrl);
				String mediaId = Source.Type.Flickr + "::"+photo.getId(); 
				mediaItem.setId(mediaId);
				mediaItem.setType("image");
				mediaItem.setThumbnail(thumbnail);
				mediaItem.setRef(id);
				mediaItems.put(mediaUrl, mediaItem);
				mediaLinks.add(new MediaItemLight(url, thumbnail));
				mediaIds.add(mediaId);
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FlickrException e) {
			e.printStackTrace();
		}
		
		links = new URL[0];
		
		if(photo.hasGeoData()){
			
			GeoData geo = photo.getGeoData();
			
			double latitude = (double)geo.getLatitude();
			double longitude = (double) geo.getLongitude();
			
			location = new Location(latitude, longitude);
		}
		
		popularity = new HashMap<String, Integer>();
		int numOfComments = photo.getComments();
		
		popularity.put("comments", numOfComments);

	}
	
	public FlickrItem(Photo photo,Feed itemFeed) {
		super(Source.Type.Flickr.toString(), Operation.NEW_UPDATE);
		if (photo == null || photo.getId() == null) return;
		
		id = Source.Type.Flickr + "::" + photo.getId();
		
		source = "Flickr";
		
		User user = photo.getOwner();
		if(user != null) {
			streamUser = new FlickrStreamUser(user);
			uid = streamUser.getId();
			author = streamUser.getId();
		}
		
		feed = itemFeed;
		feedType = itemFeed.getFeedtype().toString();
		
		Date datePosted = photo.getDatePosted();
		publicationTime = datePosted.getTime();
		
		description = photo.getDescription();
		title = photo.getTitle();
		
		tags = null;
		@SuppressWarnings("unchecked")
		Collection<Tag> photoTags = photo.getTags();
		if (photoTags != null) {
			tags = new String[photoTags.size()];
			int i = 0;
			for(Tag tag : photoTags) {
				tags[i++] = tag.getValue();
			}
		}
		
		comments = new String[0];
		
		mediaLinks = new ArrayList<MediaItemLight>();
		Set<URL> mlinks = new HashSet<URL>();
		String url = null;
		try {
			String thumbnail = photo.getMediumUrl();
			if(thumbnail==null) {
				thumbnail = photo.getThumbnailUrl();
			}
			URL mediaUrl = null;
			if((url = photo.getLargeUrl()) != null) {
				mediaUrl = new URL(url);
			
			}
			else if ((url = photo.getMediumUrl()) != null) {
				mediaUrl = new URL(url);
			}
			
			
			if(mediaUrl!=null){
				mlinks.add(mediaUrl);
				
				MediaItem mediaItem = new MediaItem(mediaUrl);
				String mediaId = Source.Type.Flickr + "::"+photo.getId(); 
				mediaItem.setId(mediaId);
				mediaItem.setType("image");
				mediaItem.setThumbnail(thumbnail);
				mediaItem.setRef(id);
				mediaItems.put(mediaUrl, mediaItem);
				mediaLinks.add(new MediaItemLight(url, thumbnail));
				mediaIds.add(mediaId);
				mediaItem.setDyscoId(feed.getDyscoId());
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		links = new URL[0];
		
		if(photo.hasGeoData()){
			
			GeoData geo = photo.getGeoData();
			
			double latitude = (double)geo.getLatitude();
			double longitude = (double) geo.getLongitude();
			
			location = new Location(latitude, longitude);
		}
		
		popularity = new HashMap<String, Integer>();
		int numOfComments = photo.getComments();
		
		popularity.put("comments", numOfComments);

	}
	
}